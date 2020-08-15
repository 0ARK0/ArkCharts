package com.ark.arkcharts.serviceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ark.arkcharts.entity.chartdata.*;
import com.ark.arkcharts.service.FileService;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
     * @param file excel文件
     * @param type 转换的excel格式类型，取值为0和1。0代表系列在第一列，X轴数据项在第一行，中间的单元格填写数据；
     *             1带表X轴数据项在第一列，系列在第一行，中间的单元格填写数据。
     * @return BarData
     */
    @Override
    public BarData excelToBar(MultipartFile file, String type, String toChartType) {
        BarData barData = new BarData();
        setChartName(barData, file.getOriginalFilename());
        Workbook workbook = getWorkbook(file); // 获取文档
        Sheet sheet = workbook.getSheetAt(0); // 获取文档中第一个sheet页
        int rowNum = sheet.getPhysicalNumberOfRows(); // 获取总行数
        int colNum = getNumberOfCols(sheet); // 获取总列数
        // 准备集合
        List<String> legendArray = new ArrayList<>();
        List<String> xData = new ArrayList<>();
        String[][] yData = new String[0][];
        List<ChartSeries> seriesArray = new ArrayList<>();
        // 按不同类型的表格内容读取表格数据存入对象
        if ("0".equals(type)){
            yData = new String[rowNum - 1][colNum - 1];
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
                    series.setType(toChartType);
                    series.setData(yData[i - 1]);
                    seriesArray.add(series);
                }
            }
        } else if ("1".equals(type)) {
            yData = new String[colNum - 1][rowNum - 1];
            for (int i = 0; i < colNum; i++) {
                for (int j = 0; j < rowNum; j++) {
                    if(i == 0 && j == 0){
                        continue;
                    }
                    String value = getCellValueByCell(sheet.getRow(j).getCell(i));
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
     * 根据柱状图的json文件生成excel表格，以文件流的形式返回，前端下载
     * @param chartPath
     * @param response
     */
    @Override
    public void barToExcel(String chartPath, HttpServletResponse response) {
        // 根据图表json文件的路径读取文件内容
        JSONObject chartObj = getChartJsonObj(chartPath);
        // 获取图表中的数据
        JSONArray legendArray = chartObj.getJSONArray("legendArray");
        JSONArray xData = chartObj.getJSONArray("xData");
        JSONArray yData = chartObj.getJSONArray("yData");
        String fileName = chartObj.getString("chartName");
        // 创建excel表格文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 添加一个sheet页
        XSSFSheet sheet = workbook.createSheet("sheet1");
        // 创建第一行，柱状图的第一行是xData
        XSSFRow row1 = sheet.createRow(0);
        // 第一行第一个单元格为空值
        row1.createCell(0).setCellValue("");
        for (int i = 0; i < xData.size(); i++) {
            row1.createCell(i + 1).setCellValue(xData.getString(i));
        }
        for (int i = 0; i < legendArray.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            // 每一行的第一个单元格都是系列名称
            row.createCell(0).setCellValue(legendArray.getString(i));
            JSONArray valueData = yData.getJSONArray(i);
            for (int j = 0; j < xData.size(); j++) {
                row.createCell(j + 1).setCellValue(valueData.getDoubleValue(j));
            }
        }
        // 输出
        outputExcel(fileName, workbook, response);
    }

    /**
     * 根据思维导图的json文件创建一个excel文件并返回
     * @param chartPath
     * @param response
     */
    @Override
    public void mindMapToExcel(String chartPath, HttpServletResponse response) {
        JSONObject chartObj = getChartJsonObj(chartPath);
        JSONObject rootNode = chartObj.getJSONObject("rootNode");
        String fileName = chartObj.getString("chartName");
        // 创建excel表格文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 添加一个sheet页
        XSSFSheet sheet = workbook.createSheet("sheet1");
        // 获取根节点的子节点数组
        JSONArray rootChildren = rootNode.getJSONArray("children");
        int rowIndex = 0;
        for (int i = 0; i < rootChildren.size(); i++) {
            rowIndex = mindMapNodeIntoExcel(rootChildren.getJSONObject(i), sheet, rowIndex, 0);
        }
        // 输出
        outputExcel(fileName, workbook, response);
    }

    @Override
    public BarData excelToLine(MultipartFile file, String type) {
        BarData barData = excelToBar(file, type, "line");
        return barData;
    }

    /**
     * 根据图表json文件的路径读取文件内容
     * @param chartPath
     * @return
     */
    private JSONObject getChartJsonObj(String chartPath){
        String chartStr = "";
        try {
            chartStr = FileUtils.readFileToString(new File(chartPath), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(chartStr);
    }

    /**
     * 将表格文件输出到response输出流
     * @param fileName excel文件的文件名
     * @param workbook 表格对象
     * @param response response对象
     */
    private void outputExcel(String fileName, XSSFWorkbook workbook, HttpServletResponse response){
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                new String((fileName + ".xlsx").getBytes(), StandardCharsets.ISO_8859_1));
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            // 将表格文件输出
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            if(out != null){
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }

    /**
     * 递归完成json节点填入单元格的操作
     * @param node 思维导图对象节点
     * @param sheet 需要填写数据的表格sheet页
     * @param rowIndex 当前填写数据的单元格的行标
     * @param colIndex 当前填写数据的单元格的行列
     * @return int值，表示下个节点的填写数据的单元格的行标
     */
    private int mindMapNodeIntoExcel(JSONObject node, XSSFSheet sheet, int rowIndex, int colIndex){
        XSSFRow row = sheet.getRow(rowIndex);
        if(row == null){
            row = sheet.createRow(rowIndex);
        }
        row.createCell(colIndex).setCellValue(node.getString("name"));
        JSONArray children = node.getJSONArray("children");
        if (children == null || children.size() == 0){
            return rowIndex + 1;
        }
        for (int i = 0; i < children.size(); i++) {
            rowIndex = mindMapNodeIntoExcel(children.getJSONObject(i), sheet, rowIndex,
                    colIndex + 1);
        }
        return rowIndex;
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
