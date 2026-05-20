<script setup>
import * as echarts from "echarts";
import { onMounted, ref, onUnmounted, defineProps, watch } from "vue";

const props = defineProps({
  sentimentLinearData: {
    type: Object,
    default: () => ({}),
  },
});

const sentimentLinear = ref(null);
let myChart = null;

const renderChart = () => {
  if (!myChart) return;
  myChart.showLoading();

  const { xAxis = [], data = [[], [], []] } = props.sentimentLinearData;

  const option = {
    title: {
      text: "不同时段情感倾向分布",
      left: "center",
      textStyle: { color: "#fff" },
    },
    tooltip: {
      trigger: "axis",
      textStyle: { color: "#fff" },
      backgroundColor: "rgba(0,0,0,0.7)",
    },
    legend: {
      data: ["中性", "正面", "负面"],
      top: "10%",
      textStyle: { color: "#fff" },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: xAxis,
      axisLine: { lineStyle: { color: "#fff" } },
      axisLabel: { color: "#fff" },
    },
    yAxis: {
      type: "value",
      name: "数量",
      axisLine: { lineStyle: { color: "#fff" } },
      axisLabel: { color: "#fff" },
    },
    series: [
      // 中性 → 蓝色
      {
        name: "中性",
        type: "line",
        smooth: true,
        data: data[0],
        itemStyle: { color: "#409eff" },
        lineStyle: { width: 3 },
      },
      // 正面 → 绿色
      {
        name: "正面",
        type: "line",
        smooth: true,
        data: data[1],
        itemStyle: { color: "#52c41a" },
        lineStyle: { width: 3 },
      },
      // 负面 → 红色
      {
        name: "负面",
        type: "line",
        smooth: true,
        data: data[2],
        itemStyle: { color: "#ff4d4f" },
        lineStyle: { width: 3 },
      },
    ],
  };

  myChart.setOption(option, true);
  myChart.hideLoading();
};

onMounted(() => {
  myChart = echarts.init(sentimentLinear.value);
  renderChart();
});

watch(
  () => props.sentimentLinearData,
  () => renderChart(),
  { deep: true }
);

onUnmounted(() => {
  myChart?.dispose();
});
</script>

<template>
  <div ref="sentimentLinear" class="sentimentLinear" style="width: 75%; height: 400px"></div>
</template>

<style scoped>
.sentimentLinear {
  margin-right: 5px;
  background: rgba(4, 8, 16);
  border-radius: 8px;
  padding: 10px;
}
</style>
