<script setup>
import * as echarts from "echarts";
import { onMounted, ref, onUnmounted, defineProps, watch } from "vue";

// 定义props
const props = defineProps({
  sentimentLinearData: {
    type: Object,
    default: () => ({}),
  },
});

// 定义DOM引用
const sentimentLinear = ref(null);
let myChart = null;

// 【核心抽离】：把重复的渲染逻辑写成函数，只写一次
const renderChart = () => {
  if (!myChart) return; // 容错：图表实例未初始化则返回
  myChart.showLoading();
  // 解构数据并兜底，避免data[1]/data[2]为undefined
  const { xAxis = [], data = [[], [], []] } = props.sentimentLinearData;

  const option = {
    title: {
      text: "不同时段情感倾向分布",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      triggerOn: "mousemove|click",
      formatter: function(params) {
        let res = params[0].name + "<br/>";
        // 遍历所有系列，强制输出
        const seriesMap = {
          "中性": data[0][params[0].dataIndex] || 0,
          "正面": data[1][params[0].dataIndex] || 0,
          "负面": data[2][params[0].dataIndex] || 0
        };
        for (const name in seriesMap) {
          res += name + ": " + seriesMap[name] + "<br/>";
        }
        return res;
      },
      // 强制显示tooltip的坐标轴指示器
      axisPointer: {
        type: "shadow",
        label: { show: true }
      },
      showContent: true,
      alwaysShowContent: true
    },
    legend: {
      data: ["中性", "正面", "负面"],
      top: "10%",
      // 强制开启图例多选，确保系列不被隐藏
      selectedMode: "multiple",
      selected: { "中性": true, "正面": true, "负面": true }
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    toolbox: {
      feature: {
        saveAsImage: {},
      },
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: xAxis,
    },
    yAxis: {
      type: "value",
      name: "数量",
    },
    series: [

      {
        name: "正面",
        type: "line",
        smooth: true,
        data: data[1],
        tooltip: { show: true },
        show: true
      },
      {
        name: "中性",
        type: "line",
        smooth: true,
        data: data[0],

        tooltip: { show: true },
        show: true
      },
      {
        name: "负面",
        type: "line",
        smooth: true,
        data: data[2],
        tooltip: { show: true },
        show: true
      },
    ],
  };
  // 强制重置配置，覆盖所有旧状态
  myChart.setOption(option, true);
  myChart.hideLoading();
};

onMounted(() => {
  // 初始化图表实例
  myChart = echarts.init(sentimentLinear.value);
  // 直接执行首次渲染，不做任何判断
  renderChart();
});

watch(()=>props.sentimentLinearData, (newVal, oldVal) => {
  if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
    console.log(newVal)
    renderChart();
  }
}, { deep: true })

onUnmounted(() => {
  if (myChart) {
    myChart.dispose();
  }
  myChart = null;
});
</script>

<template>
  <div ref="sentimentLinear" class="sentimentLinear" style="width: 75%; height: 400px"></div>
</template>

<style scoped>
.sentimentLinear {
  margin-right: 5px;
}
</style>
