package com.ark.arkcharts.test;

import com.ark.arkcharts.Application;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Ark
 * @date 2020/06/01 16:24
 */

@SpringBootTest(classes = Application.class)
public class ApplicationTest {
    // 日志记录器
    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test(){
        // 日志级别(由低到高)：
        logger.trace("这是trace日志..."); // 跟踪信息，用于记录一些关键数据，可以取代system.out.println
        logger.debug("这是debug日志..."); // 调试信息
        // springboot默认是info级别，也就是只打印info及以上级别的日志，可以在配置文件中修改
        logger.info("这是info日志..."); // 自定义的一些信息
        logger.warn("这是warn日志..."); // 这是警告信息
        logger.error("这是error日志..."); // 这是异常信息

    }
}
