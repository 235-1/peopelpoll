<script setup>
import * as echarts from "echarts";
import { onMounted, onUnmounted, ref, watch, computed, nextTick } from "vue";

defineOptions({
  name: "TotalWeiboStatistic",
});

const prop = defineProps({
  data: {
    type: Array,
    default: () => [],
  },
  userAuthData: {
    type: Array,
    default: () => [],
  },
});

// leading throttle，立即执行不积压
const throttle = (func, delay) => {
  let lastTime = 0;
  return function (...args) {
    const now = Date.now();
    if (now - lastTime >= delay) {
      lastTime = now;
      func.apply(this, args);
    }
  };
};

const pieAndLine = ref(null);
let myChart = null;
let axisPointerHandler = null;

// 计算折线图 Y 轴最大值（基于 data，二维数组格式）
const maxY = computed(() => {
  const authData = prop.data || [];
  const allValues = [];
  for (let i = 1; i < authData.length; i++) {
    const row = authData[i] || [];
    for (let j = 1; j < row.length; j++) {
      const num = Number(row[j]);
      if (!isNaN(num)) allValues.push(num);
    }
  }
  return allValues.length > 0 ? Math.max(...allValues) : 100;
});

// 将对象数组转换为 ECharts dataset source 需要的二维数组格式
// 例：[{name:'A', value:1}, ...] => [['name','value'],['A',1],...]
const normalizeDataset = (raw) => {
  if (!raw || raw.length === 0) return [];
  // 已经是二维数组，直接返回
  if (Array.isArray(raw[0])) return raw;
  // 是对象数组，转换
  const keys = Object.keys(raw[0]);
  const header = keys;
  const rows = raw.map((item) => keys.map((k) => item[k]));
  return [header, ...rows];
};

const initChart = () => {
  if (!myChart) return;
  if (!prop.data?.length || !prop.userAuthData?.length) return;

  const dataset0 = normalizeDataset(prop.data);
  const dataset1 = normalizeDataset(prop.userAuthData);

  myChart.setOption({
    animation: true,
    progressive: 2000,
    progressiveThreshold: 5000,
    legend: {},
    title: [
      {
        text: "用户认证统计",
        top: "center",
        left: "70%",
        textStyle: { color: "#fff" },
      },
      {
        text: "用户活跃度统计",
        top: "center",
        left: "20%",
        textStyle: { color: "#fff" },
      },
    ],
    tooltip: { trigger: "axis" },
    xAxis: { type: "category" },
    yAxis: {
      gridIndex: 0,
      max: maxY.value,
      splitLine: {
        lineStyle: { color: "rgba(255,255,255,0.3)" },
      },
    },
    grid: {
      top: "55%",
      bottom: "10%",
      left: "10%",
      right: "10%",
    },
    dataset: [
      { source: dataset0 },
      { source: dataset1 },
    ],
    series: [
      { name: "个人红V",       datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "个人金V",       datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "个人黄V",       datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "官方媒体(蓝V)", datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "普通用户",      datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "未认证用户",    datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      {
        type: "pie",
        id: "pie",
        datasetIndex: 0,
        radius: "30%",
        center: ["25%", "25%"],
        emphasis: { focus: "self" },
        label: {
          formatter: "{b}\n数量：{@1}  占比：{d}%",
          color: "#fff",
          fontSize: 12,
        },
        encode: { itemName: 0, value: 1, tooltip: 1 },
      },
      {
        type: "pie",
        id: "pie2",
        datasetIndex: 1,
        radius: "30%",
        center: ["75%", "25%"],
        emphasis: { focus: "self" },
        label: {
          formatter: "{b}\n数量：{@1}\n占比：{d}%",
          color: "#fff",
          fontSize: 12,
        },
        encode: { itemName: 0, value: 1, tooltip: 1 },
      },
    ],
  });

  // 注册联动事件（先解绑防止重复注册）
  if (axisPointerHandler) {
    myChart.off("updateAxisPointer", axisPointerHandler);
  }

  axisPointerHandler = throttle((event) => {
  const xAxisInfo = event.axesInfo?.[0];
  if (!xAxisInfo) return;
  const dim = xAxisInfo.value + 1;

  myChart.setOption({
    series: [
      {
        id: "pie",
        label: { formatter: `{b}\n数量：{@${dim}}  占比：{d}%` },
        encode: { value: dim, tooltip: dim },
      },
      // 删掉下面的 pie2，让用户认证统计保持初始状态，不受折线图悬浮影响
    ],
  });
}, 50);

  myChart.on("updateAxisPointer", axisPointerHandler);
};

onMounted(() => {
  myChart = echarts.init(pieAndLine.value, "dark");
  // 若数据已就绪（父组件 onMounted 里已经请求完毕），直接初始化
  if (prop.data?.length && prop.userAuthData?.length) {
    initChart();
  }
});

onUnmounted(() => {
  if (myChart) {
    if (axisPointerHandler) myChart.off("updateAxisPointer", axisPointerHandler);
    myChart.dispose();
  }
  myChart = null;
  axisPointerHandler = null;
});

// 监听数据变化：数据首次到来时完整初始化；后续变化只更新 dataset + yAxis
watch(
  () => [prop.data, prop.userAuthData],
  ([newData, newUserAuthData]) => {
    if (!newData?.length || !newUserAuthData?.length) return;
    // 等 DOM/myChart 就绪再操作
    nextTick(() => {
      if (!myChart) return;
      initChart(); // initChart 内部会做完整 setOption，幂等安全
    });
  },
  { deep: true }
);

</script>

<template>
  <div class="container">
    <div ref="pieAndLine" class="chart-box"></div>
  </div>
</template>

<style scoped>
.container {
  width: 100%;
}
.chart-box {
  width: 100%;
  height: 800px;
}
</style>
