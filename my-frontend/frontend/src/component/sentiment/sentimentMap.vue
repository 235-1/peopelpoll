<template>
  <div class="chart-wrapper">
    <!-- 左侧地图 -->
    <div ref="chartMap" class="map-chart"></div>
    <!-- 右侧条形图 -->
    <div ref="chartBar" class="bar-chart"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import geoJson from '@/assets/map/100000_full.json'

const chartMap = ref(null)
const chartBar = ref(null)
let myMap = null
let myBar = null
let resizeHandler = null

const props = defineProps({
  mapHotData: {
    type: Array,
    default: () => []
  }
})

// 数据处理
const processMapData = (data) => {
  if (!data) return []
  return data.map(item => ({
    name: item.region,
    value: item.avgScore,
    count: item.count
  }))
}

// 条形图数据（按得分排序）
const processBarData = (data) => {
  if (!data) return []
  return [...data]
    .sort((a, b) => a.avgScore - b.avgScore)
    .map(item => ({
      name: item.region,
      value: item.avgScore
    }))
}

// 节流
const throttle = (func, delay) => {
  let timer = null
  return (...args) => {
    if (timer) return
    func(...args)
    timer = setTimeout(() => timer = null, delay)
  }
}

// 渲染地图
const renderMap = () => {
  if (!myMap) return
  const data = processMapData(props.mapHotData)

  const option = {
    title: {
      text: '地区情感倾向分布',
      left: 'center',
      top: 20,
      textStyle: { fontSize: 20, color: '#fff' }
    },
    tooltip: {
      trigger: 'item',
      formatter: params => {
        if (!params.data) return '暂无数据'
        return `省份：${params.name}<br/>情感分：${params.value.toFixed(2)}`
      }
    },
    visualMap: {
      min: -1,
      max: 1,
      left: 20,
      bottom: '35%',
      calculable: true,
      inRange: { color: ['#ff4d4f', '#ccc', '#52c41a'] },
      textStyle: { color: '#fff' }
    },
    geo: {
      map: 'china',
      roam: false,
      zoom: 1.1,
      center: [105, 30],
      label: { show: true, color: '#fff' },
      itemStyle: { areaColor: 'rgba(40,45,60,0.8)', borderColor: '#999' }
    },
    series: [{
      type: 'map',
      map: 'china',
      geoIndex: 0,
      data
    }]
  }

  myMap.setOption(option)
}

// 渲染条形图
const renderBar = () => {
  if (!myBar) return
  const list = processBarData(props.mapHotData)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: { left: 15, right: 50, top: 60, bottom: 20 },
    xAxis: {
      type: 'value',
      min: -1,
      max: 1,
      axisLine: { lineStyle: { color: '#fff' } },
      axisLabel: { color: '#fff' }
    },
    yAxis: {
      type: 'category',
      data: list.map(i => i.name),
      axisLine: { lineStyle: { color: '#fff' } },
      axisLabel: { color: '#fff', fontSize: 10 }
    },
    series: [{
      type: 'bar',
      data: list.map(i => i.value),
      itemStyle: {
        color: params => {
          const val = params.data
          if (val < 0) return '#ff4d4f'
          if (val > 0) return '#52c41a'
          return '#ccc'
        }
      }
    }]
  }

  myBar.setOption(option)
}


const connectHover = () => {
  // 地图 hover  条形图高亮
  myMap.on('mouseover', (params) => {
    const idx = processBarData(props.mapHotData).findIndex(d => d.name === params.name)
    if (idx !== -1) {
      myBar.dispatchAction({ type: 'downplay' })
      myBar.dispatchAction({ type: 'highlight', seriesIndex: 0, dataIndex: idx })
    }
  })

  // 地图移出  取消高亮
  myMap.on('mouseout', () => {
    myBar.dispatchAction({ type: 'downplay' })
  })

  // 条形图 hover  地图高亮
  myBar.on('mouseover', (params) => {
    const idx = processMapData(props.mapHotData).findIndex(d => d.name === params.name)
    if (idx !== -1) {
      myMap.dispatchAction({ type: 'downplay' })
      myMap.dispatchAction({ type: 'highlight', seriesIndex: 0, dataIndex: idx })
    }
  })

  // 条形图移出  取消高亮
  myBar.on('mouseout', () => {
    myMap.dispatchAction({ type: 'downplay' })
  })
}

// 初始化
onMounted(() => {
  myMap = echarts.init(chartMap.value)
  myBar = echarts.init(chartBar.value)
  echarts.registerMap('china', geoJson)

  renderMap()
  renderBar()
  connectHover() // 改用悬浮联动

  resizeHandler = throttle(() => {
    myMap.resize()
    myBar.resize()
  }, 300)
  window.addEventListener('resize', resizeHandler)
})

watch(() => props.mapHotData, () => {
  renderMap()
  renderBar()
}, { deep: true })

onUnmounted(() => {
  window.removeEventListener('resize', resizeHandler)
  myMap.dispose()
  myBar.dispose()
})
</script>

<style scoped>
.chart-wrapper {
  display: flex;
  width: 100%;
  height: 800px;
  background-color: rgba(4, 8, 16);
  border-radius: 8px;
  overflow: hidden;
  padding: 10px;
  gap: 10px;
}

.map-chart {
  flex: 7;
  height: 100%;
}

.bar-chart {
  flex: 3;
  height: 100%;
}
</style>
