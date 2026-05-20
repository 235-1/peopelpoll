<template>
  <div class="radar-wrapper">
    <div ref="r1" class="radar-box"></div>
    <div ref="r2" class="radar-box"></div>
    <div ref="r3" class="radar-box"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import * as echarts from "echarts";

const r1 = ref(null);
const r2 = ref(null);
const r3 = ref(null);

let chart1, chart2, chart3;

const props = defineProps({
  data: {
    type: Array,
    default: () => [],
  },
});

const render = () => {
  const d = props.data || [];
  if (!d || d.length === 0) return;

  const positive = d.find((i) => i.label === "正面");
  const neutral = d.find((i) => i.label === "中性");
  const negative = d.find((i) => i.label === "负面");

  const indicator = [
    { name: "转发", max: 250 },
    { name: "评论", max: 250 },
    { name: "点赞", max: 250 },
  ];

  const opt = {
    tooltip: {
      trigger: "item",
    },
    radar: {
      indicator,
      alignTicks: false,
      axisLabel: { show: false },
      splitLine: { lineStyle: { color: "rgba(255,255,255,0.2)" } },
    },
  };

  // 正面
  chart1.setOption({
    title: { text: "正面情感", textStyle: { color: "#52c41a" } },
    ...opt,
    series: [
      {
        type: "radar",
        data: [
          {
            value: [
              positive?.avgRepost || 0,
              positive?.avgComment || 0,
              positive?.avgAttitude || 0,
            ],
            areaStyle: { color: "#52c41a55" },
          },
        ],
        itemStyle: { color: "#52c41a" },
      },
    ],
  });

  // 中性
  chart2.setOption({
    title: { text: "中性情感", textStyle: { color: "#ccc" } },
    ...opt,
    series: [
      {
        type: "radar",
        data: [
          {
            value: [neutral?.avgRepost || 0, neutral?.avgComment || 0, neutral?.avgAttitude || 0],
            areaStyle: { color: "blue" },
          },
        ],
        itemStyle: { color: "blue" },
      },
    ],
  });

  // 负面
  chart3.setOption({
    title: { text: "负面情感", textStyle: { color: "#ff4d4f" } },
    ...opt,
    series: [
      {
        type: "radar",
        data: [
          {
            value: [
              negative?.avgRepost || 0,
              negative?.avgComment || 0,
              negative?.avgAttitude || 0,
            ],
            areaStyle: { color: "#ff4d4f55" },
          },
        ],
        itemStyle: { color: "#ff4d4f" },
      },
    ],
  });
};

onMounted(() => {
  chart1 = echarts.init(r1.value);
  chart2 = echarts.init(r2.value);
  chart3 = echarts.init(r3.value);
  render();

  window.addEventListener("resize", () => {
    chart1?.resize();
    chart2?.resize();
    chart3?.resize();
  });
});

watch(
  () => props.data,
  () => render(),
  { deep: true },
);

onUnmounted(() => {
  chart1?.dispose();
  chart2?.dispose();
  chart3?.dispose();
});
</script>

<style scoped>
.radar-wrapper {
  display: flex;
  gap: 10px;
  width: 100%;
  height: 320px;
  background: rgba(4, 8, 16);
  border-radius: 8px;
  padding: 10px;
}
.radar-box {
  flex: 1;
  height: 100%;
}
</style>
