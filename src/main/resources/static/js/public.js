let winWidth;
let winHeight;
//获取窗口宽度
if (window.innerWidth){
    winWidth = window.innerWidth;
}else if ((document.body) && (document.body.clientWidth)){
    winWidth = document.body.clientWidth;
}
// 获取窗口高度
if (window.innerHeight){
    winHeight = window.innerHeight;
}else if((document.body) && (document.body.clientHeight)){
    winHeight = document.body.clientHeight;
}
// 将fullHide铺满屏幕
$(".fullHide").css("height", winHeight);
$(".fullHide").css("width", winWidth);