<script setup>
  import announcementApi from '@/api/announcement.js'
  import {nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen, Plus} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  //表格数据
  const list = ref([])
  const total = ref(0)
  //分页信息和搜索条件
  const announcementQuery = ref({
    title: '',
    status: '',
    page: 1,
    limit: 10
  })

  //公告状态选项（状态：0下架 1发布）
  const statusOptions = [
    {value: 0, label: '下架'},
    {value: 1, label: '发布'},
  ]

  //加载数据
  const createTimeRange = ref([])
  const loadData = () => {
    announcementQuery.value.beginCreateTime = createTimeRange.value?.[0]
    announcementQuery.value.endCreateTime = createTimeRange.value?.[1]

    announcementApi.list(announcementQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    announcementQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    announcementQuery.value = {
      title: '',
      status: '',
      page: 1,
      limit: 10
    }
    createTimeRange.value = []
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
      announcementApi.deleteById(id).then(result => {
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
      announcementApi.deleteAll(ids).then(result => {
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
  const drawerAnnouncementVisible = ref(false)
  const announcement = ref({})
  const title = ref()

  const showAddDialog = () => {
    drawerAnnouncementVisible.value = true
    title.value = '添加'
    announcement.value = {status: 1} //新增时状态默认为"发布"
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerAnnouncementVisible.value = true
    title.value = '编辑'
    announcement.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    announcementApi.selectById(id).then(result => {
      announcement.value = result.data
    })
  }

  const formRef = ref()
  //对话框dialog输入规则校验
  const dialogRules = {
    title: [
      {required: true, message: '请输入公告标题', trigger: 'blur'},
      {min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur'}
    ]
  }

  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (announcement.value.id) {//编辑
            announcementApi.update(announcement.value.id, announcement.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerAnnouncementVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            announcementApi.add(announcement.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerAnnouncementVisible.value = false
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

</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('announcement:add')">添加</el-button>
        <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('announcement:deleteAll')">批量删除</el-button>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="公告标题">
        <el-input v-model="announcementQuery.title" placeholder="请输入公告标题" clearable style="width: 200px"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="announcementQuery.status" placeholder="全部" clearable style="width: 130px">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
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
    <el-table :data="list" border style="width: 100%" ref="multipleTableRef" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <!--<el-table-column fixed prop="id" label="ID"/>-->
      <el-table-column prop="title" label="公告标题" width="250" :show-overflow-tooltip="true"/>
      <el-table-column prop="content" label="公告内容" :show-overflow-tooltip="true"/>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}">
          <!-- 公告状态只有2种，直接内联判断 -->
          <el-tag v-if="row.status === 1" type="success">发布</el-tag>
          <el-tag v-else type="info">下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" :show-overflow-tooltip="true"/>
      <el-table-column align="center" width="200px" fixed="right" label="操作" v-if="hasBtnPermission('announcement:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('announcement:update')">编辑</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('announcement:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="announcementQuery.page"
        v-model:page-size="announcementQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerAnnouncementVisible" :title="title" size="35%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="announcement" :rules="dialogRules">
      <el-form-item prop="title" label="公告标题" :label-width="80">
        <el-input v-model="announcement.title" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="公告内容" :label-width="80">
        <el-input v-model="announcement.content" autocomplete="off" type="textarea" :rows="4"/>
      </el-form-item>
      <el-form-item label="状态" :label-width="80">
        <el-radio-group v-model="announcement.status">
          <el-radio :value="1">发布</el-radio>
          <el-radio :value="0">下架</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerAnnouncementVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>