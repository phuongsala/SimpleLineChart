package promiennam.co.simplelinechart.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import promiennam.co.simplelinechart.R;
import promiennam.co.simplelinechart.enums.ChartViewType;
import promiennam.co.simplelinechart.helpers.ChartViewHelper;
import promiennam.co.simplelinechart.helpers.FirebaseDataHelper;
import promiennam.co.simplelinechart.interfaces.ILoadDataListener;
import promiennam.co.simplelinechart.models.Nav;
import promiennam.co.simplelinechart.models.Portfolio;
import promiennam.co.simplelinechart.tasks.LoadDataTask;
import promiennam.co.simplelinechart.utils.DateTimeUtil;
import promiennam.co.simplelinechart.utils.SortByDateUtil;

public class HomeActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private List<Portfolio> mPortfolioByDayList;  //view by daily
    private ChartViewHelper mChartViewHelper;
    private FirebaseDataHelper mDatabaseHelper;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // chart view
        LineChart chart = (LineChart) findViewById(R.id.line_chart);
        // chart description
        TextView chartDesc = (TextView) findViewById(R.id.txt_chart_desc);
        // initialize chart view helper
        mChartViewHelper = new ChartViewHelper(chart, chartDesc);
        // initialize database helper
        mDatabaseHelper = new FirebaseDataHelper();
        // progress bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        // configure chart
        configChart(chart);
        // load data
        loadData();
    }

    private void configChart(LineChart chart) {
        chart.setDrawGridBackground(true);
        chart.setOnChartValueSelectedListener(this);

        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

        chart.animateX(500);
        chart.getDescription().setText("");
        chart.setNoDataText("");

        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
    }

    private void loadData() {

        new LoadDataTask(new ILoadDataListener() {
            @Override
            public void onCompleted(List<Portfolio> portfolioList) {
                handleOnLoadDataCompleted(portfolioList);
            }

            @Override
            public void onError() {
                mChartViewHelper.showNoDataText();
            }
        }).execute(getResources().openRawResource(R.raw.data_json));

    }

    private void handleOnLoadDataCompleted(List<Portfolio> portfolioList) {
        if (portfolioList != null && !portfolioList.isEmpty()) {
            // set portfolio list
            mPortfolioByDayList = portfolioList;
            // get portfolio total by calculate from the portfolio list
            Portfolio portfolioTotalByDay = getPortfolioTotalByDay(portfolioList);
            // add portfolio total to portfolio list
            mPortfolioByDayList.add(portfolioTotalByDay);
            // set portfolio by day list for chart view helper
            mChartViewHelper.setPortfolioByDayList(mPortfolioByDayList);
            // display chart daily at the first time
            displayChartDaily();
        }
    }

    private Portfolio getPortfolioTotalByDay(List<Portfolio> portfolioByDayList) {
        Portfolio portfolioTotalByDay = new Portfolio();
        portfolioTotalByDay.setId("portfolio_total_id");
        List<Nav> navTotalList = new ArrayList<>();

        for (int i = 0; i < portfolioByDayList.size(); i++) {
            List<Nav> navList = portfolioByDayList.get(i).getNavList();
            if (navList != null) {
                if (i == 0) {
                    for (int j = 0; j < navList.size(); j++) {
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
        portfolioTotalByDay.setNavList(navTotalList);

        return portfolioTotalByDay;
    }

    private List<Portfolio> getPortfolioByMonthList(List<Portfolio> portfolioByDayList) {
        List<Portfolio> portfolioByMonthList = new ArrayList<>();
        for (int i = 0; i < portfolioByDayList.size(); i++) {
            List<Nav> navByDayList = portfolioByDayList.get(i).getNavList();
            if (navByDayList != null) {
                List<Nav> navByMonthList = new ArrayList<>();
                for (int j = 0; j < navByDayList.size(); j++) {

                    Date date = DateTimeUtil.convertStringDateToDate(navByDayList.get(j).getDate());

                    if (DateTimeUtil.isTheLastDayOfMonth(date)) {
                        navByMonthList.add(navByDayList.get(j));
                    }
                }
                Portfolio portfolioByMonth = new Portfolio();
                portfolioByMonth.setNavList(navByMonthList);
                portfolioByMonthList.add(portfolioByMonth);
            }
        }
        return portfolioByMonthList;
    }

    private List<Portfolio> getPortfolioByQuarterList(List<Portfolio> portfolioByDayList) {
        List<Portfolio> portfolioByQuarterList = new ArrayList<>();
        for (int i = 0; i < portfolioByDayList.size(); i++) {
            List<Nav> navByDayList = portfolioByDayList.get(i).getNavList();
            if (navByDayList != null) {
                List<Nav> navByQuarterList = new ArrayList<>();
                for (int j = 0; j < navByDayList.size(); j++) {

                    Date date = DateTimeUtil.convertStringDateToDate(navByDayList.get(j).getDate());

                    if (DateTimeUtil.isQuarterMonth(date)) {
                        if (DateTimeUtil.isTheLastDayOfMonth(date)) {
                            navByQuarterList.add(navByDayList.get(j));
                        }
                    }
                }
                Portfolio portfolioByQuarter = new Portfolio();
                portfolioByQuarter.setNavList(navByQuarterList);
                portfolioByQuarterList.add(portfolioByQuarter);
            }
        }
        return portfolioByQuarterList;
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
                displayChartDaily();
                return true;
            case R.id.monthly:
                displayChartMonthly();
                return true;
            case R.id.quarterly:
                displayChartQuarterly();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayChartDaily() {
        mChartViewHelper.displayChart(ChartViewType.DAILY);
        // hide progress bar
        progressBar.setVisibility(View.GONE);
        //--------------------------------
        // store database to firebase
        //--------------------------------
        mDatabaseHelper.savePortfolios(mPortfolioByDayList);
    }

    private void displayChartMonthly() {
        if (mChartViewHelper.getPortfolioByMonthList() == null) {
            progressBar.setVisibility(View.VISIBLE);
            mChartViewHelper.setPortfolioByMonthList(getPortfolioByMonthList(mPortfolioByDayList));
            progressBar.setVisibility(View.GONE);
            //--------------------------------
            // store database to firebase
            //--------------------------------
            mDatabaseHelper.savePortfolios(mChartViewHelper.getPortfolioByMonthList());
        }
        mChartViewHelper.displayChart(ChartViewType.MONTHLY);
    }

    private void displayChartQuarterly() {
        if (mChartViewHelper.getPortfolioByQuarterList() == null) {
            progressBar.setVisibility(View.VISIBLE);
            mChartViewHelper.setPortfolioByQuarterList(getPortfolioByQuarterList(mPortfolioByDayList));
            progressBar.setVisibility(View.GONE);
            //--------------------------------
            // store database to firebase
            //--------------------------------
            mDatabaseHelper.savePortfolios(mChartViewHelper.getPortfolioByQuarterList());
        }
        mChartViewHelper.displayChart(ChartViewType.QUARTERLY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, DateTimeUtil.convertFloatDateToStringDate(e.getX()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
