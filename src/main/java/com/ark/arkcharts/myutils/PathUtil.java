package com.ark.arkcharts.myutils;

/**
 * 处理文件路径的工具类
 */
public class PathUtil {
    // 服务器系统类型
    private static final String OS_TYPE = System.getProperty("os.name").toLowerCase();
    // 操作系统对应的文件路径分隔符
    public static final String SP = System.getProperty("file.separator");
    // win系统的项目起始目录
    private static final String WIN_START_DIR = "E:\\SpringBootProjects\\ArkCharts";
    // linux系统的项目起始目录
    private static final String LINUX_START_DIR = "/usr/local/SpringBootProjects/ArkCharts";

    private PathUtil() {}

    /**
     * 根据系统类型拼接文件路径，前面不加初始路径
     * @param paths
     */
    public static String combinePath(String... paths){
        StringBuilder sb = new StringBuilder();
        for (String p : paths){
            sb.append(p).append(SP);
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    /**
     * 根据系统类型拼接文件路径，前面自动加初始路径
     * @param paths
     */
    public static String combinePathWithStart(String... paths){
        StringBuilder sb = new StringBuilder();
        if(OS_TYPE.startsWith("win")){
            sb.append(WIN_START_DIR);
        }else{
            sb.append(LINUX_START_DIR);
        }
        for (String p : paths){
            sb.append(SP).append(p);
        }
        return sb.toString();
    }

    /**
     * 获取项目的起始目录
     * @return
     */
    public static String getStartPath(){
        if(OS_TYPE.startsWith("win")){
            return WIN_START_DIR;
        }else{
            return LINUX_START_DIR;
        }
    }

    /**
     * 获取用户目录的url
     * @param userId
     * @return
     */
    public static String getUserDir(String userId){
        return getStartPath() + SP + "user" + SP + userId;
    }
}
