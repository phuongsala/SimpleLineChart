package promiennam.co.simplelinechart.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import promiennam.co.simplelinechart.R;
import promiennam.co.simplelinechart.enums.ChartViewType;
import promiennam.co.simplelinechart.models.Nav;
import promiennam.co.simplelinechart.models.Portfolio;
import promiennam.co.simplelinechart.util.DateTimeUtil;

public class MainActivity extends AppCompatActivity implements
        OnChartGestureListener,
        OnChartValueSelectedListener {

    private LineChart mChart;
    private List<Portfolio> mPortfolioByDayList; // by day
    private List<Portfolio> mPortfolioByMonthList; // by month
    private List<Portfolio> mPortfolioByQuarterList; // by quarter
    private DateTimeUtil mDateTimeUtil;
    private int mTotal; // total portfolios for each day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart = (LineChart) findViewById(R.id.line_chart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        mDateTimeUtil = new DateTimeUtil();

        loadData();

        setupChart();

        displayChart(ChartViewType.DAY); // view by day at the first time
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

        // setup portfolio by month, quarter
        setupPortfolioByMonthAndQuarter();

        return mPortfolioByDayList;
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

        Description desc = new Description();
        desc.setText(getString(R.string.txt_chart_desc));
        desc.setTextSize(13);
        mChart.setDescription(desc);

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
                break;
            case MONTH:
                portfolioList = mPortfolioByMonthList;
                break;
            case QUARTER:
                portfolioList = mPortfolioByQuarterList;
                break;
            default:
                break;
        }

        if (portfolioList != null) {
            for (int i = 0; i < portfolioList.size(); i++) {

                LineDataSet line = new LineDataSet(getValues(chartViewType, i), "Portfolio " + (i + 1));

                line.setFillAlpha(110);
                line.setColor(i == 0 ? Color.YELLOW : i == 1 ? Color.BLUE : Color.RED);
                line.setCircleColor(Color.BLACK);
                line.setLineWidth(1f);
                line.setCircleRadius(3f);
                line.setDrawCircleHole(false);
                line.setValueTextSize(9f);
                line.setDrawFilled(false);

                lineList.add(line);

            }

            LineData data = new LineData(lineList);

            // set data
            mChart.setData(data);

            mChart.invalidate();
        }
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
                        return null;
                }
            }
        });
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

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
}
