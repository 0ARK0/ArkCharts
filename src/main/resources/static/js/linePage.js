let URL = window.location.protocol + "//" + window.location.host + "/" + "ArkCharts";
// 基于准备好的dom，初始化echarts实例
const myChart = echarts.init(document.getElementById("main"));
let option;
let jsonData = $("#jsonDataInput").val();
// 数据
let legendArray = []; // 系列数组，如：["系列1", "系列2", "系列3"]
let xData = []; // x轴数据，x轴数据项数组如:[x1, x2, x3]
let yData = []; // y轴数据，二维数组，其中第一个一维数组就是第一个legend的数据，以此类推
let seriesArray = []; // 一个对象数组，如：[{name:"系列1", type:"bar", data:[1, 2, 3]}...]
// 其他属性
let chartName = "折线图";
let xAxisName = '';
let yAxisName = '';
let yAxisMax = null;
let yAxisMin = null;
let markPoint = {
    data: [
        {type: 'max', name: '最大值'},
        {type: 'min', name: '最小值'}
    ]
};
let markLine = {
    data: [
        {type: 'average', name: '平均值'}
    ]
};


initData();
showChart();

// 从json对象中获取数据并填充到图表配置项，如果没有json对象，就为默认值
function initData(){
    if(jsonData !== "" && jsonData !== null && jsonData !== undefined){
        jsonData = JSON.parse(jsonData);
        legendArray = jsonData.legendArray;
        xData = jsonData.xData;
        yData = jsonData.yData;
        seriesArray = jsonData.seriesArray;
        console.log(JSON.stringify(seriesArray));
        chartName = jsonData.chartName;
        xAxisName = jsonData.xAxisName;
        yAxisName = jsonData.yAxisName;
    }
}

// 打开设置系列的对话框
function openSetLegendDiv() {
    if(legendArray.length !== 0){
        // 显示已有的系列
        for(let i in legendArray){
            let legendItem = '<div class="settingsBody-item">' +
                '系列名称：<input class="small-input" name="name" value="'+legendArray[i]+'"> &nbsp;' +
                '<img src="/ArkCharts/static/img/smallAdd.png" title="添加系列" width="30px" height="30px" onclick="addLegend()">&nbsp;';
            if(parseInt(i) === 0){
                legendItem += '</div>';
            }else{
                legendItem += '<img src="/ArkCharts/static/img/smallDel.png" title="删除系列" width="30px" height="30px" onclick="delLegend(this)">' +
                    '</div>';
            }
            $("#legendBody").append(legendItem);
        }
    }else{
        $("#legendBody").append('<div class="settingsBody-item">系列名称：<input name="name" class="small-input">&nbsp;' +
            '<img src="/ArkCharts/static/img/smallAdd.png" title="添加系列" width="30px" height="30px" onclick="addLegend()"></div>');
    }
    $(".fullHide").show();
    $("#setLegendDiv").show();
}

function closeSetLegendDiv() {
    $("#legendBody").empty();
    $("#setLegendDiv").hide();
    $(".fullHide").hide();
}

function addLegend() {
    let legendItem = '<div class="settingsBody-item">' +
        '系列名称：<input class="small-input" name="name"> &nbsp;' +
        '<img src="/ArkCharts/static/img/smallAdd.png" title="添加系列" width="30px" height="30px" onclick="addLegend()">&nbsp;' +
        '<img src="/ArkCharts/static/img/smallDel.png" title="删除系列" width="30px" height="30px" onclick="delLegend(this)">' +
        '</div>';
    $("#legendBody").append(legendItem);
}

function delLegend(img) {
    $(img).parent().remove();
}

// 设置系列
function setLegend(){
    legendArray = [];
    seriesArray = [];
    $("#legendBody").find("input[name='name']").each(function () {
        let name = $(this).val();
        if(name !== ""){
            legendArray.push(name);
        }
    });
    for (let i = 0; i < legendArray.length; i++){
        let series = {
            name: legendArray[i],
            type: 'line',
            data: yData,
        };
        seriesArray.push(series);
    }
    showChart();
    closeSetLegendDiv();
}

function openSetXDataDiv() {
    if(xData.length !== 0){
        // 显示已有的x轴数据
        for(let i in xData){
            let xItem = '<div class="settingsBody-item">' +
                '数据项名称：<input class="small-input" name="name" value="'+xData[i]+'">' +
                '<img src="/ArkCharts/static/img/smallAdd.png" title="添加数据项" width="30px" height="30px" onclick="addXData()">&nbsp;';
            if(parseInt(i) === 0){
                xItem += '</div>';
            }else {
                xItem += '<img src="/ArkCharts/static/img/smallDel.png" title="删除数据项" width="30px" height="30px" onclick="delXData(this)">' +
                    '</div>';
            }
            $("#xDataBody").append(xItem);
        }
    }else{
        $("#xDataBody").append('<div class="settingsBody-item">数据项名称：<input name="name" class="small-input">&nbsp;' +
            '<img src="/ArkCharts/static/img/smallAdd.png" title="添加数据项" width="30px" height="30px" onclick="addXData()"></div>');
    }
    $(".fullHide").show();
    $("#setXDataDiv").show();
}

function addXData() {
    let xItem = '<div class="settingsBody-item">' +
        '数据项名称：<input class="small-input" name="name">' +
        '<img src="/ArkCharts/static/img/smallAdd.png" title="添加数据项" width="30px" height="30px" onclick="addXData()">&nbsp;' +
        '<img src="/ArkCharts/static/img/smallDel.png" title="删除数据项" width="30px" height="30px" onclick="delXData(this)">' +
        '</div>';
    $("#xDataBody").append(xItem);
}

function delXData(img) {
    $(img).parent().remove();
}

function closeSetXDataDiv(){
    $("#xDataBody").empty();
    $("#setXDataDiv").hide();
    $(".fullHide").hide();
}

function setXData(){
    xData = [];
    $("#xDataBody").find("input[name='name']").each(function () {
        let name = $(this).val();
        if(name !== ""){
            xData.push(name);
        }
    });
    showChart();
    closeSetXDataDiv()
}

// 打开设置Y轴数据的div
function openSetYDataDiv(){
    // 判断是否设置了系列和X轴数据，如果没有，则不能打开
    if(legendArray.length === 0 || xData.length === 0){
        alert("请先设置系列和X轴数据项！");
        return;
    }
    // 生成表格，将X轴数据和系列填入
    // X轴数据项作为表头，系列名称作为表格第一列
    let thead = "<thead><tr><td>#</td>";
    for(let i in xData){
        thead += "<td>"+xData[i]+"</td>";
    }
    thead += "</tr></thead>";
    $(".zebra").append(thead);
    for(let i in legendArray){
        let tr = "<tr class='tr-data'><td>"+legendArray[i]+"</td>";
        for(let j in xData){
            if(yData.length !== 0){
                let value = yData[i][j];
                if(value === undefined){
                    value = "";
                }
                tr += "<td><input name='value' class='td-input' value='"+value+"'></td>";
            }else{
                tr += "<td><input name='value' class='td-input'></td>";
            }
        }
        tr += "</tr>";
        $(".zebra").append(tr);
    }
    $(".fullHide").show();
    $("#setYDataDiv").show();
}

function closeSetYDataDiv(){
    $(".zebra").empty();
    $("#setYDataDiv").hide();
    $(".fullHide").hide();
}

// 设置Y轴数据
function setYData(){
    yData = [];
    $(".zebra").find(".tr-data").each(function (i) {
        let tr = $(this).children(); // 获取每一行
        let innerArray = [];
        for (let i = 1; i <= xData.length; i++){
            let value = tr.eq(i).find('input').val();
            innerArray.push(value);
        }
        yData.push(innerArray);
        seriesArray[i].data = yData[i];
    });
    closeSetYDataDiv();
    showChart();
}

// 保存图表
function saveChart(){
    let chartObject = {
        type: "line",
        legendArray: legendArray,
        xData: xData,
        yData: yData,
        seriesArray: seriesArray,
        chartName: chartName,
        xAxisName: xAxisName,
        yAxisName: yAxisName
    };
    if(jsonData === "" || jsonData === null || jsonData === undefined){
        // 保存新图表
        $.ajax({
            url: URL + "/chart/saveNew",
            type: "post",
            data: JSON.stringify(chartObject),
            contentType: "application/json; charset=utf-8",
            traditional: true,
            async: false,
            success: function (data) {
                alert("保存成功!");
                jsonData = JSON.stringify(chartObject);
                $("#chartPathInput").val(data.path);
                $("#jsonDataInput").val(JSON.stringify(chartObject));
            },
            error: function () {
                alert("服务器错误");
            }
        });
    }else{
        // 保存已有的图表
        let chartPath = $("#chartPathInput").val();
        let tranData = {chartPath: chartPath, "chartObject":JSON.stringify(chartObject)};
        $.ajax({
            url: URL + "/chart/save",
            type: "post",
            data: tranData,
            async: false,
            success: function () {
                alert("保存成功!");
                $("#jsonFileNameInput").val(JSON.stringify(chartObject));
            },
            error: function () {
                alert("服务器错误");
            }
        });
    }
}

// 返回我的图表页面
function toStart(){
    location.href = "/ArkCharts/toStart";
}

// 防止默认菜单弹出
$('#main').bind("contextmenu", function () { return false; });

let barParams = null; // 用于保存被右击的柱子的全局变量

// 右击事件
// param参数包含的内容有：
//        param.seriesIndex：系列序号（series中当前图形是第几个图形第几个，从0开始计数）
//        param.dataIndex：数值序列（X轴上当前点是第几个点，从0开始计数）
//        param.seriesName：legend名称
//        param.name：X轴值
//        param.data：Y轴值
//        param.value：Y轴值
//        param.type：点击事件均为click
myChart.getZr().on('contextmenu',params=>{
    const pointInPixel= [params.offsetX, params.offsetY];
    if (myChart.containPixel('grid',pointInPixel)) {
        // 柱子的下标位置
        let xIndex = myChart.convertFromPixel({seriesIndex:0},[params.offsetX, params.offsetY])[0];

    }
});

// 点击事件
myChart.getZr().on('click',params=>{
    const pointInPixel= [params.offsetX, params.offsetY];
    if (myChart.containPixel('grid',pointInPixel)) {
        let xIndex = myChart.convertFromPixel({seriesIndex:0},[params.offsetX, params.offsetY])[0];

    }
});

// 打开文件格式要求对话框
function openMsgDiv() {
    $(".fullHide").show();
    $(".msgDiv").show();
}

// 选择需要上传的Excel文件的内容格式
function chooseExcelFileType() {
    let f = $('#fileUploader').get(0).files[0];
    if("" === f || null === f || f === undefined){
        alert("请选择文件！");
        return;
    }
    let string = '<p>选择上传的Excel文件的内容格式</p>';
    string += '<p><input type="radio" name="excelTypeRadio" value="0"><a href="/ArkCharts/static/img/barExcelData.png" target="_blank"><img src="/ArkCharts/static/img/barExcelData.png" width="150px" height="80px" title="点击查看大图"></a></p>';
    string += '<p><input type="radio" name="excelTypeRadio" value="1"><a href="/ArkCharts/static/img/barExcelData2.png" target="_blank"><img src="/ArkCharts/static/img/barExcelData2.png" width="150px" height="80px" title="点击查看大图"></a></p>';
    string += '<button onclick="uploadFile()">确定</button>';
    $(".input-menu").append(string);
    $('#moreSettings').hide();
    isOpen = false;
    $(".fullHide").show();
    $(".input-menu").show();
}

// 上传Excel文件，传回json对象
function uploadFile() {
    let f = $('#fileUploader').get(0).files[0];
    let typeVal = $(".input-menu").find("input:radio[name='excelTypeRadio']:checked").val();
    cancelInput();
    let formData = new FormData();
    formData.append("file", f);
    formData.append("typeVal", typeVal);
    $.ajax({
        url: URL + '/file/excelToLine',
        type: 'POST',
        data: formData,
        dataType: "json",
        contentType: false,
        processData: false,
        success: function (data) {
            legendArray = data.legendArray;
            xData = data.xData;
            yData = data.yData;
            seriesArray = data.seriesArray;
            chartName = data.chartName;
            showChart();
        },
        error: function () {
            alert("服务器出错！");
        }
    });
}

// 关闭文件格式要求对话框
function closeMsgDiv() {
    $(".msgDiv").hide();
    $(".fullHide").hide();
}

let isOpen = false;
// 打开更多设置对话框
function openMoreSettings() {
    if(!isOpen){
        $('#moreSettings').css({
            'display': 'block',
            'left': $('#openMS').offset().left - 20,
            'top' : $('#openMS').offset().top + 40
        });
        isOpen = true;
    }else{
        $('#moreSettings').hide();
        isOpen = false;
    }
}

$('#main').click(function () { // 点击任意处关闭弹框
    $('#moreSettings').hide();
    isOpen = false;
});

// 打开设置图表名称对话框
function openSetChartName() {
    $(".input-menu").append('<p>图表名称:<input type="text" value="'+chartName+'"></p>' +
        '<button onclick="setChartName()">确定</button>' +
        '<button style="margin-left: 50px" onclick="cancelInput()">取消</button>');
    $('#moreSettings').hide();
    $(".fullHide").show();
    $(".input-menu").show();
}

// 设置图表名称
function setChartName() {
    let newName = $(".input-menu").find("input").val();
    if(newName === ""){
        return;
    }
    chartName = newName;
    showChart();
    cancelInput();
}

// 打开设置坐标轴名称的对话框
function openSetAxisName() {
    $(".input-menu").append('<p>X轴名称:<input name="xAxisName" type="text" value="'+xAxisName+'"></p>' +
        '<p>Y轴名称:<input name="yAxisName" type="text" value="'+yAxisName+'"></p>' +
        '<button onclick="setAxisName()">确定</button>' +
        '<button style="margin-left: 50px" onclick="cancelInput()">取消</button>');
    $('#moreSettings').hide();
    isOpen = false;
    $(".fullHide").show();
    $(".input-menu").show();
}

// 设置坐标轴名称
function setAxisName() {
    let newXName = $(".input-menu").find("input[name='xAxisName']").val();
    let newYName = $(".input-menu").find("input[name='yAxisName']").val();
    xAxisName = newXName;
    yAxisName = newYName;
    showChart();
    cancelInput();
}

// 打开设置Y轴刻度最大值最小值
function openSetMaxMin() {
    $(".input-menu").append('<p>Y轴刻度最大值:<input name="yAxisMax" type="text"></p>' +
        '<p>Y轴刻度最小值:<input name="yAxisMin" type="text"></p>' +
        '<button onclick="setMaxMin()">确定</button>' +
        '<button style="margin-left: 50px" onclick="cancelInput()">取消</button>');
    $('#moreSettings').hide();
    isOpen = false;
    $(".fullHide").show();
    $(".input-menu").show();
}

// 设置Y轴刻度最大值最小值
function setMaxMin() {
    let newMax = $(".input-menu").find("input[name='yAxisMax']").val();
    let newMin = $(".input-menu").find("input[name='yAxisMin']").val();
    yAxisMax = newMax;
    yAxisMin = newMin;
    showChart();
    cancelInput();
}

// 打开显示平均值对话框
function openSetAverage() {
    let string = '<p>选择需要显示平均值的系列</p>';
    for(let i in legendArray){
        string += '<p><input type="checkbox" name="legendCheckBox" value="'+i+'"> '+legendArray[i]+'</p>';
    }
    string += '<button onclick="setAverage()">确定</button><button style="margin-left: 50px" onclick="cancelInput()">取消</button>';
    $(".input-menu").append(string);
    $('#moreSettings').hide();
    isOpen = false;
    $(".fullHide").show();
    $(".input-menu").show();
}

// 显示或隐藏平均值
function setAverage() {
    $(".input-menu").find("input:checkbox[name='legendCheckBox']:checked").each(function () {
        let value = $(this).val();
        seriesArray[parseInt(value)].markLine = markLine;
    });
    showChart();
    cancelInput();
}

// 下载为Excel文件
function downloadAsExcel() {
    let chartPath = $("#chartPathInput").val();
    if (chartPath === "" || chartPath === null || chartPath === undefined){
        alert("请先保存图表后再下载文件");
        cancelInput();
        return;
    }
    chartPath = chartPath.replace(/\\/g,"/");
    window.open(URL + "/file/barToExcel?chartPath=" + chartPath);
    cancelInput();
}

// 取消设置
function cancelInput() {
    $(".input-menu").empty();
    $(".input-menu").hide();
    isOpen = false;
    $(".fullHide").hide();
}

function showChart() {
    $("#main").css('width', $("#main").width());
    $("#main").css('height', (parseInt(winHeight) - 70) + "px");
    myChart.resize();
    myChart.setOption(option = {
        // 标题
        title: {
            text: chartName,
            left: 'left',
        },
        // 工具箱
        toolbox: {
            show: true,
            feature: {
                saveAsImage: {
                    show: true
                },
                restore: {
                    show: true
                },
                magicType: {
                    type: ['line', 'bar']
                }

            }
        },
        // color:['#4472C5','#ED7C30','#80FF80','#FF8096','#800080'], // 修改图例颜色
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        // 图例
        legend: {
            data: legendArray
        },
        // x轴
        xAxis: {
            data: xData,
            name: xAxisName,
            axisLabel: {
                show: true,
                textStyle: {
                    fontSize : 16      //更改坐标轴文字大小
                }
            },
        },
        yAxis: {
            type: 'value',
            name: yAxisName,
            max: yAxisMax,
            min: yAxisMin,
            axisLabel: {
                show: true,
                textStyle: {
                    fontSize : 14      //更改坐标轴文字大小
                }
            },
        },
        // 数据
        series: seriesArray,
        dataZoom: [
            {
                type: 'slider', //滑动条
                show: true,      //开启
                xAxisIndex: [0],
                left: '93%',  //滑动条位置
                start: 1,    //初始化时，滑动条宽度开始标度
                end: 100      //初始化时，滑动条宽度结束标度
            },
            {
                type: 'inside',  //内置滑动，随鼠标滚轮展示
                xAxisIndex: [0],
                start: 1,//初始化时，滑动条宽度开始标度
                end: 100  //初始化时，滑动条宽度结束标度
            }]
    });
}




