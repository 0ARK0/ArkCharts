let URL = window.location.protocol + "//" + window.location.host + "/" + "ArkCharts";
// 后端的json数据
let jsonInputVal = $("#jsonDataInput").val();
// 关系图的json对象
let graphData = {};
// 图表初始化
let gChart = echarts.init(document.getElementById('main'));
// 系列数组
let categories = [];
// 节点数组
let nodeData = [];
// 连接线数组
let linksData = [];
// 相关参数
let chartName = "知识图谱";

let highLight = ""; //用于将匹配文本高亮显示的全局变量

if(jsonInputVal === "" || jsonInputVal === null || jsonInputVal === undefined){
    // 创建一个新的关系图
    nodeData = [{name: "Begin", id: "0", value: "0", category: null}]
}else{
    graphData = JSON.parse(jsonInputVal);
    chartName = graphData.chartName;
    categories = graphData.categories;
    nodeData = graphData.nodeData;
    linksData = graphData.linksData;
}

showGChart();

// 防止默认菜单弹出
$('#main').bind("contextmenu", function () { return false; });

let gNodeParam = null;

gChart.on('contextmenu', function (params) {
    if(params.value.indexOf("->") === -1){
        // 右击了节点
        $(".aboutGNode").show();
        $(".aboutLink").hide();
    }else{
        // 右击了连线
        $(".aboutGNode").hide();
        $(".aboutLink").show();
    }
    $('#graph-right-click-menu').css({ // 右击节点弹出选项框
        'left': params.event.offsetX + 10,
        'top' : params.event.offsetY + 60,
        "visibility": "visible"
    });
    gNodeParam = params;
    $('#graph-right-click-menu').show();
});

// 点击任意处关闭弹框
$('#main').click(function () {
    $('.click-menu').hide();
    $('.click-menu').find('input').val("");
    if("" !== highLight){ // 如果有高亮显示的节点则点击任意处消除
        highLight = "";
        showGChart();
    }
    isOpen = false;
});

function showGChart(){
    $("#main").css('width', $("#main").width());
    $("#main").css("height", (parseInt(winHeight) - 70) + "px");
    gChart.resize();
    option = {
        // 图的标题
        title: {
            text: chartName
        },
        // 工具箱
        toolbox:{
            show:true,
            top: "3%",
            feature:{
                // 刷新
                restore:{
                    show:true,
                    title:"刷新",
                },
                // 保存图片
                saveAsImage:{
                    show: true,                // 是否显示该工具。
                    type:"png",                // 保存的图片格式。支持 'png' 和 'jpeg'。
                    name:"arkchart-picture",               // 保存的文件名称，默认使用 title.text 作为名称
                    backgroundColor:"#ffffff", // 保存的图片背景色，默认使用 backgroundColor，如果backgroundColor不存在的话会取白色
                    title:"保存为图片",
                    pixelRatio:1
                }
            }
        },
        // 提示框的配置
        tooltip: {
            formatter: function (x) {
                if(x.data.id === undefined){
                    return "";
                }
                return "id:"+x.data.id;
            }
        },
        legend: [{
            data: categories.map(function (a) {
                return a.name;
            })
        }],
        series: [{
            type: 'graph', // 类型:关系图
            layout: 'force', // 图的布局，类型为力导图
            symbolSize: 40, // 调整节点的大小
            roam: true, // 是否开启鼠标缩放和平移漫游。默认不开启。如果只想要开启缩放或者平移,可以设置成 'scale' 或者 'move'。设置成 true 为都开启
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [2, 10],
            edgeLabel: {
                normal: {
                    textStyle: {
                        fontSize: 16
                    }
                }
            },
            force: {
                repulsion: 1000,
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
            label: {
                normal: {
                    show: true,
                    textStyle: {},
                    formatter: function (param) {
                        //  formatter通过设置为函数，对name进行判断，看是否有匹配的关键字，如果匹配上了，就返回一个匹配富文本的格式；下边的rich就是富文本样式设置。
                        if ("" !== highLight && param.name.match(highLight)) {
                            return '{a|' + param.name + '}';
                        } else {
                            return param.name;
                        }
                    },
                    rich: {
                        a: { // 查询时显示出的高亮节点
                            color: 'red',
                            lineHeight: 10,
                            fontFamily: 'Microsoft YaHei',
                            fontSize: 24
                        }
                    }
                }
            },
            // 数据
            data: nodeData,
            links: linksData,
            categories: categories
        }]
    };
    gChart.setOption(option);
}

// 搜索关系图的节点
function searchData(){
    let text = $("#searchInput").val();
    if(text === "" || text == null){
        alert("请输入需要匹配的文字！");
        return;
    }
    let expression = /^\s+?/;
    if(text.match(expression)){ // 不匹配空白字符
        return;
    }
    let isFound = findNodesAndMatchTextForGraph(text);
    if(isFound){
        highLight = text;
        showGChart();
    }
}

function findNodesAndMatchTextForGraph(text){
    for(let i in nodeData){
        if(nodeData[i].name.indexOf(text) >= 0){
            return true;
        }
    }
    return false;
}

// 保存图表
function saveChart(){
    let chartObject = {
        categories: categories,
        nodeData: nodeData,
        linksData: linksData,
        chartName: chartName,
        type: 'graph',
    };
    if(jsonInputVal === "" || jsonInputVal === null || jsonInputVal === undefined){
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
                jsonInputVal = JSON.stringify(chartObject);
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

// 打开设置图表名称对话框
function openSetChartName() {
    $("#msgInputDiv").append('<p>图表名称:<input type="text" value="'+chartName+'"></p>' +
        '<button onclick="setChartName()">确定</button>' +
        '<button style="margin-left: 50px" onclick="cancelInput()">取消</button>');
    $('#moreSettings').hide();
    $(".fullHide").show();
    $("#msgInputDiv").show();
}

// 设置图表名称
function setChartName() {
    let newName = $("#msgInputDiv").find("input").val();
    if(newName === ""){
        return;
    }
    chartName = newName;
    showGChart();
    cancelInput();
}

// 取消设置
function cancelInput() {
    $("#msgInputDiv").empty();
    $(".input-menu").hide();
    isOpen = false;
    $(".fullHide").hide();
}


// 打开设置系列对话框
function openSetCategoryDiv() {
    if(categories.length !== 0){
        // 显示已有的系列
        for(let i in categories){
            let categoryItem = '<div class="settingsBody-item">' +
                '系列名称：<input class="small-input" name="name" value="'+categories[i].name+'">' +
                '<img src="/ArkCharts/static/img/smallAdd.png" title="添加系列" width="30px" height="30px" onclick="addCategory()">&nbsp;';
            if(parseInt(i) === 0){
                categoryItem += '</div>';
            }else {
                categoryItem += '<img src="/ArkCharts/static/img/smallDel.png" title="删除系列" width="30px" height="30px" onclick="delCategory(this)">' +
                    '</div>';
            }
            $("#categoryBody").append(categoryItem);
        }
    }else{
        $("#categoryBody").append('<div class="settingsBody-item">系列名称：<input name="name" class="small-input">&nbsp;' +
            '<img src="/ArkCharts/static/img/smallAdd.png" title="添加系列" width="30px" height="30px" onclick="addCategory()"></div>');
    }
    $(".fullHide").show();
    $("#setCategoryDiv").show();
}

// 关闭设置系列对话框
function closeSetCategoryDiv() {
    $("#categoryBody").empty();
    $("#setCategoryDiv").hide();
    $(".fullHide").hide();
}

function addCategory() {
    let categoryItem = '<div class="settingsBody-item">' +
        '系列名称：<input class="small-input" name="name">' +
        '<img src="/ArkCharts/static/img/smallAdd.png" title="添加系列" width="30px" height="30px" onclick="addCategory()">&nbsp;' +
        '<img src="/ArkCharts/static/img/smallDel.png" title="删除系列" width="30px" height="30px" onclick="delCategory(this)">' +
        '</div>';
    $("#categoryBody").append(categoryItem);
}

function delCategory(img) {
    $(img).parent().remove();
}

// 设置系列
function setCategory() {
    categories = [];
    $("#categoryBody").find("input[name='name']").each(function () {
        let name = $(this).val();
        if(name !== ""){
            categories.push({name: name});
        }
    });
    showGChart();
    closeSetCategoryDiv();
}

// 打开设置节点系列对话框
function openForSetCategory() {
    $('#graph-right-click-menu').hide();
    $('#setCategoryMenu').css({ // 右击节点弹出选项框
        'left': gNodeParam.event.offsetX + 10,
        'top' : gNodeParam.event.offsetY + 60,
    });
    let string = '';
    for (let i in categories){
        string += '<input name="category" type="radio" value="'+i+'"> <input style="width: 100px;" value="'+categories[i].name+'" disabled> </br>';
    }
    $(".chooseCategory").append(string);
    $('#setCategoryMenu').show();
}
// 设置节点的系列
function setCategoryForNode() {
    let category = $(".chooseCategory").find("input:radio[name='category']:checked").val();
    for (let j in nodeData){
        if(nodeData[j].id === gNodeParam.value){
            nodeData[j].category = parseInt(category);
        }
    }
    showGChart();
    cancelSetCategory();
}
// 取消设置节点的系列
function cancelSetCategory() {
    $('.chooseCategory').empty();
    $('#setCategoryMenu').hide();
}

// 跳转到我的图表页面
function toStart() {
    location.href = "/ArkCharts/toStart";
}

// 新增关系图节点弹出框
function openForNewGNode(){
    $('#graph-right-click-menu').hide();
    $('#newGNodeMenu').css({ // 右击节点弹出选项框
        'left': gNodeParam.event.offsetX + 10,
        'top' : gNodeParam.event.offsetY + 60,
    });
    $('#newGNodeMenu').show();
}

// 新增关系图节点
function newGNode() {
    let name = $("#newGNodeName").val();
    if(name === ""){
        alert("节点名不可以为空");
        return;
    }
    $('.click-menu').hide();
    // 生成新节点的pid，策略是按顺序遍历点击节点pid前缀相同的所有节点，找到空的插入
    let brother = [];
    let gLength = gNodeParam.value.split("\.").length + 1;
    for(let i in nodeData){
        let spArray = nodeData[i].value.split("\.");
        if(spArray.length === gLength && nodeData[i].value.indexOf(gNodeParam.value) === 0 && nodeData[i].value !== gNodeParam.value){
            brother.push(nodeData[i].value);
        }
    }
    brother.sort();
    let flag = false;
    let newNodePid = gNodeParam.value;
    let j;
    for(j = 0; j< brother.length; j++){
        let strArray = brother[j].split("\.");
        let str = strArray[strArray.length - 1];
        if(parseInt(str) !== j){
            flag = true;
            newNodePid += "." + j;
            break;
        }
    }
    if(flag === false){
        newNodePid += "." + j;
    }
    let graphNode = {
        name: name,
        id: newNodePid,
        value: newNodePid,
        category: null
    };
    nodeData.push(graphNode);
    let linkName = $("#newGLinkName").val();
    let link = {
        source: gNodeParam.value,
        target: newNodePid,
        name: linkName,
        id: gNodeParam.value + "->" + newNodePid,
        value: gNodeParam.value + "->" + newNodePid
    };
    linksData.push(link);
    showGChart();
}

// 取消新增节点
function cancelNewGNode() {
    $('.click-menu').find('input').val("");
    $('#newGNodeMenu').hide();
}

// 编辑关系图节点弹出框
function openForEditGNode() {
    $('#graph-right-click-menu').hide();
    $('#editGNodeMenu').css({ // 右击节点弹出选项框
        'left': gNodeParam.event.offsetX + 10,
        'top' : gNodeParam.event.offsetY + 60,
    });
    $('#editGNodeMenu').show();
}

// 编辑关系图节点
function editGNode() {
    let newName = $("#editGNodeName").val();
    $('.click-menu').hide();
    let nodeId = gNodeParam.value;
    for(let i in nodeData){
        if(nodeData[i].id === nodeId){
            nodeData[i].name = newName;
            showGChart();
            return;
        }
    }
}

// 取消编辑节点
function cancelEditGNode() {
    $('.click-menu').find('input').val("");
    $('#editGNodeMenu').hide();
}

// 删除关系图节点
function deleteGNode() {
    $('.click-menu').hide();
    let nodeId = gNodeParam.value;
    for(let i in nodeData){
        if(nodeData[i].id === nodeId){
            nodeData.splice(i, 1);
            break;
        }
    }
    // 删除与该节点相关的所有连接线
    for(let j in linksData){
        let linkIdSp = linksData[j].id.split("->");
        if(linkIdSp[0] === nodeId || linkIdSp[1] === nodeId){
            linksData.splice(j, 1);
        }
    }
    showGChart();
}

// 新增连接弹出框
function openForNewLink() {
    $('#graph-right-click-menu').hide();
    $('#newLinkMenu').css({ // 右击节点弹出选项框
        'left': gNodeParam.event.offsetX + 10,
        'top' : gNodeParam.event.offsetY + 60,
    });
    $('#newLinkMenu').show();
}

// 新增连线
function newLink() {
    let targetId = $("#targetId").val();
    let newLinkName = $("#newLinkName").val();
    // 数据校验：id是否存在，id是否自身，是否已存在该连接线
    if(targetId === gNodeParam.value){
        alert("目标id不可以是自身");
        return;
    }
    if(linkIdExistence(gNodeParam.value + "->" + targetId) === true){
        alert("该连接线已经存在");
        return;
    }
    if(nodeIdExistence(targetId) !== true){
        alert("目标id不存在");
        return;
    }
    let link = {
        source: gNodeParam.value,
        target: targetId,
        name: newLinkName,
        id: gNodeParam.value + "->" + targetId,
        value: gNodeParam.value + "->" + targetId
    };
    linksData.push(link);
    showGChart();
    $('.click-menu').find('input').val("");
    $('#newLinkMenu').hide();
}

// 取消新增连线
function cancelNewLink() {
    $('.click-menu').find('input').val("");
    $('#newLinkMenu').hide();
}

// 判断连接线id是否存在
function linkIdExistence(id) {
    for(let i in linksData){
        if(linksData[i].id === id){
            return true;
        }
    }
}

// 判断节点id是否存在
function nodeIdExistence(id) {
    for(let i in nodeData){
        if(id === nodeData[i].id){
            return true;
        }
    }
    return false;
}

// 标红连接线
function redLink() {
    $('.click-menu').hide();
    let linkId = gNodeParam.value;
    for(let i in linksData){
        if(linksData[i].id === linkId){
            if(linksData[i].label !== undefined && linksData[i].label.color === "red"){
                linksData[i].label = {};
                linksData[i].lineStyle = {};
            }else {
                linksData[i].label = {color: "red"};
                linksData[i].lineStyle = {color: "red"};
            }
            showGChart();
            return;
        }
    }
}

// 编辑连线名称弹出框
function openForEditLink() {
    $('#graph-right-click-menu').hide();
    $('#editLinkMenu').css({ // 右击节点弹出选项框
        'left': gNodeParam.event.offsetX + 10,
        'top' : gNodeParam.event.offsetY + 60,
    });
    $('#editLinkMenu').show();
}

// 编辑连接线
function editLink() {
    let newName = $("#editLinkName").val();
    $('.click-menu').hide();
    let linkId = gNodeParam.value;
    for(let i in linksData){
        if(linksData[i].id === linkId){
            linksData[i].name = newName;
            showGChart();
            return;
        }
    }
}

// 取消编辑
function cancelEditLink() {
    $('.click-menu').find('input').val("");
    $('#editLinkName').hide();
}

// 删除连接线
function deleteLink() {
    $('.click-menu').hide();
    let linkId = gNodeParam.value;
    for(let i in linksData){
        if(linksData[i].id === linkId){
            linksData.splice(i, 1);
            showGChart();
            return;
        }
    }
}