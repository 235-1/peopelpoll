<template>
  <div ref="chart" class="china-map-container"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import geoJson from '@/assets/map/100000_full.json'


const chart = ref(null)
let myChart = null
let resizeHandler = null

const props = defineProps({
  mapHotData: {
    type: Array,
    default: () => []
  }
})

// 对数据处理 name:region value：hot
const processData = (data) => {
  if (!data) return []
  return data.map(item => ({
    name: item.region,
    value: item.hot,
    ...item
  }))
}

// 定义节流
const throttle = (func, delay) => {
  // 定义一个定时器
  let timer = null
  // 返回一个新的包装函数
  const func1 =(...args) => {
    // 如果定时器是开的 则直接返回
    if(timer) return
    // 否则 执行函数 并设置定时器
    func(...args)
    timer = setTimeout(() => {
      timer = null
    }, delay)
  }

  return func1
}



// 渲染图表
const renderChart = () => {
  if (!myChart) return
  const data = processData(props.mapHotData) || []

  const option = {
    animation: true, // 开启动画
    title: {
      text: '地区活跃度',
      left: 'center',
      top: '20px',
      textStyle: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#fff',
        shadowColor: 'rgba(0,0,0,0.4)',
        shadowBlur: 2
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: function (params) {
        if (!params.data) return '暂无数据'
        return `省份：${params.name}<br/>热度：${params.value}`
      }

    },
    visualMap: {
      min: 0,
      max: 100,
      left: 'left',
      bottom: '15%',
      calculable: true,
      inRange: { color: ['#e0f7fa', '#80deea', '#ffcc80', 'red'] },
      textStyle: { color: '#666' },
      handleColor: '#ff8a65',
    },
    geo: {
      map: 'china',
      roam: false,
      zoom: 1.3,
      center: [105.000, 30.000],
      label: {
        show: true,
        fontSize: 10,
        color: 'primary',
      },
      itemStyle: {
        // areaColor: 'rgba(40, 45, 60, 1)',
        areaColor: 'rgba(40, 45, 60, 0.8)',
        borderColor: '#999',
        borderWidth: 1.2,
      },
      emphasis: {
        itemStyle: { areaColor: '#9e9e9e' },
        label: { color: '#fff' },
      }
    },
    series: [{
      type: 'map',
      map: 'china',
      geoIndex: 0,
      data: data,
      label: { show: false },
    }]
  }

  myChart.setOption(option, false)
}

// 包装renderChart函数
const renderChartThrottle = throttle(renderChart, 500)

// 初始化
onMounted(() => {
  if (!chart.value) return
  myChart = echarts.init(chart.value)
  echarts.registerMap('china', geoJson)
  renderChart()

  resizeHandler = () => myChart?.resize()
  window.addEventListener('resize', resizeHandler)
})

// 监听数据更新
watch(() => props.mapHotData, () => {
  renderChartThrottle()
}, { deep: true })

// 销毁
onUnmounted(() => {
  window.removeEventListener('resize', resizeHandler)
  myChart?.dispose()
})
</script>

<style scoped>
.china-map-container {
  width: 100%;
  height: 1000px;
  background-color: rgba(4, 8, 16);
  border-radius: 8px;
  overflow: hidden;
}
</style>
