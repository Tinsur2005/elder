<script setup>
  import carePlanApi from '@/api/carePlan.js'
  import elderApi from '@/api/elder.js'
  import userApi from '@/api/user.js'
  import careLevelApi from '@/api/careLevel.js'
  import careItemApi from '@/api/care.js'
  import {nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen, Plus} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  // ================== 对象 ==================

  //表格数据
  const list = ref([])
  const total = ref(0)
  //单个护理计划对象，在添加/编辑时临时保存填写的数据
  const carePlan = ref({})

  // ================== 选项 ==================

  // 状态选项（状态：0结束 1开始）
  const statusOptions = [
    {value: 1, label: '进行中'},
    {value: 0, label: '已结束'},
  ]

  // 执行周期选项（执行周期：0天 1周 2月）
  const executeCycleOptions = [
    {value: 0, label: '每天'},
    {value: 1, label: '每周'},
    {value: 2, label: '每月'},
  ]

  // 执行日（每周）选项：执行周期选"每周"时显示，值1-7对应周一~周日
  const weekDayOptions = [
    {value: 1, label: '周一'},
    {value: 2, label: '周二'},
    {value: 3, label: '周三'},
    {value: 4, label: '周四'},
    {value: 5, label: '周五'},
    {value: 6, label: '周六'},
    {value: 7, label: '周日'},
  ]

  // 执行日（每月）选项：执行周期选"每月"时显示，值1号~31号
  const monthDayOptions = Array.from({length: 31}, (_, i) => ({value: i + 1, label: `${i + 1}号`}))

  // ================== 下拉数据 ==================

  // 护理等级选项，进入页面一次加载全部启用状态等级，供下拉框展示"等级名称"
  const careLevelOptions = ref([])
  const loadCareLevelOptions = () => {
    careLevelApi.listAll().then(result => {
      careLevelOptions.value = result.data
    })
  }
  loadCareLevelOptions()

  // 护理项目选项：进入页面一次加载全部启用状态护理项目，供添加/编辑里"护理项目下拉框"选择
  const careItemOptions = ref([])
  const loadCareItemOptions = () => {
    careItemApi.listAll().then(result => {
      careItemOptions.value = result.data
    })
  }
  loadCareItemOptions()

  // 老人远程搜索：存放远程搜索出来的可选老人列表，供下拉框展示"姓名（身份证号）"
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

  // 护理人员远程搜索：存放远程搜索出来的可选护理人员列表，供下拉框展示"姓名（用户名）"
  // 不做角色限定（搜索所有用户），避免删掉"护工"角色后导致无法绑定护理人员
  const caregiverOptions = ref([])
  // 是否正在加载远程搜索结果（控制下拉框的loading转圈）
  const caregiverLoading = ref(false)
  // 远程搜索方法：根据用户输入的护理人员姓名（可部分可全部）去后端搜索所有用户
  const loadCaregiverOptions = (query) => {
    // 没有输入内容时不搜索，直接清空列表
    if (!query) {
      caregiverOptions.value = []
      return
    }
    caregiverLoading.value = true
    userApi.searchByName(query).then(result => {
      caregiverOptions.value = result.data
    }).finally(() => {
      caregiverLoading.value = false
    })
  }

  // ================== 变量 ==================

  //分页信息和搜索条件（按计划名称、老人、状态模糊搜索）
  const carePlanQuery = ref({
    name: '',
    elderId: '',
    status: '',
    page: 1,
    limit: 10
  })

  //创建时间范围，用于模糊搜索用，初始化置为空，在日期选择框选择后被赋值
  const createTimeRange = ref([])

  //添加、编辑对话框标题
  const title = ref()
  //添加、编辑对话框的弹出控制
  const drawerCarePlanVisible = ref(false)

  //护理项目子表行数据（添加/编辑护理计划时，动态维护该计划包含哪些护理项目）
  const planItemList = ref([])

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    carePlanQuery.value.beginCreateTime = createTimeRange.value?.[0]
    carePlanQuery.value.endCreateTime = createTimeRange.value?.[1]

    carePlanApi.list(carePlanQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    carePlanQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    carePlanQuery.value = {
      name: '',
      elderId: '',
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
      carePlanApi.deleteById(id).then(result => {
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
      carePlanApi.deleteAll(ids).then(result => {
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
    drawerCarePlanVisible.value = true
    title.value = '添加'
    carePlan.value = {status: 1} //状态默认开始
    elderOptions.value = [] //清空老人搜索结果
    caregiverOptions.value = [] //清空护工搜索结果
    planItemList.value = [] //新增时护理项目子表默认为空，由用户点"添加项目"新增
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerCarePlanVisible.value = true
    title.value = '编辑'
    carePlan.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    carePlanApi.selectById(id).then(result => {
      carePlan.value = result.data
      //编辑时回显该计划包含的护理项目子表
      carePlanApi.getCareItemsById(id).then(res => {
        planItemList.value = (res.data || []).map(item => ({
          careItemId: item.careItemId,
          executeTime: item.executeTime || '',
          executeDay: item.executeDay || '',
          executeCycle: item.executeCycle,
          remark: item.remark || ''
        }))
      })
      //编辑时回显当前绑定的老人：把该老人生成一个可显示的下拉选项，这样下拉框才能显示出"姓名（身份证号）"，而不是只显示一个数字id
      if (carePlan.value.elderId) {
        elderApi.selectById(carePlan.value.elderId).then(res => {
          if (res.code === 1) {
            elderOptions.value = [res.data]
          }
        })
      }
      //编辑时回显当前绑定的护理人员：把该护卫生成一个可显示的下拉选项，这样下拉框才能显示出"姓名（用户名）"，而不是只显示一个数字id
      if (carePlan.value.userId) {
        userApi.selectById(carePlan.value.userId).then(res => {
          if (res.code === 1) {
            caregiverOptions.value = [res.data]
          }
        })
      }
    })
  }

  const formRef = ref()
  //对话框dialog输入规则校验
  const dialogRules = {
    name: [
      {required: true, message: '请输入计划名称', trigger: 'blur'},
      {min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur'}
    ],
    elderId: [
      {required: true, message: '请选择老人', trigger: 'blur'}
    ],
    userId: [
      {required: true, message: '请选择护理人员', trigger: 'blur'}
    ],
    careLevelId: [
      {required: true, message: '请选择护理等级', trigger: 'blur'}
    ],
    startDate: [
      {required: true, message: '请选择开始日期', trigger: 'blur'}
    ],
    endDate: [
      {required: true, message: '创建计划后需一次性生成任务，必须选择结束日期', trigger: 'blur'}
    ]
  }

  //点击【添加项目】按钮：在护理项目子表里新增一行空白子条目
  const addPlanItem = () => {
    planItemList.value.push({
      careItemId: '',          //护理项目id，暂未选，保存时忽略空行
      executeTime: '',         //护理服务时间
      executeDay: '',          //执行日：选"每周"存周几、选"每月"存几号，每天为空
      executeCycle: 0,         //执行周期，默认每天
      remark: ''
    })
  }

  //删除护理项目子表的某一行
  const removePlanItem = (index) => {
    planItemList.value.splice(index, 1)
  }

  //提取护理项目子表中已选护理项目的行，转成后端需要的CarePlanItem结构（没选护理项目的空行直接忽略）
  const buildCarePlanItems = () => {
    return planItemList.value
        .filter(item => item.careItemId)
        .map(item => ({
          careItemId: item.careItemId,
          executeTime: item.executeTime,
          executeDay: item.executeDay,
          executeCycle: item.executeCycle,
          remark: item.remark
        }))
  }

  //保存某计划的护理项目（先删后插），前端不传id，由后端统一处理
  const saveCareItems = (id, carePlanItems) => {
    carePlanApi.updateCareItems(id, carePlanItems).then(result => {
      if (result.code !== 1) {
        ElMessage.error(result.msg)
      }
    })
  }

  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //丢开没选护理项目的空行，再校验已选项目必须填写护理服务时间
          const carePlanItems = buildCarePlanItems()
          for (const item of carePlanItems) {
            if (!item.executeTime) {
              ElMessage.error('请为每个护理项目选择护理服务时间')
              return
            }
            //执行周期是每周/每月时，还必须选择执行日（周几/几号），否则不符合业务
            if (item.executeCycle !== 0 && !item.executeDay) {
              ElMessage.error('每周、每月的护理项目必须选择执行日（周几/几号）')
              return
            }
          }
          //校验通过，执行新增/编辑接口
          if (carePlan.value.id) {//编辑
            carePlanApi.update(carePlan.value.id, carePlan.value).then(result => {
              if (result.code === 1) {
                //保存该计划包含的护理项目
                saveCareItems(carePlan.value.id, carePlanItems)
                ElMessage.success(result.msg)
                drawerCarePlanVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            carePlanApi.add(carePlan.value).then(result => {
              if (result.code === 1) {
                //新增接口返回新计划id，用它保存该计划包含的护理项目
                saveCareItems(result.data, carePlanItems)
                ElMessage.success(result.msg)
                drawerCarePlanVisible.value = false
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
          <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('carePlan:add')">添加</el-button>
          <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('carePlan:deleteAll')">批量删除</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="计划名称">
        <el-input v-model="carePlanQuery.name" placeholder="请输入计划名称" clearable style="width: 180px"/>
      </el-form-item>
      <!-- 按老人搜索护理计划，复用远程搜索下拉框 -->
      <el-form-item label="老人">
        <el-select
            v-model="carePlanQuery.elderId"
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
      <el-form-item label="状态">
        <el-select v-model="carePlanQuery.status" placeholder="全部" clearable style="width: 130px">
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
      <el-table-column prop="name" label="计划名称" width="180" :show-overflow-tooltip="true"/>
      <el-table-column prop="elderName" label="老人" width="120"/>
      <el-table-column prop="userName" label="护理人员" width="120"/>
      <el-table-column prop="careLevelName" label="护理等级" width="120"/>
      <el-table-column prop="startDate" label="开始日期" width="110" align="center"/>
      <el-table-column prop="endDate" label="结束日期" width="110" align="center">
        <template #default="{row}">
          {{ row.endDate || '—' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{row}">
          <!-- 状态只有2种，直接内联判断 -->
          <el-tag v-if="row.status === 1" type="success">进行中</el-tag>
          <el-tag v-else type="info">已结束</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160"/>
      <el-table-column align="center" width="250px" fixed="right" label="操作">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('carePlan:update')">编辑</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('carePlan:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="carePlanQuery.page"
        v-model:page-size="carePlanQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerCarePlanVisible" :title="title" size="60%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="carePlan" :rules="dialogRules">
      <el-form-item prop="name" label="计划名称" :label-width="80">
        <el-input v-model="carePlan.name" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="elderId" label="老人" :label-width="80">
        <el-select
            v-model="carePlan.elderId"
            filterable
            remote
            reserve-keyword
            clearable
            placeholder="请输入老人姓名搜索"
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
      <el-form-item prop="userId" label="护理人员" :label-width="80">
        <el-select
            v-model="carePlan.userId"
            filterable
            remote
            reserve-keyword
            clearable
            placeholder="请输入护工姓名搜索"
            :remote-method="loadCaregiverOptions"
            :loading="caregiverLoading"
            style="width: 220px">
          <el-option
              v-for="item in caregiverOptions"
              :key="item.id"
              :label="`${item.realName}（${item.name}）`"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="careLevelId" label="护理等级" :label-width="80">
        <el-select v-model="carePlan.careLevelId" placeholder="请选择护理等级" style="width: 220px">
          <el-option
              v-for="item in careLevelOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="startDate" label="开始日期" :label-width="80">
        <el-date-picker v-model="carePlan.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择开始日期" style="width: 220px"/>
      </el-form-item>
      <el-form-item prop="endDate" label="结束日期" :label-width="80">
        <el-date-picker v-model="carePlan.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择结束日期" style="width: 220px"/>
      </el-form-item>
      <el-form-item prop="status" label="状态" :label-width="80">
        <el-select v-model="carePlan.status" placeholder="请选择状态" style="width: 220px">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <!--护理项目子表：在添加/编辑护理计划时分配该计划包含哪些护理项目-->
    <el-divider content-position="left">护理项目</el-divider>
    <!--点击【添加项目】按钮，在下面的表格里新增一行空白子条目-->
    <el-button size="small" type="primary" :icon="Plus" @click="addPlanItem">添加项目</el-button>
    <el-table :data="planItemList" border size="small" style="width: 100%; margin-top: 10px">
      <el-table-column label="护理项目" min-width="150">
        <template #default="{ row }">
          <el-select v-model="row.careItemId" placeholder="请选择护理项目" filterable clearable style="width: 100%">
            <el-option
                v-for="item in careItemOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="护理服务时间" width="160" align="center">
        <template #default="{ row }">
          <el-time-picker
              v-model="row.executeTime"
              value-format="HH:mm:ss"
              placeholder="选择时间"
              style="width: 140px"
          />
        </template>
      </el-table-column>
      <!--执行日：执行周期选"每周"时选周几、选"每月"时选几号，每天时禁用-->
      <el-table-column label="执行日" width="120" align="center">
        <template #default="{ row }">
          <el-select
              v-model="row.executeDay"
              placeholder="选择"
              :disabled="row.executeCycle === 0"
              style="width: 100px">
            <el-option
                v-for="item in (row.executeCycle === 1 ? weekDayOptions : monthDayOptions)"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="执行周期" width="120" align="center">
        <template #default="{ row }">
          <el-select v-model="row.executeCycle" style="width: 100px">
            <el-option
                v-for="item in executeCycleOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="删除" width="70" align="center">
        <template #default="{ $index }">
          <el-button size="small" type="danger" :icon="Delete" @click="removePlanItem($index)"/>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerCarePlanVisible = false">取消</el-button>
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
</style>