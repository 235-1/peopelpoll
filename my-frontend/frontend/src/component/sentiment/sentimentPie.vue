<script setup>
import { ref, onMounted, onUnmounted, defineProps, watch } from "vue";
import * as echarts from "echarts";

const chartDom = ref(null);
let myChart = null;

const props = defineProps({
  labelCount: {
    type: Array,
    default: () => [],
  },
});


onMounted(() => {
  if (!chartDom.value) return;
  myChart = echarts.init(chartDom.value);
});

onUnmounted(() => {
  if (myChart) {
    myChart.dispose();
  }

});

watch(
  () => props.labelCount,
  (newVal) => {
    if (!myChart || !newVal || newVal.length === 0) return;

    myChart.showLoading();

    const option = {
      // 深色大屏配色
      backgroundColor: "transparent",

      // 中性 / 正面 / 负面
      color: ["#909399", "#67C23A", "#F56C6C"],

      tooltip: {
        trigger: "item",
        backgroundColor: "rgba(0,0,0,0.8)",
        borderColor: "#333",
        textStyle: {
          color: "#fff",
        },
        formatter: "{b}：{c}条 ({d}%)",
      },

      legend: {
        top: "bottom",

        itemWidth: 10,
        itemHeight: 10,

        textStyle: {
          color: "#fff",
          fontSize: 14,
        },
      },

      series: [
        {
          name: "情感统计",

          type: "pie",

          radius: ["40%", "70%"],

          center: ["60%", "45%"],

          avoidLabelOverlap: true,

          label: {
            show: true,

            formatter: "{b}\n{d}%",

            fontSize: 14,

            color: "#fff",
          },

          labelLine: {
            show: true,

            lineStyle: {
              color: "#fff",
            },
          },

          itemStyle: {
            borderColor: "#111",
            borderWidth: 2,
          },

          emphasis: {
            scale: true,
            scaleSize: 8,
          },

          data: props.labelCount.map((item) => ({
            name: item.label,
            value: item.value,
          })),
        },
      ],
    };

    myChart.setOption(option, true);

    myChart.hideLoading();
  },
  {
    immediate: true,
    deep: true,
  },
);
</script>

<template>
  <div
    ref="chartDom"
    style="width: 25%; height: 400px"
  ></div>
</template>

<style scoped></style>
