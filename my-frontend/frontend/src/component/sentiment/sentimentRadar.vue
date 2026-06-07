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

defineOptions({
  name: "SentimentRadarComponent",
});

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
    { name: "转发", max: 400 },
    { name: "评论", max: 400 },
    { name: "点赞", max: 400 },
  ];

  // ====== 核心修改：大幅提亮雷达图的骨架网格线 ======
  const opt = {
    backgroundColor: "transparent",
    tooltip: {
      trigger: "item",
      backgroundColor: "rgba(11, 19, 36, 0.95)", // 略微加深提示框，强化对比
      borderColor: "#00bfff",
      borderWidth: 1,
      textStyle: { color: "#fff" }
    },
    radar: {
      indicator,
      alignTicks: false,
      radius: "60%",
      center: ["50%", "62%"],
      startAngle: 30,
      axisName: {
        color: "#fff", // 【提亮】文字直接使用纯白，在科技蓝底色上最清晰
        fontSize: 12,
        fontWeight: "bold",
        padding: [2, 5]
      },
      // 【关键提亮】大幅提升雷达图圈线的可见度，使用科技青偏蓝的半透明线
      splitLine: {
        lineStyle: {
          color: "rgba(0, 229, 255, 0.25)",
          width: 1
        }
      },
      // 给网格层级铺上明显的有迹可循的明暗交替底色
      splitArea: {
        areaStyle: {
          color: ["rgba(255, 255, 255, 0.02)", "rgba(255, 255, 255, 0.06)"]
        }
      },
      // 【关键提亮】中心向四周散射的骨架轴线
      axisLine: {
        lineStyle: {
          color: "rgba(0, 229, 255, 0.3)",
          width: 1.5 // 稍微加粗轴线
        }
      }
    },
  };

  // 正面
  chart1.setOption({
    ...opt,
    title: {
      text: "正面情感互动",
      top: "8%",
      left: "center",
      textStyle: { color: "#52c41a", fontSize: 14, fontWeight: "bold" }
    },
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
            // 【提亮】加大区域填充的浓度，形成实体色块
            areaStyle: { color: "rgba(82, 196, 26, 0.35)" },
          },
        ],
        itemStyle: { color: "#52c41a" },
        // 【提亮】使用 100% 纯度不透明的加粗线条，去掉虚化的阴影
        lineStyle: {
          width: 3,
          color: "#52c41a",
          opacity: 1
        },
        symbol: "circle",
        symbolSize: 7, // 放大锚点
        emphasis: {
          lineStyle: { width: 4 },
          itemStyle: { scale: true, symbolSize: 9 }
        }
      },
    ],
  }, true);

  // 中性
  chart2.setOption({
    ...opt,
    title: {
      text: "中性情感互动",
      top: "8%",
      left: "center",
      textStyle: { color: "#cbd5e1", fontSize: 14, fontWeight: "bold" } // 中性标题同步稍微提亮
    },
    series: [
      {
        type: "radar",
        data: [
          {
            value: [
              neutral?.avgRepost || 0,
              neutral?.avgComment || 0,
              neutral?.avgAttitude || 0
            ],
            areaStyle: { color: "rgba(203, 213, 225, 0.3)" },
          },
        ],
        itemStyle: { color: "#cbd5e1" },
        lineStyle: {
          width: 3,
          color: "#cbd5e1",
          opacity: 1
        },
        symbol: "circle",
        symbolSize: 7,
        emphasis: {
          lineStyle: { width: 4 },
          itemStyle: { scale: true, symbolSize: 9 }
        }
      },
    ],
  }, true);

  // 负面
  chart3.setOption({
    ...opt,
    title: {
      text: "负面情感互动",
      top: "8%",
      left: "center",
      textStyle: { color: "#ff4d4f", fontSize: 14, fontWeight: "bold" }
    },
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
            areaStyle: { color: "rgba(255, 77, 79, 0.35)" },
          },
        ],
        itemStyle: { color: "#ff4d4f" },
        lineStyle: {
          width: 3,
          color: "#ff4d4f",
          opacity: 1
        },
        symbol: "circle",
        symbolSize: 7,
        emphasis: {
          lineStyle: { width: 4 },
          itemStyle: { scale: true, symbolSize: 9 }
        }
      },
    ],
  }, true);
};

onMounted(() => {
  chart1 = echarts.init(r1.value, "dark");
  chart2 = echarts.init(r2.value, "dark");
  chart3 = echarts.init(r3.value, "dark");
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
  gap: 15px;
  width: 100%;
  height: 340px;
  background: rgba(11, 19, 36, 0.9);
  border: 1px solid #1e293b;
  box-shadow: 0 0 15px rgba(0, 191, 255, 0.05);
  border-radius: 12px;
  padding: 15px;
  box-sizing: border-box;
}

.radar-box {
  flex: 1;
  height: 100%;
}
</style>
