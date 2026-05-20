<!--
主要用于展示微博的整体数据统计情况
1、 给 ECharts 准备一个有宽高的容器
2、 初始化 ECharts 实例  在生命周期中初始化mounted
3、 配置图表

-->
// 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口。
<script setup>
import PieComponent from "@/component/pieAndLine.vue";
import UpTotal from "@/component/upTotal.vue";
import MapHotComponent from "@/component/mapHot.vue";
import { getWeiboTotalStatistic, getWeiboTotalStatisticByAuth, getMapHotData } from "@/api/totalStatistic";
import { onMounted, ref, onUnmounted } from "vue";
import FooterComponent from "@/component/footer.vue";

defineOptions({
  name: "TotalWeiboStatistic",
});
// 请求图表数据
const data = ref(null);
const userAuthData = ref(null);
const mapHotData = ref(null);
const totalData = ref(null);
let timer = null;
// 加载数据
const load = async () => {
  const res = await getWeiboTotalStatistic();
  const res2 = await getWeiboTotalStatisticByAuth();
  const res3 = await getMapHotData();
  data.value = res.data;
  userAuthData.value = res2.data;
  mapHotData.value = res3.data;
  // 微博总数 从微博整体数据统计中获取
  totalData.value = res2.data.map((item) => item.count).reduce((a, b) => a + b, 0);
  console.log("微博整体数据统计:", data.value);
  console.log("按用户认证状态统计:", userAuthData.value);
  console.log("地图热点数据:", mapHotData.value);
  console.log("微博总数:", totalData.value);
};

onMounted(() => {
  if(timer) clearInterval(timer);
  load();

  // 每 3 秒自动刷新一次数据
  timer = setInterval(() => {
    load();
  }, 10000);

  // 页面离开时清除定时器
});
onUnmounted(() => clearInterval(timer)

);
</script>
<template>
  <div class="container">
    <UpTotal :totalData="totalData"></UpTotal>
    <MapHotComponent :mapHotData="mapHotData"></MapHotComponent>
    <PieComponent :data="data" :userAuthData="userAuthData"></PieComponent>
  </div>

  <FooterComponent></FooterComponent>
</template>

<style scoped></style>
