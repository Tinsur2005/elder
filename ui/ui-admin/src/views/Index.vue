<script setup>
  import {
    Management,
    Promotion,
    UserFilled,
    User,
    Crop,
    EditPen,
    SwitchButton,
    CaretBottom, Plus
  } from '@element-plus/icons-vue'
  import avatar from '@/assets/default.png'
  //条目被点击后,调用的函数
  import {useRouter} from 'vue-router'
  import {useTokenStore} from "@/store/token.js";
  import {ElMessage, ElMessageBox} from "element-plus";
  import {useUserInfoStore} from "@/store/userInfo.js";
  import userApi from "@/api/user.js";
  import {ref} from "vue";

  const userInfoStore = useUserInfoStore()
  const tokenStore = useTokenStore()
  const router = useRouter();

  userApi.userInfo().then(result => {
    if(result.code === 1) {
      userInfoStore.setUserInfo(result.data)
    }
  })

  //控制用户信息对话框
  const dialogFormVisible = ref(false)
  const user = ref({})
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
    } else {
      //路由
      router.push('/user/' + command)
    }
  }
</script>

<template>
  <!-- element-plus中的容器 -->
  <el-container class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside width="200px">
      <div class="el-aside__logo"></div>
      <!-- element-plus的菜单标签 -->
      <el-menu active-text-color="#ffd04b" background-color="#232323" text-color="#fff"
               router>
        <el-menu-item index="/user">
          <el-icon>
            <Management/>
          </el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/elder">
          <el-icon>
            <Promotion/>
          </el-icon>
          <span>老人管理</span>
        </el-menu-item>
        <el-sub-menu index="1">
          <template #title>
            <el-icon>
              <UserFilled/>
            </el-icon>
            <span>个人中心</span>
          </template>
          <el-menu-item index="/user/info">
            <el-icon>
              <User/>
            </el-icon>
            <span>基本资料</span>
          </el-menu-item>
          <el-menu-item index="/user/avatar">
            <el-icon>
              <Crop/>
            </el-icon>
            <span>更换头像</span>
          </el-menu-item>
          <el-menu-item index="/user/resetPassword">
            <el-icon>
              <EditPen/>
            </el-icon>
            <span>重置密码</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
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
              <el-dropdown-item command="avatar" :icon="Crop">更换头像</el-dropdown-item>
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
  <el-dialog v-model="dialogFormVisible" :title="title" width="500" :lock-scroll="false" :close-on-click-modal="false">
    <el-form :model="user">
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
      <el-form-item label="姓名" :label-width="60">
        <el-input v-model="user.name" autocomplete="off" :disabled="user.id"/>
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
        <el-button type="primary" @click="addOrUpdate">
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
      background-color: #232323;

      &__logo {
        height: 120px;
        background: url('@/assets/logo.png') no-repeat center / 150px auto;
      }

      .el-menu {
        border-right: none;
      }
    }

    .el-header {
      background-color: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;

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

    .el-footer {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      color: #666;
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