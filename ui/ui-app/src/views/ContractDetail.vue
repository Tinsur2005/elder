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
<script setup>
  import {ref} from 'vue'
  import {showToast} from 'vant'
  import {useRoute, useRouter} from 'vue-router'
  import contractApi from '@/api/contract.js'

  const route = useRoute()
  const router = useRouter()

  // ================== 对象 ==================

  //合同对象
  const contract = ref({})

  // ================== 选项 ==================

  // 合同类型选项（类型：0服务合同 1入住合同 2其他）
  const contractTypeOptions = [
    {value: 0, label: '服务合同'},
    {value: 1, label: '入住合同'},
    {value: 2, label: '其他'},
  ]

  //合同状态颜色（与合同列表页的推导规则一致）
  const statusColors = {
    '待签订': '#999999',
    '已过期': '#EE0A24',
    '待生效': '#1989FA',
    '生效中': '#07C160',
  }

  // ================== 下拉数据 ==================

  //加载合同详情
  const loadContract = () => {
    contractApi.selectById(route.query.id).then(result => {
      if (result.code === 1) {
        contract.value = result.data
      }
    })
  }

  loadContract()

  // ================== 方法 ==================

  //根据合同类型获取展示文本
  const getContractType = (value) => {
    return contractTypeOptions.find(option => option.value === value)?.label || '-'
  }

  //合同状态不由数据库存储，根据签订时间与过期时间推导（规则与后台管理端保持一致）
  const getContractStatus = (row) => {
    const now = new Date()
    if (!row.signTime) {
      return {label: '待签订', color: statusColors['待签订']}
    }
    if (row.expireTime && new Date(row.expireTime) < now) {
      return {label: '已过期', color: statusColors['已过期']}
    }
    if (new Date(row.signTime) > now) {
      return {label: '待生效', color: statusColors['待生效']}
    }
    return {label: '生效中', color: statusColors['生效中']}
  }

  //查看合同附件
  const viewFile = () => {
    if (!contract.value.fileUrl) {
      showToast('暂无合同附件')
      return
    }
    window.open(contract.value.fileUrl)
  }
</script>

<template>
  <div class="contract-detail">
    <van-nav-bar title="合同详情" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 合同基本信息 -->
    <div class="detail-card">
      <div class="detail-top">
        <van-tag type="primary">合同</van-tag>
        <span class="detail-name">{{ contract.contractName }}</span>
        <span class="detail-status" :style="{color: getContractStatus(contract).color}">{{ getContractStatus(contract).label }}</span>
      </div>
      <van-cell-group :border="false">
        <van-cell title="老人" :value="contract.elderName"/>
        <van-cell title="合同编号" :value="contract.contractNo"/>
        <van-cell title="合同类型" :value="getContractType(contract.contractType)"/>
        <van-cell title="生效时间" :value="contract.signTime ? contract.signTime.slice(0, 10) : '-'"/>
        <van-cell title="过期时间" :value="contract.expireTime ? contract.expireTime.slice(0, 10) : '-'"/>
        <van-cell title="备注" :value="contract.remark || '-'"/>
      </van-cell-group>
    </div>

    <!-- 合同附件 -->
    <div class="detail-card" @click="viewFile">
      <div class="detail-top">
        <div class="file-info">
          <van-icon name="description" size="20" color="#1989FA"/>
          <span class="file-name">合同附件</span>
        </div>
        <van-icon name="arrow" color="#C8C9CC"/>
      </div>
      <p class="file-tips">点击查看合同文件（PDF）</p>
    </div>
  </div>
</template>

<style scoped>
  .contract-detail {
    min-height: 100vh;
    padding: 12px 12px 20px;
  }

  .detail-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 14px 16px;
    margin-bottom: 12px;
  }

  .detail-top {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }

  .detail-name {
    flex: 1;
    margin-left: 8px;
    font-size: 15px;
    font-weight: bold;
  }

  .detail-status {
    font-size: 13px;
  }

  .file-info {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .file-name {
    font-size: 14px;
    font-weight: bold;
  }

  .file-tips {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
  }
</style>
