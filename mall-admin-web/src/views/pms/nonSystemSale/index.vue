<template>
  <div class="non-system-sale-container">
    <!-- 销售单列表 -->
    <div v-if="!showDialog" class="sale-list">
      <div class="table-header">
        <div class="header-left">
          <el-button type="primary" @click="handleCreateSale">
            <i class="el-icon-plus"></i> 创建销售单
          </el-button>
          <div class="tip-box">
            <i class="el-icon-info"></i>
            <span>点击"查看详情"可以下载销售单表格文件</span>
          </div>
        </div>
      </div>

      <el-table
        :data="saleList"
        style="width: 100%"
        v-loading="listLoading">
        <el-table-column label="销售单号" width="200" align="center">
          <template slot-scope="scope">{{ scope.row.saleNo }}</template>
        </el-table-column>
        <el-table-column label="销售类型" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.saleTypeName }}</template>
        </el-table-column>
        <el-table-column label="销售日期" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.saleDate | formatDate }}</template>
        </el-table-column>
        <el-table-column label="销售金额" width="120" align="center">
          <template slot-scope="scope">¥{{ scope.row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="销售数量" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.totalQuantity }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ scope.row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="客户信息" width="150" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.customerName || scope.row.customerPhone">
              <div>{{ scope.row.customerName || '-' }}</div>
              <div style="font-size: 12px; color: #999;">{{ scope.row.customerPhone || '-' }}</div>
            </div>
            <span v-else style="color: #999;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作人" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.operatorName }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template slot-scope="scope">{{ scope.row.createTime | formatDateTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="400" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleViewDetail(scope.row)">
              查看详情
            </el-button>
            <el-button 
              v-if="scope.row.status === 1 || scope.row.status === 2"
              size="mini" 
              type="warning" 
              @click="handleShare(scope.row)">
              分享
            </el-button>
            <el-button 
              v-if="scope.row.status === 1 && isAdmin"
              size="mini" 
              type="success" 
              @click="handleApprove(scope.row)">
              审核
            </el-button>
            <el-button 
              v-if="scope.row.status === 1 && isAdmin"
              size="mini" 
              type="danger" 
              @click="handleReject(scope.row)">
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          layout="total, sizes, prev, pager, next, jumper"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :current-page.sync="pageNum"
          :total="total">
        </el-pagination>
      </div>
    </div>

    <!-- 驳回对话框 -->
    <el-dialog 
      title="驳回销售单" 
      :visible.sync="showRejectDialog"
      width="500px"
      @close="handleCloseRejectDialog">
      
      <el-form label-width="100px">
        <el-form-item label="销售单号：">
          <span>{{ rejectForm.saleNo }}</span>
        </el-form-item>
        <el-form-item label="驳回原因：">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            rows="4"
            placeholder="请输入驳回原因">
          </el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="showRejectDialog = false">取消</el-button>
        <el-button 
          type="danger" 
          @click="handleConfirmReject"
          :loading="rejectLoading">
          确认驳回
        </el-button>
      </span>
    </el-dialog>

    <!-- 分享对话框 -->
    <el-dialog 
      title="分享销售单" 
      :visible.sync="showShareDialog"
      width="600px">
      
      <div v-loading="shareLoading">
        <div v-if="shareInfo" class="share-content">
          <div class="share-section">
            <h4>小程序短链接</h4>
            <div class="share-link-box">
              <el-input 
                v-model="shareInfo.shareLink" 
                readonly
                style="flex: 1;">
              </el-input>
              <el-button 
                type="primary" 
                @click="copyShareLink"
                style="margin-left: 10px;">
                复制链接
              </el-button>
            </div>
            <p class="share-tip">客户点击此链接可直接打开小程序查看销售单</p>
          </div>
          
          <div class="share-section">
            <h4>小程序码</h4>
            <div class="qrcode-box">
              <img 
                v-if="shareInfo.miniProgramCodeBase64" 
                :src="shareInfo.miniProgramCodeBase64" 
                class="qrcode-img"
                alt="小程序码">
              <div v-else class="qrcode-placeholder">
                {{ shareInfo.miniProgramCodeUrl || '小程序码生成失败' }}
              </div>
            </div>
            <el-button 
              v-if="shareInfo.miniProgramCodeBase64"
              type="primary" 
              @click="downloadQrCode"
              style="margin-top: 10px;">
              下载小程序码
            </el-button>
          </div>
        </div>
        <div v-else class="share-empty">
          <p>正在生成分享信息...</p>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="showShareDialog = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog 
      title="销售单详情" 
      :visible.sync="showDetailDialog"
      width="1000px"
      @close="handleCloseDetailDialog">
      
      <div v-if="detailData" class="detail-content">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-row">
            <div class="detail-item">
              <label>销售单号：</label>
              <span>{{ detailData.saleNo }}</span>
            </div>
            <div class="detail-item">
              <label>销售类型：</label>
              <span>{{ detailData.saleTypeName }}</span>
            </div>
            <div class="detail-item">
              <label>销售日期：</label>
              <span>{{ detailData.saleDate | formatDate }}</span>
            </div>
            <div class="detail-item">
              <label>状态：</label>
              <el-tag :type="getStatusTag(detailData.status)">
                {{ detailData.statusName }}
              </el-tag>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-item">
              <label>操作人：</label>
              <span>{{ detailData.operatorName }}</span>
            </div>
            <div class="detail-item">
              <label>创建时间：</label>
              <span>{{ detailData.createTime | formatDateTime }}</span>
            </div>
            <div class="detail-item">
              <label>更新时间：</label>
              <span>{{ detailData.updateTime | formatDateTime }}</span>
            </div>
          </div>
          <div v-if="detailData.remark" class="detail-row">
            <div class="detail-item full-width">
              <label>备注：</label>
              <span>{{ detailData.remark }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>销售商品明细</h4>
          <el-table
            :data="detailData.items"
            style="width: 100%"
            size="small">
            <el-table-column label="商品名称" width="200" align="center">
              <template slot-scope="scope">
                <div class="product-info">
                  <div>{{ scope.row.productName }}</div>
                  <div style="font-size: 12px; color: #999;">ID: {{ scope.row.productId }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="SKU编码/规格" width="180" align="center">
              <template slot-scope="scope">
                <div>{{ scope.row.skuCode }}</div>
                <div v-if="scope.row.specs" style="font-size: 12px; color: #666; margin-top: 4px;">
                  {{ scope.row.specs }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="销售数量" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.quantity }}</template>
            </el-table-column>
            <el-table-column label="系统价格" width="100" align="center">
              <template slot-scope="scope">¥{{ scope.row.systemPrice | formatPrice }}</template>
            </el-table-column>
            <el-table-column label="销售价格" width="100" align="center">
              <template slot-scope="scope">¥{{ scope.row.salePrice | formatPrice }}</template>
            </el-table-column>
            <el-table-column label="行金额" width="100" align="center">
              <template slot-scope="scope">¥{{ scope.row.lineAmount | formatPrice }}</template>
            </el-table-column>
          </el-table>

          <div class="items-summary">
            <div class="summary-item">总数量：<strong>{{ detailData.totalQuantity }}</strong></div>
            <div class="summary-item">总金额：<strong>¥{{ detailData.totalAmount | formatPrice }}</strong></div>
          </div>
        </div>

        <!-- 库存扣减详情 -->
        <div v-if="detailData.items && detailData.items.length > 0" class="detail-section">
          <h4>库存扣减详情</h4>
          <div class="stock-deduction-detail">
            <div v-for="item in detailData.items" :key="item.id" class="deduction-item">
              <div class="item-header">
                <div class="product-header">
                  <strong>{{ item.productName }}</strong>
                  <span class="sku-code">{{ item.skuCode }}</span>
                </div>
                <div class="quantity-badge">销售数量：<span class="quantity-value">{{ item.quantity }}</span> 件</div>
              </div>
              <div v-if="item.stockDeductionDetail" class="item-details">
                <div class="deduction-table">
                  <div class="table-header">
                    <div class="col-store">门店名称</div>
                    <div class="col-stock">库存</div>
                    <div class="col-deduction">调动数量</div>
                    <div class="col-after">调动后库存</div>
                  </div>
                  <div v-for="detail in parseDeductionDetail(item.stockDeductionDetail)" :key="detail.storeId" class="table-row">
                    <div class="col-store">
                      <span class="store-name">{{ detail.storeName }}</span>
                      <span v-if="detail.isWarehouse" class="warehouse-badge">[地库]</span>
                    </div>
                    <div class="col-stock">{{ detail.storeStock }}</div>
                    <div class="col-deduction">
                      <span v-if="detail.deductionQuantity > 0" class="deduction-qty">{{ detail.deductionQuantity }}</span>
                      <span v-else class="no-deduction">-</span>
                    </div>
                    <div class="col-after">{{ detail.storeStock - detail.deductionQuantity }}</div>
                  </div>
                </div>
              </div>
              <div v-else class="item-details">
                <div class="detail-line">
                  <span class="label">销售门店扣减：</span>
                  <span class="value">{{ item.storeStockDeducted || 0 }} 件</span>
                </div>
                <div class="detail-line">
                  <span class="label">其他门店调动：</span>
                  <span class="value">{{ item.totalStockDeducted || 0 }} 件</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="handleExportSale(detailData.id)">导出 Excel</el-button>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 创建销售单对话框 -->
    <el-dialog 
      title="创建销售单" 
      :visible.sync="showDialog"
      width="1000px"
      @close="handleCloseDialog">
      
      <!-- 第一步：选择销售类型、学校和门店 -->
      <div v-if="dialogStep === 1" class="dialog-content">
        <div class="step-header">
          <h3>第一步：选择学校、销售类型和门店</h3>
          <p style="color: #666; font-size: 12px; margin-top: 5px;">提示：请先选择学校，系统将自动加载该学校对应的门店</p>
        </div>
        <el-form label-width="120px">
          <el-form-item label="学校：" required>
            <el-select 
              v-model="saleForm.schoolId" 
              placeholder="请选择学校"
              @change="handleSchoolChange"
              clearable>
              <el-option
                v-for="school in schoolList"
                :key="school.id"
                :label="school.schoolName"
                :value="school.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="销售类型：" required>
            <el-select 
              v-model="saleForm.saleTypeId" 
              placeholder="请选择销售类型" 
              :disabled="!saleForm.schoolId"
              clearable>
              <el-option
                v-for="type in saleTypes"
                :key="type.id"
                :label="type.name"
                :value="type.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="门店：" required>
            <el-select 
              v-model="saleForm.storeId" 
              placeholder="请先选择学校" 
              :disabled="!saleForm.schoolId"
              clearable>
              <el-option
                v-for="store in storeList"
                :key="store.id"
                :label="store.addressName"
                :value="store.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="销售日期：">
            <el-date-picker
              v-model="saleForm.saleDate"
              type="date"
              placeholder="选择销售日期">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="客户姓名：" required>
            <el-input
              v-model="saleForm.customerName"
              placeholder="输入客户姓名（用于分享给客户查看）"
              style="width: 300px">
            </el-input>
          </el-form-item>
          <el-form-item label="客户手机号：" required>
            <el-input
              v-model="saleForm.customerPhone"
              placeholder="输入客户手机号（用于验证查看权限）"
              style="width: 300px">
            </el-input>
          </el-form-item>
          <el-form-item label="备注：">
            <el-input
              v-model="saleForm.remark"
              type="textarea"
              rows="3"
              placeholder="输入销售备注信息（可选）">
            </el-input>
          </el-form-item>
        </el-form>

        <div class="dialog-footer">
          <el-button @click="showDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleNextStep"
            :disabled="!saleForm.schoolId || !saleForm.saleTypeId || !saleForm.storeId">
            下一步
          </el-button>
        </div>
      </div>

      <!-- 第二步：选择商品和SKU -->
      <div v-if="dialogStep === 2" class="dialog-content">
        <div class="step-header">
          <h3>第二步：选择销售商品</h3>
          <div class="selected-info">
            <div>销售类型：<strong>{{ getSelectedTypeName() }}</strong></div>
            <div>学校：<strong>{{ getSelectedSchoolName() }}</strong></div>
            <div>门店：<strong>{{ getSelectedStoreName() }}</strong></div>
          </div>
        </div>

        <div class="sale-items-section">
          <div class="discount-section">
            <div class="discount-input-group">
              <label>统一折扣：</label>
              <el-input-number 
                v-model="saleForm.discount" 
                :min="0"
                :max="1"
                :step="0.01"
                :precision="2"
                style="width: 150px"
                placeholder="0-1">
              </el-input-number>
              <span style="margin-left: 10px; color: #666;">折（1.0=原价，0.9=九折）</span>
              <el-button 
                type="primary" 
                size="small"
                @click="applyDiscountToAll"
                style="margin-left: 15px">
                应用折扣到所有商品
              </el-button>
            </div>
          </div>

          <div class="add-item-btn">
            <el-button type="primary" @click="showProductSelectDialog = true">
              <i class="el-icon-plus"></i> 选择商品
            </el-button>
          </div>

          <!-- 库存不足提示 -->
          <div v-if="hasInsufficientStoreStock()" class="stock-warning">
            <i class="el-icon-warning"></i>
            <span>部分商品门店库存不足，将从其他门店（地库）调货进行销售</span>
          </div>

          <el-table
            :data="saleForm.items"
            style="width: 100%"
            v-loading="itemLoading">
            <el-table-column label="商品名称" width="200" align="center">
              <template slot-scope="scope">
                <div class="product-info">
                  <div>{{ scope.row.productName }}</div>
                  <div style="font-size: 12px; color: #999;">ID: {{ scope.row.productId }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="SKU编码/规格" width="200" align="center">
              <template slot-scope="scope">
                <div>{{ scope.row.skuCode }}</div>
                <div v-if="scope.row.specs" style="font-size: 12px; color: #666; margin-top: 4px;">
                  {{ scope.row.specs }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="门店库存" width="100" align="center">
              <template slot-scope="scope">
                {{ scope.row.storeStock !== undefined ? scope.row.storeStock : 0 }}
              </template>
            </el-table-column>
            <el-table-column label="总库存" width="100" align="center">
              <template slot-scope="scope">
                {{ scope.row.availableStock }}
              </template>
            </el-table-column>
            <el-table-column label="销售数量" width="150" align="center">
              <template slot-scope="scope">
                <el-input-number 
                  v-model="scope.row.quantity" 
                  :min="1"
                  :max="scope.row.availableStock"
                  @change="calculateLineAmount(scope.row)">
                </el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="系统价格" width="100" align="center">
              <template slot-scope="scope">
                ¥{{ scope.row.systemPrice | formatPrice }}
              </template>
            </el-table-column>
            <el-table-column label="折扣" width="130" align="center">
              <template slot-scope="scope">
                <el-input-number 
                  v-model="scope.row.discount" 
                  :min="0"
                  :max="0.99"
                  :step="0.01"
                  :precision="2"
                  @change="calculateSalePriceByDiscount(scope.row)">
                </el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="销售价格" width="120" align="center">
              <template slot-scope="scope">
                <el-input 
                  v-model.number="scope.row.salePrice"
                  type="number"
                  step="0.01"
                  @change="calculateLineAmount(scope.row)">
                </el-input>
              </template>
            </el-table-column>
            <el-table-column label="行金额" width="100" align="center">
              <template slot-scope="scope">
                ¥{{ scope.row.lineAmount | formatPrice }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="scope">
                <el-button 
                  size="mini" 
                  type="danger" 
                  @click="handleRemoveItem(scope.$index)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="items-summary">
            <div class="summary-item">总数量：<strong>{{ getTotalQuantity() }}</strong></div>
            <div class="summary-item">总金额：<strong>¥{{ getTotalAmount() | formatPrice }}</strong></div>
          </div>
        </div>

        <div class="dialog-footer">
          <el-button @click="dialogStep = 1">上一步</el-button>
          <el-button @click="showDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleNextStep"
            :disabled="saleForm.items.length === 0">
            下一步
          </el-button>
        </div>
      </div>

      <!-- 使用商品选择器组件 - 只加载对应学校的商品 -->
      <product-sku-selector 
        :visible.sync="showProductSelectDialog"
        :store-id="saleForm.storeId"
        :school-id="saleForm.schoolId"
        @confirm="handleProductsSelected">
      </product-sku-selector>

      <!-- 第三步：确认提交 -->
      <div v-if="dialogStep === 3" class="dialog-content">
        <div class="step-header">
          <h3>第三步：确认销售单信息</h3>
        </div>

        <div class="confirm-form">
          <div class="form-row">
            <div class="form-item">
              <label>销售类型：</label>
              <span>{{ getSelectedTypeName() }}</span>
            </div>
            <div class="form-item">
              <label>销售日期：</label>
              <span>{{ saleForm.saleDate | formatDate }}</span>
            </div>
          </div>

          <div class="form-row">
            <div class="form-item">
              <label>总数量：</label>
              <span>{{ getTotalQuantity() }}</span>
            </div>
            <div class="form-item">
              <label>总金额：</label>
              <span>¥{{ getTotalAmount() | formatPrice }}</span>
            </div>
          </div>

          <div v-if="saleForm.remark" class="form-row">
            <div class="form-item full-width">
              <label>备注：</label>
              <span>{{ saleForm.remark }}</span>
            </div>
          </div>

          <div class="form-row">
            <div class="form-item full-width">
              <label>销售商品明细：</label>
              <el-table
                :data="saleForm.items"
                style="width: 100%; margin-top: 10px;"
                size="small">
                <el-table-column label="商品名称" prop="productName" align="center"></el-table-column>
                <el-table-column label="SKU编码/规格" align="center">
              <template slot-scope="scope">
                <div>{{ scope.row.skuCode }}</div>
                <div v-if="scope.row.specs" style="font-size: 12px; color: #666; margin-top: 4px;">
                  {{ scope.row.specs }}
                </div>
              </template>
            </el-table-column>
                <el-table-column label="数量" prop="quantity" align="center"></el-table-column>
                <el-table-column label="销售价格" align="center">
                  <template slot-scope="scope">
                    ¥{{ scope.row.salePrice | formatPrice }}
                  </template>
                </el-table-column>
                <el-table-column label="行金额" align="center">
                  <template slot-scope="scope">
                    ¥{{ scope.row.lineAmount | formatPrice }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>

          <!-- 库存扣减方案 -->
          <div v-if="stockDeductionPlans.length > 0" class="form-row">
            <div class="form-item full-width">
              <label>库存扣减方案：</label>
              <div class="stock-deduction-plan" style="margin-top: 10px;">
                <div v-for="plan in stockDeductionPlans" :key="plan.skuId" class="plan-item">
                  <div class="plan-header">
                    <strong>{{ plan.productName }}</strong> ({{ plan.skuCode }}) - 需要扣减 {{ plan.totalQuantity }} 件
                  </div>
                  <div class="plan-details">
                    <div v-for="detail in plan.storeDeductions" :key="detail.storeId" class="detail-row">
                      <span class="store-name">
                        {{ detail.storeName }}
                        <span v-if="detail.isWarehouse" style="color: #f56c6c; font-weight: bold;">[地库]</span>
                      </span>
                      <span class="deduction-info">
                        <span v-if="detail.deductionQuantity > 0" style="color: #409eff;">
                          调动 {{ detail.deductionQuantity }} 件
                        </span>
                        <span v-else style="color: #999;">
                          无需调动
                        </span>
                        <span style="color: #999;">(库存: {{ detail.storeStock }} → {{ detail.storeStock - detail.deductionQuantity }})</span>
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="dialog-footer">
          <el-button @click="dialogStep = 2">上一步</el-button>
          <el-button @click="showDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleSubmitSale"
            :loading="submitLoading">
            提交销售单
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSaleTypes, getSkuStock, submitNonSystemSale, getNonSystemSaleList, getNonSystemSaleDetail, approveSale, rejectSale, getStoreList, getStoreSkuStock, getSkuStockWithStore, getStoreListBySchool, getProductListBySchool, calculateStockDeductionPlan, exportSale, generateShareInfo } from '@/api/nonSystemSale'
import { fetchEnabledSchools } from '@/api/school'
import ProductSkuSelector from '@/components/ProductSkuSelector'
import { mapState } from 'vuex'

export default {
  name: 'NonSystemSale',
  components: {
    ProductSkuSelector
  },
  data() {
    return {
      // 列表相关
      saleList: [],
      listLoading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,

      // 对话框相关
      showDialog: false,
      dialogStep: 1,
      submitLoading: false,
      itemLoading: false,
      approveLoading: false,

      // 详情对话框相关
      showDetailDialog: false,
      detailData: null,
      detailLoading: false,

      // 驳回对话框相关
      showRejectDialog: false,
      rejectLoading: false,
      rejectForm: {
        saleId: null,
        saleNo: '',
        reason: ''
      },

      // 管理员权限
      isAdmin: false,

      // 销售类型
      saleTypes: [],

      // 学校列表
      schoolList: [],

      // 门店列表
      storeList: [],

      // 库存扣减方案
      stockDeductionPlans: [],

      // 商品选择对话框
      showProductSelectDialog: false,

      // 销售单表单
      saleForm: {
        schoolId: null,
        saleTypeId: null,
        storeId: null,
        saleDate: new Date(),
        remark: '',
        customerName: '',
        customerPhone: '',
        discount: 0,
        items: []
      },

      // 分享对话框相关
      showShareDialog: false,
      shareLoading: false,
      shareInfo: null
    }
  },
  computed: {
    ...mapState({
      roles: state => state.user.roles
    })
  },
  created() {
    this.checkAdminRole()
    this.getList()
    this.loadSaleTypes()
    this.loadSchools()
  },
  methods: {
    // 检查是否有门店库存不足的商品
    hasInsufficientStoreStock() {
      return this.saleForm.items.some(item => {
        const storeStock = item.storeStock !== undefined ? item.storeStock : 0
        return item.quantity > storeStock
      })
    },

    // 计算库存扣减方案
    calculateStockDeductionPlan() {
      const items = this.saleForm.items.map(item => ({
        skuId: item.skuId,
        skuCode: item.skuCode,
        productName: item.productName,
        quantity: item.quantity
      }))

      calculateStockDeductionPlan(this.saleForm.schoolId, this.saleForm.storeId, items).then(response => {
        this.stockDeductionPlans = response.data || []
      }).catch(error => {
        this.$message.error('计算库存扣减方案失败：' + (error.message || '未知错误'))
      })
    },

    // 获取销售单列表
    getList() {
      this.listLoading = true
      getNonSystemSaleList(this.pageNum, this.pageSize).then(response => {
        this.saleList = response.data.list
        this.total = response.data.total
      }).catch(error => {
        this.$message.error('获取销售单列表失败：' + (error.message || '未知错误'))
      }).finally(() => {
        this.listLoading = false
      })
    },

    // 加载销售类型
    loadSaleTypes() {
      getSaleTypes().then(response => {
        this.saleTypes = response.data
      }).catch(error => {
        this.$message.error('获取销售类型失败：' + (error.message || '未知错误'))
      })
    },

    // 加载学校列表
    loadSchools() {
      fetchEnabledSchools().then(response => {
        this.schoolList = response.data || []
      }).catch(error => {
        this.$message.error('获取学校列表失败：' + (error.message || '未知错误'))
      })
    },

    // 加载门店列表
    loadStoreList() {
      getStoreList().then(response => {
        this.storeList = response.data
      }).catch(error => {
        this.$message.error('获取门店列表失败：' + (error.message || '未知错误'))
      })
    },

    // 根据学校加载门店列表
    loadStoreListBySchool(schoolId) {
      if (!schoolId) {
        this.storeList = []
        return
      }
      getStoreListBySchool(schoolId).then(response => {
        this.storeList = response.data || []
      }).catch(error => {
        this.$message.error('获取门店列表失败：' + (error.message || '未知错误'))
      })
    },

    // 创建销售单
    handleCreateSale() {
      this.resetSaleForm()
      this.showDialog = true
      this.dialogStep = 1
    },

    // 学校变化时更新门店列表
    handleSchoolChange() {
      this.saleForm.storeId = null
      this.saleForm.saleTypeId = null
      if (this.saleForm.schoolId) {
        this.loadStoreListBySchool(this.saleForm.schoolId)
      } else {
        this.storeList = []
      }
    },

    // 下一步
    handleNextStep() {
      if (this.dialogStep === 1) {
        if (!this.saleForm.schoolId) {
          this.$message.warning('请先选择学校')
          return
        }
        if (!this.saleForm.saleTypeId) {
          this.$message.warning('请选择销售类型')
          return
        }
        if (!this.saleForm.storeId) {
          this.$message.warning('请选择门店')
          return
        }
        if (!this.saleForm.customerName || !this.saleForm.customerName.trim()) {
          this.$message.warning('请输入客户姓名')
          return
        }
        if (!this.saleForm.customerPhone || !this.saleForm.customerPhone.trim()) {
          this.$message.warning('请输入客户手机号')
          return
        }
        // 验证手机号格式
        const phoneReg = /^1[3-9]\d{9}$/
        if (!phoneReg.test(this.saleForm.customerPhone.trim())) {
          this.$message.warning('请输入正确的手机号码')
          return
        }
        this.dialogStep = 2
      } else if (this.dialogStep === 2) {
        if (this.saleForm.items.length === 0) {
          this.$message.warning('请至少添加一个销售商品')
          return
        }
        // 验证所有商品信息完整
        for (let item of this.saleForm.items) {
          if (!item.productId || !item.skuCode || !item.quantity || !item.salePrice) {
            this.$message.warning('请完整填写所有商品信息')
            return
          }
        }
        // 计算库存扣减方案
        this.calculateStockDeductionPlan()
        this.dialogStep = 3
      }
    },

    // 商品选择完成回调
    handleProductsSelected(selectedItems) {
      if (!selectedItems || selectedItems.length === 0) {
        this.$message.warning('请选择至少一个商品')
        return
      }
      
      // 计算选中商品的行金额并获取门店库存
      selectedItems.forEach(item => {
        if (item.quantity && item.salePrice) {
          item.lineAmount = parseFloat((item.quantity * item.salePrice).toFixed(2))
        }
        
        // 设置默认库存值为0
        item.storeStock = 0
        item.availableStock = 0
        // 设置默认折扣为0
        item.discount = 0
        
        // 获取该商品的门店库存信息
        getSkuStockWithStore(item.productId).then(response => {
          const skuData = response.data.find(sku => sku.id === item.skuId)
          if (skuData) {
            // 设置总库存
            item.availableStock = skuData.stock || 0
            
            // 获取当前门店的库存
            if (skuData.storeStocks && skuData.storeStocks.length > 0) {
              const storeStock = skuData.storeStocks.find(s => s.storeId === this.saleForm.storeId)
              item.storeStock = storeStock ? storeStock.stock : 0
            } else {
              item.storeStock = 0
            }
          }
        }).catch(error => {
          this.$message.error('获取门店库存失败：' + (error.message || '未知错误'))
        })
      })
      
      // 添加到销售单商品列表
      this.saleForm.items = this.saleForm.items.concat(selectedItems)
      
      this.$message.success('成功添加 ' + selectedItems.length + ' 个商品')
    },

    // 删除商品
    handleRemoveItem(index) {
      this.saleForm.items.splice(index, 1)
    },

    // 计算行金额
    calculateLineAmount(item) {
      if (item.quantity && item.salePrice) {
        item.lineAmount = parseFloat((item.quantity * item.salePrice).toFixed(2))
      }
    },

    // 根据折扣计算销售价格
    calculateSalePriceByDiscount(item) {
      if (item.discount !== undefined && item.discount !== null && item.systemPrice) {
        const discountedPrice = parseFloat((item.systemPrice * item.discount).toFixed(2))
        item.salePrice = discountedPrice
        this.calculateLineAmount(item)
      }
    },

    // 应用折扣到所有商品
    applyDiscountToAll() {
      if (this.saleForm.discount === undefined || this.saleForm.discount === null || this.saleForm.discount < 0 || this.saleForm.discount > 1) {
        this.$message.warning('请输入有效的折扣值（0-1，其中1.0=原价，0.9=九折）')
        return
      }

      if (this.saleForm.items.length === 0) {
        this.$message.warning('请先添加商品')
        return
      }

      this.saleForm.items.forEach((item, index) => {
        this.$set(item, 'discount', this.saleForm.discount)
        this.calculateSalePriceByDiscount(item)
      })

      this.$message.success('已应用折扣到所有商品')
      this.$forceUpdate()
    },

    // 获取总数量
    getTotalQuantity() {
      return this.saleForm.items.reduce((sum, item) => sum + (item.quantity || 0), 0)
    },

    // 获取总金额
    getTotalAmount() {
      return this.saleForm.items.reduce((sum, item) => sum + (item.lineAmount || 0), 0)
    },

    // 获取选中的销售类型名称
    getSelectedTypeName() {
      const type = this.saleTypes.find(t => t.id === this.saleForm.saleTypeId)
      return type ? type.name : '未选择'
    },

    // 获取选中的学校名称
    getSelectedSchoolName() {
      const school = this.schoolList.find(s => s.id === this.saleForm.schoolId)
      return school ? school.schoolName : '未选择'
    },

    // 获取选中的门店名称
    getSelectedStoreName() {
      const store = this.storeList.find(s => s.id === this.saleForm.storeId)
      return store ? store.addressName : '未选择'
    },

    // 提交销售单
    handleSubmitSale() {
      this.submitLoading = true
      const requestData = {
        saleTypeId: this.saleForm.saleTypeId,
        storeId: this.saleForm.storeId,
        storeName: this.getSelectedStoreName(),
        saleDate: this.saleForm.saleDate,
        remark: this.saleForm.remark,
        customerName: this.saleForm.customerName,
        customerPhone: this.saleForm.customerPhone,
        items: this.saleForm.items,
        stockDeductionPlans: this.stockDeductionPlans
      }

      submitNonSystemSale(requestData).then(response => {
        this.$message.success('销售单提交成功')
        this.showDialog = false
        this.getList()
      }).catch(error => {
        this.$message.error('提交销售单失败：' + (error.message || '未知错误'))
      }).finally(() => {
        this.submitLoading = false
      })
    },

    // 查看详情
    handleViewDetail(row) {
      this.detailLoading = true
      getNonSystemSaleDetail(row.id).then(response => {
        this.detailData = response.data
        this.showDetailDialog = true
      }).catch(error => {
        this.$message.error('获取销售单详情失败：' + (error.message || '未知错误'))
      }).finally(() => {
        this.detailLoading = false
      })
    },

    // 关闭详情对话框
    handleCloseDetailDialog() {
      this.detailData = null
      this.showDetailDialog = false
    },

    // 分享销售单
    handleShare(row) {
      if (!row.customerPhone) {
        this.$message.warning('该销售单未设置客户手机号，无法分享')
        return
      }
      
      this.showShareDialog = true
      this.shareLoading = true
      this.shareInfo = null
      
      generateShareInfo(row.id).then(response => {
        this.shareLoading = false
        if (response.code === 200) {
          this.shareInfo = response.data
        } else {
          this.$message.error(response.message || '生成分享信息失败')
        }
      }).catch(error => {
        this.shareLoading = false
        this.$message.error('生成分享信息失败：' + (error.message || '未知错误'))
      })
    },

    // 复制分享链接
    copyShareLink() {
      if (!this.shareInfo || !this.shareInfo.shareLink) {
        this.$message.warning('分享链接不存在')
        return
      }
      
      const textarea = document.createElement('textarea')
      textarea.value = this.shareInfo.shareLink
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
      this.$message.success('链接已复制到剪贴板')
    },

    // 下载小程序码
    downloadQrCode() {
      if (!this.shareInfo || !this.shareInfo.miniProgramCodeBase64) {
        this.$message.warning('小程序码不存在')
        return
      }
      
      const link = document.createElement('a')
      // 后端返回的 miniProgramCodeBase64 已经包含 data:image/png;base64, 前缀
      link.href = this.shareInfo.miniProgramCodeBase64
      link.download = '销售单_' + (this.shareInfo.saleNo || 'qrcode') + '.png'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      this.$message.success('小程序码已下载')
    },

    // 解析库存扣减详情 JSON
    parseDeductionDetail(jsonStr) {
      try {
        return JSON.parse(jsonStr)
      } catch (e) {
        console.error('解析库存扣减详情失败', e)
        return []
      }
    },

    // 导出销售单为 Excel
    handleExportSale(saleId) {
      if (!saleId) {
        this.$message.warning('销售单ID不存在')
        return
      }
      
      this.$confirm('确定要导出该销售单为 Excel 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message({
          message: '正在导出，请稍候...',
          type: 'info'
        })
        
        // 调用导出 API
        exportSale(saleId).then(response => {
          // 获取文件名
          const disposition = response.headers['content-disposition']
          let fileName = 'sale_export_' + Date.now() + '.xlsx'
          if (disposition && disposition.includes('filename=')) {
            fileName = decodeURIComponent(disposition.split('filename=')[1])
          }
          
          // 创建 blob 并下载
          const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = fileName
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
          
          this.$message.success('销售单导出成功')
        }).catch(error => {
          this.$message.error('导出销售单失败：' + (error.message || '未知错误'))
        })
      }).catch(() => {
        this.$message.info('已取消导出')
      })
    },

    // 检查管理员角色
    checkAdminRole() {
      if (this.roles && this.roles.length > 0) {
        this.isAdmin = this.roles.some(role => 
          role === 'admin' || 
          role === '管理员' || 
          role === 'super_admin' || 
          role === '超级管理员'
        )
      }
    },

    // 审核销售单
    handleApprove(row) {
      this.$confirm('确定要审核该销售单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.approveLoading = true
        approveSale(row.id).then(response => {
          this.$message.success('销售单审核成功')
          this.getList()
        }).catch(error => {
          this.$message.error('审核失败：' + (error.message || '未知错误'))
        }).finally(() => {
          this.approveLoading = false
        })
      }).catch(() => {
        this.$message.info('已取消审核')
      })
    },

    // 驳回销售单
    handleReject(row) {
      this.rejectForm.saleId = row.id
      this.rejectForm.saleNo = row.saleNo
      this.rejectForm.reason = ''
      this.showRejectDialog = true
    },

    // 确认驳回
    handleConfirmReject() {
      if (!this.rejectForm.reason || this.rejectForm.reason.trim() === '') {
        this.$message.warning('请输入驳回原因')
        return
      }

      this.$confirm('确定要驳回该销售单吗？驳回后库存将被恢复。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.rejectLoading = true
        rejectSale(this.rejectForm.saleId, this.rejectForm.reason).then(response => {
          this.$message.success('销售单驳回成功，库存已恢复')
          this.showRejectDialog = false
          this.getList()
        }).catch(error => {
          this.$message.error('驳回失败：' + (error.message || '未知错误'))
        }).finally(() => {
          this.rejectLoading = false
        })
      }).catch(() => {
        this.$message.info('已取消驳回')
      })
    },

    // 关闭驳回对话框
    handleCloseRejectDialog() {
      this.rejectForm = {
        saleId: null,
        saleNo: '',
        reason: ''
      }
      this.showRejectDialog = false
    },

    // 分页变化
    handleSizeChange(size) {
      this.pageSize = size
      this.pageNum = 1
      this.getList()
    },

    handleCurrentChange(page) {
      this.pageNum = page
      this.getList()
    },

    // 关闭对话框
    handleCloseDialog() {
      this.resetSaleForm()
      this.dialogStep = 1
    },

    // 重置表单
    resetSaleForm() {
      this.saleForm = {
        schoolId: null,
        saleTypeId: null,
        storeId: null,
        saleDate: new Date(),
        remark: '',
        discount: 0,
        items: []
      }
    },

    // 获取状态标签类型
    getStatusTag(status) {
      switch (status) {
        case 1:
          return 'warning'
        case 2:
          return 'success'
        case 3:
          return 'danger'
        default:
          return ''
      }
    }
  },
  filters: {
    formatPrice(value) {
      if (!value) return '0.00'
      return parseFloat(value).toFixed(2)
    },
    formatDate(value) {
      if (!value) return ''
      const date = new Date(value)
      return date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0') + '-' + String(date.getDate()).padStart(2, '0')
    },
    formatDateTime(value) {
      if (!value) return ''
      const date = new Date(value)
      return date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0') + '-' + String(date.getDate()).padStart(2, '0') + ' ' + 
             String(date.getHours()).padStart(2, '0') + ':' + String(date.getMinutes()).padStart(2, '0') + ':' + String(date.getSeconds()).padStart(2, '0')
    }
  }
}
</script>

<style scoped>
.non-system-sale-container {
  padding: 20px;
}

.table-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.tip-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background-color: #e6f7ff;
  border-left: 3px solid #1890ff;
  border-radius: 4px;
  color: #0050b3;
  font-size: 13px;
}

.tip-box i {
  font-size: 14px;
  color: #1890ff;
}

.stock-warning {
  margin: 15px 0;
  padding: 12px 15px;
  background-color: #fef0f0;
  border-left: 4px solid #f56c6c;
  border-radius: 4px;
  color: #f56c6c;
  display: flex;
  align-items: center;
  gap: 10px;
}

.stock-warning i {
  font-size: 16px;
}

.stock-deduction-plan {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 10px;
}

.plan-item {
  margin-bottom: 15px;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

.plan-header {
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.plan-details {
  margin-left: 10px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 5px 0;
  font-size: 13px;
  color: #666;
}

.store-name {
  min-width: 80px;
  font-weight: 500;
  color: #333;
}

.deduction-info {
  flex: 1;
}

.table-header {
  margin-bottom: 20px;
}

.step-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ddd;
}

.step-header h3 {
  margin: 0 0 10px 0;
}

.selected-type {
  color: #666;
  font-size: 14px;
}

.sale-items-section {
  margin-bottom: 20px;
}

.discount-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.discount-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.discount-input-group label {
  font-weight: bold;
  min-width: 80px;
}

.add-item-btn {
  margin-bottom: 15px;
}

.items-summary {
  display: flex;
  gap: 30px;
  margin-top: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.summary-item {
  font-size: 16px;
}

.summary-item strong {
  color: #ff7043;
  font-weight: bold;
}

.confirm-form {
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  gap: 40px;
  margin-bottom: 15px;
}

.form-item {
  flex: 1;
  display: flex;
  gap: 10px;
}

.form-item.full-width {
  flex-basis: 100%;
}

.form-item label {
  font-weight: bold;
  min-width: 80px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

.sale-list {
  width: 100%;
}

.detail-content {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h4 {
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #ddd;
  font-size: 16px;
  font-weight: bold;
}

.detail-row {
  display: flex;
  gap: 40px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.detail-item {
  flex: 1;
  min-width: 250px;
  display: flex;
  gap: 10px;
}

.detail-item.full-width {
  flex-basis: 100%;
}

.detail-item label {
  font-weight: bold;
  min-width: 80px;
  color: #666;
}

.detail-item span {
  color: #333;
}

.product-info {
  text-align: left;
}

.product-info div:first-child {
  font-weight: 500;
}

.product-info div:last-child {
  font-size: 12px;
  color: #999;
}

/* 库存扣减详情样式 */
.stock-deduction-detail {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 0;
}

.deduction-item {
  margin-bottom: 0;
  padding: 15px;
  background-color: white;
  border-radius: 4px;
  border-left: 4px solid #409eff;
  margin-bottom: 15px;
}

.deduction-item:last-child {
  margin-bottom: 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.product-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-header strong {
  font-size: 15px;
  color: #333;
}

.sku-code {
  font-size: 12px;
  color: #999;
  background-color: #f0f0f0;
  padding: 2px 8px;
  border-radius: 3px;
}

.quantity-badge {
  font-size: 13px;
  color: #666;
  background-color: #f0f0f0;
  padding: 4px 12px;
  border-radius: 3px;
}

.quantity-value {
  font-weight: bold;
  color: #ff7043;
  font-size: 14px;
}

.item-details {
  margin-top: 10px;
}

.deduction-table {
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.table-header {
  display: flex;
  background-color: #f5f7fa;
  border-bottom: 2px solid #ddd;
  font-weight: bold;
  font-size: 13px;
  color: #333;
}

.table-row {
  display: flex;
  border-bottom: 1px solid #eee;
  align-items: center;
  padding: 0;
}

.table-row:last-child {
  border-bottom: none;
}

.col-store {
  flex: 2;
  padding: 12px 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-right: 1px solid #eee;
}

.col-stock,
.col-deduction,
.col-after {
  flex: 1;
  padding: 12px 15px;
  text-align: center;
  border-right: 1px solid #eee;
}

.col-after {
  border-right: none;
}

.table-header .col-store,
.table-header .col-stock,
.table-header .col-deduction,
.table-header .col-after {
  border-right: 1px solid #ddd;
}

.table-header .col-after {
  border-right: none;
}

.store-name {
  font-weight: 500;
  color: #333;
}

.warehouse-badge {
  background-color: #fef0f0;
  color: #f56c6c;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
}

.deduction-qty {
  background-color: #e6f7ff;
  color: #409eff;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 3px;
  display: inline-block;
}

.no-deduction {
  color: #999;
  font-size: 14px;
}

.detail-line {
  display: flex;
  align-items: center;
  padding: 8px 0;
  font-size: 13px;
}

.detail-line .label {
  font-weight: 500;
  color: #666;
  min-width: 100px;
}

.detail-line .value {
  color: #333;
  font-weight: bold;
}

/* 分享对话框样式 */
.share-content {
  padding: 10px 0;
}

.share-section {
  margin-bottom: 30px;
}

.share-section h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #333;
  border-left: 3px solid #409eff;
  padding-left: 10px;
}

.share-link-box {
  display: flex;
  align-items: center;
}

.share-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.qrcode-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.qrcode-img {
  width: 200px;
  height: 200px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.qrcode-placeholder {
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border: 1px dashed #ddd;
  border-radius: 8px;
  color: #999;
  font-size: 14px;
  text-align: center;
  padding: 20px;
}

.share-empty {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
