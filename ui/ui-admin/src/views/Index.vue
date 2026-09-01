<script setup>
  import {
    Management,
    Promotion,
    UserFilled,
    User,
    Crop,
    EditPen,
    SwitchButton,
    CaretBottom, Plus,
    CollectionTag, SetUp,
    Document,
    HomeFilled
  } from '@element-plus/icons-vue'
  import avatar from '@/assets/default.png'
  //条目被点击后,调用的函数
  import {useRouter} from 'vue-router'
  import {useTokenStore} from "@/store/token.js";
  import {ElMessage, ElMessageBox} from "element-plus";
  import {useUserInfoStore} from "@/store/userInfo.js";
  import userApi from "@/api/user.js";
  import {nextTick, ref} from "vue";

  // ============ 对象  ============

  // 用户对象：当前登录的用户封装成的对象
  const user = ref({})
  // 修改密码DTO对象：用于修改密码时存储老密码和新密码
  const userPasswordDTO = ref({
    oldPassword: '',
    newPassword: ''
  })

  // ============ 存储  ============
  const userInfoStore = useUserInfoStore()
  const tokenStore = useTokenStore()
  const router = useRouter()
  const resetForm = ref()

  // 菜单
  const menuData = ref([]);

  // ============ 对话框控制  ============
  //控制用户信息对话框
  const dialogFormVisible = ref(false)
  //控制重置密码对话框
  const dialogResetPasswordDialog = ref(false)

  // ============ 方法  ============
  //获取用户信息
  const getUserInfo = () => {
    userApi.userInfo().then(result => {
      if(result.code === 1) {
        userInfoStore.setUserInfo(result.data.user)
        menuData.value = result.data.routerList
        userInfoStore.setBtnList(result.data.btnList)
      }
    })
  }
  getUserInfo()


  //上传图片
  const handleAvatarSuccess = (result) => {
    user.value.avatar = result.data;
  }

  //重置密码
  const resetPassword = async (formEl) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
      if (valid) {
        ElMessageBox.confirm(
            '确定修改密码？修改后需要重新登录',
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            }
        ).then(() => {
          userApi.resetPassword(userPasswordDTO.value).then(result => {
            if (result.code === 1) {
              ElMessage.success(result.msg)
              dialogResetPasswordDialog.value = false
              tokenStore.removeToken();
              userInfoStore.removeUserInfo();
              router.push('/login')
            } else {
              ElMessage.error(result.msg)
            }
          })
        })
      } else {
        ElMessage.error('表单验证失败');
      }
    })
  }

  //修改当前登录的用户信息
  const updateUserInfo = () => {
    userApi.update(user.value.id, user.value).then(result => {
      if (result.code == 1) {
        ElMessage.success(result.msg)
        dialogFormVisible.value = false
        getUserInfo()
      }
    })
  }

  //左上角下拉菜单点击功能
  const handleCommand = (command) => {
    //判断指令
    if (command === 'logout') {
        ElMessageBox.confirm(
            '确认退出吗？',
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            }
        ).then(() => {
            //退出登录
            tokenStore.removeToken()
            userInfoStore.removeUserInfo()
            ElMessage.success('退出登录成功')
            router.push('/login')
        })
    } else if (command === 'updateUserInfo') {
      dialogFormVisible.value = true
      //这样下会有严重问题，两个数据是绑定在一起的，修改了admin里面数据，adminInfoStore.admin也会修改，
      //如果用户点击取消没有修改，就会造成adminInfoStore.admin里面数据修改了
      //admin.value = adminInfoStore.admin
      Object.assign(user.value, userInfoStore.user) //不把两个数据绑定在一起
    } else if (command === 'resetPassword') {
      dialogResetPasswordDialog.value = true
      userPasswordDTO.value = {}
      nextTick(()=>{
        resetForm.value.resetFields()
      })
    } else {
      //路由
      router.push('/user/' + command)
    }
  }

  // ============ 规则校验  ============
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

  //自定义确认密码校验函数
  const rePasswordValid = (rule, value, callback) => {
    if (value == null || value == ''){
      return callback(new Error('请再次确认密码'))
    }
    if(userPasswordDTO.value.newPassword !== value) {
      return callback(new Error('两次输入的密码不一致'))
    }
    callback()
  }

  //表单校验规则
  const rules = ref({
    oldPassword: [
      {required: true, message: '请输入密码', trigger: 'blur'},
      {min: 3, max: 16, message: '密码长度必须为3~16位', trigger: 'blur'}
    ],
    newPassword: [
      {required: true, message: '请输入密码', trigger: 'blur'},
      {min: 3, max: 16, message: '密码长度必须为3~16位', trigger: 'blur'}
    ],
    reNewPassword: [
      {required: true, message: '请输入密码', trigger: 'blur'},
      {validator: rePasswordValid, trigger: 'blur' }
    ]
  })
</script>

<template>
  <!-- element-plus中的容器 -->
  <el-container class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside width="200px">
      <div class="el-aside__logo"></div>
      <!-- 用el-scrollbar包裹菜单：菜单过长时显示细圆角滚动条，替代丑陋的原生滚动条 -->
      <el-scrollbar class="aside-scrollbar">
        <!-- element-plus的菜单标签 -->
        <el-menu :default-active="$route.path" active-text-color="#409EFF" text-color="#303133"
                 :background-color="'#fff'" router>
        <!-- 动态生成菜单 -->
        <template v-for="(menu, index) in menuData" :index="index.toString()">
          <el-sub-menu v-if="menu.children?.length>0" :index="menu.name">
            <template #title>
              <component
                  class="icons"
                  :is="menu.icon"
                  style="width: 1em; height: 1em; margin-right: 8px" >
              </component>
              <span>{{ menu.name }}</span>
            </template>
            <el-menu-item v-for="(child, ind) in menu.children" :index="child.path">
              <el-icon><component :is="child.icon"></component></el-icon>
              <span>{{ child.name }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.path">
            <el-icon><component :is="menu.icon"></component></el-icon>
            <span>{{ menu.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>
      </el-scrollbar>
    </el-aside>
    <!-- 右侧主区域 -->
    <el-container>
      <!-- 头部区域 -->
      <el-header>
        <div><strong>智慧社区养老平台后台管理系统</strong></div>
        <!-- 下拉菜单 -->
        <!-- command: 条目被点击后会触发,在事件函数上可以声明一个参数,接收条目对应的指令 -->
        <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar :src="userInfoStore.user.avatar?userInfoStore.user.avatar:avatar"/>
                        <span style="margin-left: 8px;">欢迎您：</span>
                        <strong>{{ userInfoStore.user.name }}</strong>
                        <el-icon>
                            <CaretBottom/>
                        </el-icon>
                    </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="updateUserInfo" :icon="User">基本资料</el-dropdown-item>
              <el-dropdown-item command="resetPassword" :icon="EditPen">重置密码</el-dropdown-item>
              <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <!-- 中间区域 -->
      <el-main>
        <!-- <div style="width: 1290px; height: 570px;border: 1px solid red;">
                    内容展示区
                </div> -->
        <router-view></router-view>
      </el-main>
      <!-- 底部区域 -->
      <el-footer>©2026 智慧社区养老管理系统</el-footer>
    </el-container>
  </el-container>

  <!-- 修改个人信息的对话框 -->
  <el-dialog v-model="dialogFormVisible" :title="'个人信息'" width="500" :lock-scroll="false" :close-on-click-modal="false">
    <el-form :model="user">
      <el-form-item label="头像" :label-width="60">
        <el-upload
            class="avatar-uploader"
            action="/api/upload?dir=avatar"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="{Authorization: tokenStore.token}">
          <img v-if="user.avatar" :src="user.avatar" class="avatar"/>
          <el-icon v-else class="avatar-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>
        <div class="avatar-uploader-tips">
          头像图片建议尺寸150x150，文件大小不超过2MB，支持jpg/png/webp格式
        </div>
      </el-form-item>
      <el-form-item label="用户名" :label-width="60">
        <el-input v-model="user.name" autocomplete="off" :disabled="user.id"/>
      </el-form-item>
      <el-form-item label="姓名" :label-width="60">
        <el-input v-model="user.realName" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="邮箱" :label-width="60">
        <el-input v-model="user.email" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="手机号" :label-width="60">
        <el-input v-model="user.phone" autocomplete="off"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="updateUserInfo">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 重置密码的对话框 -->
  <el-dialog  v-model="dialogResetPasswordDialog" title="重置密码" width="500" :lock-scroll="false">
    <el-form ref="resetForm" :rules="rules" :model="userPasswordDTO">
      <el-form-item prop="oldPassword" label="原密码" :label-width="100">
        <el-input v-model="userPasswordDTO.oldPassword" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="newPassword" label="新密码" :label-width="100">
        <el-input v-model="userPasswordDTO.newPassword" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="reNewPassword" label="重复新密码" :label-width="100">
        <el-input v-model="userPasswordDTO.reNewPassword" autocomplete="off"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogResetPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="resetPassword(resetForm)">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
  .layout-container {
    height: 100vh;

    .el-aside {
      background-color: #fff;
      border-right: 1px solid #f0f0f0;
      display: flex;
      flex-direction: column;

      &__logo {
        height: 120px;
        background: url('@/assets/logo.png') no-repeat center / 170px auto;
        border-bottom: 1px solid #f5f5f5;
      }

      // 侧边栏滚动区域：占据logo以下的所有剩余高度，菜单过长时出现细滚动条
      .aside-scrollbar {
        flex: 1;

        // 美化滚动条：6px细轨道、圆角浅灰滑块，悬停时加深
        :deep(.el-scrollbar__bar.is-vertical) {
          width: 6px;
          right: 2px;
        }

        :deep(.el-scrollbar__thumb) {
          background-color: #c0c4cc;
          border-radius: 3px;
        }
      }

      .el-menu {
        border-right: none;

        .el-menu-item.is-active {
          background-color: #ecf5ff;
          border-right: 3px solid var(--el-color-primary);
          font-weight: 600;
        }
      }
    }

    .el-header {
      background-color: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid #f0f0f0;
      padding: 0 20px;

      .el-dropdown__box {
        display: flex;
        align-items: center;

        .el-icon {
          color: #999;
          margin-left: 10px;
        }

        &:active,
        &:focus {
          outline: none;
        }
      }
    }

    .el-main {
      background-color: #f5f7fa;
      padding: 16px;
    }

    .el-footer {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      color: #909399;
      background-color: #fff;
      height: 44px;
      border-top: 1px solid #f0f0f0;
    }
  }

  //对话框头像区域样式
  .avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  .avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
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