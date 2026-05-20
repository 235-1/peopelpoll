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
import * as echarts from "echarts";
import { onMounted, onUnmounted, ref, watch } from "vue";
defineOptions({
  name: "TotalWeiboStatistic",
});
// 接收父组件传递过来的数据
const prop = defineProps({
  data: {
    type: Array,
    default: () => [], // 规则：默认值为空数组
  },
  userAuthData: {
    type: Array,
    default: () => [], // 规则：默认值为空数组
  },
});

// 配置节流防抖  func是需要防抖的函数  delay是防抖的时间间隔
// 防抖函数：在事件触发后，等待 delay 时间，只执行一次函数
// const debounce = (func, delay) => {
//   let timer = null;
//   return function (...args) {
//     if (timer) clearTimeout(timer); //每次触发，都把上一次计时清掉

//     timer = setTimeout(() => {
//       func.apply(this, args);
//     }, delay);
//   };
// };
//节流函数：在事件触发后，只执行一次函数，后续事件触发后，等待 delay 时间，再执行一次函数
const throttle = (func, delay) => {
  let timer = null;
  return function (...args) {
    if (timer) return; // 时间没到拒绝执行
    timer = setTimeout(() => {
      func.apply(this, args);
      timer = null;
    }, delay);
  };
};

// 鼠标事件

/* 拿到容器 */
const pieAndLine = ref(null);
/* 初始化 ECharts 实例 */
let myChart = null;
onMounted(() => {
  myChart = echarts.init(pieAndLine.value, "dark");
});

onUnmounted(() => {
  if (myChart) myChart.dispose();
  myChart = null;
});
//监听data变化
watch(
  () => [prop.data, prop.userAuthData],
  ([data, userAuthData]) => {
    if (!myChart) return;   // 图表实例不存在，直接返回
    // 图表实例存在，继续配置图表
    myChart.clear();

    const authData = data || [];
    const allValues = [];

    // 跳过表头，遍历所有数字
    for (let i = 1; i < authData.length; i++) {
      const row = authData[i] || [];
      for (let j = 1; j < row.length; j++) {
        const num = Number(row[j]);
        if (!isNaN(num)) allValues.push(num);
      }
    }

    const maxY = allValues.length > 0 ? Math.max(...allValues) : 100;
    if (data && userAuthData) {
      /* 配置图表 */
      myChart.showLoading();
      const option = {
        animation: true, // 开启动画
        progressive: 2000, // 渐进式渲染
        progressiveThreshold: 5000, // 超过该值启用渐进式渲染
        // 配置图的公共属性
        // 配置图例
        legend: {},
        // 配置标题 依靠top和left属性来指定标题的位置
        title: [
          // 指定位置配置用户认证统计标题
          {
            text: "用户认证统计",
            top: "center",
            left: "70%",
            right: "30%",
            textStyle: {
              color: "#fff",
            },
          },
          // 指定位置配置用户活跃度统计标题
          {
            text: "用户活跃度统计",

            textStyle: {
              color: "#fff",
            },
            top: "center",
            left: "20%",
            right: "80%",
          },
        ],
        // 配置提示框
        tooltip: {
          trigger: "axis", // 在x轴上触发提示框 也就是折线图才有提示框
          // trigger: "item", // 点击触发提示框 扇形图
          // showContent: false,
        },
        // 配置数据
        dataset: [
          { source: prop.data }, // 表格数据
          { source: prop.userAuthData },
        ],
        // 配置x轴 type 配置x轴的类型 category 分类轴 value 数值轴
        // 表格数据的第一行就是 x轴的分类
        xAxis: { type: "category" },
        // 配置y轴 type 配置y轴的类型 value 数值轴默认
        yAxis: {
          gridIndex: 0,
          max: maxY, // 取得第二个表的最大值
          splitLine: {
            lineStyle: {
              color: "rgba(255,255,255,0.3)", // 配置网格线颜色
            },
          },
        },
        // 配置网格用于调整折线图的位置和大小
        grid: {
          top: "55%",
          bottom: "10%",
          left: "10%",
          right: "10%", // 调整边距，避免刻度文字被截断
        },
        // 配置六条折线图
        series: [
          // 第1行数据 → 个人红V
          // name 配置折线图的名称
          // type 配置折线图的类型
          // smooth 配置折线图是否平滑
          // seriesLayoutBy 配置折线图的布局方式
          // row 行布局 column 列布局 这决定读取数据的方式
          { name: "个人红V", datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },

          // 第2行数据 → 个人金V
          { name: "个人金V", datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },

          // 第3行数据 → 个人黄V
          { name: "个人黄V", datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },

          // 第4行数据 → 官方媒体(蓝V)
          {
            name: "官方媒体(蓝V)",
            datasetIndex: 0,
            type: "line",
            smooth: true,
            seriesLayoutBy: "row",
          },

          // 第5行数据 → 普通用户
          { name: "普通用户", datasetIndex: 0, type: "line", smooth: true, seriesLayoutBy: "row" },

          // 第6行数据 → 未认证用户
          {
            name: "未认证用户",
            datasetIndex: 0,
            type: "line",
            smooth: true,
            seriesLayoutBy: "row",
          },
          // 配置两个饼图
          {
            type: "pie",
            datasetIndex: 0,
            id: "pie", // 配置饼图id 用于鼠标联动
            radius: "30%", // 配置饼图半径
            center: ["25%", "25%"], // 配置饼图中心位置
            emphasis: {
              // 配置饼图选中时的样式
              focus: "self", // 选中时的样式是鼠标放上去时 只显示选中的部分
            },
            label: {
              // 扇形图显示的文字{b}扇形名字 {@1} = 第 1 列数值 {d} = 百分比
              formatter: "{b}\n数量：{@1}  占比：{d}%",
              color: "#fff",
              fontSize: 12,
            },
            // 配置饼图 数据 配和 dataset表数据使用
            encode: {
              // 配置饼图 数据 配和 dataset表数据使用
              itemName: 0, // 告诉了label{b}名字在哪里
              value: 1, // 默认显示第 1 列 数值从第1列开始
              tooltip: 1, // 默认显示第 1 列
            },
          },
          {
            type: "pie",
            id: "pie2", // 配置饼图id
            title: "用户认证统计",
            datasetIndex: 1,
            radius: "30%",
            center: ["75%", "25%"], // 水平 和 垂直 饼图中心位置
            emphasis: {
              focus: "self",
            },
            // 配置饼图标签 默认显示
            label: {
              formatter: "{b}\n数量：{@1}\n占比：{d}%",
              color: "#fff",
              fontSize: 12,
            },
            encode: {
              itemName: 0,
              value: 1, // 默认显示第 1 列 数值从第1列开始
              tooltip: 1, // 默认显示第 1 列
            },
          },
        ],
      };
      /* 配置图表事件 跟随鼠标x轴两图联动 */
      const update = (event) => {
        const xAxisInfo = event.axesInfo[0];
        if (xAxisInfo) {
          const dimension = xAxisInfo.value + 1;
          myChart.setOption(
            {
              series: {
                id: "pie",
                label: {
                  formatter: "{b}\n数量：{@" + dimension + "}占比：{d}%",
                  color: "#fff",
                  fontSize: 12,
                },
                encode: {
                  itemName: 0,
                  value: dimension,
                  tooltip: dimension,
                },
              },
            },
            false, // 只更饼图
          );
        }
      };
      // 节流
      const throttleUpdate = throttle(update, 30);

      // 鼠标事件
      // 3. 先清旧事件，再绑定
      myChart.off("updateAxisPointer");
      myChart.on("updateAxisPointer", throttleUpdate);

      // true 表示刷新图表
      myChart.setOption(option, true);
      myChart.hideLoading();
    }
  },
  { deep: true, immediate: true },
);
</script>
<template>
  <div class="container">
    <div ref="pieAndLine" class="container" style="width: 100%; height: 800px"></div>
    <!-- <div ref="pie" style="height: 1000px; width: 50%"></div> -->
  </div>
</template>

<style scoped>
/* .container {
  display: flex;
  flex-direction: row;
} */
</style>
