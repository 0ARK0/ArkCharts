package com.ark.arkcharts.service;

import com.ark.arkcharts.entity.chartdata.BarData;
import com.ark.arkcharts.entity.chartdata.Node;
import com.ark.arkcharts.entity.chartdata.PieData;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Ark
 * @date 2020/05/17 7:53
 */
public interface FileService {

    Node excelToMindMap(MultipartFile file);

    BarData excelToBar(MultipartFile file);

    PieData excelToPie(MultipartFile file);
}
