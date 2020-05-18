package com.ark.arkcharts.controller;

import com.ark.arkcharts.entity.Chart;
import com.ark.arkcharts.entity.User;
import com.ark.arkcharts.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ark
 * @date 2020/05/16 13:19
 */
@Controller
@RequestMapping("/chart")
public class ChartController {

    @Autowired
    ChartService chartService;

    @RequestMapping("/saveNew")
    @ResponseBody
    public Chart saveNewChart(@RequestBody String chartObject, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        Chart chart = chartService.saveNewChart(chartObject, user);
        return chart;
    }

    @RequestMapping("/save")
    @ResponseBody
    public void saveChart(String chartPath, String chartObject, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        chartService.saveChart(chartPath, chartObject, user);
    }

    @RequestMapping("/delChart")
    @ResponseBody
    public void delChart(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        String chartId = request.getParameter("chartId");
        chartService.delChart(user.getUserId(), chartId);
    }
}
