package com.ark.arkcharts.controller;

import com.ark.arkcharts.entity.chartdata.BarData;
import com.ark.arkcharts.entity.chartdata.Node;
import com.ark.arkcharts.entity.chartdata.PieData;
import com.ark.arkcharts.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
    public BarData excelToBar(MultipartFile file) {
        return fileService.excelToBar(file);
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
}
