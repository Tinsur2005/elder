<script setup>
  import contractApi from '@/api/contract.js'
  import elderApi from '@/api/elder.js'
  import {ref} from 'vue'
  import {useRoute} from 'vue-router'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Plus, Delete, EditPen, Upload, Document} from '@element-plus/icons-vue'
  import {useTokenStore} from '@/store/token.js'
  import hasBtnPermission from "@/utils/btnPermission.js";
  const tokenStore = useTokenStore()
  const route = useRoute()

  // ================== 对象 ==================

  //分页信息和搜索条件（按合同名称、创建时间模糊搜索）
  const contractQuery = ref({
    page: 1,
    limit: 10
  })

  // 单个合同对象，在添加时用来临时保存填写的数据
  const contract = ref({})

  // ================== 选项 ==================

  // 合同类型选项
  const contractTypeOptions = [
    {value: 0, label: '服务合同'},
    {value: 1, label: '入住合同'},
    {value: 2, label: '其他'},
  ]

  // ================== 变量 ==================

  // 标题，用于显示添加/修改合同对话框的标题，例如“添加合同”、“编辑合同”
  const title = ref()

  // 表格数据
  const list = ref([]) // 表格List原始置为空
  const total = ref(0)

  // 创建时间范围，用于模糊搜索用，初始化置为空，在日期选择框选择后被赋值
  const createTimeRange = ref([])

  // ========== 对话框dialog弹出控制 ==========
  const drawerContractVisible = ref(false) // 弹出新增/编辑对话框dialog

  // ========== 老人远程搜索（选择老人） ==========

  // 存放远程搜索出来的可选老人列表，供下拉框展示"姓名（身份证号）"
  const elderOptions = ref([])

  // 是否正在加载远程搜索结果（控制下拉框的loading转圈）
  const elderLoading = ref(false)

  // 远程搜索方法：根据用户输入的老人姓名（可部分可全部）去后端模糊搜索老人
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

  // ============== 方法 ==============

  // 查看合同：点击后在新窗口打开合同的url链接
  const viewContract = (row) => {
    if (!row.fileUrl) {
      ElMessage.info('该合同尚未上传合同文件')
      return
    }
    window.open(row.fileUrl)
  }

  //上传合同文件成功后，把返回的url存到合同对象的fileUrl字段
  const handleFileSuccess = (result) => {
    contract.value.fileUrl = result.data;
  }
  //上传时校验合同文件的格式
  const allowedFileTypes = [
    'application/pdf', // pdf
    'application/msword', // doc
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document' // docx
  ]
  const beforeFileUpload = (rawFile) => {
    if (!allowedFileTypes.includes(rawFile.type)) {
      ElMessage.error('不支持的文件格式（仅支持pdf/word）')
      return false
    } else if (rawFile.size / 1024 / 1024 > 10) {
      ElMessage.error('上传的文件大小不允许超过10MB')
      return false
    }
    return true
  }

  // 加载数据
  const loadData = () => {
    contractQuery.value.beginCreateTime = createTimeRange.value?.[0]
    contractQuery.value.endCreateTime = createTimeRange.value?.[1]

    contractApi.list(contractQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  // 页面加载时：如果从老人列表带elderId跳转过来，自动按这个老人搜索，并在搜索框回显"姓名（身份证号）"
  const initQuery = () => {
    if (route.query.elderId) {
      contractQuery.value.elderId = route.query.elderId
      // 把该老人生成一个可显示的下拉选项，让搜索框回显姓名而不是数字id
      elderApi.selectById(route.query.elderId).then(result => {
        if (result.code === 1) {
          // 用后端返回的id回填，保证与el-option:value严格相等（Long被序列化成字符串）
          contractQuery.value.elderId = result.data.id
          elderOptions.value = [result.data]
        }
      })
    }
  }

  initQuery()
  loadData()

  // 搜索按钮点击事件
  const onSearch = () => {
    contractQuery.value.page = 1 // 重置搜索时页码
    loadData()
  }

  // 重置按钮点击事件
  const reset = () => {
    contractQuery.value = {
      contractName: '',
      elderId: '',
      page: 1,
      limit: 10
    }
    createTimeRange.value = []
    loadData()
  }

  // 根据id删除
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
      contractApi.deleteById(id).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  // 多选删除相关
  let ids = []
  const handleSelectionChange = (rows) => {
    ids = rows.map(row => row.id)
    console.log(ids)
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
      contractApi.deleteAll(ids).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }

  // 添加、编辑
  const showAddDialog = () => {
    drawerContractVisible.value = true
    title.value = '添加'
    contract.value = {}
    elderOptions.value = [] // 清空老人搜索结果
  }

  const showUpdateDialog = (id) => {
    drawerContractVisible.value = true
    title.value = '编辑'
    contract.value = {}
    contractApi.selectById(id).then(result => {
      contract.value = result.data
      // 编辑时回显当前绑定的老人：把该老人生成一个可显示的下拉选项，
      // 这样下拉框才能显示出"姓名（身份证号）"，而不是只显示一个数字id
      if (contract.value.elderId) {
        elderApi.selectById(contract.value.elderId).then(result => {
          if (result.code === 1) {
            elderOptions.value = [result.data]
          }
        })
      }
    })
  }

  const formRef = ref()
  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          // 校验通过，执行新增/编辑接口
          if (contract.value.id) {
            // 编辑
            contractApi.update(contract.value.id, contract.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerContractVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {
            // 添加
            contractApi.add(contract.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerContractVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          }
        })
        .catch(() => {
          // 校验失败
          ElMessage.error('请检查表单填写是否正确')
        })
  }

  // 对话框dialog输入规则校验
  const dialogRules = {
    contractNo: [
      {required: true, message: '请输入合同编号', trigger: 'blur'}
    ],
    elderId: [
      {required: true, message: '请输入老人ID', trigger: 'blur'}
    ],
  }
</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <div class="header-left">
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('contract:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('contract:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="合同名称">
        <el-input v-model="contractQuery.contractName" placeholder="请输入合同名称" clearable style="width: 200px"/>
      </el-form-item>
      <!-- 按老人搜索合同，复用对话框里的远程搜索，使用已存在的loadElderOptions、elderOptions和elderLoading -->
      <el-form-item label="老人">
        <el-select
            v-model="contractQuery.elderId"
            filterable
            remote
            reserve-keyword
            clearable
            placeholder="请输入老人姓名搜索"
            :remote-method="loadElderOptions"
            :loading="elderLoading"
            style="width: 200px">
          <el-option
              v-for="item in elderOptions"
              :key="item.id"
              :label="`${item.realName}（${item.idCardNo}）`"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
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
    <el-table :data="list" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="contractNo" label="合同编号" width="150" :show-overflow-tooltip="true"/>
      <el-table-column prop="elderName" label="老人" width="100"/>
      <el-table-column prop="contractName" label="合同名称" width="180" :show-overflow-tooltip="true">
        <template #default="{row}">
          {{ row.contractName || '未命名合同' }}
        </template>
      </el-table-column>
      <el-table-column prop="contractType" label="类型" width="100">
        <template #default="{row}">
          <!-- 合同类型只有3种，直接内联判断，无需额外方法 -->
          <span v-if="row.contractType === 0">服务合同</span>
          <span v-else-if="row.contractType === 1">入住合同</span>
          <span v-else>其他</span>
        </template>
      </el-table-column>
      <el-table-column prop="signTime" label="生效时间" width="160"/>
      <el-table-column prop="expireTime" label="过期时间" width="160"/>
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <!-- 合同状态按时间自动判定，无需存储，参考elder.vue的el-tag v-if写法 -->
          <el-tag type="danger" v-if="row.expireTime && new Date(row.expireTime).getTime() < Date.now()">已过期</el-tag>
          <el-tag type="info" v-else-if="!row.signTime">待签订</el-tag>
          <el-tag type="primary" v-else-if="new Date(row.signTime).getTime() > Date.now()">待生效</el-tag>
          <el-tag type="success" v-else>生效中</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" :show-overflow-tooltip="true"/>
      <el-table-column prop="createTime" label="创建时间" width="160"/>
      <el-table-column align="center" width="280" fixed="right" label="操作">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('contract:update')">编辑</el-button>
          <el-button size="small" type="success" :icon="Document" @click="viewContract(row)" v-if="hasBtnPermission('contract:get')">查看合同</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('contract:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="contractQuery.page"
        v-model:page-size="contractQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>

  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerContractVisible" :title="title" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="contract" :rules="dialogRules">
      <el-form-item prop="contractNo" label="合同编号" :label-width="80">
        <el-input v-model="contract.contractNo" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="合同名称" :label-width="80">
        <el-input v-model="contract.contractName" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="elderId" label="老人" :label-width="80">
        <el-select
            v-model="contract.elderId"
            filterable
            remote
            reserve-keyword
            clearable
            placeholder="请输入老人姓名进行搜索"
            :remote-method="loadElderOptions"
            :loading="elderLoading"
            style="width: 220px">
          <el-option
              v-for="item in elderOptions"
              :key="item.id"
              :label="`${item.realName}（${item.idCardNo}）`"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="合同类型" :label-width="80">
        <el-select v-model="contract.contractType" placeholder="请选择合同类型" style="width: 220px">
          <el-option
              v-for="item in contractTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="生效时间" :label-width="80">
        <el-date-picker v-model="contract.signTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择签订时间"/>
      </el-form-item>
      <el-form-item label="过期时间" :label-width="80">
        <el-date-picker v-model="contract.expireTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择过期时间"/>
      </el-form-item>
      <el-form-item label="合同文件" :label-width="80">
        <div class="contract-file-field">
          <el-upload
              action="/api/upload?dir=contract"
              :show-file-list="false"
              :on-success="handleFileSuccess"
              :before-upload="beforeFileUpload"
              :headers="{Authorization: tokenStore.token}"
              name="file">
            <el-button type="primary" :icon="Upload">上传合同文件</el-button>
          </el-upload>
          <div v-if="contract.fileUrl" class="contract-file-link">
            <el-link :href="contract.fileUrl" target="_blank" :icon="Document">已上传，点击查看</el-link>
          </div>
          <div class="avatar-uploader-tips">仅支持pdf/word格式，文件大小不超过10MB</div>
        </div>
      </el-form-item>
      <el-form-item label="备注" :label-width="80">
        <el-input v-model="contract.remark" autocomplete="off"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerContractVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
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

  .contract-file-field {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .contract-file-link {
    margin-top: 8px;
  }

  .avatar-uploader-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
    margin-top: 6px;
  }
</style>