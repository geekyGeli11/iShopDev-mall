<template>
  <div class="freight-calculator">
    <el-form :model="calculateForm" ref="calculateForm" label-width="120px" size="small">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="商品数量：" prop="quantity">
            <el-input-number 
              v-model="calculateForm.quantity" 
              :min="1" 
              :precision="getPrecision()"
              style="width: 100%">
            </el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="8" v-if="chargeType === 2">
          <el-form-item label="商品重量：" prop="weight">
            <el-input-number 
              v-model="calculateForm.weight" 
              :min="0" 
              :precision="2"
              style="width: 100%">
            </el-input-number>
            <span style="margin-left: 5px">kg</span>
          </el-form-item>
        </el-col>
        <el-col :span="8" v-if="chargeType === 3">
          <el-form-item label="商品体积：" prop="volume">
            <el-input-number 
              v-model="calculateForm.volume" 
              :min="0" 
              :precision="2"
              style="width: 100%">
            </el-input-number>
            <span style="margin-left: 5px">m³</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="订单金额：" prop="orderAmount">
            <el-input-number 
              v-model="calculateForm.orderAmount" 
              :min="0" 
              :precision="2"
              style="width: 100%">
            </el-input-number>
            <span style="margin-left: 5px">元</span>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="收货省份：" prop="receiverProvince">
            <el-input v-model="calculateForm.receiverProvince" placeholder="请输入收货省份" style="width: 100%"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收货城市：" prop="receiverCity">
            <el-input v-model="calculateForm.receiverCity" placeholder="请输入收货城市" style="width: 100%"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button type="primary" @click="calculateFreight" :loading="calculating">
              计算运费
            </el-button>
            <el-button @click="resetCalculateForm">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    
    <!-- 计算结果 -->
    <el-card v-if="calculateResult" class="result-card" shadow="never">
      <div slot="header" class="clearfix">
        <span>计算结果</span>
      </div>
      
      <div class="result-item">
        <span class="result-label">运费金额：</span>
        <span class="result-value" style="color: #f56c6c; font-size: 18px; font-weight: bold;">
          ￥{{ calculateResult.totalFreight }}
        </span>
      </div>
      
      <div v-if="calculateResult.isFreeShipping" class="result-item">
        <el-tag type="success">满足包邮条件，免运费</el-tag>
      </div>
      
      <div v-if="calculateResult.details" class="result-details">
        <h4>计算详情：</h4>
        <div v-for="(detail, index) in calculateResult.details" :key="index" class="detail-item">
          <span>{{ detail.description }}：{{ detail.amount }}元</span>
        </div>
      </div>
      
      <div v-if="calculateResult.errorMessage" class="result-item">
        <el-alert :title="calculateResult.errorMessage" type="warning" show-icon :closable="false"></el-alert>
      </div>
    </el-card>
  </div>
</template>

<script>
  import { calculateFreight } from '@/api/freight'

  export default {
    name: 'FreightCalculator',
    props: {
      templateId: {
        type: [Number, String],
        required: true
      },
      chargeType: {
        type: Number,
        default: 1
      }
    },
    data() {
      return {
        calculating: false,
        calculateForm: {
          quantity: 1,
          weight: 0,
          volume: 0,
          orderAmount: 0,
          receiverProvince: '',
          receiverCity: '',
          receiverLongitude: null,
          receiverLatitude: null
        },
        calculateResult: null
      }
    },
    methods: {
      calculateFreight() {
        // 验证必填项
        if (!this.calculateForm.receiverProvince) {
          this.$message.warning('请输入收货省份')
          return
        }
        if (!this.calculateForm.receiverCity) {
          this.$message.warning('请输入收货城市')
          return
        }
        
        this.calculating = true
        
        let params = {
          templateId: this.templateId,
          items: [{
            productId: 1, // 模拟商品ID
            skuId: 1, // 模拟SKU ID
            quantity: this.calculateForm.quantity,
            weight: this.calculateForm.weight,
            volume: this.calculateForm.volume
          }],
          orderAmount: this.calculateForm.orderAmount,
          receiverProvince: this.calculateForm.receiverProvince,
          receiverCity: this.calculateForm.receiverCity,
          receiverRegion: '',
          receiverLongitude: this.calculateForm.receiverLongitude,
          receiverLatitude: this.calculateForm.receiverLatitude
        }
        
        calculateFreight(params).then(response => {
          this.calculating = false
          this.calculateResult = response.data
          this.$message.success('运费计算完成')
        }).catch(error => {
          this.calculating = false
          this.calculateResult = {
            totalFreight: 0,
            errorMessage: error.message || '计算失败，请检查参数设置'
          }
          this.$message.error('运费计算失败')
        })
      },
      resetCalculateForm() {
        this.calculateForm = {
          quantity: 1,
          weight: 0,
          volume: 0,
          orderAmount: 0,
          receiverProvince: '',
          receiverCity: '',
          receiverLongitude: null,
          receiverLatitude: null
        }
        this.calculateResult = null
      },
      getPrecision() {
        return this.chargeType === 2 || this.chargeType === 3 ? 2 : 0
      }
    }
  }
</script>

<style scoped>
  .freight-calculator {
    margin-top: 20px;
  }
  
  .result-card {
    margin-top: 20px;
  }
  
  .result-item {
    margin-bottom: 15px;
  }
  
  .result-label {
    font-weight: 600;
    color: #333;
    margin-right: 10px;
  }
  
  .result-value {
    color: #666;
  }
  
  .result-details {
    margin-top: 20px;
  }
  
  .result-details h4 {
    margin-bottom: 10px;
    color: #333;
  }
  
  .result-details .detail-item {
    padding: 5px 0;
    color: #666;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .result-details .detail-item:last-child {
    border-bottom: none;
  }
</style> 