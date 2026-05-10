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
    public Statistic getStatisticData() {
        List<DayPost> dayPost = dayPostCountMapper.selectAll();
        List<HourPost> hourPost = hourPostCountMapper.selectAll();
        List<TimePost> timePost = timePeriodCountMapper.selectAll();
        List<UserPost> userPost = userPostCountMapper.selectAll();
        Statistic statistic = new Statistic();
        Map<String, Object> data = new HashMap<>();
        data.put("dayPost", dayPost);
        data.put("hourPost", hourPost);
        data.put("timePost", timePost);
        data.put("userPost", userPost);
        statistic.setData(data);
        return statistic;
    }
}
