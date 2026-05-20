<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import * as echarts from 'echarts';

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  }
});

const chartRef = ref(null);
let myChart = null;

const renderChart = () => {
  if (!myChart || !props.data.length) return;
  const list = props.data;
  const authList = [...new Set(list.map(v => v.userAuthentication))];

  let posArr = [], neuArr = [], negArr = [];
  authList.forEach(auth => {
    const group = list.filter(v => v.userAuthentication === auth);
    const total = group.reduce((s, i) => s + i.value, 0);
    if(total === 0){
      posArr.push(0);neuArr.push(0);negArr.push(0);
      return;
    }
    const pos = group.find(i=>i.label==='正面')?.value||0
    const neu = group.find(i=>i.label==='中性')?.value||0
    const neg = group.find(i=>i.label==='负面')?.value||0
    posArr.push(Number((pos/total*100).toFixed(1)))
    neuArr.push(Number((neu/total*100).toFixed(1)))
    negArr.push(Number((neg/total*100).toFixed(1)))
  })

  const option = {
    tooltip:{
      trigger:'axis',
      axisPointer:{type:'shadow'},
      formatter:p=>{
        let str = p[0].axisValue+'<br>'
        p.forEach(item=>str+=`${item.seriesName}：${item.data}%<br>`)
        return str
      }
    },
    legend:{
      data:['正面','中性','负面'],
      textStyle:{color:'#fff'},
      top:10
    },
    grid:{left:'3%',right:'6%',bottom:'3%',containLabel:true},
    xAxis:{
      type:'value',
      max:100,
      axisLabel:{color:'#fff',formatter:'{value}%'},
      splitLine:{lineStyle:{color:'rgba(255,255,255,0.1)'}},
      axisLine:{lineStyle:{color:'rgba(255,255,255,0.3)'}}
    },
    yAxis:{
      type:'category',
      data:authList,
      axisLabel:{color:'#fff'},
      axisLine:{lineStyle:{color:'rgba(255,255,255,0.3)'}}
    },
    series:[
      {name:'正面',type:'bar',stack:'total',data:posArr,itemStyle:{color:'#52c41a'}},
      {name:'中性',type:'bar',stack:'total',data:neuArr,itemStyle:{color:'#409eff'}},
      {name:'负面',type:'bar',stack:'total',data:negArr,itemStyle:{color:'#ff4d4f'}}
    ]
  }
  myChart.setOption(option)
}

onMounted(()=>{
  myChart = echarts.init(chartRef.value)
  renderChart()
  window.addEventListener('resize',()=>myChart.resize())
})
watch(()=>props.data,renderChart,{deep:true})
onUnmounted(()=>myChart?.dispose())
</script>

<template>
  <div ref="chartRef" class="chart-box"></div>
</template>

<style scoped>
.chart-box{
  width:100%;
  height:420px;
  background:rgba(4,8,16);
  border-radius:10px;
  padding:15px;
}
</style>
