package com.ark.arkcharts.mapper;

import com.ark.arkcharts.entity.Chart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Ark
 * @date 2020/05/16 14:30
 */
public interface ChartMapper {

    @Insert("INSERT INTO chart VALUES(#{chartId}, #{chartName}, #{type}, #{path}, #{userId})")
    void saveNew(Chart chart);

    @Select("SELECT * FROM chart WHERE user_id = #{userId}")
    @Results({
            @Result(property = "chartId", column = "chart_id"),
            @Result(property = "chartName", column = "chart_name"),
            @Result(property = "userId", column = "user_id")
    })
    List<Chart> getAllChartsByUser(String userId);

    @Select("SELECT * FROM chart WHERE chart_id = #{chartId}")
    @Results({
            @Result(property = "chartId", column = "chart_id"),
            @Result(property = "chartName", column = "chart_name"),
            @Result(property = "userId", column = "user_id")
    })
    Chart getChartById(String chartId);

    @Delete("DELETE FROM chart WHERE user_id = #{userId} AND chart_id = #{chartId}")
    void delChart(String userId, String chartId);

    @Update("UPDATE chart SET chart_name = #{chartName} WHERE path = #{chartPath}")
    void updateChartNameByChartPath(String chartPath, String chartName);
}
