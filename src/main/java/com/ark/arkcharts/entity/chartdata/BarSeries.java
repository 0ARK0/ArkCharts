package com.ark.arkcharts.entity.chartdata;

/**
 * @author Ark
 * @date 2020/05/17 8:27
 * 柱状图的series对象
 */
public class BarSeries extends ChartSeries {

    private String[] data;

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }
}
