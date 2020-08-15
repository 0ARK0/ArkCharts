package com.ark.arkcharts.controller;

import com.ark.arkcharts.entity.Chart;
import com.ark.arkcharts.entity.User;
import com.ark.arkcharts.myutils.PathUtil;
import com.ark.arkcharts.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Ark
 * @date 2020/05/14 19:44
 */
@Controller
public class WelcomeController {

    @Autowired
    ChartService chartService;

    @RequestMapping("/")
    public String welcomeToStart(){
        return "forward:/toStart";
    }

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/toRegister")
    public String toRegister(){
        return "register";
    }

    @RequestMapping("/toStart")
    public String toMyCharts(HttpServletRequest request, Model model) {
        // 查找该用户的所有图表，并返回
        User user = (User)request.getSession().getAttribute("user");
        List<Chart> chartList = chartService.getAllChartsByUser(user);
        model.addAttribute("chartList", chartList);
        return "start";
    }

    @RequestMapping("/toBarPage")
    public String toBarPage(HttpServletRequest request, Model model){
        String chartId = request.getParameter("chartId");
        if(chartId == null){
            return "barPage";
        }
        // 打开一个柱状图
        Chart chart = chartService.getChartById(chartId);
        model.addAttribute("jsonData", chart.getJsonStr());
        model.addAttribute("chartPath", chart.getPath());
        return "barPage";
    }

    @RequestMapping("/toMindMapPage")
    public String toMindMapPage(HttpServletRequest request, Model model){
        String chartId = request.getParameter("chartId");
        if(chartId == null){
            return "mindMapPage";
        }
        // 打开一个思维导图
        Chart chart = chartService.getChartById(chartId);
        model.addAttribute("jsonData", chart.getJsonStr());
        model.addAttribute("chartPath", chart.getPath());
        return "mindMapPage";
    }

    @RequestMapping("/toGraphPage")
    public String toGraphPage(HttpServletRequest request, Model model){
        String chartId = request.getParameter("chartId");
        if(chartId == null){
            return "graphPage";
        }
        // 打开一个关系图
        Chart chart = chartService.getChartById(chartId);
        model.addAttribute("jsonData", chart.getJsonStr());
        model.addAttribute("chartPath", chart.getPath());
        return "graphPage";
    }

    @RequestMapping("/toPiePage")
    public String toPiePage(HttpServletRequest request, Model model){
        String chartId = request.getParameter("chartId");
        if(chartId == null){
            return "piePage";
        }
        // 打开一个饼图
        Chart chart = chartService.getChartById(chartId);
        model.addAttribute("jsonData", chart.getJsonStr());
        model.addAttribute("chartPath", chart.getPath());
        return "piePage";
    }

    @RequestMapping("/toLinePage")
    public String toLinePage(HttpServletRequest request, Model model){
        String chartId = request.getParameter("chartId");
        if (chartId == null){
            return "linePage";
        }
        // 打开一个折线图
        Chart chart = chartService.getChartById(chartId);
        model.addAttribute("jsonData", chart.getJsonStr());
        model.addAttribute("chartPath", chart.getPath());
        return "linePage";
    }
}
