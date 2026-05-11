<script setup>
import UpNavComponent from '@/component/upnav.vue'
defineOptions({
  name: 'UserProfile',
})
import { provide } from 'vue'
// 提供项目名称
const name = 'User Profile'
provide('name', name)
</script>

<template>
  <div>
    <UpNavComponent/>
    <h1>User Profile</h1>
  </div>
</template>

<style scoped>

</style>
