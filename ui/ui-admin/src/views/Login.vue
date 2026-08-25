<script setup>
  import {ref} from "vue";
  import {User, Lock} from "@element-plus/icons-vue";
  import {ElMessage} from "element-plus";
  import {useRouter} from "vue-router";
  import userApi from "@/api/user.js";
  import {useTokenStore} from "@/store/token.js";

  const tokenStore = useTokenStore();
  const router = useRouter()

  //表单校验模型
  const rules = {
    name: [
      {required: true, message: '请输入用户名', trigger: 'blur'},
      {min: 4, max: 16, message: '长度在 4 到 16 个字符', trigger: 'blur'}
    ],
    password: [
      {required: true, message: '请输入密码', trigger: 'blur'},
      {min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur'}
    ]
  }

  //定义数据模型
  const user = ref({
    name: '',
    password: ''
  })

  const login = () => {

    userApi.login(user.value).then(result => {
      if(result.code == 1) {
        ElMessage.success(result.msg)
        tokenStore.setToken(result.data)
        router.push('/')
      } else {
        ElMessage.error(result.msg)
      }
    })

  }
</script>

<template>
  <div class="login-bg">
    <!-- 登录表单 -->
    <el-form class="form-login" ref="form" size="large" autocomplete="off" :model="user" :rules="rules">
      <el-form-item>
        <h1>登录</h1>
      </el-form-item>
      <el-form-item prop="name">
        <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="user.name"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码"
                  v-model="user.password" show-password="true"></el-input>
      </el-form-item>
      <el-form-item class="flex">
        <div class="flex">
          <el-checkbox>记住我</el-checkbox>
<!--          <el-link type="primary" :underline="false">忘记密码？</el-link>-->
        </div>
      </el-form-item>
      <!-- 登录按钮 -->
      <el-form-item>
        <el-button class="button" type="primary" auto-insert-space @click="login">登录</el-button>
      </el-form-item>
    </el-form>
  </div>

</template>

<style scoped>

  .login-bg {
    height: 100%;
    background-repeat: no-repeat;
    background-position: center;
    background-attachment: fixed;
    background-size: cover;
  }

  .form-login {
    width: 280px;
    padding: 20px;
    position: absolute;
    top: 20%;
    left: calc(50% - 150px);
    background-color: #FFF;
    box-shadow: 10px 10px 30px #000;
  }
</style>