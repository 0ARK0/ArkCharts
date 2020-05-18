package com.ark.arkcharts.entity.chartdata;

import java.util.List;

/**
 * @author Ark
 * @date 2020/05/18 17:39
 */
public class PieData extends ChartData{
    private List<String> dataArray;

    public List<String> getDataArray() {
        return dataArray;
    }

    public void setDataArray(List<String> dataArray) {
        this.dataArray = dataArray;
    }
}
