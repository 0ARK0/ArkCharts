package com.ark.arkcharts.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.ark.arkcharts.entity.Chart;
import com.ark.arkcharts.entity.User;
import com.ark.arkcharts.mapper.ChartMapper;
import com.ark.arkcharts.myutils.PathUtil;
import com.ark.arkcharts.service.ChartService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Ark
 * @date 2020/05/16 13:25
 */
@Service
public class ChartServiceImpl implements ChartService {

    @Autowired
    ChartMapper chartMapper;

    @Override
    public Chart saveNewChart(String chartObject, User user) {
        File userDir = new File(PathUtil.getUserDir(user.getUserId()));
        if(!userDir.exists()){
            userDir.mkdirs();
        }
        // 将图表信息存入数据库
        Chart chart = new Chart();
        String chartId = UUID.randomUUID().toString();
        chart.setChartId(chartId);
        String filePath = userDir.getPath() + PathUtil.SP + chartId + ".json";
        chart.setPath(filePath);
        JSONObject jsonObj = JSONObject.parseObject(chartObject);
        String type = jsonObj.getString("type");
        chart.setType(type);
        String chartName = jsonObj.getString("chartName");
        chart.setChartName(chartName);
        chart.setUserId(user.getUserId());
        chartMapper.saveNew(chart);
        // 将图表的json字符串存入用户目录下
        File chartFile = new File(filePath);
        try {
            chartFile.createNewFile();
            FileUtils.writeStringToFile(chartFile, chartObject, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chart;
    }

    @Override
    public void saveChart(String chartPath, String chartObject, User user) {
        // 用户可能修改了chartName，需要修改数据库表中的该字段
        String chartName = JSONObject.parseObject(chartObject).getString("chartName");
        chartMapper.updateChartNameByChartPath(chartPath, chartName);
        // 覆盖原来的json文件
        File chartFile = new File(chartPath);
        if(!chartFile.exists()){
            return;
        }
        try {
            FileUtils.writeStringToFile(chartFile, chartObject, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Chart> getAllChartsByUser(User user) {
        File userDir = new File(PathUtil.getUserDir(user.getUserId()));
        if(!userDir.exists()){
            userDir.mkdirs();
            return new ArrayList<>();
        }
        List<Chart> chartList = chartMapper.getAllChartsByUser(user.getUserId());
        if(chartList.size() == 0){
            return new ArrayList<>();
        }
        for(Chart chart : chartList){
            File chartFile = new File(chart.getPath());
            try {
                chart.setJsonStr(FileUtils.readFileToString(chartFile, "utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return chartList;
    }

    @Override
    public Chart getChartById(String chartId) {
        Chart chart = chartMapper.getChartById(chartId);
        File chartFile = new File(chart.getPath());
        try {
            chart.setJsonStr(FileUtils.readFileToString(chartFile, "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chart;
    }

    @Override
    public void delChart(String userId, String chartId) {
        Chart chart = chartMapper.getChartById(chartId);
        File chartFile = new File(chart.getPath());
        if(chartFile.exists()){
            chartFile.delete();
        }
        chartMapper.delChart(userId, chartId);
    }

}
