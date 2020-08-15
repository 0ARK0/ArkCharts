package com.ark.arkcharts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Ark
 * @date 2020/05/21 10:41
 * 系统路径配置
 */
@ConfigurationProperties(prefix = "pathConfig")
public class PathConfig {
    public static String win; // 在win系统上的系统目录路径
    public static String linux; // 在linux系统上的系统目录路径
}
