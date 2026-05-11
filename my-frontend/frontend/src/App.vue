<script setup>
import { ref } from 'vue'


import { onMounted, provide } from 'vue'
/* 请求项目列表 */
import { getAll } from '@/api/ProjectMemApi.js'
const projectList = ref([])
/* 获取项目列表 */
const useProjectList = async () => {
  const res = await getAll()
  projectList.value = res.data

  console.log(res)
}
/* 提供列表传递给子组件 */
provide('projectList', projectList)
console.log(projectList.value)
onMounted(() => {
   useProjectList()
})
</script>

<template>
  <RouterView></RouterView>
</template>

<style scoped>

</style>



