package promiennam.co.simplelinechart.helpers;

import android.graphics.Color;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import promiennam.co.simplelinechart.R;
import promiennam.co.simplelinechart.enums.ChartViewType;
import promiennam.co.simplelinechart.models.Nav;
import promiennam.co.simplelinechart.models.Portfolio;
import promiennam.co.simplelinechart.utils.DateTimeUtil;

/**
 * Created by Phuong on 15-May-17.
 */

public class ChartViewHelper {

    private LineChart mChart;
    private TextView mChartDesc;

    private List<Portfolio> mPortfolioByDayList;
    private List<Portfolio> mPortfolioByMonthList;
    private List<Portfolio> mPortfolioByQuarterList;

    public ChartViewHelper(LineChart chart, TextView chartDesc) {
        mChart = chart;
        mChartDesc = chartDesc;
    }

    public void displayChart(ChartViewType chartViewType) {

        // display x-axis with date time again
        reformatXValues(mChart, chartViewType);

        List<Portfolio> portfolioList = null;

        switch (chartViewType) {
            case DAILY:
                portfolioList = mPortfolioByDayList;
                mChartDesc.setText(R.string.txt_chart_desc_daily);
                break;
            case MONTHLY:
                portfolioList = mPortfolioByMonthList;
                mChartDesc.setText(R.string.txt_chart_desc_monthly);
                break;
            case QUARTERLY:
                portfolioList = mPortfolioByQuarterList;
                mChartDesc.setText(R.string.txt_chart_desc_quarterly);
                break;
            default:
                break;
        }

        if (portfolioList != null) {
            // set data
            mChart.setData(getLineData(portfolioList));
            // draw chart
            mChart.invalidate();
        } else {
            showNoDataText();
        }
    }

    private void reformatXValues(LineChart chart, final ChartViewType chartViewType) {
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                switch (chartViewType) {
                    case DAILY:
                        return DateTimeUtil.convertFloatDateToStringDate(value);
                    case MONTHLY:
                        return DateTimeUtil.convertFloatDateToMonth(value);
                    case QUARTERLY:
                        return DateTimeUtil.convertFloatDateToQuarter(value);
                    default:
                        return "";
                }
            }
        });
    }

    private LineData getLineData(List<Portfolio> portfolioList) {
        if (portfolioList != null) {
            // create new line list
            ArrayList<ILineDataSet> lineList = new ArrayList<>();
            // add each line to line list
            for (int i = 0; i < portfolioList.size(); i++) {
                LineDataSet line = new LineDataSet(getEntryList(portfolioList.get(i).getNavList()),
                        i < 3 ? "Portfolio " + (i + 1) : "Total");

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
            return new LineData(lineList);
        }
        return null;
    }

    private ArrayList<Entry> getEntryList(List<Nav> navList) {
        if (navList != null) {
            ArrayList<Entry> entryList = new ArrayList<>();
            for (int i = 0; i < navList.size(); i++) {
                // convert date to timestamp
                float timestamp = DateTimeUtil.convertDateStringToMillis(navList.get(i).getDate());
                // add entry to entry list
                entryList.add(new Entry(timestamp, navList.get(i).getAmount()));
            }
            return entryList;
        }
        return null;
    }

    private int randomColor(int i) {
        return i == 0 ? Color.GREEN : i == 1 ? Color.BLUE : i == 2 ? Color.RED : Color.BLACK;
    }

    public void showNoDataText(){
        mChart.setNoDataText(mChart.getContext().getString(R.string.error_load_data));
    }

    public void setPortfolioByDayList(List<Portfolio> mPortfolioByDayList) {
        this.mPortfolioByDayList = mPortfolioByDayList;
    }

    public void setPortfolioByMonthList(List<Portfolio> mPortfolioByMonthList) {
        this.mPortfolioByMonthList = mPortfolioByMonthList;
    }

    public void setPortfolioByQuarterList(List<Portfolio> mPortfolioByQuarterList) {
        this.mPortfolioByQuarterList = mPortfolioByQuarterList;
    }

    public List<Portfolio> getPortfolioByDayList() {
        return mPortfolioByDayList;
    }

    public List<Portfolio> getPortfolioByMonthList() {
        return mPortfolioByMonthList;
    }

    public List<Portfolio> getPortfolioByQuarterList() {
        return mPortfolioByQuarterList;
    }
}
