package com.xbx.peoplepoll.controller;


import com.xbx.peoplepoll.pojo.*;
import com.xbx.peoplepoll.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



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
    @GetMapping("/statistic")
    public Result getStatisticData(){
        Statistic data = statisticService.getStatisticData();
        return Result.success(data);
    }

}
