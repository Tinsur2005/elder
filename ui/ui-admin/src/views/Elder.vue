<script setup>
  import elderApi from '@/api/elder.js'
  import {ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Plus} from '@element-plus/icons-vue'
  import {useTokenStore} from '@/store/token.js'
  const tokenStore = useTokenStore()

  //表格数据
  const list = ref([])
  const total = ref(0)
  //分页信息和搜索条件
  const elderQuery = ref({
    name: '',
    email: '',
    page: 1,
    limit: 10
  })

  //加载数据
  const createTimeRange = ref([])
  const loadData = () => {
    elderQuery.value.beginCreateTime = createTimeRange.value?.[0]
    elderQuery.value.endCreateTime = createTimeRange.value?.[1]

    elderApi.list(elderQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    elderQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    elderQuery.value = {
      name: '',
      email: '',
      page: 1,
      limit: 10
    }
    createTimeRange.value = []
    loadData()
  }

  //根据id更新状态（0：停用，1：正常）
  const handleSwitchChange = (row) => {
    elderApi.update(row.id, row).then(result => {
      if (result.code === 1) {
        if(row.status === 1) {
          ElMessage.success("已启用")
        } else {
          ElMessage.primary("已禁用")
        }
      } else {
        ElMessage.error(result.msg)
        loadData() //失败则重新加载还原
      }
    })
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
      elderApi.deleteById(id).then(result => {
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
    //console.log('多选', rows)
    ids = rows.map(row => row.id)
    console.log(ids)
  }

  const deleteAll = () => {
    if(ids.length === 0){
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
      elderApi.deleteAll(ids).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    })
  }


  //添加、编辑
  const dialogFormVisible = ref(false)
  const elder = ref({})
  const title = ref()

  const showAddDialog = () => {
    dialogFormVisible.value = true
    title.value = '添加'
    elder.value = {}
  }

  const showUpdateDialog = (id) => {
    dialogFormVisible.value = true
    title.value = '编辑'
    elder.value = {}
    elderApi.selectById(id).then(result => {
      elder.value = result.data
    })
  }

  const addOrUpdate = () => {
    if(!elder.value.name || !elder.value.password || !elder.value.phone || !elder.value.start) {
      ElMessage.error('姓名、密码、手机号和状态不允许为空');
      return
    }
    if (elder.value.id) {//编辑
      elderApi.update(elder.value.id, elder.value).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          dialogFormVisible.value = false
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    } else {//添加
      elderApi.add(elder.value).then(result => {
        if (result.code === 1) {
          ElMessage.success(result.msg)
          dialogFormVisible.value = false
          loadData()
        } else {
          ElMessage.error(result.msg)
        }
      })
    }
  }

  //把日期格式化为 YYYY-MM-DD
  const formatDate = (value) => {
    if (!value) return ''
    return String(value).slice(0, 10)
  }

  //上传图片
  const handleAvatarSuccess = (result) => {
    elder.value.avatar = result.data;
  }
  //上传时校验头像的文件格式
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
  const beforeAvatarUpload = (rawFile) => {
    if (!allowedTypes.includes(rawFile.type)) {
      ElMessage.error('不支持的文件格式')
      return false
    } else if (rawFile.size / 1024 / 1024 > 2) {
      ElMessage.error('上传的文件大小不允许超过2MB')
      return false
    }
    return true
  }
  //dialog对话框状态选项
  const statusOptions = [
    {
      value: 0,
      label: '禁用',
    },
    {
      value: 1,
      label: '正常',
    },
    {
      value: 2,
      label: '请假',
    },
    {
      value: 3,
      label: '退住中',
    },
    {
      value: 4,
      label: '入住中',
    },
  ]

  //对话框dialog输入规则校验
  const dialogRules = {
    name: [
      {required: true, message: '请输入用户名', trigger: 'blur'},
      {min: 4, max: 16, message: '长度在 4 到 16 个字符', trigger: 'blur'}
    ],
    password: [
      {required: true, message: '请输入密码', trigger: 'blur'},
      {min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur'}
    ],
    status: [
      {required: true, message: '请选择状态', trigger: 'blur'}
    ],
    phone: [
      {required: true, message: '请输入手机号', trigger: 'blur'},
      {min: 11, max: 11, message: '手机号格式错误', trigger: 'blur'}
    ]
  }

</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <el-button type="primary" @click="showAddDialog">添加</el-button>
        <el-button type="danger" @click="deleteAll">批量删除</el-button>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="姓名">
        <el-input v-model="elderQuery.name" placeholder="请输入姓名" clearable style="width: 200px"/>
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
    <el-table :data="list" border style="width: 100%" ref="multipleTableRef" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <!--<el-table-column fixed prop="id" label="ID"/>-->
      <el-table-column prop="avatar" label="头像" width="70">
        <template #default="{row}">
          <img :src="row.avatar" style="max-height: 40px; max-width: 40px;"/>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="姓名" width="100" :show-overflow-tooltip="true"/>
      <el-table-column prop="phone" label="电话" :show-overflow-tooltip="true"/>
      <el-table-column prop="idCardNo" label="身份证号" :show-overflow-tooltip="true"/>
      <el-table-column prop="birthday" label="生日" :show-overflow-tooltip="true">
        <template #default="{row}">
          {{ formatDate(row.birthday) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" min-width="30" :resizable="false">
        <template #default="{row}">
            <el-tag type="danger" v-if="row.status === 0">禁用</el-tag>
            <el-tag type="success" v-else-if="row.status === 1">正常</el-tag>
            <el-tag type="primary" v-else-if="row.status === 2">请假</el-tag>
            <el-tag type="info" v-else-if="row.status === 3">退住中</el-tag>
            <el-tag type="info" v-else-if="row.status === 4">入住中</el-tag>
            <el-tag type="danger" v-else-if="row.status === 5">已退住</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" :show-overflow-tooltip="true"/>
      <el-table-column align="center" width="150px" fixed="right" label="操作">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="showUpdateDialog(row.id)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteById(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="elderQuery.page"
        v-model:page-size="elderQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-dialog v-model="dialogFormVisible" :title="title" width="500" :lock-scroll="false" :close-on-click-modal="false">
    <el-form :model="elder" :rules="dialogRules">
      <el-form-item label="头像" :label-width="60">
        <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="{Authorization: tokenStore.token}"
        >
          <img v-if="elder.avatar" :src="elder.avatar" class="avatar"/>
          <el-icon v-else class="avatar-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>
        <div class="avatar-uploader-tips">
          头像图片建议尺寸150x150，文件大小不超过2MB，支持jpg/png/webp格式
        </div>
      </el-form-item>
      <el-form-item prop="name" label="姓名" :label-width="80">
        <el-input v-model="elder.name" autocomplete="off" :disabled="elder.id"/>
      </el-form-item>
      <el-form-item prop="password" label="密码" :label-width="80">
        <el-input v-model="elder.password" autocomplete="off" show-password="true" type="password"/>
      </el-form-item>
      <el-form-item prop="phone" label="手机号" :label-width="80">
        <el-input v-model="elder.phone" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="身份证号" :label-width="80">
        <el-input v-model="elder.idCardNo" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="地址" :label-width="80">
        <el-input v-model="elder.address" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="生日" :label-width="80">
        <el-date-picker v-model="elder.birthday" type="date" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择生日"/>
      </el-form-item>
      <el-form-item prop="status" label="状态" :label-width="80">
        <el-select v-model="elder.status" placeholder="请选择状态" style="width: 220px">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>


<style scoped>
  .avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  .avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
  }

  .el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
  }

  .avatar-uploader-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
  }

</style>