<script setup>
  import userApi from '@/api/user.js'
  import {ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {
    Delete,
    Edit,
    Upload,
    Download,
    Plus
  } from '@element-plus/icons-vue'
  import {useTokenStore} from '@/store/token.js'
  const tokenStore = useTokenStore()

  //表格数据
  const list = ref([])
  const total = ref(0)
  //分页信息和搜索条件
  const userQuery = ref({
    name: '',
    email: '',
    page: 1,
    limit: 10
  })

  /*function loadData() {
      userApi.list(userQuery.value).then(result => {
          list.value = result.data.records
          total.value = result.data.total
      })
  }*/
  //加载数据
  const createTimeRange = ref([])
  const loadData = () => {
    userQuery.value.beginCreateTime = createTimeRange.value?.[0]
    userQuery.value.endCreateTime = createTimeRange.value?.[1]

    userApi.list(userQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    userQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    userQuery.value = {
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
    userApi.update(row.id, row).then(result => {
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
      userApi.deleteById(id).then(result => {
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
      userApi.deleteAll(ids).then(result => {
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
  const user = ref({})
  const title = ref()

  const showAddDialog = () => {
    dialogFormVisible.value = true
    title.value = '添加'
    user.value = {}
  }

  const showUpdateDialog = (id) => {
    dialogFormVisible.value = true
    title.value = '编辑'
    user.value = {}
    userApi.selectById(id).then(result => {
      user.value = result.data
    })
  }

  const formRef = ref()
  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (user.value.id) {//编辑
            userApi.update(user.value.id, user.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                dialogFormVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            userApi.add(user.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                dialogFormVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          }
        })
        .catch(() => {
          //校验失败
          ElMessage.error('请检查表单填写是否正确')
        })
  }

  //对话框dialog输入规则校验
  const dialogRules = {
    name: [
      {required: true, message: '请输入用户名', trigger: 'blur'},
      {min: 2, max: 16, message: '长度在 2 到 16 个字符', trigger: 'blur'}
    ],
    password: [
      {required: true, message: '请输入密码', trigger: 'blur'},
      {min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur'}
    ],
    realName: [
      {required: true, message: '请输入姓名', trigger: 'blur'}
    ],
    email: [
      {required: true, message: '请输入邮箱', trigger: 'blur'},
      {type: 'email', message: '邮箱格式错误', trigger: 'blur'}
    ],
    phone: [
      {required: true, message: '请输入手机号', trigger: 'blur'},
      {min: 11, max: 11, message: '手机号格式错误', trigger: 'blur'}
    ]
  }

  //上传图片
  const handleAvatarSuccess = (result) => {
    user.value.avatar = result.data;
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

  //Excel导出
  const exportExcel = () => {
    ElMessageBox.confirm(
        '您确认要导出吗Excel吗？',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'info',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      userApi.exportExcel().then((response) => {
        //从响应头 Content-Disposition 解析后端返回的文件名,后端做过 URLEncoder.encode,需要解码
        const disposition = response.headers['content-disposition'];
        let fileName = '用户信息.xlsx'; //兜底名
        if (disposition) {
          fileName = decodeURIComponent(disposition.split('filename=')[1]);
        }
        //responseType 为 blob 时 result.data 本身就是 Blob,直接用即可
        //封装的Axios类里面针对responseType 设置为 blob 的响应数据直接返回全部的response，不再返回response.data，这里直接使用 response.data
        let url = window.URL.createObjectURL(response.data);
        const link = document.createElement("a"); // 创建a标签
        link.href = url;
        link.download = fileName; // 使用后端返回的文件名
        link.click();
        URL.revokeObjectURL(url);
      });
    })
  }

  //导入Excel成功后调用此处
  const importExcelSuccess = (result) => {
    if (result.code ==1) {
      ElMessage.success(result.msg)
      loadData()
    }
  }
</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <el-button type="primary" :icon="Plus" @click="showAddDialog">添加</el-button>
        <el-button type="danger" :icon="Delete" @click="deleteAll">批量删除</el-button>
        <el-button type="primary" :icon="Download" @click="exportExcel">导出Excel</el-button>
        <el-upload
            :icon="Upload"
            class="inline-block"
            multiple=""
            method="post"
            action="/api/users/importExcel"
            style="display:inline-block;margin-left: 12px"
            accept=".xlsx,.xls"
            :show-file-list="false"
            :on-success="importExcelSuccess"
            :headers="{Authorization: tokenStore.token}"
            name="file">
          <el-button type="primary" :icon="Upload">导入Excel</el-button>
        </el-upload>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="用户名">
        <el-input v-model="userQuery.name" placeholder="请输入用户名" clearable style="width: 200px"/>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="userQuery.email" placeholder="请输入邮箱" clearable style="width: 200px"/>
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
    <el-table :data="list" border style="width: 100%" ref="multipleTableRef" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <!--<el-table-column fixed prop="id" label="ID"/>-->
      <el-table-column prop="avatar" label="头像" width="70">
        <template #default="{row}">
          <img :src="row.avatar" style="max-height: 40px; max-width: 40px;"/>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="用户名" width="100" :show-overflow-tooltip="true"/>
      <el-table-column prop="realName" label="姓名" width="100" :show-overflow-tooltip="true"/>
      <!--<el-table-column prop="password" label="密码"/>-->
      <el-table-column prop="phone" label="电话" :show-overflow-tooltip="true"/>
      <el-table-column prop="email" label="邮箱" :show-overflow-tooltip="true"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              active-text="已启用"
              inactive-text="已禁用"
              @change="handleSwitchChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间"/>
      <el-table-column align="center" width="150px" fixed="right" label="操作">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="showUpdateDialog(row.id)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteById(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="userQuery.page"
        v-model:page-size="userQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-dialog v-model="dialogFormVisible" :title="title" width="500" :lock-scroll="false" :close-on-click-modal="false">
    <el-form ref="formRef" :model="user" :rules="dialogRules">
      <el-form-item label="头像" :label-width="60">
        <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="{Authorization: tokenStore.token}"
        >
          <img v-if="user.avatar" :src="user.avatar" class="avatar"/>
          <el-icon v-else class="avatar-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>
        <div class="avatar-uploader-tips">
          头像图片建议尺寸150x150，文件大小不超过2MB，支持jpg/png/webp格式
        </div>
      </el-form-item>
      <el-form-item prop="name" label="用户名" :label-width="80">
        <el-input v-model="user.name" autocomplete="off" :disabled="user.id"/>
      </el-form-item>
      <el-form-item prop="password" label="密码" :label-width="80">
        <el-input v-model="user.password" autocomplete="off" show-password="true" type="password"/>
      </el-form-item>
      <el-form-item prop="realName" label="姓名" :label-width="80">
        <el-input v-model="user.realName" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="email" label="邮箱" :label-width="80">
        <el-input v-model="user.email" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="phone" label="手机号" :label-width="80">
        <el-input v-model="user.phone" autocomplete="off"/>
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
  .header {
    display: flex;
    align-items: center;
  }

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