package com.ark.arkcharts.serviceImpl;

import com.ark.arkcharts.entity.chartdata.*;
import com.ark.arkcharts.service.FileService;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ark
 * @date 2020/05/17 7:53
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     * excel表格转思维导图
     * @param file
     * @return
     */
    @Override
    public Node excelToMindMap(MultipartFile file) {
        Workbook workbook = getWorkbook(file); // 获取文档
        Sheet sheet = workbook.getSheetAt(0); // 获取文档中第一个sheet页
        String[] longName = file.getOriginalFilename().split("\\/");
        String tempName = longName[longName.length - 1];
        Node root = new Node();
        root.setName(tempName.split("\\.")[0]);
        int rowNum = sheet.getPhysicalNumberOfRows(); // 获取总行数
        generateObject(sheet, 0, rowNum, 0, getNumberOfCols(sheet), root);
        return root;
    }

    /**
     * excel表格转柱状图
     * @param file
     * @return
     */
    @Override
    public BarData excelToBar(MultipartFile file) {
        BarData barData = new BarData();
        setChartName(barData, file.getOriginalFilename());
        Workbook workbook = getWorkbook(file); // 获取文档
        Sheet sheet = workbook.getSheetAt(0); // 获取文档中第一个sheet页
        int rowNum = sheet.getPhysicalNumberOfRows(); // 获取总行数
        int colNum = getNumberOfCols(sheet); // 获取总列数
        // 准备集合
        List<String> legendArray = new ArrayList<>();
        List<String> xData = new ArrayList<>();
        String[][] yData = new String[rowNum - 1][colNum - 1];
        List<ChartSeries> seriesArray = new ArrayList<>();
        // 读取表格数据存入对象
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                if(i == 0 && j == 0){
                    continue;
                }
                String value = getCellValueByCell(sheet.getRow(i).getCell(j));
                if(i == 0){
                    xData.add(value);
                    continue;
                }
                if(j == 0){
                    legendArray.add(value);
                    continue;
                }
                yData[i - 1][j - 1] = value;
            }
            if(i > 0){
                BarSeries series = new BarSeries();
                series.setName(legendArray.get(i - 1));
                series.setType("bar");
                series.setData(yData[i - 1]);
                seriesArray.add(series);
            }
        }
        barData.setLegendArray(legendArray);
        barData.setxData(xData);
        barData.setyData(yData);
        barData.setSeriesArray(seriesArray);
        return barData;
    }

    /**
     * excel表格转饼图
     * @param file
     * @return
     */
    @Override
    public PieData excelToPie(MultipartFile file) {
        PieData pieData = new PieData();
        setChartName(pieData, file.getOriginalFilename());
        Workbook workbook = getWorkbook(file); // 获取文档
        Sheet sheet = workbook.getSheetAt(0); // 获取文档中第一个sheet页
        int rowNum = sheet.getPhysicalNumberOfRows(); // 获取总行数
        int colNum = getNumberOfCols(sheet); // 获取总列数
        // 准备集合
        List<String> legendArray = new ArrayList<>();
        List<String> dataArray = new ArrayList<>();
        List<ChartSeries> seriesArray = new ArrayList<>();
        // 表格只有两行，并且第二行全部都是数值类型的情况
        if (rowNum == 2 && isSheetRowNumber(sheet, 1, colNum)){
            Row row0 = sheet.getRow(0);
            Row row1 = sheet.getRow(1);
            for (int i = 0; i < colNum; i++) {
                PieSeries pieSeries = new PieSeries();
                String name = getCellValueByCell(row0.getCell(i));
                String value = getCellValueByCell(row1.getCell(i));
                legendArray.add(name);
                dataArray.add(value);
                pieSeries.setName(name);
                pieSeries.setValue(value);
                seriesArray.add(pieSeries);
            }
        }else if(colNum == 2 && isSheetColNumber(sheet, 1, rowNum)){
            // 表格只有两列，并且第二列全部都是数值类型的情况
            for (int i = 0; i < rowNum; i++) {
                PieSeries pieSeries = new PieSeries();
                Row row = sheet.getRow(i);
                String name = getCellValueByCell(row.getCell(0));
                String value = getCellValueByCell(row.getCell(1));
                legendArray.add(name);
                dataArray.add(value);
                pieSeries.setName(name);
                pieSeries.setValue(value);
                seriesArray.add(pieSeries);
            }
        }else{
            return null;
        }
        pieData.setLegendArray(legendArray);
        pieData.setDataArray(dataArray);
        pieData.setSeriesArray(seriesArray);
        return pieData;
    }

    /**
     * 根据excel文件的后缀名来获取对应的文档
     * @param file
     * @return
     */
    private Workbook getWorkbook(MultipartFile file) {
        Workbook workbook = null;
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
        InputStream in;
        try {
            in = file.getInputStream();
            if ("xls".equals(suffix)) {
                workbook = new HSSFWorkbook(in);
            } else if ("xlsx".equals(suffix)) {
                workbook = new XSSFWorkbook(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    /**
     * (思维导图)
     * 递归将excel表中的数据封装成java对象
     * @param sheet
     * @param start
     * @param end
     * @param index
     * @param colNum
     * @param node
     */
    private void generateObject(Sheet sheet, int start, int end, int index, int colNum, Node node) {
        if (index >= colNum) {
            return;
        }
        List<Node> children = new ArrayList<>();
        int newStart = 0;
        int newEnd = 0;
        for (int i = start; i < end; i++) {
            Cell cell = sheet.getRow(i).getCell(index);
            String name = getCellValueByCell(cell);
            if (!"".equals(name)) {
                Node node1 = new Node();
                node1.setName(name);
                newStart = i;
                int k = i + 1;
                while (k < end && "".equals(getCellValueByCell(sheet.getRow(k).getCell(index)))) {
                    k++;
                }
                newEnd = k;
                i = k - 1;
                generateObject(sheet, newStart, newEnd, index + 1, colNum, node1);
                children.add(node1);
            }
        }
        node.setChildren(children);
    }

    /**
     * 判断excel表格中单元格的数据类型，并以字符串形式返回
     * @param cell
     * @return
     */
    private String getCellValueByCell(Cell cell) {
        // 判断是否为null或空串
        if (cell == null || cell.toString().trim().equals("")) {
            return "";
        }
        String cellValue;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {  // 判断日期类型
                    cellValue = new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                } else {  // 否
                    cellValue = new DecimalFormat("#.######").format(cell.getNumericCellValue());
                }
                break;
            case STRING:
                cellValue = cell.getStringCellValue().trim();
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
                cellValue = "";
                break;
        }
        return cellValue;
    }

    /**
     * 获取excel表格的总列数（有内容的部分）
     * @param sheet
     * @return
     */
    private int getNumberOfCols(Sheet sheet) {
        int rowNum = sheet.getPhysicalNumberOfRows();
        int maxColNum = sheet.getRow(0).getLastCellNum();
        for (int i = 1; i < rowNum; i++) {
            int colNum = sheet.getRow(i).getLastCellNum();
            if (colNum > maxColNum) {
                maxColNum = colNum;
            }
        }
        return maxColNum;
    }

    /**
     * 设置图表名称
     * @param chartData
     * @param fileName
     */
    private void setChartName(ChartData chartData, String fileName){
        assert fileName != null;
        chartData.setChartName(fileName.substring(0, fileName.lastIndexOf(".")));
    }

    /**
     * 判断表格中的某一行是否全部是数值类型
     * @param sheet
     * @return boolean
     */
    private boolean isSheetRowNumber(Sheet sheet, int rowIndex, int colNum){
        Row row = sheet.getRow(rowIndex);
        for (int i = 0; i < colNum; i++) {
            String value = getCellValueByCell(row.getCell(i));
            if (!isStringANumber(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断表格中的某一列是否全部是数值类型
     * @param sheet
     * @param colIndex
     * @param rowNum
     * @return
     */
    private boolean isSheetColNumber(Sheet sheet, int colIndex, int rowNum){
        for (int i = 0; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            String value = getCellValueByCell(row.getCell(colIndex));
            if (!isStringANumber(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否是纯数字
     * @return
     */
    private boolean isStringANumber(String str){
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }else{
            return true;
        }
    }
}
