<!--
主要用于展示微博的整体数据统计情况
1、 给 ECharts 准备一个有宽高的容器
2、 拿到容器
3、 初始化 ECharts 实例  在生命周期中初始化mounted
4、 配置图表
  1、配置标题
  2、配置图例
  3、配置数据

-->
// 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口。
<script setup>
import * as echarts from 'echarts';
import { onMounted, ref } from 'vue';
defineOptions({
  name: 'TotalWeiboStatistic',
})

/* 拿到容器 */
const pieAndLine = ref(null)
/* 初始化 ECharts 实例 */
let myChart = null
onMounted(() => {
  myChart = echarts.init(pieAndLine.value, 'dark')
  /* 配置图表 */
  const option = {
     legend: {},

    tooltip: {
      trigger: 'axis',
      showContent: false
    },
    // 配置数据
    dataset: {
      source: [
        ['product', '2012', '2013', '2014', '2015', '2016', '2017'], // x轴
        // y轴 同时也是饼图数据
        ['Milk Tea', 56.5, 82.1, 88.7, 70.1, 53.4, 85.1],
        ['Matcha Latte', 51.1, 51.4, 55.1, 53.3, 73.8, 68.7],
        ['Cheese Cocoa', 40.1, 62.2, 69.5, 36.4, 45.2, 32.5],
        ['Walnut Brownie', 25.2, 37.1, 41.2, 18, 33.9, 49.1]
      ]
    },
    xAxis: { type: 'category' },
    yAxis: { gridIndex: 0 },
    grid: { top: '55%' },
    // 配置四条折线图
    series: [
      {
        type: 'line',
        smooth: true,
        seriesLayoutBy: 'row',
        emphasis: { focus: 'series' }
      },
      {
        type: 'line',
        smooth: true,
        seriesLayoutBy: 'row',
        emphasis: { focus: 'series' }
      },
      {
        type: 'line',
        smooth: true,
        seriesLayoutBy: 'row',
        emphasis: { focus: 'series' }
      },
      {
        type: 'line',
        smooth: true,
        seriesLayoutBy: 'row',
        emphasis: { focus: 'series' }
      },
      // 配置饼图
      {
        type: 'pie',
        id: 'pie',
        radius: '30%',
        center: ['50%', '25%'],
        emphasis: {
          focus: 'self'
        },
        // 配置饼图标签 默认显示
        label: {
          formatter: '{b}: {@2012} ({d}%)'
        },
        // 配置饼图数据 默认显示
        encode: {
          itemName: 'product',
          value: '2012',
          tooltip: '2012'
        }
      }
    ]
  }
  /* 配置图表事件 跟随鼠标x轴两图联动 */
  myChart.on('updateAxisPointer', function (event) {
    const xAxisInfo = event.axesInfo[0];
    if (xAxisInfo) {
      const dimension = xAxisInfo.value + 1;
      myChart.setOption({
        series: {
          id: 'pie',
          label: {
            formatter: '{b}: {@[' + dimension + ']} ({d}%)'
          },
          encode: {
            value: dimension,
            tooltip: dimension
          }
        }
      });
    }
  });

  myChart.setOption(option)

})

</script>
<template>
  <div ref="pie" class="container" style="width: 100%; height: 800px;">

  </div>
</template>

<style scoped>

</style>
