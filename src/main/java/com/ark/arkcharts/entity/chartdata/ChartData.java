package com.ark.arkcharts.entity.chartdata;

import java.util.List;

/**
 * @author Ark
 * @date 2020/05/18 17:33
 * 图表数据的抽象类
 */
public abstract class ChartData {
    protected String chartName;
    protected List<String> legendArray;
    protected List<ChartSeries> seriesArray;

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public List<String> getLegendArray() {
        return legendArray;
    }

    public void setLegendArray(List<String> legendArray) {
        this.legendArray = legendArray;
    }

    public List<ChartSeries> getSeriesArray() {
        return seriesArray;
    }

    public void setSeriesArray(List<ChartSeries> seriesArray) {
        this.seriesArray = seriesArray;
    }
}
