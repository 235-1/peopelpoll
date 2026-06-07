package com.xbx.peoplepoll.service.impl;

import com.xbx.peoplepoll.utils.cache.SentimentDataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 舆情大屏数据预计算定时清洗服务（9项指标全激活版）
 * @author X
 * @date 2026/5/29 17:17
 */
@Service
public class SentimentScheduleServiceImpl {

    @Autowired
    private SentimentDataCache dataCache;


    @Autowired
    private PredictStatisticServiceImpl predictStatisticService;

    @Autowired
    private StatisticServiceImpl statisticService;

    // fixedRate = 360000 表示每 360,000毫秒执行一次 也就是6min
    @Scheduled(fixedRate = 10000 * 60 * 5)
    public void refreshSentimentCache() {
        try {
            System.out.println("读取9个接口的数据");

            Object labelCountData       = predictStatisticService.getLabelCount();              // 1. 情感总占比
            Object labelCountByTimeData = predictStatisticService.getLabelCountByTime();        // 2. 舆情时间走势
            Object regionHeatData       = predictStatisticService.getRegionHeatStatistics();    // 3. 区域预测热力统计
            Object interactionData      = predictStatisticService.getInteractionEmotion();      // 4. 交互度情绪统计
            Object userAuthEmotionData  = predictStatisticService.getUserAuthEmotion();         // 5. 用户认证情绪统计
            Object opinionTrendData     = predictStatisticService.getOpinionTrend();            // 6. 舆情随日期变化趋势


            Object totalStatisticData   = statisticService.getTotalStatisticData();             // 7. 时段认证交叉大矩阵(二维数组)
            Object userStatisticData    = statisticService.getUserStatisticData();              // 8. 用户发帖量统计排行
            Object regionHotData        = statisticService.getRegionHotData();                  // 9. 真实地图热点数据

            // 2. 统一非空校验：只要有一个为空，直接不更新缓存
            boolean allDataValid = isNotEmpty(labelCountData)
                    && isNotEmpty(labelCountByTimeData)
                    && isNotEmpty(regionHeatData)
                    && isNotEmpty(interactionData)
                    && isNotEmpty(userAuthEmotionData)
                    && isNotEmpty(opinionTrendData)
                    && isNotEmpty(totalStatisticData)
                    && isNotEmpty(userStatisticData)
                    && isNotEmpty(regionHotData);

            if (!allDataValid) {
                System.err.println("存在空数据，不更新缓存");
                return;
            }

            dataCache.put("labelCount", labelCountData);
            dataCache.put("labelCountByTime", labelCountByTimeData);
            dataCache.put("regionHeat", regionHeatData);
            dataCache.put("sentimentInteraction", interactionData);
            dataCache.put("sentimentUserAuth", userAuthEmotionData);
            dataCache.put("sentimentStack", opinionTrendData);

            dataCache.put("sentimentLinear", totalStatisticData); // 对应那个复杂的时段交叉矩阵
            dataCache.put("userStatistic", userStatisticData);
            dataCache.put("sentimentMap", regionHotData);

            System.out.println("大屏全部 9 ");
        } catch (Exception e) {
            // 9个任务里只要有一个因为数据库写锁、锁表被卡住报错，立刻触发保护机制。
            // 绝不执行任何 cache.put()，保存完好的 9 组数据
            System.err.println("全量清洗数据异常。。错误原因: " + e.getMessage());
        }
    }




    private boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }
        // 集合判空
        if (obj instanceof Collection) {
            return !CollectionUtils.isEmpty((Collection<?>) obj);
        }
        // Map判空
        if (obj instanceof Map) {
            return !((Map<?, ?>) obj).isEmpty();
        }
        // 数组判空
        if (obj.getClass().isArray()) {
            return ((Object[]) obj).length > 0;
        }
        // 字符串判空
        if (obj instanceof String) {
            return !((String) obj).trim().isEmpty();
        }
        // 其他对象默认视为有效
        return true;
    }
}