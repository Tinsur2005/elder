<!--
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
-->
<script setup lang="ts">
  import {getCurrentInstance, ref} from 'vue'
  import type {ComponentInternalInstance} from 'vue'
  const {appContext: {app: {config: {globalProperties}}}} = getCurrentInstance() as ComponentInternalInstance
  interface Props {
    modelValue: string
  }
  const props = defineProps<Props>()
  const emits = defineEmits(['update:modelValue'])

  //控制弹窗显隐，选择完图标后关闭
  const popoverVisible = ref(false)

  //选中图标：把值传给父组件，并关闭弹窗
  const handleIconClick = (icon: String) => {
    emits('update:modelValue', icon)
    popoverVisible.value = false
  }
</script>

<template>
  <el-popover trigger="click" :width="450" popper-class="icon-picker-popover" v-model:visible="popoverVisible">
    <template #reference>
      <el-button style="width: 150px" :icon="modelValue">{{ modelValue || '点击此处选择图标' }}</el-button>
    </template>
    <div class="el-icon-picker">
      <component v-for="icon in globalProperties.$icons" :key="icon"
                 :class="[icon, 'icon', { 'icon-active': icon == modelValue }]"
                 :is="icon"
                 @click="handleIconClick(icon)">
      </component>
    </div>
  </el-popover>
</template>

<style scoped>
</style>

<!-- 这里的样式去掉 scoped：
     因为 el-popover 的内容会被 teleport 到 body 渲染，
     加 scoped 会导致宽度等样式在弹窗内容上不生效 -->
<style>
  /* 强制锁死 popper 宽度：el-popover 的 :width 在当前版本不一定生效，
     必须通过 popper-class 覆写到 .el-popper 上才算数 */
  .icon-picker-popover.el-popper {
    width: 450px !important;
  }

  .el-icon-picker {
    width: 100%;
    height: 260px;
    overflow-y: auto;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 8px;
    background-color: #fff;
    box-sizing: border-box;
  }

  .icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 26px;
    height: 26px;
    color: var(--el-text-color-regular);
    font-size: 18px;
    border-radius: 4px;
    cursor: pointer;
  }

  .icon:hover {
    color: var(--el-color-primary);
  }

  .icon-active {
    color: var(--el-color-primary);
  }
</style>