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

/**
 * 【新增核心清洗逻辑】
 * 解决粉色不对的根源：后端的 userAuthData 包含了 label 维度，直接用会导致数据重复、映射错位。
 * 通过 computed 将其按“认证类型”进行总数累加，清洗为标准饼图所需的二维结构。
 */
const cleanedUserAuthData = computed(() => {
  const raw = prop.userAuthData || [];
  const map = {};

  raw.forEach((item) => {
    // 兼容后端 POJO 字段名，如果字段名是大写或不同请在此处对齐
    const auth = item.userAuthentication;
    const count = Number(item.count || 0);
    if (auth) {
      map[auth] = (map[auth] || 0) + count;
    }
  });

  // 生成标准的扁平化对象数组
  return Object.keys(map).map((key) => ({
    "用户认证": key,
    "数量": map[key],
  }));
});

// 将对象数组转换为 ECharts dataset source 需要的二维数组格式
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

  // dataset0 使用原始折线图大矩阵
  const dataset0 = normalizeDataset(prop.data);
  // dataset1 使用前端清洗合并完成后的干净认证数据
  const dataset1 = normalizeDataset(cleanedUserAuthData.value);

  myChart.setOption({
    animation: true,
    progressive: 2000,
    progressiveThreshold: 5000,
    legend: {
      orient: "horizontal",
      top: "5%",
      textStyle: { color: "#fff" }
    },
    title: [
      {
        text: "用户认证统计",
        top: "45%",
        left: "75%",
        textAlign: "center",
        textStyle: { color: "#fff", fontSize: 16 },
      },
      {
        text: "用户活跃度统计",
        top: "45%",
        left: "25%",
        textAlign: "center",
        textStyle: { color: "#fff", fontSize: 16 },
      },
    ],
    tooltip: { trigger: "axis" },
    xAxis: { type: "category" },
    yAxis: {
      gridIndex: 0,
      max: maxY.value,
      splitLine: {
        lineStyle: { color: "rgba(255,255,255,0.1)" },
      },
    },
    grid: {
      top: "50%",
      bottom: "8%",
      left: "8%",
      right: "8%",
    },
    dataset: [
      { source: dataset0 },
      { source: dataset1 },
    ],
    series: [
      // 折线图系列 (读取 datasetIndex: 0)
      { name: "个人红V",       datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "个人金V",       datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "个人黄V",       datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "官方媒体(蓝V)", datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "普通用户",      datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },
      { name: "未认证用户",    datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },

      // 左侧饼图：联动折线图时段活跃度 (读取 datasetIndex: 0)
      {
        type: "pie",
        id: "pie",
        datasetIndex: 0,
        radius: "28%",
        center: ["25%", "25%"],
        emphasis: { focus: "self" },
        label: {
          formatter: "{b}\n数量：{@1}  占比：{d}%",
          color: "#fff",
          fontSize: 11,
        },
        encode: { itemName: 0, value: 1, tooltip: 1 },
      },

      // 右侧饼图：核心修正后的【用户认证统计】(读取 datasetIndex: 1)
      {
        type: "pie",
        id: "pie2",
        datasetIndex: 1,
        radius: "28%",
        center: ["75%", "25%"],
        emphasis: { focus: "self" },
        label: {
          // 由于经过 cleanedUserAuthData 重新排布，数量位于第 1 列
          formatter: "{b}\n数量：{@1}\n占比：{d}%",
          color: "#fff",
          fontSize: 11,
        },
        // 映射对齐：itemName 映射到第 0 列(用户认证名称)，value 映射到第 1 列(合并后的数量)
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
        // pie2 独立不参与时间联动，坚守初始饱满状态
      ],
    });
  }, 50);

  myChart.on("updateAxisPointer", axisPointerHandler);
};

onMounted(() => {
  myChart = echarts.init(pieAndLine.value, "dark");
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

// 监听数据变化
watch(
  () => [prop.data, prop.userAuthData],
  ([newData, newUserAuthData]) => {
    if (!newData?.length || !newUserAuthData?.length) return;
    nextTick(() => {
      if (!myChart) return;
      initChart();
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
