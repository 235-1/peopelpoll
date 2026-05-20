package com.xbx.peoplepoll.controller;

import com.xbx.peoplepoll.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.DeleteProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.xbx.peoplepoll.service.PredictStatisticService;
/**
 * @author X
 * @date 2026/5/19 22:32
 */
/*
* 处理预测统计页面的请求
* */
@Slf4j
@RestController
public class PredictStatisticController {
    @Autowired
    private PredictStatisticService predictStatisticService;

    // 获取label_count
    @GetMapping("/labelCount")
    public Result getLabelCount(){
        List<LabelCount> labelCounts = predictStatisticService.getLabelCount();
        return Result.success(labelCounts);
    }

    // 获取label和时间段上午下午....的统计
    @GetMapping("/labelCountByTime")
    public Result getLabelCountByTime(){
        List<LabelCountByTime> labelCountByTimes = predictStatisticService.getLabelCountByTime();
        return Result.success(labelCountByTimes);
    }

    //情绪地区统计
    @GetMapping("/regionEmotion")
    public Result getRegionEmotion(){
        List<RegionHeatStatistic> regionHeatStatistics = predictStatisticService.getRegionHeatStatistics();
        return Result.success(regionHeatStatistics);
    }

    // 交互度和情绪统计
    @GetMapping("/interactionEmotion")
    public Result getInteractionEmotion(){
        List<InteractionCountByLabel> interactionCountByLabels = predictStatisticService.getInteractionEmotion();
        return Result.success(interactionCountByLabels);
    }

    // 用户认证情绪统计
    @GetMapping("/userAuthEmotion")
    public Result getUserAuthEmotion(){
        List<UserAuthLabelCount> userAuthLabelCounts = predictStatisticService.getUserAuthEmotion();
        return Result.success(userAuthLabelCounts);
    }

    //舆情随日期的变化趋势
    @GetMapping("/opinionTrend")
    public Result getOpinionTrend(){
        List<DayLabelRatio> dayLabelRatios = predictStatisticService.getOpinionTrend();
        return Result.success(dayLabelRatios);
    }
}
