<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="订单编号"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleSearchList"
      />
      <el-select
        v-model="listQuery.productionStatus"
        placeholder="生产状态"
        clearable
        style="width: 150px"
        class="filter-item"
      >
        <el-option label="待生产" :value="0"/>
        <el-option label="生产中" :value="1"/>
        <el-option label="已完成" :value="2"/>
      </el-select>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd"
        style="width: 240px;"
        class="filter-item"
        @change="handleDateChange">
      </el-date-picker>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleSearchList">
        查询
      </el-button>
      <el-button class="filter-item" @click="handleResetSearch">
        重置
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ statistics.pending || 0 }}</div>
            <div class="stat-label">待生产</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ statistics.processing || 0 }}</div>
            <div class="stat-label">生产中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ statistics.completed || 0 }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ statistics.total || 0 }}</div>
            <div class="stat-label">总计</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-table
      :key="tableKey"
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%;"
    >
      <el-table-column label="编号" prop="id" align="center" width="80">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单编号" align="center" width="180">
        <template slot-scope="{row}">
          <span>{{ row.orderSn }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="center" width="150">
        <template slot-scope="{row}">
          <span>{{ row.productName || '暂无' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户名称" align="center" width="120">
        <template slot-scope="{row}">
          <span>{{ row.memberUsername || '暂无' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收货人信息" align="center" width="200">
        <template slot-scope="{row}">
          <div v-if="row.receiverName">
            <div>{{ row.receiverName }} {{ row.receiverPhone }}</div>
            <div style="font-size: 12px; color: #999;">
              {{ formatAddress(row) }}
            </div>
          </div>
          <span v-else>暂无</span>
        </template>
      </el-table-column>
      <el-table-column label="订单金额" align="center" width="120">
        <template slot-scope="{row}">
          <div v-if="row.payAmount">
            <div style="color: #f56c6c; font-weight: bold;">¥{{ row.payAmount }}</div>
            <div style="font-size: 12px; color: #999;">运费: ¥{{ row.freightAmount || 0 }}</div>
          </div>
          <span v-else>暂无</span>
        </template>
      </el-table-column>
      <el-table-column label="预览图" width="200" align="center">
        <template slot-scope="{row}">
          <div v-if="getPreviewImages(row).length > 0" style="display: flex; flex-wrap: wrap; gap: 5px; justify-content: center;">
            <div v-for="(img, index) in getPreviewImages(row)" :key="index" style="position: relative;">
              <img :src="img.url" style="height: 60px; width: 60px; object-fit: cover; border: 1px solid #ddd; border-radius: 4px;">
              <div style="position: absolute; bottom: 0; left: 0; right: 0; background: rgba(0,0,0,0.6); color: white; font-size: 10px; text-align: center; padding: 2px;">
                {{ img.name }}
              </div>
            </div>
          </div>
          <span v-else style="color: #999;">暂无预览图</span>
        </template>
      </el-table-column>
      <el-table-column label="生产状态" width="120" align="center">
        <template slot-scope="{row}">
          <el-tag :type="getStatusType(row.productionStatus)">
            {{ getStatusText(row.productionStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160" align="center">
        <template slot-scope="{row}">
          <span>{{ $formatDate(row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="280" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="info" size="mini" @click="handleDetail(row)">
            详情
          </el-button>
          <el-button type="primary" size="mini" @click="handleDownload(row)">
            下载
          </el-button>
          <el-button 
            v-if="row.productionStatus === 0" 
            type="success" 
            size="mini" 
            @click="handleGenerate(row)"
          >
            生成文件
          </el-button>
          <el-dropdown v-if="row.productionStatus !== 2" @command="handleStatusCommand">
            <el-button type="warning" size="mini">
              更新状态<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command="{id: row.id, status: 1}">设为生产中</el-dropdown-item>
              <el-dropdown-item :command="{id: row.id, status: 2}">设为已完成</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :page-size="listQuery.pageSize"
        :page-sizes="[10,20,50]"
        :current-page.sync="listQuery.pageNum"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {
  fetchOrderDiyInfoList,
  updateProductionStatus,
  downloadDesignFile,
  generateProductionFile,
  countByProductionStatus
} from '@/api/diyOrder'

export default {
  name: 'DiyOrderList',
  data() {
    return {
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 20,
        keyword: undefined,
        productionStatus: undefined,
        startTime: null,
        endTime: null
      },
      dateRange: null, // 日期范围选择器
      statistics: {
        pending: 0,
        processing: 0,
        completed: 0,
        total: 0
      }
    }
  },
  created() {
    this.getList()
    this.getStatistics()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchOrderDiyInfoList(this.listQuery).then(response => {
        this.list = response.data.list
        this.total = response.data.total
        this.listLoading = false
      })
    },
    getStatistics() {
      // 获取各状态统计
      Promise.all([
        countByProductionStatus({ productionStatus: 0 }),
        countByProductionStatus({ productionStatus: 1 }),
        countByProductionStatus({ productionStatus: 2 }),
        countByProductionStatus({})
      ]).then(responses => {
        this.statistics = {
          pending: responses[0].data,
          processing: responses[1].data,
          completed: responses[2].data,
          total: responses[3].data
        }
      })
    },
    handleSearchList() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleResetSearch() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 20,
        keyword: undefined,
        productionStatus: undefined,
        startTime: null,
        endTime: null
      };
      this.dateRange = null;
      this.getList();
    },
    handleDateChange(dateRange) {
      if (dateRange && dateRange.length === 2) {
        this.listQuery.startTime = dateRange[0];
        this.listQuery.endTime = dateRange[1];
      } else {
        this.listQuery.startTime = null;
        this.listQuery.endTime = null;
      }
    },
    handleDetail(row) {
      this.$router.push({ path: '/oms/diyOrderDetail', query: { id: row.id }})
    },
    async handleDownload(row) {
      try {
        // 解析设计数据
        let designData = null
        let finalImagesData = null

        // 解析 designSnapshot
        if (row.designSnapshot) {
          try {
            designData = JSON.parse(row.designSnapshot)
          } catch (e) {
            console.error('解析designSnapshot失败:', e)
          }
        }

        // 解析 finalImages
        if (row.finalImages) {
          try {
            finalImagesData = JSON.parse(row.finalImages)
          } catch (e) {
            console.error('解析finalImages失败:', e)
          }
        }

        // 合并数据
        const faces = []
        if (designData && designData.faces) {
          designData.faces.forEach((face, index) => {
            const mergedFace = { ...face }

            // 从 finalImages 合并数据
            if (finalImagesData && Array.isArray(finalImagesData)) {
              const finalFace = finalImagesData.find(f =>
                (f.faceId && face.faceId && f.faceId === face.faceId) ||
                (f.faceId && face.id && f.faceId === face.id)
              ) || finalImagesData[index]

              if (finalFace) {
                Object.assign(mergedFace, {
                  finalImage: finalFace.finalImage,
                  hdImage: finalFace.hdImage,
                  previewImageUrl: finalFace.previewImageUrl,
                  aiGeneratedImageUrl: finalFace.aiGeneratedImageUrl,
                  originalImage: finalFace.originalImage
                })
              }
            }

            faces.push(mergedFace)
          })
        } else if (finalImagesData && Array.isArray(finalImagesData)) {
          faces.push(...finalImagesData)
        }

        if (faces.length === 0) {
          this.$message({
            message: '没有可下载的设计文件',
            type: 'warning',
            duration: 2000
          })
          return
        }

        this.$message({
          message: '正在准备下载文件...',
          type: 'info',
          duration: 2000
        })

        const JSZip = require('jszip')
        const FileSaver = require('file-saver')
        const zip = new JSZip()

        // 遍历每个面,创建文件夹并下载图片
        const downloadPromises = faces.map(async (face, index) => {
          const faceName = face.faceName || face.name || `面${index + 1}`
          const faceFolder = zip.folder(faceName)

          // 收集该面的所有图片
          const images = []

          // 1. AI生成图
          if (face.aiGeneratedImageUrl) {
            images.push({ url: face.aiGeneratedImageUrl, name: 'AI生成图' })
          }

          // 2. 画布设计图
          if (face.canvasImagePath) {
            images.push({ url: face.canvasImagePath, name: '画布设计图' })
          }

          // 3. 最终效果图
          if (face.finalImage) {
            images.push({ url: face.finalImage, name: '最终效果图' })
          }

          // 4. 高清图
          if (face.hdImage) {
            images.push({ url: face.hdImage, name: '高清图' })
          }

          // 5. 预览图
          if (face.previewImageUrl && face.previewImageUrl !== face.finalImage) {
            images.push({ url: face.previewImageUrl, name: '预览图' })
          }

          // 6. 原始图片
          if (face.originalImage || face.image) {
            images.push({ url: face.originalImage || face.image, name: '原始图片' })
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
        const fileName = `DIY订单_${row.orderSn || row.id}_设计文件.zip`
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
    handleGenerate(row) {
      this.$confirm('是否生成生产文件?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        generateProductionFile(row.id).then(response => {
          if (response.data) {
            this.$message({
              message: '生成成功！',
              type: 'success',
              duration: 1000
            })
            this.getList()
            this.getStatistics()
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
    handleStatusCommand(command) {
      this.$confirm('是否要修改生产状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateProductionStatus(command.id, { productionStatus: command.status }).then(response => {
          this.$message({
            message: '修改成功！',
            type: 'success',
            duration: 1000
          })
          this.getList()
          this.getStatistics()
        })
      })
    },
    formatAddress(row) {
      if (!row.receiverProvince) return ''
      return `${row.receiverProvince || ''}${row.receiverCity || ''}${row.receiverRegion || ''}${row.receiverDetailAddress || ''}`
    },
    getPreviewImages(row) {
      const images = []

      // 优先从 finalImages 解析(这是小程序端上传的最终效果图)
      if (row.finalImages) {
        try {
          const facesData = JSON.parse(row.finalImages)
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
      if (images.length === 0 && row.previewImage) {
        images.push({
          url: row.previewImage,
          name: '预览图'
        })
      }

      return images
    },
    getStatusType(status) {
      const statusMap = {
        0: 'warning',
        1: 'primary',
        2: 'success'
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        0: '待生产',
        1: '生产中',
        2: '已完成'
      }
      return statusMap[status] || '未知'
    },
    handleSizeChange(val) {
      this.listQuery.pageNum = 1
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
}

.stat-item {
  padding: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}
</style>
