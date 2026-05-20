<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  }
})

const chartRef = ref(null)
let myChart = null

const renderChart = () => {
  if (!myChart || !props.data.length) return

  // 提取所有日期并排序
  const dateList = [...new Set(props.data.map(item => item.dt))].sort()

  // 按日期装填 正面/中性/负面 占比
  const positiveRatio = []
  const neutralRatio = []
  const negativeRatio = []

  dateList.forEach(date => {
    const dayData = props.data.filter(d => d.dt === date)
    positiveRatio.push(dayData.find(i => i.label === '正面')?.ratio || 0)
    neutralRatio.push(dayData.find(i => i.label === '中性')?.ratio || 0)
    negativeRatio.push(dayData.find(i => i.label === '负面')?.ratio || 0)
  })

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function (params) {
        let tip = params[0].axisValue + '<br/>'
        params.forEach(p => {
          tip += `${p.seriesName}：${p.data}%<br/>`
        })
        return tip
      }
    },
    legend: {
      data: ['中性', '正面', '负面'],
      textStyle: { color: '#fff' },
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '12%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dateList,
      axisLabel: { color: '#fff', rotate: 25 },
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } }
    },
    yAxis: {
      type: 'value',
      max: 100,
      name: '占比(%)',
      nameTextStyle: { color: '#fff' },
      axisLabel: { color: '#fff', formatter: '{value}%' },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } }
    },
    series: [
      {
        name: '中性',
        type: 'line',
        stack: 'total',
        smooth: true,
        itemStyle: { color: '#409eff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.7)' },
            { offset: 1, color: 'rgba(64,158,255,0.1)' }
          ])
        },
        data: neutralRatio
      },
      {
        name: '正面',
        type: 'line',
        stack: 'total',
        smooth: true,
        itemStyle: { color: '#52c41a' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(82,196,26,0.7)' },
            { offset: 1, color: 'rgba(82,196,26,0.1)' }
          ])
        },
        data: positiveRatio
      },
      {
        name: '负面',
        type: 'line',
        stack: 'total',
        smooth: true,
        itemStyle: { color: '#ff4d4f' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255,77,79,0.7)' },
            { offset: 1, color: 'rgba(255,77,79,0.1)' }
          ])
        },
        data: negativeRatio
      }
    ]
  }

  myChart.setOption(option)
}

onMounted(() => {
  myChart = echarts.init(chartRef.value)
  renderChart()
})

watch(() => props.data, renderChart, { deep: true })

onUnmounted(() => {
  myChart?.dispose()
})
</script>

<template>
  <div ref="chartRef" class="trend-chart"></div>
</template>

<style scoped>
.trend-chart {
  width: 100%;
  height: 420px;
  background: rgba(4, 8, 16);
  border-radius: 10px;
  padding: 15px;
}
</style>
