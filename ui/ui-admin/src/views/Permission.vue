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
  import permissionApi from "@/api/permission.js"
  import {ref} from "vue"
  import {ElMessage, ElMessageBox} from "element-plus"
  import {Plus, EditPen, Delete} from '@element-plus/icons-vue'
  import IconPicker from "@/components/IconPicker.vue";
  import hasBtnPermission from "@/utils/btnPermission.js";

  // ========== 变量 ==========
  //保存返回树形结构数据，List<PermissionVO>
  const list = ref([])

  //标题，用于显示添加/编辑对话框的标题
  const title = ref()

  //当前操作的权限对象，在添加/编辑时临时保存填写的数据
  const permission = ref({})

  //各个权限类型的禁用标志，控制添加子节点时可选的类型
  const typeDisabled = ref(false)
  const type0Disabled = ref(false)
  const type1Disabled = ref(false)
  const type2Disabled = ref(false)

  // ========== 对话框dialog弹出控制 ==========
  const drawerPermissionVisible = ref(false)

  // ========== 方法 ==========
  //加载数据
  const loadData = () => {
    permissionApi.selectPermissionTree().then(result => {
      list.value = result.data
    })
  }
  loadData()

  //根据id删除
  const deleteById = (id) => {
    ElMessageBox.confirm(
        '您确认要删除吗?',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          lockScroll: false //防止抖动
        }
    ).then(() => {
      permissionApi.deleteById(id).then(result => {
        ElMessage.success(result.msg)
        loadData()
      })
    })
  }

  //根据id更新状态（0：禁止，1：正常）
  const handleSwitchChange = (row) => {
    permissionApi.update(row).then(result => {
      if (result.code === 1) {
        if (row.status === 1) {
          ElMessage.success('已启用')
        } else {
          ElMessage.info('已禁用')
        }
      } else {
        ElMessage.error(result.msg)
        loadData() //失败则重新加载还原
      }
    })
  }

  //根据行数据打开添加子菜单对话框（传row为添加下级，不传为添加顶级菜单）
  const showAddDialog = (row) => {
    drawerPermissionVisible.value = true
    permission.value = {sort: 0, status: 1} //排序默认0、状态默认启用(1)
    title.value = '添加下级菜单'

    if (row) {//点击右侧添加按钮
      permission.value.parentId = row.id
      permission.value.parentName = row.name
      if (row.type == 0) {
        //在目录下面添加，只能选择：菜单、按钮（不能选目录本身）
        permission.value.type = 1
        typeDisabled.value = false
        type0Disabled.value = true
        type1Disabled.value = false
        type2Disabled.value = false
      } else if (row.type == 1) {
        //在菜单下面添加，只能选择：按钮（不能选目录和菜单）
        permission.value.type = 2
        typeDisabled.value = false
        type0Disabled.value = true
        type1Disabled.value = true
        type2Disabled.value = false
      }
    } else {
      //添加顶级菜单
      title.value = '添加顶级菜单'
      //typeDisabled.value = true;
      type0Disabled.value = false
      type1Disabled.value = false
      type2Disabled.value = true
      permission.value.type = 0
      permission.value.parentId = 0
    }
  }

  //根据编辑时当前节点的类型和是否有子节点，计算允许切换的权限类型
//type：当前权限类型（0目录、1菜单、2按钮）；children：该节点的子节点列表
const setEditTypeDisable = (type, children) => {
  //判断当前节点下是否还有子节点（目录/菜单下面可能还有下级）
  const hasChildren = children && children.length > 0
  //最终允许勾选上的类型集合
  let allow = []
  if (hasChildren) {
    //有子节点的节点类型不能改：目录下面有菜单只能保持目录，菜单下面有子级只能保持菜单
    allow = [type]
  } else {
    //无子节点（叶子）才允许切换，并按当前类型限定可切换范围
    if (type === 0) {
      //目录叶子：可切换为 目录、菜单、按钮
      allow = [0, 1, 2]
    } else if (type === 1) {
      //菜单叶子：可切换为 菜单、按钮
      allow = [1, 2]
    } else {
      //按钮叶子（以此类推）：可切换为 菜单、按钮
      allow = [1, 2]
    }
  }
  //不在允许集合里的类型就禁用对应的单选项
  type0Disabled.value = !allow.includes(0)
  type1Disabled.value = !allow.includes(1)
  type2Disabled.value = !allow.includes(2)
}

//根据id打开编辑对话框
const showUpdateDialog = (row) => {
  drawerPermissionVisible.value = true
  title.value = '编辑'
  permission.value = {}
  permissionApi.selectById(row.id).then(result => {
    permission.value = result.data
    //根据当前节点类型和是否有子节点，计算编辑时可切换的类型
    setEditTypeDisable(result.data.type, row.children)
  })
}

  //新增或编辑提交
  const addOrUpdate = () => {
    if (permission.value.id) {//编辑
      permissionApi.update(permission.value).then(result => {
        ElMessage.success(result.msg)
        drawerPermissionVisible.value = false
        loadData()
      })
    } else {//添加
      permissionApi.add(permission.value).then(result => {
        ElMessage.success(result.msg)
        drawerPermissionVisible.value = false
        loadData()
      })
    }
  }
</script>

<template>
  <el-card>
    <template #header>
      <div class="header">
        <div class="header-left">
          <el-button type="primary" :icon="Plus" @click="showAddDialog()">添加顶级菜单</el-button>
        </div>
        <div class="header-right"></div>
      </div>
    </template>
    <el-table :data="list" style="width: 100%; margin-bottom: 20px" row-key="id" border>
    <el-table-column prop="name" label="名称"/>
      <el-table-column prop="icon" width="80px" label="图标" #default="{row}">
        <el-icon><component :is="row.icon" /></el-icon>
      </el-table-column>
    <el-table-column prop="type" label="权限类型">
      <template #default="{row}">
        <el-tag v-if="row.type == 0">目录权限</el-tag>
        <el-tag v-if="row.type == 1" type="success">菜单权限</el-tag>
        <el-tag v-if="row.type == 2" type="warning">按钮权限</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="path" label="路由地址"></el-table-column>
    <el-table-column prop="permissionValue" label="按钮权限"></el-table-column>
    <el-table-column prop="sort" label="排序"></el-table-column>
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
    <el-table-column label="操作" align="center" width="250px" fixed="right" v-if="hasBtnPermission('permission:operation')">
      <template #default="{row}">
        <el-button size="small" type="success" :icon="Plus" @click="showAddDialog(row)" :disabled="row.type == 2">添加</el-button>
        <el-button size="small" type="primary" :icon="EditPen" @click="showUpdateDialog(row)">修改</el-button>
        <el-button size="small" type="danger" :icon="Delete" @click="deleteById(row.id)" :disabled="row.children?.length > 0">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 添加、修改的dialog -->
  <el-drawer v-model="drawerPermissionVisible" :title="title" size="35%">
    <el-form :model="permission" label-width="80px">
      <el-form-item label="上级权限" v-if="!permission.id">
        <el-input v-model="permission.parentName" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="权限类型">
        <el-radio-group v-model="permission.type" :disabled="typeDisabled">
          <!-- 每个类型的可切换与否由 type0Disabled/type1Disabled/type2Disabled 控制，不再整组禁用，这样编辑时才能切换类型 -->
          <el-radio :label="0" :disabled="type0Disabled">目录</el-radio>
          <el-radio :label="1" :disabled="type1Disabled">菜单</el-radio>
          <el-radio :label="2" :disabled="type2Disabled">按钮</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="权限名称">
        <el-input v-model="permission.name"></el-input>
      </el-form-item>
      <el-form-item label="图标" v-if="permission.type == 0 || permission.type === 1">
        <IconPicker width="100px" v-model="permission.icon"></IconPicker>
      </el-form-item>
      <el-form-item label="排序">
        <div class="sort-field">
          <el-input-number
              v-model="permission.sort"
              controls-position="right"
              :min="0"
          />
          <div class="sort-tips">数字越小越靠前</div>
        </div>
      </el-form-item>
      <el-form-item label="路由地址" v-if="permission.type === 1">
        <el-input v-model="permission.path"/>
      </el-form-item>
      <el-form-item label="按钮权限" v-if="permission.type === 2">
        <el-input
            v-model="permission.permissionValue"
            maxlength="100"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawerPermissionVisible = false">取消</el-button>
        <el-button type="primary" @click="addOrUpdate">
          确认
        </el-button>
      </div>
    </template>
  </el-drawer>
  </el-card>
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

  .sort-field {
    display: flex;
    align-items: center;
  }

  .sort-tips {
    font-size: 12px;      /* 小字 */
    color: #999;          /* 灰色 */
    margin-left: 10px;    /* 与输入框保持间距 */
  }
</style>