let URL = window.location.protocol + "//" + window.location.host + "/" + "ArkCharts";
// 数据
let chartIdArray = []; // 图表id的数组
let jsonDataArray = []; // 图表的json字符串数组
let chartNameArray = []; // 图表名称名数组
let pathArray = []; // 图表路径的数组
let myChartArray = []; // 在方块中显示的echarts
let typeArray = []; // 图表类型数组

// 初始化页面
// 获取图表id
$("#chartIdList input").each(function (i, n) {
    let childStr = $(n).val();
    chartIdArray[i] = childStr;
});
// 遍历隐藏的div，其子元素是input，每个input中都保存着后端发过来的json字符串
$("#jsonDataList input").each(function (i, n) {
    let childStr = $(n).val();
    let tJson = JSON.parse(childStr);
    jsonDataArray[i] = tJson;
});

// 遍历隐藏的div，子元素input中存放每个图表对应的图表名
$("#chartNameList input").each(function (i, n) {
    let childStr = $(n).val();
    chartNameArray[i] = childStr;
});

// 遍历隐藏的div，子元素input中存放每个图表对应的json文件的路径
$("#filePathList input").each(function (i, n) {
    let childStr = $(n).val();
    pathArray[i] = childStr;
});

// 遍历隐藏的div，子元素input中存放每个图表的类型
$("#typeList input").each(function (i, n) {
    let childStr = $(n).val();
    typeArray[i] = childStr;
});

// 循环创建方块div
for(let i in jsonDataArray){
    let ccDiv1 = "<div id='chartName"+i+"' class='chartName'></div>";
    let ccDiv2 = "<div id='chart"+i+"' class='chartDiv'></div>";
    let cccDiv1 = "<div id='openFlag"+i+"' class='chartOpen' onclick='openChart(this)'>打开<input type='hidden' value='"+i+"'></div>";
    let cccDiv2 = "<div id='deleteFlag"+i+"' class='chartDelete' onclick='deleteChart(this)'>删除<input type='hidden' value='"+i+"'></div>";
    let ccDiv3 = "<div class='chartDivBottom'>"+cccDiv1+cccDiv2+"</div>";
    let cDiv = "<div id='chartContentDiv"+i+"' class='chartContentDiv shadow'>" + ccDiv1 + ccDiv2 + ccDiv3 + "</div>";
    $(".main").append(cDiv);
}

// 将图表显示到对应的方块中
for(let i in jsonDataArray){
    let name = "chart" + i;
    let type = typeArray[i];
    myChartArray[i] = echarts.init(document.getElementById(name));
    // 根据图表类型显示图表
    showChart(i);
    // 显示图表的名称
    let chartName = "#chartName" + i;
    $(chartName).append(chartNameArray[i]);
}

// 打开选择图表类型的对话框
function openChooseTypeDiv() {
    $(".fullHide").show();
    $("#chooseTypeDiv").show();
}

// 关闭选择图表类型的对话框
function closeChooseTypeDiv() {
    $("#chooseTypeDiv").hide();
    $(".fullHide").hide();
}

// 打开创建柱状图的页面
function toBarPage() {
    location.href = "/ArkCharts/toBarPage";
}

// 打开创建思维导图的页面
function toMindMapPage() {
    location.href = "/ArkCharts/toMindMapPage";
}

// 打开创建关系图的页面
function toGraphPage() {
    location.href = "/ArkCharts/toGraphPage";
}

function toPiePage() {
    location.href = "/ArkCharts/toPiePage";
}

// 打开图表
function openChart(dd) {
    let index = parseInt($(dd).find("input").val());
    let type = typeArray[index];
    let chartId = chartIdArray[index];
    if(type === "bar"){
        location.href = "/ArkCharts/toBarPage?chartId=" + chartId;
    }else if(type === "mindMap"){
        location.href = "/ArkCharts/toMindMapPage?chartId=" + chartId;
    }else if(type === "graph"){
        location.href = "/ArkCharts/toGraphPage?chartId=" + chartId;
    }else if(type === "pie"){
        location.href = "/ArkCharts/toPiePage?chartId=" + chartId;
    }
}

// 删除图表
function deleteChart(dd) {
    let yn = confirm("图表删除后不可恢复，确认删除该图表吗？");
    if(yn === false){
        return;
    }
    let index = parseInt($(dd).find("input").val());
    let chartId = chartIdArray[index];
    let formData = new FormData();
    formData.append("chartId", chartId);
    $.ajax({
        url: URL + '/chart/delChart',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        async: false,
        success: function () {
            $(dd).parent().parent().remove();
        },
        error: function () {
            alert("删除失败");
        }
    });
}

// 根据图表类型显示图表
function showChart(i) {
    let jsonData = jsonDataArray[i];
    let type = typeArray[i];
    if (type === "bar") {
        showBarChart(i, jsonData);
    } else if (type === "mindMap"){
        showMindMapChart(i, jsonData);
    } else if (type === "graph"){
        showGraphChart(i, jsonData);
    } else if (type === "pie"){
        showPieChart(i, jsonData);
    }
}

// 柱状图
function showBarChart(index, jsonData) {
    let chartObject = jsonData;
    // 属性
    let xData = chartObject.xData; // x轴数据
    let seriesArray = chartObject.seriesArray;
    let xAxisName = '';
    let yAxisName = '';
    let yAxisMax = null;
    let yAxisMin = null;
    myChartArray[index].resize();
    myChartArray[index].setOption(option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        // x轴
        xAxis: {
            data: xData,
            name: xAxisName,
        },
        yAxis: {
            type: 'value',
            name: yAxisName,
            max: yAxisMax,
            min: yAxisMin,
        },
        // 数据
        series: seriesArray,
        // 铺满div
        grid: {
            top: 10,
            bottom: 10
        },
    });
}

// 饼图
function showPieChart(index, jsonData) {
    let seriesArray = jsonData.seriesArray;
    let roseType = jsonData.roseType;
    myChartArray[index].setOption(option = {
        tooltip: {
            trigger: 'item',
            formatter: '{b} : {c} ({d}%)'
        },
        series: [
            {
                type: 'pie',
                radius: '66%',
                roseType: roseType,
                center: ['50%', '50%'],
                data: seriesArray,
                label:{ // 饼图图形上的文本标签
                    normal:{
                        show:true,
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

// 思维导图
function showMindMapChart(index, jsonData) {
    let rootNode = jsonData.rootNode;
    myChartArray[index].setOption(option = {
        tooltip: {
            trigger: 'item',  //触发类型，默认：item（数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用）。可选：'axis'：坐标轴触发，主要在柱状图，折线图等会使用类目轴的图表中使用。'none':什么都不触发。
            triggerOn: 'mousemove' //提示框触发的条件，默认mousemove|click（鼠标点击和移动时触发）。可选mousemove：鼠标移动时，click：鼠标点击时，none：无
        },
        series: [ //系列列表
            {
                type: 'tree',        //图表种类为树图
                data: [rootNode],        //数据数组
                top: '1%',           //与顶部的距离
                left: '7%',          //与左边的距离
                bottom: '1%',        //与底部的距离
                right: '20%',        //与右边的距离
                symbol: 'emptyCircle', //标记的样式
                itemStyle: {         //标记的颜色设置
                    color: '#d63031',
                    borderColor: '#e17055'
                },
                symbolSize: 3,      //标记（小圆圈）的大小，默认是7
                initialTreeDepth: 1, //默认：2，树图初始展开的层级（深度）。根节点是第 0 层，然后是第 1 层、第 2 层，... ，直到叶子节点
                label: {             //每个节点对应的标签的样式
                    normal: {
                        position: 'left',           //标签的位置
                        verticalAlign: 'middle',    //文字垂直对齐方式，默认自动。可选：top，middle，bottom
                        align: 'right',             //文字水平对齐方式，默认自动。可选：top，center，bottom
                        fontSize: 5                //标签文字大小
                    }
                },
                leaves: {   //叶子节点的特殊配置
                    label: {
                        normal: {
                            position: 'right',
                            verticalAlign: 'middle',
                            align: 'left'
                        }
                    }
                },
                expandAndCollapse: true,     //子树折叠和展开的交互，默认打开
                animationDuration: 1000,      //初始动画的时长，支持回调函数,默认1000
                animationDurationUpdate: 750 //数据更新动画的时长，默认300
            }
        ]
    });
}

// 知识图谱
function showGraphChart(index, jsonData) {
    let categories = jsonData.categories;
    let nodeData = jsonData.nodeData;
    let linksData = jsonData.linksData;
    myChartArray[index].setOption(option = {
        // 提示框的配置
        tooltip: {
            formatter: function (x) {
                if(x.data.id === undefined){
                    return "";
                }
                return "id:"+x.data.id;
            }
        },
        series: [{
            type: 'graph', // 类型:关系图
            layout: 'force', // 图的布局，类型为力导图
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [2, 10],
            force: {
                repulsion: 100,
                edgeLength: [10, 50]
            },
            draggable: true,
            lineStyle: {
                normal: {
                    width: 2,
                    color: '#4b565b'
                }
            },
            edgeLabel: {
                normal: {
                    show: true,
                    formatter: function (x) {
                        return x.data.name;
                    }
                }
            },
            // 数据
            data: nodeData,
            links: linksData,
            categories: categories
        }]
    });
}

