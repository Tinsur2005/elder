<script setup>
  import rolesApi from '@/api/roles.js'
  import permissionApi from '@/api/permission.js'
  import {nextTick, ref} from 'vue'
  import {ElMessage, ElMessageBox} from 'element-plus'
  import {Delete, EditPen, Plus, Stamp} from "@element-plus/icons-vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  //表格数据
  const list = ref([])
  const total = ref(0)
  //分页信息和搜索条件
  const roleQuery = ref({
    name: '',
    code: '',
    page: 1,
    limit: 10
  })

  //加载数据
  const createTimeRange = ref([])
  const loadData = () => {
    roleQuery.value.beginCreateTime = createTimeRange.value?.[0]
    roleQuery.value.endCreateTime = createTimeRange.value?.[1]

    rolesApi.list(roleQuery.value).then(result => {
      list.value = result.data.records
      total.value = result.data.total
    })
  }

  loadData()

  const onSearch = () => {
    roleQuery.value.page = 1 //重置搜索时页码
    loadData()
  }

  //重置按钮点击事件
  const reset = () => {
    roleQuery.value = {
      name: '',
      code: '',
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
      rolesApi.deleteById(id).then(result => {
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
      rolesApi.deleteAll(ids).then(result => {
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
  const drawerRoleVisible = ref(false)
  const role = ref({})
  const title = ref()

  const showAddDialog = () => {
    drawerRoleVisible.value = true
    title.value = '添加'
    role.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }

  const showUpdateDialog = (id) => {
    drawerRoleVisible.value = true
    title.value = '编辑'
    role.value = {}
    //清空上一次窗口残留的校验错误，避免红字带到新窗口里
    nextTick(() => {
      formRef.value?.clearValidate()
    })
    rolesApi.selectById(id).then(result => {
      role.value = result.data
    })
  }

  const formRef = ref()
  //对话框dialog输入规则校验
  const dialogRules = {
    name: [
      {required: true, message: '请输入角色名称', trigger: 'blur'},
      {min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur'}
    ],
    code: [
      {required: true, message: '请输入角色编码', trigger: 'blur'},
      {min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur'}
    ]
  }

  const addOrUpdate = () => {
    // 执行表单整体校验，校验不通过则不提交
    formRef.value.validate()
        .then(() => {
          //校验通过，执行新增/编辑接口
          if (role.value.id) {//编辑
            rolesApi.update(role.value.id, role.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerRoleVisible.value = false
                loadData()
              } else {
                ElMessage.error(result.msg)
              }
            })
          } else {//添加
            rolesApi.add(role.value).then(result => {
              if (result.code === 1) {
                ElMessage.success(result.msg)
                drawerRoleVisible.value = false
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

  // ============ 权限分配 ============
  //权限分配抽屉的显示控制
  const drawerPermissionVisible = ref(false)
  //权限树数据（List<PermissionVO>）
  const treeData = ref([])
  //权限树实例，用于回显勾选状态
  const treeRef = ref()
  //el-tree 的展示字段配置：树节点显示 name，子节点取 children
  const defaultProps = ref({
    children: 'children',
    label: 'name'
  })
  //递归收集权限树中的叶子节点id（没有子节点的权限，通常是按钮权限）
  const getCheckedLeafIdList = (permissionVOList, leafIdList) => {
    permissionVOList.forEach(permissionVO => {
      if (permissionVO.children && permissionVO.children.length) {
        //有子节点，继续往下递归找叶子
        getCheckedLeafIdList(permissionVO.children, leafIdList)
      } else {
        //没有子节点的就是叶子，收集它的id
        leafIdList.push(permissionVO.id)
      }
    })
  }

  //打开权限分配抽屉：加载权限树和回显该角色已分配的权限
  const showAssignedPermissionDialog = (row) => {
    drawerPermissionVisible.value = true
    role.value = row
    treeData.value = []
    //先加载权限树
    permissionApi.selectPermissionTree().then(treeResult => {
      treeData.value = treeResult.data
      //再查该角色已分配的权限id集合，用于回显勾选
      return rolesApi.selectPermissionById(row.id)
    }).then(permissionResult => {
      const assignedIds = permissionResult.data || []
      //回显时只勾选这个角色"已分配的叶子权限"（一般是按钮权限）
      //父级目录、菜单会随叶子自动联动成"勾选"或"半选"状态，
      //这样就不会把整个菜单权限误当成都勾选了
      let checkedLeafIdList = []
      getCheckedLeafIdList(treeData.value, checkedLeafIdList)
      const checkedIds = checkedLeafIdList.filter(id => assignedIds.includes(id))
      //等树节点渲染完成后，再回显勾选状态
      nextTick(() => {
        treeRef.value?.setCheckedKeys(checkedIds)
      })
    })
  }

  //保存角色权限：收集当前勾选且半选的权限id，调用先删后插接口
  const assignPermission = () => {
    //getCheckedNodes(leafOnly=false, includeHalfChecked=true)
    //既返回完全勾选的节点，也返回"半选"的父级节点（目录、菜单）
    //这样即使某个按钮没勾选，它的父级菜单处于半选状态也会一并保存，
    //避免"取消一个按钮权限就把整个菜单权限弄丢"的问题
    let checkedNodes = treeRef.value.getCheckedNodes(false, true)
    let permissionIds = checkedNodes.map(node => node.id)
    rolesApi.updatePermission(role.value.id, permissionIds).then(result => {
      if (result.code === 1) {
        ElMessage.success(result.msg)
        drawerPermissionVisible.value = false
      } else {
        ElMessage.error(result.msg)
      }
    })
  }
</script>

<template>
  <el-card class="">
    <template #header>
      <div class="header">
        <el-button type="primary" :icon="Plus" @click="showAddDialog" v-if="hasBtnPermission('role:add')">添加</el-button>
        <el-button type="danger" :icon="Delete" @click="deleteAll" v-if="hasBtnPermission('role:deleteAll')">批量删除</el-button>
      </div>
    </template>
    <!--模糊查找-->
    <el-form :inline="true">
      <el-form-item label="角色名称">
        <el-input v-model="roleQuery.name" placeholder="请输入角色名称" clearable style="width: 200px"/>
      </el-form-item>
      <el-form-item label="角色编码">
        <el-input v-model="roleQuery.code" placeholder="请输入角色编码" clearable style="width: 200px"/>
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
      <el-table-column prop="name" label="角色名称" width="150" :show-overflow-tooltip="true"/>
      <el-table-column prop="code" label="角色编码" :show-overflow-tooltip="true"/>
      <el-table-column prop="description" label="角色描述" :show-overflow-tooltip="true"/>
      <el-table-column prop="createTime" label="创建时间" :show-overflow-tooltip="true"/>
      <el-table-column align="center" width="280px" fixed="right" label="操作">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row.id)" v-if="hasBtnPermission('role:update')">编辑</el-button>
          <el-button size="small" type="success" :icon="Stamp" @click="showAssignedPermissionDialog(row)">权限分配</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" v-if="hasBtnPermission('role:deleteById')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="roleQuery.page"
        v-model:page-size="roleQuery.limit"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />
  </el-card>


  <!--添加、编辑弹出框-->
  <el-drawer v-model="drawerRoleVisible" :title="title" size="35%" :close-on-click-modal="true">
    <el-form ref="formRef" :model="role" :rules="dialogRules">
      <el-form-item prop="name" label="角色名称" :label-width="80">
        <el-input v-model="role.name" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="code" label="角色编码" :label-width="80">
        <el-input v-model="role.code" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="角色描述" :label-width="80">
        <el-input v-model="role.description" autocomplete="off" type="textarea" :rows="3"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerRoleVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
  </el-drawer>

  <!--权限分配弹出框-->
  <el-drawer v-model="drawerPermissionVisible" title="权限分配" size="35%" :close-on-click-modal="true">
    <el-tree
        :data="treeData"
        ref="treeRef"
        show-checkbox
        node-key="id"
        default-expand-all
        :props="defaultProps">
    </el-tree>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerPermissionVisible = false">取消</el-button>
        <el-button type="primary" @click="assignPermission">保存</el-button>
      </div>
    </template>
  </el-drawer>
</template>