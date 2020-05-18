package com.ark.arkcharts.entity.chartdata;

/**
 * @author Ark
 * @date 2020/05/18 17:36
 */
public abstract class ChartSeries {
    protected String name;
    protected String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
