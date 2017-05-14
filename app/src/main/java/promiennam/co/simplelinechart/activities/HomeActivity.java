package promiennam.co.simplelinechart.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import promiennam.co.simplelinechart.R;
import promiennam.co.simplelinechart.enums.ChartViewType;
import promiennam.co.simplelinechart.models.Nav;
import promiennam.co.simplelinechart.models.Portfolio;
import promiennam.co.simplelinechart.util.DateTimeUtil;
import promiennam.co.simplelinechart.util.DisplayUtil;
import promiennam.co.simplelinechart.util.SortByDateUtil;

public class HomeActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private LineChart mChart;
    private TextView mChartDesc;
    private List<Portfolio> mPortfolioByDayList;     // by day
    private List<Portfolio> mPortfolioByMonthList;   // by month
    private List<Portfolio> mPortfolioByQuarterList; // by quarter
    private Portfolio mPortfolioTotalForEachDay; // total
    private DateTimeUtil mDateTimeUtil;

    private float mCenterX; // centerX of screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mChart = (LineChart) findViewById(R.id.line_chart);
        mChart.setDrawGridBackground(true);
        mChart.setOnChartValueSelectedListener(this);

        mChartDesc = (TextView) findViewById(R.id.txt_chart_desc);

        mDateTimeUtil = new DateTimeUtil();

        DisplayUtil displayUtil = new DisplayUtil();
        mCenterX = displayUtil.getCenterX(this);

        loadData();

        setupChart();

        displayChart(ChartViewType.MONTH); // view by month at the first time
    }

    private List<Portfolio> loadData() {
        InputStream inputStream = getResources().openRawResource(R.raw.data_json);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        int index;

        try {
            index = inputStream.read();
            while (index != -1) {
                output.write(index);
                index = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_load_data, Toast.LENGTH_SHORT).show();
            return null;
        }

        if (output.toString() != null) {
            mPortfolioByDayList = new Gson().fromJson(output.toString(), new TypeToken<ArrayList<Portfolio>>() {
            }.getType());
        }

        // setup total portfolio for each day
        setupPortfolioTotalForEachDay();

        // setup portfolio by month, quarter
        setupPortfolioByMonthAndQuarter();

        return mPortfolioByDayList;
    }

    private void setupPortfolioTotalForEachDay() {

        mPortfolioTotalForEachDay = new Portfolio();

        List<Nav> navTotalList = new ArrayList<>();

        // handle update date for each nav
        for (int i = 0; i < mPortfolioByDayList.size(); i++) {
            List<Nav> navList = mPortfolioByDayList.get(i).getNavList();
            if (navList != null) {
                if (i == 0) {
                    // this line below should work, but i don't know why it doesn't work correctly
                    // navTotalList.addAll(navList);
                    //---------------------------------------
                    // just replace by these lines below T_T
                    for (int j = 0; j < navList.size(); j++){
                        navTotalList.add(new Nav(navList.get(j).getDate(), navList.get(j).getAmount()));
                    }
                } else {
                    for (int j = 0; j < navList.size(); j++) {
                        boolean flag = false;
                        for (Nav nav : navTotalList) {
                            if (nav.getDate().equals(navList.get(j).getDate())) {
                                nav.setAmount(nav.getAmount() + navList.get(j).getAmount());
                                flag = true;
                            }
                        }
                        if (!flag) {
                            navTotalList.add(navList.get(j));
                        }
                    }
                }
            }
        }

        Collections.sort(navTotalList, new SortByDateUtil());

        mPortfolioTotalForEachDay.setNavList(navTotalList);

        // add portfolio total to mPortfolioByDayList
        mPortfolioByDayList.add(mPortfolioTotalForEachDay);
    }

    private void setupPortfolioByMonthAndQuarter() {

        mPortfolioByMonthList = new ArrayList<>();
        mPortfolioByQuarterList = new ArrayList<>();

        for (int i = 0; i < mPortfolioByDayList.size(); i++) {

            Portfolio portfolioByMonth = new Portfolio();
            Portfolio portfolioByQuarter = new Portfolio();

            List<Nav> navMonthlyList = new ArrayList<>();
            List<Nav> navQuarterlyList = new ArrayList<>();

            List<Nav> navList = mPortfolioByDayList.get(i).getNavList();

            if (navList != null) {
                for (int j = 0; j < navList.size(); j++) {

                    Date date = mDateTimeUtil.convertStringDateToDate(navList.get(j).getDate());

                    if (mDateTimeUtil.isTheLastDayOfMonth(date)) {
                        navMonthlyList.add(navList.get(j));

                        if (mDateTimeUtil.isQuarterMonth(date)) {
                            navQuarterlyList.add(navList.get(j));
                        }
                    }
                }
                portfolioByMonth.setNavList(navMonthlyList);
                portfolioByQuarter.setNavList(navQuarterlyList);
            }
            mPortfolioByMonthList.add(portfolioByMonth);
            mPortfolioByQuarterList.add(portfolioByQuarter);
        }

        // test store to firebase
        testStoreDataToFirebase();
    }

    private ArrayList<Entry> getValues(ChartViewType chartViewType, int index) {
        ArrayList<Entry> values = new ArrayList<>();

        List<Nav> navList = null;

        switch (chartViewType) {
            case DAY:
                navList = mPortfolioByDayList.get(index).getNavList();
                break;
            case MONTH:
                navList = mPortfolioByMonthList.get(index).getNavList();
                break;
            case QUARTER:
                navList = mPortfolioByQuarterList.get(index).getNavList();
                break;
            default:
                break;
        }

        Log.d("size nav ", "" + navList.size());

        if (navList != null) {
            for (int i = 0; i < navList.size(); i++) {
                float timestamp = mDateTimeUtil.convertDateStringToMillis(navList.get(i).getDate());
                values.add(new Entry(timestamp, navList.get(i).getAmount()));
            }
        }

        return values;
    }

    private void setupChart() {
        Legend legend = mChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

//        Description desc = new Description();
//        desc.setText(getString(R.string.txt_chart_desc));
//        desc.setTextSize(13);
//        mChart.setDescription(desc);
//        mChart.zoomToCenter(2.0f, 2.0f);

        mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        mChart.moveViewToX(mCenterX);

        mChart.animateX(500);
        mChart.getDescription().setText("");

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
    }

    private void displayChart(ChartViewType chartViewType) {

        ArrayList<ILineDataSet> lineList = new ArrayList<>();

        List<Portfolio> portfolioList = null;

        formatXValues(chartViewType);

        switch (chartViewType) {
            case DAY:
                portfolioList = mPortfolioByDayList;
                mChartDesc.setText(R.string.txt_chart_desc_daily);
                break;
            case MONTH:
                portfolioList = mPortfolioByMonthList;
                mChartDesc.setText(R.string.txt_chart_desc_monthly);
                break;
            case QUARTER:
                portfolioList = mPortfolioByQuarterList;
                mChartDesc.setText(R.string.txt_chart_desc_quarterly);
                break;
            default:
                break;
        }

        Log.d("size portfolio", "" + portfolioList.size());

        if (portfolioList != null) {
            for (int i = 0; i < portfolioList.size(); i++) {

                LineDataSet line = new LineDataSet(getValues(chartViewType, i),
                        i < 3 ?  "Portfolio " + (i + 1) : "Total");

                line.setFillAlpha(110);
                line.setColor(randomColor(i));
                line.setCircleColor(randomColor(i));
                line.setLineWidth(3f);
                line.setCircleRadius(5f);
                line.setDrawCircleHole(false);
                line.setValueTextSize(14f);
                line.setDrawFilled(false);
                line.setDrawHighlightIndicators(true);

                lineList.add(line);

            }

            Log.d("size line list ", "" + lineList.size());

            LineData data = new LineData(lineList);

            // set data
            mChart.setData(data);

            mChart.invalidate();
        }
    }

    private int randomColor(int i) {
        return i == 0 ? Color.GREEN : i == 1 ? Color.BLUE : i == 2 ? Color.RED : Color.BLACK;
    }

    private void formatXValues(final ChartViewType chartViewType) {
        mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                switch (chartViewType) {
                    case DAY:
                        return mDateTimeUtil.convertFloatDateToStringDate(value);
                    case MONTH:
                        return mDateTimeUtil.convertFloatDateToMonth(value);
                    case QUARTER:
                        return mDateTimeUtil.convertFloatDateToQuarter(value);
                    default:
                        return "";
                }
            }
        });
    }

    private void testStoreDataToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference portfolioMonthListRef = database.getReference("portfolioByMonthList");
        portfolioMonthListRef.setValue(mPortfolioByMonthList);
        portfolioMonthListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Hello", "What's new in month?");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference portfolioQuarterListRef = database.getReference("portfolioByQuarterList");
        portfolioQuarterListRef.setValue(mPortfolioByQuarterList);
        portfolioQuarterListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Hello", "What's new in quarter?");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.daily:
                displayChart(ChartViewType.DAY);
                return true;
            case R.id.monthly:
                displayChart(ChartViewType.MONTH);
                return true;
            case R.id.quarterly:
                displayChart(ChartViewType.QUARTER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, mDateTimeUtil.convertFloatDateToStringDate(e.getX()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
