let URL = window.location.protocol + "//" + window.location.host + "/" + "ArkCharts";

// 图表对象
const myChart = echarts.init(document.getElementById("main"));
let jsonInput = $("#jsonDataInput").val();
let jsonData;
// 数据
let legendArray = []; // 系列名称数组
let dataArray = []; // 每个系列对应的数值数组
let seriesArray = []; // 一个对象数组，如：[{name: name1, value: 10}...]
// 其他属性
let chartName = "饼图";
let roseType = null; // roseType: 'radius',

initData();
showChart();

// 从json对象中获取数据并填充到图表配置项，如果没有json对象，就为默认值
function initData(){
    if(jsonInput !== "" && jsonInput !== null && jsonInput !== undefined){
        jsonData = JSON.parse(jsonInput);
        legendArray = jsonData.legendArray;
        seriesArray = jsonData.seriesArray;
        dataArray = jsonData.dataArray;
        chartName = jsonData.chartName;
        roseType = jsonData.roseType;
    }
}

// 打开设置系列的对话框
function openSetLegendDiv() {
    if(legendArray.length !== 0){
        // 显示已有的系列数据
        for(let i in legendArray){
            let legendItem = '<div class="settingsBody-item">' +
                '系列名：<input class="small-input" name="name" value="'+legendArray[i]+'"> &nbsp;' +
                '数值：<input class="small-input" name="value" value="'+dataArray[i]+'">' +
                '<img src="/ArkCharts/static/img/smallAdd.png" title="添加" width="30px" height="30px" onclick="addLegend()">&nbsp;';
            if(parseInt(i) === 0){
                legendItem += '</div>';
            }else{
                legendItem += '<img src="/ArkCharts/static/img/smallDel.png" title="删除" width="30px" height="30px" onclick="delLegend(this)">' +
                    '</div>';
            }
            $("#legendBody").append(legendItem);
        }
    }else{
        $("#legendBody").append('<div class="settingsBody-item">系列名：<input name="name" class="small-input">&nbsp;' +
            '数值：<input class="small-input" name="value">' +
            '<img src="/ArkCharts/static/img/smallAdd.png" title="添加" width="30px" height="30px" onclick="addLegend()"></div>');
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
        '系列名：<input class="small-input" name="name"> &nbsp;' +
        '数值：<input class="small-input" name="value">' +
        '<img src="/ArkCharts/static/img/smallAdd.png" title="添加" width="30px" height="30px" onclick="addLegend()">&nbsp;' +
        '<img src="/ArkCharts/static/img/smallDel.png" title="删除" width="30px" height="30px" onclick="delLegend(this)">' +
        '</div>';
    $("#legendBody").append(legendItem);
}

function delLegend(img) {
    $(img).parent().remove();
}

// 设置系列和数值
function setLegend(){
    legendArray = [];
    dataArray = [];
    seriesArray = [];
    $("#legendBody").find("input[name='name']").each(function () {
        let name = $(this).val();
        if(name !== ""){
            legendArray.push(name);
        }
    });
    $("#legendBody").find("input[name='value']").each(function () {
        let value = $(this).val();
        if(value !== ""){
            dataArray.push(value);
        }
    });
    for (let i = 0; i < legendArray.length; i++){
        let series = {
            name: legendArray[i],
            value: dataArray[i],
        };
        seriesArray.push(series);
    }
    showChart();
    closeSetLegendDiv();
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

// 打开设置平面或3D显示的对话框
function openSet3D() {
    $(".input-menu").append('<p>选择显示类型</p>');
    $(".input-menu").append('<p><input type="radio" name="showRadio" value="">半径相同</p>');
    $(".input-menu").append('<p><input type="radio" name="showRadio" value="radius">半径不同</p>');
    $(".input-menu").append('<button onclick="set3D()">确定</button><button style="margin-left: 50px" onclick="cancelInput()">取消</button>')
    $('#moreSettings').hide();
    isOpen = false;
    $(".fullHide").show();
    $(".input-menu").show();
}

function set3D() {
    let type = $(".input-menu").find("input:radio[name='showRadio']:checked").val();
    roseType = type;
    showChart();
    cancelInput();
}

// 取消设置
function cancelInput() {
    $(".input-menu").empty();
    $(".input-menu").hide();
    isOpen = false;
    $(".fullHide").hide();
}

// 保存图表
function saveChart() {
    let chartObject = {
        type: "pie",
        legendArray: legendArray,
        seriesArray: seriesArray,
        dataArray: dataArray,
        chartName: chartName,
        roseType: roseType
    };
    if(jsonInput === "" || jsonInput === null || jsonInput === undefined){
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
                jsonInput = JSON.stringify(chartObject);
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

// 打开文件格式要求对话框
function openMsgDiv() {
    $(".fullHide").show();
    $(".msgDiv").show();
}

function closeMsgDiv() {
    $(".msgDiv").hide();
    $(".fullHide").hide();
}

// 上传excel文件返回json对象
function uploadFile() {
    let f = $('#fileUploader').get(0).files[0];
    if("" === f || null === f || f === undefined){
        alert("请选择文件！");
        return;
    }
    let formData = new FormData();
    formData.append("file", f);
    $.ajax({
        url: URL + '/file/excelToPie',
        type: 'POST',
        data: formData,
        dataType: "json",
        contentType: false,
        processData: false,
        success: function (data) {
            legendArray = data.legendArray;
            seriesArray = data.seriesArray;
            dataArray = data.dataArray;
            chartName = data.chartName;
            showChart();
        },
        error: function () {
            alert("文件格式有误，请检查！");
        }
    });
}

// 返回我的图表页面
function toStart(){
    location.href = "/ArkCharts/toStart";
}

function showChart() {
    myChart.setOption(option = {
        title: {
            text: chartName,
            left: 'left'
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
            }
        },
        tooltip: {
            trigger: 'item',
            formatter: '{b} : {c} ({d}%)'
        },
        legend: {
            type: 'scroll',
            orient: 'vertical',
            right: 50,
            top: 20,
            bottom: 20,
            data: legendArray,
        },
        series: [
            {
                type: 'pie',
                radius: '70%',
                roseType: roseType,
                center: ['50%', '50%'],
                data: seriesArray,
                label:{ // 饼图图形上的文本标签
                    normal:{
                        show:true,
                        // position:'inner', //标签的位置
                        textStyle : {
                            fontWeight : 300 ,
                            fontSize : 16    //文字的字体大小
                        },
                        formatter:'{b} : {c} ({d}%)'
                    }
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    });
}