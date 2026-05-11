<!--
主要用于展示微博的整体数据统计情况
1、 给 ECharts 准备一个有宽高的容器
2、 初始化 ECharts 实例  在生命周期中初始化mounted
3、 配置图表

-->
// 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口。
<script setup>
import PieComponent from '@/component/pie.vue'
defineOptions({
  name: 'TotalWeiboStatistic',
})
</script>
<template>
  <div class="container">
    <!-- <h1>微博整体数据统计</h1> -->
    <PieComponent></PieComponent>
  </div>
</template>

<style scoped>

</style>
