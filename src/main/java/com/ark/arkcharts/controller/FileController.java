package com.ark.arkcharts.controller;

import com.ark.arkcharts.entity.chartdata.BarData;
import com.ark.arkcharts.entity.chartdata.LineData;
import com.ark.arkcharts.entity.chartdata.Node;
import com.ark.arkcharts.entity.chartdata.PieData;
import com.ark.arkcharts.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Ark
 * @date 2020/05/15 19:57
 */
@Controller
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileService fileService;

    @RequestMapping("/excelToBar")
    @ResponseBody
    public BarData excelToBar(MultipartFile file, HttpServletRequest request) {
        String type = request.getParameter("typeVal");
        return fileService.excelToBar(file, type, "bar");
    }

    @RequestMapping("/excelToLine")
    @ResponseBody
    public BarData excelToLine(MultipartFile file, HttpServletRequest request) {
        String type = request.getParameter("typeVal");
        return fileService.excelToLine(file, type);
    }

    @RequestMapping("/excelToMindMap")
    @ResponseBody
    public Node excelToMindMap(MultipartFile file) {
        return fileService.excelToMindMap(file);
    }

    @RequestMapping("/excelToPie")
    @ResponseBody
    public PieData excelToPie(MultipartFile file){
        return fileService.excelToPie(file);
    }

    @RequestMapping("/barToExcel")
    public void barToExcel(HttpServletRequest request, HttpServletResponse response) {
        String chartPath = request.getParameter("chartPath");
        fileService.barToExcel(chartPath, response);
    }

    @RequestMapping("/mindMapToExcel")
    public void mindMapToExcel(HttpServletRequest request, HttpServletResponse response){
        String chartPath = request.getParameter("chartPath");
        fileService.mindMapToExcel(chartPath, response);
    }
}
