<template>
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-picture" style="margin-right: 5px"></i>
      <span>页面底图装修</span>
    </el-card>
    
    <el-card shadow="never">
      <div class="background-config">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="首页背景" name="home">
            <background-setting 
              :config="backgroundConfigs.home" 
              @update="updateBackground('home', $event)"
              title="首页背景设置">
            </background-setting>
          </el-tab-pane>
          
          <el-tab-pane label="商品页背景" name="product">
            <background-setting 
              :config="backgroundConfigs.product" 
              @update="updateBackground('product', $event)"
              title="商品页背景设置">
            </background-setting>
          </el-tab-pane>
          
          <el-tab-pane label="个人中心背景" name="profile">
            <background-setting 
              :config="backgroundConfigs.profile" 
              @update="updateBackground('profile', $event)"
              title="个人中心背景设置">
            </background-setting>
          </el-tab-pane>
          
          <el-tab-pane label="DIY页面背景" name="diy">
            <background-setting 
              :config="backgroundConfigs.diy" 
              @update="updateBackground('diy', $event)"
              title="DIY页面背景设置">
            </background-setting>
          </el-tab-pane>
        </el-tabs>
        
        <div class="action-buttons">
          <el-button type="primary" @click="saveAllConfigs" :loading="saving">保存所有配置</el-button>
          <el-button @click="resetAllConfigs">重置所有</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import BackgroundSetting from './components/BackgroundSetting.vue'

export default {
  name: "BackgroundConfig",
  components: {
    BackgroundSetting
  },
  data() {
    return {
      activeTab: 'home',
      saving: false,
      backgroundConfigs: {
        home: {
          type: 'color', // color, image, gradient
          color: '#FFFFFF',
          image: '',
          gradient: {
            direction: 'to bottom',
            colors: ['#FFFFFF', '#F5F5F5']
          },
          opacity: 1,
          repeat: 'no-repeat',
          position: 'center center',
          size: 'cover'
        },
        product: {
          type: 'color',
          color: '#FFFFFF',
          image: '',
          gradient: {
            direction: 'to bottom',
            colors: ['#FFFFFF', '#F5F5F5']
          },
          opacity: 1,
          repeat: 'no-repeat',
          position: 'center center',
          size: 'cover'
        },
        profile: {
          type: 'color',
          color: '#FFFFFF',
          image: '',
          gradient: {
            direction: 'to bottom',
            colors: ['#FFFFFF', '#F5F5F5']
          },
          opacity: 1,
          repeat: 'no-repeat',
          position: 'center center',
          size: 'cover'
        },
        diy: {
          type: 'color',
          color: '#FFFFFF',
          image: '',
          gradient: {
            direction: 'to bottom',
            colors: ['#FFFFFF', '#F5F5F5']
          },
          opacity: 1,
          repeat: 'no-repeat',
          position: 'center center',
          size: 'cover'
        }
      }
    }
  },
  created() {
    this.loadConfigs();
  },
  methods: {
    loadConfigs() {
      // TODO: 从后端加载背景配置
    },
    updateBackground(page, config) {
      this.backgroundConfigs[page] = { ...config };
    },
    saveAllConfigs() {
      this.saving = true;
      // TODO: 保存所有背景配置到后端
      setTimeout(() => {
        this.saving = false;
        this.$message.success('背景配置保存成功');
      }, 1000);
    },
    resetAllConfigs() {
      this.$confirm('确定要重置所有背景配置吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 重置所有配置为默认值
        Object.keys(this.backgroundConfigs).forEach(key => {
          this.backgroundConfigs[key] = {
            type: 'color',
            color: '#FFFFFF',
            image: '',
            gradient: {
              direction: 'to bottom',
              colors: ['#FFFFFF', '#F5F5F5']
            },
            opacity: 1,
            repeat: 'no-repeat',
            position: 'center center',
            size: 'cover'
          };
        });
        this.$message.success('重置成功');
      });
    }
  }
}
</script>

<style scoped>
.operate-container {
  margin-bottom: 20px;
}

.background-config {
  padding: 20px;
}

.action-buttons {
  margin-top: 30px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}
</style>
