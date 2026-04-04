<template>
  <div style="display: flex; justify-content: center; align-items: center;margin-top: 10px; ">
    <div style="width: 80%;display: flex;height: 500px;">
      <div  style="margin: 80px 60px;">
        <el-form :model="formData" ref="formRef" label-width="120px">
        <el-row :gutter="25">
          <el-col :span="10">
              <el-input style="margin-bottom: 15px;" placeholder="请输入登录用户名" v-model="formData.username"></el-input>
              <el-input style="margin-bottom: 15px;" placeholder="登录密码" type="password" v-model="formData.password"></el-input>
              <el-input style="margin-bottom: 15px;" placeholder="年龄" v-model="formData.age"></el-input>
              <el-input style="margin-bottom: 15px;" placeholder="邮箱地址" v-model="formData.email"></el-input>
          </el-col>
          <el-col :span="10">
              <el-input style="margin-bottom: 15px;" placeholder="名字" v-model="formData.name"></el-input>
              <el-input style="margin-bottom: 15px;" placeholder="手机账号" v-model="formData.phone"></el-input>
              <el-form-item style="margin-left: -70px;" label="性别">
              <el-select v-model="formData.gender" placeholder="请选择性别">
                <el-option label="男" value="male"></el-option>
                <el-option label="女" value="female"></el-option>
              </el-select>
            </el-form-item>
            <el-input placeholder="验证码" v-model="formData.captcha" style="width: calc(100% - 80px);margin-right: 10px;"></el-input>
              <el-button style="background: rgb(18, 180, 139);color: aliceblue;"  @click="handleGetCaptcha">获取</el-button>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button  style="background: rgb(18, 180, 139);width: 50%;color: aliceblue;" @click="handleSubmit">立即注册<el-icon size="20"><Right /></el-icon></el-button>
        </el-form-item>
        
      </el-form>
      </div>


      <!-- 右侧 -->
        <h2 style="color: #222; font-size: 22px;">注册后您将享有</h2>
        <p style="color: #7a7a7a;" >✓ 专业的疾病查询与诊断服务</p>
        <p style="color: #7a7a7a;" >✓ 全面的药品信息与使用指导</p>
        <p style="color: #7a7a7a;" >✓ 在线智能兽医咨询服务</p>
        <p style="color: #7a7a7a;" ><strong>客服热线: 400-888-6666</strong></p>

    </div>
  </div>
</template>

<script setup>
import {ref,reactive} from 'vue'
import {ElMessage} from 'element-plus'

const formData = reactive({
  username: '',
  name: '',
  password: '',
  phone: '',
  gender: '',
  email: '',
  captcha:'',
  age: '',
})


const formRef = ref(null)

const handleGetCaptcha  = () => {
  console.log('获取验证码');
}

const handleSubmit  = () => {
  formRef.value.validate((valid) => {
    if(valid) {
      //这里添加提交表单的逻辑
      console.log('表单数据',formData);
      ElMessage.success('注册成功')
      
    } else{
      ElMessage.error('请填写完整信息')
      return false
    }
  })
}

</script>

<style scoped>
h2 {
  font-size: 24px;
  margin-bottom: 10px;
}

p {
  margin-bottom: 10px;
}

.el-button--success {
  width: 100%;
}
</style>