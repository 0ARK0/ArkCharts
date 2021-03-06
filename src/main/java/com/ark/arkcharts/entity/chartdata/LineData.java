package com.ark.arkcharts.entity.chartdata;

import java.util.List;

/**
 * @author Ark
 * @date 2020/07/18 20:00
 */
public class LineData extends ChartData {
    private List<String> xData;
    private String[][] yData;

    public List<String> getxData() {
        return xData;
    }

    public void setxData(List<String> xData) {
        this.xData = xData;
    }

    public String[][] getyData() {
        return yData;
    }

    public void setyData(String[][] yData) {
        this.yData = yData;
    }
}
