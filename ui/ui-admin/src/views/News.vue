<!--
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝
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
  import newsApi from '@/api/news.js'
  import newsCategoryApi from '@/api/newsCategory.js'
  import {nextTick, onBeforeUnmount, ref, shallowRef} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen, Plus} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";
  import {useTokenStore} from '@/store/token.js'
  import request from '@/utils/request.js'
  //富文本编辑器（wangEditor）
  import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
  import '@wangeditor/editor/dist/css/style.css'

  const tokenStore = useTokenStore()

  //表格数据
  const list = ref([])
  const total = ref(0)
  //分页信息和搜索条件
  const newsQuery = ref({
    title: '',
    categoryId: '',
    status: '',
    page: 1,
    limit: 10
  })

  //资讯状态选项（状态：0下架 1发布）
  const statusOptions = [
    {value: 0, label: '下架'},
    {value: 1, label: '发布'},
  ]
  //分类列表（搜索栏和编辑抽屉的下拉框共用）
  const categoryList = ref([])
  const loadCategoryList = () => {
    newsCategoryApi.listAll().then(result => {
      categoryList.value = result.data
    })
  }
  loadCategoryList()

  //加载数据
  const createTimeRange = ref([])
  const loadData = () => {
    newsQuery.value.beginCreateTime = createTimeRange.value?.[0]
    newsQuery.value.endCreateTime = createTimeRange.value?.[1]

    newsApi.list(newsQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    newsQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    newsQuery.value = {
      title: '',
      categoryId: '',
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
      newsApi.deleteById(id).then(result => {
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
      newsApi.deleteAll(ids).then(result => {
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
  const drawerNewsVisible = ref(false)
  const news = ref({})
  const title = ref()

  const showAddDialog = () => {
    drawerNewsVisible.value = true
    title.value = '添加'
    news.value = {status: 1, content: ''} //新增时状态默认为"发布"
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerNewsVisible.value = true
    title.value = '编辑'
    news.value = {content: ''}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    newsApi.selectById(id).then(result => {
      news.value = result.data
    })
  }

  const formRef = ref()
  //对话框dialog输入规则校验
  const dialogRules = {
    title: [
      {required: true, message: '请输入资讯标题', trigger: 'blur'},
      {min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur'}
    ],
    categoryId: [
      {required: true, message: '请选择资讯分类', trigger: 'change'}
    ],
    content: [
      {required: true, message: '请输入资讯内容', trigger: 'blur'}
    ]
  }

  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (news.value.id) {//编辑
            newsApi.update(news.value.id, news.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerNewsVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            newsApi.add(news.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerNewsVisible.value = false
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

  // ========== 富文本编辑器（wangEditor） ==========
  //编辑器实例，这里用 shallowRef，避免深层响应式影响编辑器性能
  const editorRef = shallowRef()
  //记录编辑器创建完成后的实例
  const handleCreated = (editor) => {
    editorRef.value = editor
  }
  //组件销毁时销毁编辑器实例，防止内存泄漏
  onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy()
  })

  const toolbarConfig = {}
  const editorConfig = {placeholder: '请输入资讯内容...'}
  //富文本里上传图片：复用项目统一的上传接口（走axios封装，自动携带token）
  editorConfig.MENU_CONF['uploadImage'] = {
    async customUpload(file, insertFn) {
      const formData = new FormData()
      formData.append('file', file)
      request.post('/upload?dir=news', formData).then(result => {
        if (result.code === 1) {
          //依次传入图片url、alt、href，编辑器会把图片插入正文
          insertFn(result.data, file.name, '')
        } else {
          ElMessage.error(result.msg)
        }
      })
    }
  }

  //上传封面图片成功后回调
  const handleCoverSuccess = (result) => {
    news.value.coverImage = result.data;
  }
  //上传时校验封面图片的文件格式
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
  const beforeCoverUpload = (rawFile) => {
    if (!allowedTypes.includes(rawFile.type)) {
      ElMessage.error('不支持的文件格式')
      return false
    } else if (rawFile.size / 1024 / 1024 > 2) {
      ElMessage.error('上传的文件大小不允许超过2MB')
      return false
    }
    return true
  }

</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('news:add')">添加</el-button>
        <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('news:deleteAll')">批量删除</el-button>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="资讯标题">
        <el-input v-model="newsQuery.title" placeholder="请输入资讯标题" clearable style="width: 200px"/>
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="newsQuery.categoryId" placeholder="全部" clearable style="width: 130px">
          <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="newsQuery.status" placeholder="全部" clearable style="width: 130px">
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
      <el-table-column prop="coverImage" label="封面图" width="100" align="center">
        <template #default="{row}">
          <el-image
              v-if="row.coverImage"
              :src="row.coverImage"
              :preview-src-list="[row.coverImage]"
              preview-teleported
              fit="cover"
              style="width: 60px; height: 40px; border-radius: 4px"
          />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="资讯标题" min-width="200" :show-overflow-tooltip="true"/>
      <el-table-column prop="categoryName" label="分类" width="110" :show-overflow-tooltip="true"/>
      <el-table-column prop="author" label="作者" width="100" :show-overflow-tooltip="true"/>
      <el-table-column prop="views" label="阅读量" width="90" align="center"/>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}">
          <!-- 资讯状态只有2种，直接内联判断 -->
          <el-tag v-if="row.status === 1" type="success">发布</el-tag>
          <el-tag v-else type="info">下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" :show-overflow-tooltip="true"/>
      <el-table-column align="center" width="200px" fixed="right" label="操作" v-if="hasBtnPermission('news:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('news:update')">编辑</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('news:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="newsQuery.page"
        v-model:page-size="newsQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerNewsVisible" :title="title" size="50%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="news" :rules="dialogRules">
      <el-form-item prop="categoryId" label="资讯分类" :label-width="80">
        <el-select v-model="news.categoryId" placeholder="请选择资讯分类" style="width: 100%">
          <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="title" label="资讯标题" :label-width="80">
        <el-input v-model="news.title" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="作者" :label-width="80">
        <el-input v-model="news.author" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="封面图片" :label-width="80">
        <el-upload
            class="cover-uploader"
            action="/admin/api/upload?dir=news"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
            :headers="{Authorization: tokenStore.token}"
        >
          <img v-if="news.coverImage" :src="news.coverImage" class="cover"/>
          <el-icon v-else class="cover-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>
        <div class="cover-uploader-tips">
          封面图片建议尺寸360x240，文件大小不超过2MB，支持jpg/png/webp格式
        </div>
      </el-form-item>
      <el-form-item label="资讯摘要" :label-width="80">
        <el-input v-model="news.summary" autocomplete="off" type="textarea" :rows="3" maxlength="500" show-word-limit/>
      </el-form-item>
      <el-form-item prop="content" label="资讯内容" :label-width="80">
        <div class="editor-container">
          <Toolbar
              class="editor-toolbar"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              mode="default"
          />
          <Editor
              class="editor-content"
              v-model="news.content"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleCreated"
          />
        </div>
      </el-form-item>
      <el-form-item label="状态" :label-width="80">
        <el-radio-group v-model="news.status">
          <el-radio :value="1">发布</el-radio>
          <el-radio :value="0">下架</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerNewsVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<style scoped>
  .cover-uploader .cover {
    width: 178px;
    height: 118px;
    display: block;
    object-fit: cover;
  }

  .cover-uploader-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
  }

  /* 富文本编辑器：工具栏和编辑区拼接成一个整体 */
  .editor-container {
    width: 100%;
    border: 1px solid #ccc;
    z-index: 100;
  }

  .editor-toolbar {
    border-bottom: 1px solid #ccc;
  }

  .editor-content {
    height: 350px;
    overflow-y: hidden;
  }
</style>

<style>
  .cover-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  .cover-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
  }

  .el-icon.cover-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 118px;
    text-align: center;
    line-height: 118px;
  }
</style>
