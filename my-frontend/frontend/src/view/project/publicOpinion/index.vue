<script setup>
import UpNavComponent from '@/component/upnav.vue'
defineOptions({
  name: 'PublicOpinion',
})
import { provide } from 'vue'
// 提供项目名称
const name = 'Public Opinion'
provide('name', name)
</script>

<template>
  <div>
    <UpNavComponent/>
    <h1>Public Opinion</h1>
  </div>
</template>

<style scoped>

</style>
