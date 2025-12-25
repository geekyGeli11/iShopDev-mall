<template>
  <!-- 顶部返回按钮 -->
  <div class="back-icon-container-fixed">
    <van-icon name="arrow-left" size="60" @click="goBack" class="back-icon" />
  </div>
  <div class="register-bg">
    <div class="register-card">
      <!-- 标题 -->
      <div class="register-title">会员注册流程</div>
      <!-- 步骤1 -->
      <div class="step1-block">
        <div class="step1-row">
          <div class="step-badge">1</div>
          <div class="step-label">扫描小程序二维码</div>
        </div>
        <div class="step1-qrcode-bg">
          <img class="step1-qrcode-img" src="https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/images/qrcode.jpg" alt="小程序二维码" />
        </div>
      </div>
      <!-- 分隔线1 -->
      <div class="step-dash" style="top:699px;"></div>
      <!-- 步骤2 -->
      <div class="step-block" style="top:723px;">
        <div class="step-badge">2</div>
        <div class="step-label">点击个人页</div>
      </div>
      <!-- 分隔线2 -->
      <div class="step-dash" style="top:819px;"></div>
      <!-- 步骤3 -->
      <div class="step-block" style="top:843px;">
        <div class="step-badge">3</div>
        <div class="step-label">完善个人资料</div>
      </div>
      <!-- 分隔线3 -->
      <div class="step-dash" style="top:939px;"></div>
      <!-- 步骤4 -->
      <div class="step-block" style="top:963px;">
        <div class="step-badge">4</div>
        <div class="step-label">注册成功</div>
      </div>
    </div>
  </div>
  <!-- 底部会员结算按钮 -->
  <div class="bottom-button-fixed">
    <button class="member-checkout-btn" @click="showMemberLogin = true">会员结算</button>
    <MemberLoginModal
      v-model="showMemberLogin"
      @login-success="handleLoginSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import MemberLoginModal from '@/components/business/MemberLoginModal/index.vue';

const router = useRouter();
const showMemberLogin = ref(false);

// 返回方法
const goBack = () => {
  router.back()
}

const handleLoginSuccess = (memberInfo: any) => {
  showMemberLogin.value = false;
  // 登录成功后可以跳转到会员结算页面
  router.push('/scan');
};
</script>

<style scoped lang="scss">
.register-bg {
  position: relative;
  width: 100vw;
  min-height: 100vh;
  background: #f5f5f5;
}
.register-card {
  position: absolute;
  width: 940px;
  height: 1257px;
  left: 70px;
  top: 150px;
  background: #fff;
  border-radius: 16px;
  box-sizing: border-box;
}
.back-icon-container-fixed {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 120px;
  background: #fff;
  border-bottom: 1px solid #E5E5E5;
  display: flex;
  align-items: center;
  z-index: 100;
  padding-left: 20px;
}
.back-icon {
  cursor: pointer;
}
.register-title {
  position: absolute;
  width: 288px;
  height: 67px;
  left: 50%;
  top: 48px;
  transform: translateX(-50%);
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 600;
  font-size: 48px;
  line-height: 67px;
  color: #0A0D05;
  text-align: center;
}
.step1-block {
  position: absolute;
  width: 472px;
  height: 544px;
  left: calc(50% - 472px/2 + 3px);
  top: 155px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 24px;
}
.step1-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 24px;
  width: 328px;
  height: 48px;
}
.step-badge {
  width: 48px;
  height: 48px;
  background: #A9FF00;
  border-radius: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 32px;
  line-height: 45px;
  color: #0A0D05;
}
.step-label {
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 32px;
  line-height: 45px;
  color: #0A0D05;
  text-align: center;
}
.step1-qrcode-bg {
  position: absolute;
  width: 472px;
  height: 472px;
  left: 0;
  top: 72px;
  background: rgba(169,255,0,0.3);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.step1-qrcode-img {
  width: 404px;
  height: 404px;
  border-radius: 16px;
  object-fit: contain;
  background: #fff;
}
.step-block {
  position: absolute;
  width: 478px;
  height: 96px;
  left: calc(50% - 478px/2 + 3px);
  background: #E5FFB3;
  border-radius: 16px;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 24px;
  gap: 24px;
  box-sizing: border-box;
}
.step-dash {
  position: absolute;
  width: 0px;
  height: 42px;
  left: calc(50% - 478px/2 + 50px);
  border-left: 2px dashed #0A0D05;
  z-index: 2;
}
.bottom-button-fixed {
  position: absolute;
  left: 0;
  width: 100vw;
  display: flex;
  justify-content: center;
  /* 距离 register-bg 容器底部 60px */
  top: calc(150px + 1257px + 60px);
  z-index: 101;
}
.member-checkout-btn {
  width: 940px;
  height: 126px;
  background-color: #20201E;
  border: none;
  border-radius: 16px;
  font-size: 48px;
  font-weight: 400;
  color: #A9FF00;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'PingFang SC';
  line-height: 1.4em;
}
</style>