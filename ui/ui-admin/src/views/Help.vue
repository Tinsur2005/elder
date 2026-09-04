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
  import helpApi from '@/api/help.js'
  import elderApi from '@/api/elder.js'
  import {nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  // ================== 对象 ==================

  //表格数据
  const list = ref([])
  const total = ref(0)

  // ================== 选项 ==================

  // 求助类型选项（0健康 1生活 2安全 3其他），type为el-tag的type
  const typeOptions = [
    {value: 0, label: '健康', type: 'success'},
    {value: 1, label: '生活', type: 'primary'},
    {value: 2, label: '安全', type: 'danger'},
    {value: 3, label: '其他', type: 'info'},
  ]

  // 紧急程度选项（0普通 1紧急 2非常紧急），type为el-tag的type
  const urgencyOptions = [
    {value: 0, label: '普通', type: 'info'},
    {value: 1, label: '紧急', type: 'warning'},
    {value: 2, label: '非常紧急', type: 'danger'},
  ]

  // 状态选项（0未处理 1已处理 2已忽略），type为el-tag的type
  const statusOptions = [
    {value: 0, label: '未处理', type: 'warning'},
    {value: 1, label: '已处理', type: 'success'},
    {value: 2, label: '已忽略', type: 'info'},
  ]

  // ================== 下拉数据 ==================

  // 老人远程搜索：存放远程搜索出来的可选老人列表，供下拉框展示姓名加身份证号
  const elderOptions = ref([])
  // 是否正在加载远程搜索结果，控制下拉框的loading转圈
  const elderLoading = ref(false)
  // 远程搜索方法，根据用户输入的老人姓名可部分可全部去后端模糊搜索老人
  const loadElderOptions = (query) => {
    // 没有输入内容时不搜索，直接清空列表
    if (!query) {
      elderOptions.value = []
      return
    }
    elderLoading.value = true
    elderApi.searchByName(query).then(result => {
      elderOptions.value = result.data
    }).finally(() => {
      elderLoading.value = false
    })
  }

  // ================== 变量 ==================

  //分页信息和搜索条件，按老人求助类型紧急程度状态和创建时间范围搜索
  const helpQuery = ref({
    elderId: '',
    type: '',
    urgency: '',
    status: '',
    page: 1,
    limit: 10
  })

  //创建时间范围，用于模糊搜索用，初始化置为空，在日期选择框选择后被赋值
  const createTimeRange = ref([])

  //处理对话框的弹出控制
  const drawerHandleVisible = ref(false)
  //当前正在处理的求助对象，处理结果在result字段上填写
  const handleForm = ref({})
  //处理对话框输入规则校验
  const formRef = ref()
  const handleRules = {
    result: [
      {required: true, message: '请输入处理结果', trigger: 'blur'},
      {min: 2, max: 500, message: '长度在 2 到 500 个字符', trigger: 'blur'}
    ]
  }

  // ================== 方法 ==================

  //根据选项获取展示文本，通用做法
  const getLabel = (options, value) => {
    return options.find(option => option.value === value)?.label || '-'
  }

  //根据选项获取el-tag的type，用于状态和紧急程度上色
  const getTagType = (options, value) => {
    return options.find(option => option.value === value)?.type || 'info'
  }

  //加载数据
  const loadData = () => {
    helpQuery.value.beginCreateTime = createTimeRange.value?.[0]
    helpQuery.value.endCreateTime = createTimeRange.value?.[1]

    helpApi.list(helpQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    helpQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    helpQuery.value = {
      elderId: '',
      type: '',
      urgency: '',
      status: '',
      page: 1,
      limit: 10
    }
    createTimeRange.value = []
    elderOptions.value = []
    loadData()
  }

  //根据id删除
  const deleteById = (id) => {
    ElMessageBox.confirm(
        '您确认要删除么?',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      helpApi.deleteById(id).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  let ids = []
  const handleSelectionChange = (rows) => {
    ids = rows.map(row => row.id)
  }

  const deleteAll = () => {
    if (ids.length === 0) {
      ElMessage.error('请选择要删除的记录')
      return
    }
    ElMessageBox.confirm(
        '您确认要删除么?',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      helpApi.deleteAll(ids).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  //打开处理对话框，回显求助内容供处理人参考
  const showHandleDialog = (row) => {
    drawerHandleVisible.value = true
    handleForm.value = {
      id: row.id,
      content: row.content,
      elderName: row.elderName,
      result: ''
    }
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  //提交处理结果，校验通过后将该求助置为已处理
  const submitHandle = () => {
    formRef.value.validate()
        .then(() => {
          helpApi.handle(handleForm.value.id, handleForm.value.result).then(result => {
            if (result.code === 1) {
              ElMessage.success(result.msg)
              drawerHandleVisible.value = false
              loadData()
            } else {
              ElMessage.error(result.msg)
            }
          })
        })
        .catch(() => {
          //校验失败
          ElMessage.error('请检查表单填写是否正确')
        })
  }

  //忽略求助，确认后直接将该求助置为已忽略
  const ignoreHelp = (row) => {
    ElMessageBox.confirm(
        '确认忽略该条求助？忽略后将标记为已忽略',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      helpApi.ignore(row.id).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }
</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <div class="header-left">
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('help:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <!-- 按老人搜索求助，复用远程搜索下拉框 -->
      <el-form-item label="老人">
        <el-select
            v-model="helpQuery.elderId"
            filterable
            remote
            reserve-keyword
            clearable
            placeholder="请输入老人姓名搜索"
            :remote-method="loadElderOptions"
            :loading="elderLoading"
            style="width: 180px">
          <el-option
              v-for="item in elderOptions"
              :key="item.id"
              :label="`${item.realName}（${item.idCardNo}）`"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="求助类型">
        <el-select v-model="helpQuery.type" placeholder="全部" clearable style="width: 130px">
          <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="紧急程度">
        <el-select v-model="helpQuery.urgency" placeholder="全部" clearable style="width: 130px">
          <el-option
              v-for="item in urgencyOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="helpQuery.status" placeholder="全部" clearable style="width: 130px">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="求助时间">
        <el-date-picker
            v-model="createTimeRange"
            type="daterange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
    <!--表单-->
    <el-table :data="list" border style="width: 100%" ref="multipleTableRef" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="elderName" label="求助老人" width="120"/>
      <el-table-column label="求助类型" width="100" align="center">
        <template #default="{row}">
          <el-tag :type="getTagType(typeOptions, row.type)">{{ getLabel(typeOptions, row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="紧急程度" width="100" align="center">
        <template #default="{row}">
          <el-tag :type="getTagType(urgencyOptions, row.urgency)">{{ getLabel(urgencyOptions, row.urgency) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="求助内容" min-width="200" :show-overflow-tooltip="true"/>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{row}">
          <el-tag :type="getTagType(statusOptions, row.status)">{{ getLabel(statusOptions, row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="result" label="处理结果" min-width="160" :show-overflow-tooltip="true"/>
      <el-table-column prop="handlerName" label="处理人" width="100"/>
      <el-table-column prop="handleTime" label="处理时间" width="160">
        <template #default="{row}">
          {{ row.handleTime || '—' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="求助时间" width="160"/>
      <el-table-column align="center" width="250px" fixed="right" label="操作" v-if="hasBtnPermission('help:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showHandleDialog(row)"
                     v-if="row.status === 0 && hasBtnPermission('help:handle')">提交处理</el-button>
          <el-button size="small" type="warning" @click="ignoreHelp(row)"
                     v-if="row.status === 0 && hasBtnPermission('help:ignore')">忽略</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)"
                     v-if="hasBtnPermission('help:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="helpQuery.page"
        v-model:page-size="helpQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--处理对话框-->
  <el-drawer v-model="drawerHandleVisible" title="提交处理" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="handleForm" :rules="handleRules">
      <el-form-item label="求助老人" :label-width="80">
        <el-input v-model="handleForm.elderName" disabled/>
      </el-form-item>
      <el-form-item label="求助内容" :label-width="80">
        <el-input v-model="handleForm.content" type="textarea" :rows="4" disabled/>
      </el-form-item>
      <el-form-item prop="result" label="处理结果" :label-width="80">
        <el-input v-model="handleForm.result" type="textarea" :rows="5" placeholder="请填写处理结果说明"/>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerHandleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">
          确认
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<style scoped>
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-left,
  .header-right {
    display: flex;
    align-items: center;
  }
</style>
