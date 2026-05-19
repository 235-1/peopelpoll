<script setup>
import { computed } from 'vue'

const props = defineProps({
  totalData: {
    type: Number,
    default: 0,
  },
});

// 把数字拆分成单个数字数组
const numArr = computed(() => {
  return String(props.totalData || 0).split('')
})
</script>

<template>
  <!-- 修复：Element 只支持 always / hover / never -->
  <el-card class="total-card" shadow="never">
    <div class="card-title">当前统计微博总数</div>
    <div class="flip-board">
      <span
        class="flip-item"
        v-for="(item, index) in numArr"
        :key="index"
      >
        {{ item }}
      </span>
    </div>
  </el-card>
</template>

<style scoped>
/* 黑色主题卡片 */
.total-card {
  --el-card-bg-color: #141824;
  --el-card-border-color: #2a2f3f;
  text-align: center;
  padding: 20px 10px;
  border-radius: 5px;
}

:deep(.el-card__body) {
  padding: 0;
}

/* 标题 */
.card-title {
  font-size: 18px;
  color: #d1d5db;
  margin-bottom: 20px;
  letter-spacing: 2px;
}

/* 翻牌容器 */
.flip-board {
  display: flex;
  justify-content: center;
  gap: 8px;
}

/* 单个数字翻牌样式（黑色主题大屏专用） */
.flip-item {
  width: 58px;
  height: 63px;
  line-height: 63px;
  font-size: 42px;
  font-weight: bold;
  font-family: monospace;
  color: #ffffff;
  background: #0f141f;
  border-radius: 8px;
  border: 1px solid #374151;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.5);
  text-align: center;
}
</style>
