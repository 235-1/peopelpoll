package com.xbx.peoplepoll.service.impl;

import com.xbx.peoplepoll.mapper.DayPostCountMapper;
import com.xbx.peoplepoll.mapper.HourPostCountMapper;
import com.xbx.peoplepoll.mapper.TimePeriodCountMapper;
import com.xbx.peoplepoll.mapper.UserPostCountMapper;
import com.xbx.peoplepoll.pojo.*;
import com.xbx.peoplepoll.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author X
 * @date 2026/5/10 11:18
 */



     /*
     前端需要
     ['timePeriod', '晚间', '凌晨', '午夜', '下午', '早晨', '上午'], // x轴
     ['官方媒体(蓝V)', 56.5, 82.1, 88.7, 70.1, 53.4, 85.1],
     ['个人黄V', 51.1, 51.4, 55.1, 53.3, 73.8, 68.7],
     ['个人金V', 40.1, 62.2, 69.5, 36.4, 45.2, 32.5],
     ['未认证用户', 25.2, 37.1, 41.2, 18, 33.9, 49.1]
     ['未认证用户', 25.2, 37.1, 41.2, 18, 33.9, 49.1]
      */

@Service
public class StatisticServiceImpl implements StatisticService {
    @Autowired
    private DayPostCountMapper dayPostCountMapper;
    @Autowired
    private HourPostCountMapper hourPostCountMapper;
    @Autowired
    private TimePeriodCountMapper timePeriodCountMapper;
    @Autowired
    private UserPostCountMapper userPostCountMapper;
    @Override
    public List<List<String, Object>> getStatisticData() {
        List<DayPost> dayPost = dayPostCountMapper.selectAll();
        List<HourPost> hourPost = hourPostCountMapper.selectAll();
        List<TimePost> timePost = timePeriodCountMapper.selectAll();
        List<UserPost> userPost = userPostCountMapper.selectAll();

        return statistic;
    }
}
