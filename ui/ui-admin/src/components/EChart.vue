<script setup>
  // 通用ECharts图表组件：父组件传入option即可渲染，option变化自动更新、窗口缩放自动自适应
  import * as echarts from 'echarts'
  import {onBeforeUnmount, onMounted, ref, watch} from 'vue'

  // 组件入参：option为echarts配置项，height为图表容器高度
  const props = defineProps({
    option: {
      type: Object,
      required: true
    },
    height: {
      type: String,
      default: '300px'
    }
  })

  //图表挂载的dom容器
  const chartRef = ref()
  //echarts实例（不需要响应式，用普通变量即可）
  let chart = null

  //窗口缩放时让图表自适应宽度
  const handleResize = () => {
    if (chart) {
      chart.resize()
    }
  }

  onMounted(() => {
    //初始化图表并应用配置
    chart = echarts.init(chartRef.value)
    chart.setOption(props.option)
    window.addEventListener('resize', handleResize)
  })

  //监听option变化：父组件异步拿到数据后重新赋值option，图表随之更新
  watch(() => props.option, (newOption) => {
    if (chart) {
      chart.setOption(newOption)
    }
  }, {deep: true})

  onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    //销毁实例，防止内存泄漏
    if (chart) {
      chart.dispose()
    }
  })
</script>

<template>
  <div ref="chartRef" :style="{width: '100%', height: height}"/>
</template>