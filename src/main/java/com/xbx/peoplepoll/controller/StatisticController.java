package com.xbx.peoplepoll.controller;


import com.xbx.peoplepoll.pojo.*;
import com.xbx.peoplepoll.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author X
 * @date 2026/5/9 21:10
 */
@Slf4j
@RestController
public class StatisticController {
    @Autowired
    private StatisticService statisticService;

    // 获取统计数据

    @GetMapping("/statistics")
    public Result getStatisticData(){
        List<List<Object>> data = statisticService.getTotalStatisticData();
        return Result.success(data);
    }

    @GetMapping("/statistics/user")
    public Result getUserStatisticData(){
        List<UserPost> data = statisticService.getUserStatisticData();
        return Result.success(data);
    }

    // 请求地图热点数据
    @GetMapping("/statistics/region")
    public Result getRegionHotData(){
        List<RegionHot> data = statisticService.getRegionHotData();
        return Result.success(data);
    }

}
