package com.ark.arkcharts.service;

import com.ark.arkcharts.entity.Chart;
import com.ark.arkcharts.entity.User;

import java.util.List;

/**
 * @author Ark
 * @date 2020/05/16 13:21
 */
public interface ChartService {

    Chart saveNewChart(String chartObject, User user);

    void saveChart(String chartPath, String chartObject, User user);

    List<Chart> getAllChartsByUser(User user);

    Chart getChartById(String chartId);

    void delChart(String userId, String chartId);
}
