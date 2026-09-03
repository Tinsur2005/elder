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
  import examItemApi from '@/api/examItem.js'
  import {nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen, Plus} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  // ================== 对象 ==================

  //表格数据
  const list = ref([])
  const total = ref(0)
  //单个体检项目对象，在添加/编辑时临时保存填写的数据
  const examItem = ref({})

  // ================== 选项 ==================

  // 状态选项（状态：0禁用 1启用）
  const statusOptions = [
    {value: 1, label: '启用'},
    {value: 0, label: '禁用'},
  ]

  // 结果类型选项（结果类型：0文本 1数值）
  const resultTypeOptions = [
    {value: 1, label: '数值'},
    {value: 0, label: '文本'},
  ]

  // ================== 变量 ==================

  //分页信息和搜索条件（按名称、状态模糊搜索）
  const examItemQuery = ref({
    name: '',
    status: '',
    page: 1,
    limit: 10
  })

  //创建时间范围，用于模糊搜索用，初始化置为空，在日期选择框选择后被赋值
  const createTimeRange = ref([])

  //添加、编辑对话框标题
  const title = ref()
  //添加、编辑对话框的弹出控制
  const drawerExamItemVisible = ref(false)

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    examItemQuery.value.beginCreateTime = createTimeRange.value?.[0]
    examItemQuery.value.endCreateTime = createTimeRange.value?.[1]

    examItemApi.list(examItemQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    examItemQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    examItemQuery.value = {
      name: '',
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
      examItemApi.deleteById(id).then(result => {
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
      examItemApi.deleteAll(ids).then(result => {
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
  const showAddDialog = () => {
    drawerExamItemVisible.value = true
    title.value = '添加'
    examItem.value = {sort: 0, status: 1, resultType: 0} //排序默认0，状态默认启用，结果类型默认文本
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerExamItemVisible.value = true
    title.value = '编辑'
    examItem.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    examItemApi.selectById(id).then(result => {
      examItem.value = result.data
    })
  }

  const formRef = ref()
  //对话框dialog输入规则校验
  const dialogRules = {
    name: [
      {required: true, message: '请输入体检项目名称', trigger: 'blur'},
      {min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur'}
    ],
    price: [
      {required: true, message: '请输入单项价格', trigger: 'blur'}
    ]
  }

  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (examItem.value.id) {//编辑
            examItemApi.update(examItem.value.id, examItem.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerExamItemVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            examItemApi.add(examItem.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerExamItemVisible.value = false
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
        <div class="header-left">
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('examItem:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('examItem:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="项目名称">
        <el-input v-model="examItemQuery.name" placeholder="请输入项目名称" clearable style="width: 200px"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="examItemQuery.status" placeholder="全部" clearable style="width: 140px">
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
      <el-table-column prop="name" label="项目名称" width="200" :show-overflow-tooltip="true"/>
      <el-table-column prop="price" label="单项价格" width="120" align="center">
        <template #default="{ row }">
          <span>￥{{ row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="unit" label="单位" width="100" align="center">
        <template #default="{ row }">
          <span>{{ row.unit || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="resultType" label="结果类型" width="100" align="center">
        <template #default="{ row }">
          <!-- 结果类型只有2种，直接内联判断 -->
          <el-tag v-if="row.resultType === 1" type="warning">数值</el-tag>
          <el-tag v-else type="info">文本</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="参考范围" align="center">
        <template #default="{ row }">
          <!-- 数值型显示参考范围，文本型没有参考范围 -->
          <span v-if="row.resultType === 1">{{ row.referenceMin }} ~ {{ row.referenceMax }} {{ row.referenceUnit || '' }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="项目说明" :show-overflow-tooltip="true"/>
      <el-table-column prop="sort" label="排序" width="80" align="center"/>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{row}">
          <!-- 状态只有2种，直接内联判断 -->
          <el-tag v-if="row.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="info">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160"/>
      <el-table-column align="center" width="200px" fixed="right" label="操作" v-if="hasBtnPermission('examItem:operation')">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('examItem:update')">编辑</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('examItem:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="examItemQuery.page"
        v-model:page-size="examItemQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerExamItemVisible" :title="title" size="40%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="examItem" :rules="dialogRules">
      <el-form-item prop="name" label="项目名称" :label-width="80">
        <el-input v-model="examItem.name" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="price" label="单项价格" :label-width="80">
        <el-input-number
            v-model="examItem.price"
            :min="0"
            :precision="2"
            :step="1"
            controls-position="right"
            style="width: 220px"
        />
      </el-form-item>
      <el-form-item label="单位" :label-width="80">
        <el-input v-model="examItem.unit" autocomplete="off" style="width: 220px"/>
      </el-form-item>
      <el-form-item prop="resultType" label="结果类型" :label-width="80">
        <el-radio-group v-model="examItem.resultType">
          <el-radio v-for="item in resultTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
        </el-radio-group>
        <div class="result-type-tips">数值型结果将自动与参考范围比对判定是否异常</div>
      </el-form-item>
      <template v-if="examItem.resultType === 1">
        <el-form-item label="参考下限" :label-width="80">
          <el-input-number
              v-model="examItem.referenceMin"
              :min="0"
              :precision="2"
              controls-position="right"
              style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="参考上限" :label-width="80">
          <el-input-number
              v-model="examItem.referenceMax"
              :min="0"
              :precision="2"
              controls-position="right"
              style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="参考单位" :label-width="80">
          <el-input v-model="examItem.referenceUnit" autocomplete="off" style="width: 220px"/>
        </el-form-item>
      </template>
      <el-form-item label="项目说明" :label-width="80">
        <el-input
            v-model="examItem.description"
            autocomplete="off"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
        />
      </el-form-item>
      <el-form-item label="排序" :label-width="80">
        <el-input-number
            v-model="examItem.sort"
            :min="0"
            controls-position="right"
        />
        <div class="sort-tips">数字越小越靠前</div>
      </el-form-item>
      <el-form-item prop="status" label="状态" :label-width="80">
        <el-select v-model="examItem.status" placeholder="请选择状态" style="width: 220px">
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
        <el-button @click="drawerExamItemVisible = false">取消</el-button>
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

  /*  “数值型结果将自动与参考范围比对判定是否异常”提示的样式  */
  .result-type-tips {
    display: block;       /* 独占一行，避免和单选框挤在一起 */
    width: 100%;
    margin-top: 4px;
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
  }

  .sort-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
    margin-left: 10px;    /* 与输入框保持间距 */
  }
</style>