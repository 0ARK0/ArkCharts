package com.ark.arkcharts.service;

import com.ark.arkcharts.entity.chartdata.BarData;
import com.ark.arkcharts.entity.chartdata.LineData;
import com.ark.arkcharts.entity.chartdata.Node;
import com.ark.arkcharts.entity.chartdata.PieData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Ark
 * @date 2020/05/17 7:53
 */
public interface FileService {

    Node excelToMindMap(MultipartFile file);

    BarData excelToBar(MultipartFile file, String type, String toChartType);

    PieData excelToPie(MultipartFile file);

    void barToExcel(String chartPath, HttpServletResponse outputStream);

    void mindMapToExcel(String chartPath, HttpServletResponse response);

    BarData excelToLine(MultipartFile file, String type);
}
