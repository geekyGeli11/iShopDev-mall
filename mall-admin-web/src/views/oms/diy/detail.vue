<template>
  <div class="app-container">
    <el-card class="box-card" shadow="never">
      <div slot="header" class="clearfix">
        <span>DIY订单详情</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="$router.back()">返回</el-button>
      </div>
      
      <div v-if="diyInfo">
        <div class="info-section">
          <h3>基本信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">订单编号:</span>
                <span class="info-value">{{ diyInfo.orderSn }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">商品名称:</span>
                <span class="info-value">{{ diyInfo.productName || '暂无' }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">用户账号:</span>
                <span class="info-value">{{ diyInfo.memberUsername || '暂无' }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">生产状态:</span>
                <el-tag :type="getStatusType(diyInfo.productionStatus)">
                  {{ diyInfo.productionStatusDesc }}
                </el-tag>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">创建时间:</span>
                <span class="info-value">{{ $formatDate(diyInfo.createTime) }}</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 收货地址信息 -->
        <div v-if="diyInfo.receiverName" class="info-section">
          <h3>收货地址信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">收货人:</span>
                <span class="info-value">{{ diyInfo.receiverName }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">联系电话:</span>
                <span class="info-value">{{ diyInfo.receiverPhone }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">邮政编码:</span>
                <span class="info-value">{{ diyInfo.receiverPostCode || '无' }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <span class="info-label">收货地址:</span>
                <span class="info-value">{{ formatAddress(diyInfo) }}</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 订单金额信息 -->
        <div v-if="diyInfo.payAmount" class="info-section">
          <h3>订单金额信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">商品总额:</span>
                <span class="info-value amount">¥{{ diyInfo.totalAmount || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">运费金额:</span>
                <span class="info-value">¥{{ diyInfo.freightAmount || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">促销优惠:</span>
                <span class="info-value">-¥{{ diyInfo.promotionAmount || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">优惠券抵扣:</span>
                <span class="info-value">-¥{{ diyInfo.couponAmount || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">积分抵扣:</span>
                <span class="info-value">-¥{{ diyInfo.integrationAmount || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">实付金额:</span>
                <span class="info-value amount-pay">¥{{ diyInfo.payAmount }}</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="info-section">
          <h3>DIY设计信息</h3>

          <!-- 预览图展示 - 显示所有面 -->
          <div class="design-preview-section">
            <h4>设计预览图</h4>
            <div v-if="previewImages.length > 0" class="preview-images-grid">
              <div v-for="(img, index) in previewImages" :key="index" class="preview-image-item">
                <div class="preview-image-wrapper">
                  <img :src="img.url" :alt="img.name">
                  <div class="preview-image-label">{{ img.name }}</div>
                </div>
              </div>
            </div>
            <p v-else style="color: #999; padding: 20px;">暂无预览图</p>
          </div>

          <!-- 设计数据展示 - 格式化显示 -->
          <div class="design-data-section">
            <h4>设计数据详情</h4>
            <div v-if="designDataParsed" class="design-data-formatted">
              <el-collapse v-model="activeCollapse">
                <el-collapse-item
                  v-for="(face, index) in designDataParsed.faces"
                  :key="index"
                  :title="`${face.faceName || face.name || '面' + (index + 1)} (${face.elements ? face.elements.length : 0}个元素)`"
                  :name="index"
                >
                  <div class="face-info">
                    <div class="face-detail-item">
                      <span class="label">面ID:</span>
                      <span class="value">{{ face.faceId || face.id || '未知' }}</span>
                    </div>
                    <div class="face-detail-item">
                      <span class="label">面名称:</span>
                      <span class="value">{{ face.faceName || face.name || '未命名' }}</span>
                    </div>

                    <!-- 画布设计图 -->
                    <div v-if="face.canvasImagePath" class="face-detail-item face-image-item">
                      <span class="label">画布设计图:</span>
                      <div class="image-preview-wrapper">
                        <img :src="face.canvasImagePath" alt="画布设计图" class="face-image" @click="previewImage(face.canvasImagePath)" />
                        <a :href="face.canvasImagePath" target="_blank" class="image-link">在新窗口打开</a>
                      </div>
                    </div>

                    <!-- AI生成图 -->
                    <div v-if="face.aiGeneratedImageUrl" class="face-detail-item face-image-item">
                      <span class="label">AI生成图:</span>
                      <div class="image-preview-wrapper">
                        <img :src="face.aiGeneratedImageUrl" alt="AI生成图" class="face-image" @click="previewImage(face.aiGeneratedImageUrl)" />
                        <a :href="face.aiGeneratedImageUrl" target="_blank" class="image-link">在新窗口打开</a>
                      </div>
                    </div>

                    <!-- 原始图片 -->
                    <div v-if="face.originalImage || face.image" class="face-detail-item face-image-item">
                      <span class="label">原始图片:</span>
                      <div class="image-preview-wrapper">
                        <img :src="face.originalImage || face.image" alt="原始图片" class="face-image" @click="previewImage(face.originalImage || face.image)" />
                        <a :href="face.originalImage || face.image" target="_blank" class="image-link">在新窗口打开</a>
                      </div>
                    </div>

                    <!-- 最终效果图 -->
                    <div v-if="face.finalImage" class="face-detail-item face-image-item">
                      <span class="label">最终效果图:</span>
                      <div class="image-preview-wrapper">
                        <img :src="face.finalImage" alt="最终效果图" class="face-image" @click="previewImage(face.finalImage)" />
                        <a :href="face.finalImage" target="_blank" class="image-link">在新窗口打开</a>
                      </div>
                    </div>

                    <!-- 高清图 -->
                    <div v-if="face.hdImage" class="face-detail-item face-image-item">
                      <span class="label">高清图:</span>
                      <div class="image-preview-wrapper">
                        <img :src="face.hdImage" alt="高清图" class="face-image" @click="previewImage(face.hdImage)" />
                        <a :href="face.hdImage" target="_blank" class="image-link">在新窗口打开</a>
                      </div>
                    </div>

                    <!-- 预览图URL -->
                    <div v-if="face.previewImageUrl" class="face-detail-item face-image-item">
                      <span class="label">预览图URL:</span>
                      <div class="image-preview-wrapper">
                        <img :src="face.previewImageUrl" alt="预览图" class="face-image" @click="previewImage(face.previewImageUrl)" />
                        <a :href="face.previewImageUrl" target="_blank" class="image-link">在新窗口打开</a>
                      </div>
                    </div>

                    <!-- 设计元素列表 -->
                    <div v-if="face.elements && face.elements.length > 0" class="face-detail-item">
                      <span class="label">设计元素 ({{ face.elements.length }}个):</span>
                      <div class="elements-list">
                        <div v-for="(element, eIndex) in face.elements" :key="eIndex" class="element-item">
                          <div class="element-header">
                            <el-tag size="small" :type="getElementTagType(element.type)">
                              {{ getElementTypeName(element.type) }}
                            </el-tag>
                            <span v-if="element.name" class="element-name">{{ element.name }}</span>
                          </div>
                          <div class="element-details">
                            <!-- 文本元素 -->
                            <template v-if="element.type === 'text'">
                              <div v-if="element.content" class="element-prop">
                                <span class="prop-label">内容:</span>
                                <span class="prop-value">{{ element.content }}</span>
                              </div>
                              <div v-if="element.fontSize" class="element-prop">
                                <span class="prop-label">字号:</span>
                                <span class="prop-value">{{ element.fontSize }}px</span>
                              </div>
                              <div v-if="element.fontFamily" class="element-prop">
                                <span class="prop-label">字体:</span>
                                <span class="prop-value">{{ element.fontFamily }}</span>
                              </div>
                              <div v-if="element.color" class="element-prop">
                                <span class="prop-label">颜色:</span>
                                <span class="prop-value" :style="{ color: element.color }">{{ element.color }}</span>
                              </div>
                            </template>

                            <!-- 图片元素 -->
                            <template v-if="element.type === 'image'">
                              <div v-if="element.src || element.url" class="element-prop">
                                <span class="prop-label">图片:</span>
                                <a :href="element.src || element.url" target="_blank" class="prop-value link">查看图片</a>
                              </div>
                              <div v-if="element.width" class="element-prop">
                                <span class="prop-label">宽度:</span>
                                <span class="prop-value">{{ element.width }}px</span>
                              </div>
                              <div v-if="element.height" class="element-prop">
                                <span class="prop-label">高度:</span>
                                <span class="prop-value">{{ element.height }}px</span>
                              </div>
                            </template>

                            <!-- 位置信息 -->
                            <div v-if="element.x !== undefined || element.y !== undefined" class="element-prop">
                              <span class="prop-label">位置:</span>
                              <span class="prop-value">X: {{ element.x || 0 }}, Y: {{ element.y || 0 }}</span>
                            </div>

                            <!-- 旋转角度 -->
                            <div v-if="element.rotation" class="element-prop">
                              <span class="prop-label">旋转:</span>
                              <span class="prop-value">{{ element.rotation }}°</span>
                            </div>

                            <!-- 透明度 -->
                            <div v-if="element.opacity !== undefined && element.opacity !== 1" class="element-prop">
                              <span class="prop-label">透明度:</span>
                              <span class="prop-value">{{ (element.opacity * 100).toFixed(0) }}%</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>

              <!-- 原始JSON数据 -->
              <div style="margin-top: 20px;">
                <el-button size="small" @click="showRawJson = !showRawJson">
                  {{ showRawJson ? '隐藏' : '显示' }}原始JSON数据
                </el-button>
                <el-input
                  v-if="showRawJson"
                  v-model="designDataFormatted"
                  type="textarea"
                  :rows="10"
                  readonly
                  style="margin-top: 10px;"
                />
              </div>
            </div>
            <el-input
              v-else
              v-model="designDataFormatted"
              type="textarea"
              :rows="10"
              readonly
              placeholder="暂无设计数据"
            />
          </div>
        </div>

        <div v-if="diyInfo.template" class="info-section">
          <h3>关联模板信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">模板名称:</span>
                <span class="info-value">{{ diyInfo.template.name }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">模板描述:</span>
                <span class="info-value">{{ diyInfo.template.description || '无' }}</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <div style="margin-top: 20px;">
          <h3>操作</h3>
          <el-button type="primary" @click="handleDownload">下载设计文件</el-button>
          <el-button v-if="diyInfo.productionStatus === 0" type="success" @click="handleGenerate">生成生产文件</el-button>
          <el-dropdown v-if="diyInfo.productionStatus !== 2" @command="handleStatusCommand">
            <el-button type="warning">
              更新生产状态<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command="1">设为生产中</el-dropdown-item>
              <el-dropdown-item :command="2">设为已完成</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { 
  getOrderDiyInfo, 
  updateProductionStatus, 
  downloadDesignFile, 
  generateProductionFile 
} from '@/api/diyOrder'

export default {
  name: 'DiyOrderDetail',
  data() {
    return {
      diyInfo: null,
      activeCollapse: [0], // 默认展开第一个面
      showRawJson: false // 是否显示原始JSON
    }
  },
  computed: {
    designDataFormatted() {
      if (!this.diyInfo || !this.diyInfo.designSnapshot) {
        return ''
      }
      try {
        return JSON.stringify(JSON.parse(this.diyInfo.designSnapshot), null, 2)
      } catch (e) {
        return this.diyInfo.designSnapshot
      }
    },
    designDataParsed() {
      if (!this.diyInfo) {
        return null
      }

      try {
        // 首先解析 designSnapshot (包含 elements 等设计数据)
        let designData = null
        if (this.diyInfo.designSnapshot) {
          designData = JSON.parse(this.diyInfo.designSnapshot)
        }

        // 然后解析 finalImages (包含最终效果图等数据)
        let finalImagesData = null
        if (this.diyInfo.finalImages) {
          finalImagesData = JSON.parse(this.diyInfo.finalImages)
        }

        // 如果两者都没有,返回null
        if (!designData && !finalImagesData) {
          return null
        }

        // 合并两个数据源
        const result = designData || { faces: [] }

        // 如果有 finalImages 数据,将其字段合并到对应的 face 中
        if (finalImagesData && Array.isArray(finalImagesData)) {
          finalImagesData.forEach((finalFace, index) => {
            // 尝试通过 faceId 匹配,如果没有则通过索引匹配
            let targetFace = null
            if (result.faces && result.faces.length > 0) {
              targetFace = result.faces.find(f =>
                (f.faceId && finalFace.faceId && f.faceId === finalFace.faceId) ||
                (f.id && finalFace.faceId && f.id === finalFace.faceId)
              )

              // 如果通过ID没找到,使用索引匹配
              if (!targetFace && result.faces[index]) {
                targetFace = result.faces[index]
              }
            }

            // 如果找到了对应的face,合并数据
            if (targetFace) {
              Object.assign(targetFace, {
                finalImage: finalFace.finalImage,
                hdImage: finalFace.hdImage,
                previewImageUrl: finalFace.previewImageUrl,
                aiGeneratedImageUrl: finalFace.aiGeneratedImageUrl,
                originalImage: finalFace.originalImage,
                isPreviewGenerated: finalFace.isPreviewGenerated,
                previewStatus: finalFace.previewStatus
              })
            } else {
              // 如果没找到,直接添加这个face
              if (!result.faces) {
                result.faces = []
              }
              result.faces.push(finalFace)
            }
          })
        }

        return result
      } catch (e) {
        console.error('解析设计数据失败:', e)
        return null
      }
    },
    previewImages() {
      const images = []

      // 优先从 finalImages 解析(这是小程序端上传的最终效果图)
      if (this.diyInfo && this.diyInfo.finalImages) {
        try {
          const facesData = JSON.parse(this.diyInfo.finalImages)
          if (Array.isArray(facesData)) {
            facesData.forEach(face => {
              // 优先使用finalImage(最终效果图)，其次是previewImageUrl
              const imageUrl = face.finalImage || face.previewImageUrl || face.previewImage
              if (imageUrl) {
                images.push({
                  url: imageUrl,
                  name: face.faceName || face.name || '未命名'
                })
              }
            })
          }
        } catch (e) {
          console.error('解析finalImages失败:', e)
        }
      }

      // 如果没有从 finalImages 获取到图片，尝试从 previewImage 字段获取
      if (images.length === 0 && this.diyInfo && this.diyInfo.previewImage) {
        images.push({
          url: this.diyInfo.previewImage,
          name: '预览图'
        })
      }

      return images
    }
  },
  created() {
    if (this.$route.query.id != null) {
      this.getDiyInfo(this.$route.query.id)
    }
  },
  methods: {
    getDiyInfo(id) {
      getOrderDiyInfo(id).then(response => {
        this.diyInfo = response.data
      })
    },
    formatAddress(row) {
      if (!row.receiverProvince) return ''
      return `${row.receiverProvince || ''}${row.receiverCity || ''}${row.receiverRegion || ''}${row.receiverDetailAddress || ''}`
    },
    async handleDownload() {
      if (!this.designDataParsed || !this.designDataParsed.faces || this.designDataParsed.faces.length === 0) {
        this.$message({
          message: '没有可下载的设计文件',
          type: 'warning',
          duration: 2000
        })
        return
      }

      try {
        this.$message({
          message: '正在准备下载文件...',
          type: 'info',
          duration: 2000
        })

        const JSZip = require('jszip')
        const FileSaver = require('file-saver')
        const zip = new JSZip()

        // 遍历每个面,创建文件夹并下载图片
        const downloadPromises = this.designDataParsed.faces.map(async (face, index) => {
          const faceName = face.faceName || face.name || `面${index + 1}`
          const faceFolder = zip.folder(faceName)

          // 收集该面的所有图片
          const images = []

          // 1. AI生成图
          if (face.aiGeneratedImageUrl) {
            images.push({
              url: face.aiGeneratedImageUrl,
              name: 'AI生成图'
            })
          }

          // 2. 画布设计图
          if (face.canvasImagePath) {
            images.push({
              url: face.canvasImagePath,
              name: '画布设计图'
            })
          }

          // 3. 最终效果图
          if (face.finalImage) {
            images.push({
              url: face.finalImage,
              name: '最终效果图'
            })
          }

          // 4. 高清图
          if (face.hdImage) {
            images.push({
              url: face.hdImage,
              name: '高清图'
            })
          }

          // 5. 预览图
          if (face.previewImageUrl && face.previewImageUrl !== face.finalImage) {
            images.push({
              url: face.previewImageUrl,
              name: '预览图'
            })
          }

          // 6. 原始图片
          if (face.originalImage || face.image) {
            images.push({
              url: face.originalImage || face.image,
              name: '原始图片'
            })
          }

          // 下载所有图片并添加到文件夹
          const imagePromises = images.map(async (img) => {
            try {
              const response = await fetch(img.url)
              if (!response.ok) {
                console.error(`下载失败: ${img.name} - ${img.url}`)
                return
              }
              const blob = await response.blob()
              const ext = img.url.split('.').pop().split('?')[0] || 'png'
              faceFolder.file(`${img.name}.${ext}`, blob)
            } catch (error) {
              console.error(`下载图片失败: ${img.name}`, error)
            }
          })

          await Promise.all(imagePromises)
        })

        await Promise.all(downloadPromises)

        // 生成ZIP文件并下载
        const content = await zip.generateAsync({ type: 'blob' })
        const fileName = `DIY订单_${this.diyInfo.orderSn || this.diyInfo.id}_设计文件.zip`
        FileSaver.saveAs(content, fileName)

        this.$message({
          message: '下载成功！',
          type: 'success',
          duration: 2000
        })
      } catch (error) {
        console.error('下载失败:', error)
        this.$message({
          message: '下载失败: ' + error.message,
          type: 'error',
          duration: 2000
        })
      }
    },

    // 获取元素类型的Tag颜色
    getElementTagType(type) {
      const typeMap = {
        'text': 'success',
        'image': 'primary',
        'shape': 'warning',
        'background': 'info'
      }
      return typeMap[type] || ''
    },

    // 获取元素类型的中文名称
    getElementTypeName(type) {
      const nameMap = {
        'text': '文本',
        'image': '图片',
        'shape': '形状',
        'background': '背景'
      }
      return nameMap[type] || type
    },

    // 预览图片
    previewImage(imageUrl) {
      if (!imageUrl) return
      // 在新窗口打开图片
      window.open(imageUrl, '_blank')
    },
    handleGenerate() {
      this.$confirm('是否生成生产文件?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        generateProductionFile(this.diyInfo.id).then(response => {
          if (response.data) {
            this.$message({
              message: '生成成功！',
              type: 'success',
              duration: 1000
            })
            this.getDiyInfo(this.diyInfo.id)
          } else {
            this.$message({
              message: '生成失败',
              type: 'error',
              duration: 1000
            })
          }
        })
      })
    },
    handleStatusCommand(status) {
      this.$confirm('是否要修改生产状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateProductionStatus(this.diyInfo.id, { productionStatus: status }).then(response => {
          this.$message({
            message: '修改成功！',
            type: 'success',
            duration: 1000
          })
          this.getDiyInfo(this.diyInfo.id)
        })
      })
    },
    getStatusType(status) {
      const statusMap = {
        0: 'warning',
        1: 'primary',
        2: 'success'
      }
      return statusMap[status] || 'info'
    }
  }
}
</script>

<style scoped>
.box-card {
  margin: 20px;
}

.info-section {
  margin-bottom: 30px;
}

.info-section h3 {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409EFF;
  color: #333;
  font-size: 16px;
}

.info-section h4 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #666;
  font-size: 14px;
  font-weight: bold;
}

.info-item {
  margin-bottom: 15px;
  line-height: 32px;
}

.info-label {
  font-weight: bold;
  color: #666;
  margin-right: 10px;
  min-width: 100px;
  display: inline-block;
}

.info-value {
  color: #333;
}

.info-value.amount {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

.info-value.amount-pay {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}

/* 预览图样式 */
.design-preview-section {
  margin-bottom: 30px;
}

.preview-images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.preview-image-item {
  text-align: center;
}

.preview-image-wrapper {
  position: relative;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}

.preview-image-wrapper:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.preview-image-wrapper img {
  width: 100%;
  height: 200px;
  object-fit: contain;
  display: block;
}

.preview-image-label {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 8px;
  font-size: 14px;
  text-align: center;
}

/* 设计数据样式 */
.design-data-section {
  margin-top: 20px;
}

.design-data-formatted {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.face-info {
  padding: 10px;
  background: white;
  border-radius: 4px;
}

.face-detail-item {
  margin-bottom: 12px;
  padding: 8px;
  background: #f9f9f9;
  border-radius: 4px;
}

.face-detail-item .label {
  font-weight: bold;
  color: #666;
  margin-right: 10px;
  min-width: 80px;
  display: inline-block;
}

.face-detail-item .value {
  color: #333;
}

.face-detail-item .value.link {
  color: #409EFF;
  text-decoration: none;
}

.face-detail-item .value.link:hover {
  text-decoration: underline;
}

/* 图片预览样式 */
.face-image-item {
  display: block;
}

.face-image-item .label {
  display: block;
  margin-bottom: 10px;
}

.image-preview-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.face-image {
  max-width: 100%;
  width: auto;
  max-height: 300px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  object-fit: contain;
  background: #f9f9f9;
}

.face-image:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.image-link {
  color: #409eff;
  font-size: 13px;
  text-decoration: none;
  align-self: flex-start;
}

.image-link:hover {
  text-decoration: underline;
}

.elements-list {
  margin-top: 8px;
}

.element-item {
  padding: 12px;
  background: white;
  border-radius: 4px;
  margin-bottom: 12px;
  border-left: 3px solid #409eff;
}

.element-item:last-child {
  margin-bottom: 0;
}

.element-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.element-name {
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

.element-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-left: 8px;
}

.element-prop {
  display: flex;
  align-items: flex-start;
  font-size: 13px;
}

.prop-label {
  color: #909399;
  min-width: 60px;
  flex-shrink: 0;
}

.prop-value {
  color: #606266;
  word-break: break-all;
}

.prop-value.link {
  color: #409eff;
  cursor: pointer;
  text-decoration: none;
}

.prop-value.link:hover {
  text-decoration: underline;
}

.element-content {
  color: #666;
  font-size: 13px;
}
</style>
