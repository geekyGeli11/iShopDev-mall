<template>
  <div style="margin-top: 50px">
    <el-form :model="value" ref="productAttrForm" label-width="120px" class="form-inner-container" size="small">
      <el-form-item label="属性类型：">
        <el-select v-model="value.productAttributeCategoryId" placeholder="请选择属性类型" @change="handleProductAttrChange">
          <el-option v-for="item in productAttributeCategoryOptions" :key="item.value" :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品规格：">
        <el-card shadow="never" class="cardBg">
          <div v-for="(productAttr, idx) in selectProductAttr">
            {{ productAttr.name }}：
            <el-checkbox-group v-if="productAttr.handAddStatus === 0" v-model="selectProductAttr[idx].values">
              <el-checkbox v-for="item in getInputListArr(productAttr.inputList)" :label="item" :key="item"
                class="littleMarginLeft"></el-checkbox>
            </el-checkbox-group>
            <div v-else>
              <el-checkbox-group v-model="selectProductAttr[idx].values">
                <div v-for="(item, index) in selectProductAttr[idx].options" style="display: inline-block"
                  class="littleMarginLeft">
                  <el-checkbox :label="item" :key="item"></el-checkbox>
                  <el-button type="text" class="littleMarginLeft" @click="handleRemoveProductAttrValue(idx, index)">删除
                  </el-button>
                </div>
              </el-checkbox-group>
              <el-input v-model="addProductAttrValue" style="width: 160px;margin-left: 10px" clearable></el-input>
              <el-button class="littleMarginLeft" @click="handleAddProductAttrValue(idx)">增加</el-button>
            </div>
          </div>
        </el-card>
        <el-table style="width: 100%;margin-top: 20px" :data="value.skuStockList" border>
          <el-table-column v-for="(item, index) in selectProductAttr" :label="item.name" :key="item.id" align="center">
            <template slot-scope="scope">
              {{ getProductSkuSp(scope.row, index) }}
            </template>
          </el-table-column>
          <el-table-column label="销售价格" width="100" align="center">
            <template slot-scope="scope">
              <el-input v-model="scope.row.price" :class="{ 'is-error': scope.row.priceError }">
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="促销价格" width="100" align="center">
            <template slot-scope="scope">
              <el-input v-model="scope.row.promotionPrice"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="商品库存" width="80" align="center">
            <template slot-scope="scope">
              <el-input v-model="scope.row.stock" :class="{ 'is-error': scope.row.stockError }">
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="库存预警值" width="80" align="center">
            <template slot-scope="scope">
              <el-input v-model="scope.row.lowStock"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="SKU编号" width="160" align="center">
            <template slot-scope="scope">
              <el-input v-model="scope.row.skuCode"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template slot-scope="scope">
              <el-button type="text" @click="handleRemoveProductSku(scope.$index, scope.row)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" style="margin-top: 20px" @click="handleRefreshProductSkuList">刷新列表
        </el-button>
        <el-button type="primary" style="margin-top: 20px" @click="handleSyncProductSkuPrice">同步价格
        </el-button>
        <el-button type="primary" style="margin-top: 20px" @click="handleSyncProductSkuStock">同步库存
        </el-button>
      </el-form-item>
      <!-- 门店库存管理 -->
      <el-form-item label="门店库存分配：" v-if="value.skuStockList && value.skuStockList.length > 0">
        <el-card shadow="never" class="cardBg">
          <div style="margin-bottom: 15px;">
            <el-button type="primary" size="small" @click="handleShowStoreStockDialog">
              <i class="el-icon-setting"></i> 管理门店库存分配
            </el-button>
            <el-button type="success" size="small" @click="handleAutoDistributeAllSkuStock" style="margin-left: 10px;">
              <i class="el-icon-magic-stick"></i> 一键均分所有SKU库存
            </el-button>
            <span style="margin-left: 15px; color: #606266; font-size: 12px;">
              点击管理按钮可为每个SKU设置门店库存分配，门店库存总和必须等于SKU总库存
            </span>
          </div>

          <!-- 门店库存分配汇总展示 -->
          <div v-if="storeStockSummary.length > 0">
            <div style="margin-bottom: 10px; font-weight: bold; color: #409EFF;">门店库存分配概览：</div>
            <el-table :data="storeStockSummary" border size="small" style="width: 100%;">
              <el-table-column label="SKU规格" width="200" align="center">
                <template slot-scope="scope">
                  <span>{{ getSkuDisplayName(scope.row.skuId) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="SKU总库存" width="100" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.totalStock }}</span>
                </template>
              </el-table-column>
              <el-table-column label="已分配库存" width="100" align="center">
                <template slot-scope="scope">
                  <span :class="{ 'stock-warning': scope.row.allocatedStock !== scope.row.totalStock }">
                    {{ scope.row.allocatedStock }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="分配状态" width="100" align="center">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.allocatedStock === scope.row.totalStock ? 'success' : 'warning'"
                    size="small">
                    {{ scope.row.allocatedStock === scope.row.totalStock ? '已完成' : '未完成' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="门店分配详情" align="center">
                <template slot-scope="scope">
                  <div style="max-height: 60px; overflow-y: auto;">
                    <span v-for="(store, index) in scope.row.storeDetails" :key="index"
                      style="display: inline-block; margin: 2px;">
                      <el-tag size="mini" type="info">{{ store.storeName }}: {{ store.stock }}</el-tag>
                    </span>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-form-item>
      <el-form-item label="属性图片：" v-if="hasAttrPic">
        <el-card shadow="never" class="cardBg">
          <div v-for="(item, index) in selectProductAttrPics">
            <span>{{ item.name }}:</span>
            <single-upload v-model="item.pic"
              style="width: 300px;display: inline-block;margin-left: 10px"></single-upload>
          </div>
        </el-card>
      </el-form-item>
      <el-form-item label="商品参数：">
        <el-card shadow="never" class="cardBg">
          <div v-for="(item, index) in selectProductParam" :class="{ littleMarginTop: index !== 0 }">
            <div class="paramInputLabel">{{ item.name }}:</div>
            <el-select v-if="item.inputType === 1" class="paramInput" v-model="selectProductParam[index].value">
              <el-option v-for="item in getParamInputList(item.inputList)" :key="item" :label="item" :value="item">
              </el-option>
            </el-select>
            <el-input v-else class="paramInput" v-model="selectProductParam[index].value"></el-input>
          </div>
        </el-card>
      </el-form-item>
      <el-form-item label="商品相册：">
        <multi-upload v-model="selectProductPics" :max-count="10"></multi-upload>
      </el-form-item>
      <el-form-item label="商品详情：">
        <el-tabs v-model="activeHtmlName" type="card">
          <el-tab-pane label="电脑端详情" name="pc">
            <tinymce :width="595" :height="300" v-model="value.detailHtml"></tinymce>
          </el-tab-pane>
          <el-tab-pane label="移动端详情" name="mobile">
            <tinymce :width="595" :height="300" v-model="value.detailMobileHtml"></tinymce>
          </el-tab-pane>
        </el-tabs>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button size="medium" @click="handlePrev">上一步，填写商品促销</el-button>
        <el-button type="primary" size="medium" @click="handleNext">下一步，选择商品关联</el-button>
      </el-form-item>
    </el-form>

    <!-- 门店库存分配对话框 -->
    <el-dialog title="门店库存分配管理" :visible.sync="storeStockDialogVisible" width="80%" :close-on-click-modal="false">
      <div v-if="currentEditingSkuList.length > 0">
        <div style="margin-bottom: 15px;">
          <el-alert title="提示：每个SKU的门店库存总和必须等于该SKU的总库存，否则无法保存" type="info" :closable="false"
            style="margin-bottom: 15px;">
          </el-alert>
        </div>

        <el-tabs v-model="activeSkuTab" type="card">
          <el-tab-pane v-for="(sku, skuIndex) in currentEditingSkuList" :key="sku.id"
            :label="getSkuTabLabel(sku, skuIndex)" :name="String(skuIndex)">

            <div style="margin-bottom: 15px;">
              <div style="display: flex; align-items: center; justify-content: space-between;">
                <div>
                  <span style="font-weight: bold;">SKU规格：</span>
                  <span>{{ getSkuDisplayName(sku.id) }}</span>
                  <span style="margin-left: 20px; font-weight: bold;">总库存：</span>
                  <span style="color: #409EFF; font-size: 16px;">{{ sku.stock }}</span>
                </div>
                <el-button type="primary" size="small" @click="handleAutoDistributeSingleSku(skuIndex)">
                  <i class="el-icon-magic-stick"></i> 均分此SKU库存
                </el-button>
              </div>

              <div style="margin-top: 10px;">
                <span style="font-weight: bold;">已分配库存：</span>
                <span
                  :style="{ color: getDistributedStock(skuIndex) === sku.stock ? '#67C23A' : '#F56C6C', fontSize: '16px' }">
                  {{ getDistributedStock(skuIndex) }}
                </span>
                <span style="margin-left: 10px;">
                  <el-tag :type="getDistributedStock(skuIndex) === sku.stock ? 'success' : 'danger'" size="small">
                    {{ getDistributedStock(skuIndex) === sku.stock ? '分配完成' : '分配不足或超量' }}
                  </el-tag>
                </span>
              </div>
            </div>

            <el-table :data="allStores" border size="small" style="width: 100%;">
              <el-table-column label="门店名称" width="200" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.addressName }}</span>
                </template>
              </el-table-column>
              <el-table-column label="门店地址" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.province }}{{ scope.row.city }}{{ scope.row.region }}{{ scope.row.detailAddress }}</span>
                </template>
              </el-table-column>
              <el-table-column label="分配库存" width="120" align="center">
                <template slot-scope="scope">
                  <el-input-number 
                    v-if="storeStockData[skuIndex] && storeStockData[skuIndex][scope.row.id]"
                    v-model="storeStockData[skuIndex][scope.row.id].stock"
                    @change="handleStoreStockChange"
                    :min="0" :max="sku.stock" size="small"
                    controls-position="right">
                  </el-input-number>
                  <span v-else>0</span>
                </template>
              </el-table-column>
              <el-table-column label="预警库存" width="120" align="center">
                <template slot-scope="scope">
                  <el-input-number 
                    v-if="storeStockData[skuIndex] && storeStockData[skuIndex][scope.row.id]"
                    v-model="storeStockData[skuIndex][scope.row.id].lowStock"
                    :min="0"
                    :max="storeStockData[skuIndex][scope.row.id].stock" 
                    size="small" 
                    controls-position="right">
                  </el-input-number>
                  <span v-else>0</span>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100" align="center">
                <template slot-scope="scope">
                  <el-switch 
                    v-if="storeStockData[skuIndex] && storeStockData[skuIndex][scope.row.id]"
                    v-model="storeStockData[skuIndex][scope.row.id].status"
                    :active-value="1" 
                    :inactive-value="0">
                  </el-switch>
                  <span v-else>--</span>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="storeStockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveStoreStock" :loading="saveStoreStockLoading">保存分配</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList as fetchProductAttrCateList } from '@/api/productAttrCate'
import { fetchList as fetchProductAttrList } from '@/api/productAttr'
import { fetchAllStores, batchSaveStoreSkuStockByProduct, autoDistributeStock, fetchStoreSkuStockByProduct } from '@/api/storeSkuStock'
import SingleUpload from '@/components/Upload/singleUpload'
import MultiUpload from '@/components/Upload/multiUpload'
import Tinymce from '@/components/Tinymce'

export default {
  name: "ProductAttrDetail",
  components: { SingleUpload, MultiUpload, Tinymce },
  props: {
    value: Object,
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      //编辑模式时是否初始化成功
      hasEditCreated: false,
      //商品属性分类下拉选项
      productAttributeCategoryOptions: [],
      //选中的商品属性
      selectProductAttr: [],
      //选中的商品参数
      selectProductParam: [],
      //选中的商品属性图片
      selectProductAttrPics: [],
      //可手动添加的商品属性
      addProductAttrValue: '',
      //商品富文本详情激活类型
      activeHtmlName: 'mobile',
      //门店库存管理相关数据
      storeStockDialogVisible: false,
      allStores: [],
      currentEditingSkuList: [],
      storeStockData: {}, // 格式: {skuIndex: {storeId: {stock: 0, lowStock: 0, status: 1}}}
      activeSkuTab: '0',
      saveStoreStockLoading: false,
      storeStockSummary: []
    }
  },
  computed: {
    //是否有商品属性图片
    hasAttrPic() {
      if (this.selectProductAttrPics.length < 1) {
        return false;
      }
      return true;
    },
    //商品的编号
    productId() {
      return this.value.id;
    },
    //商品的主图和画册图片
    selectProductPics: {
      get: function () {
        let pics = [];
        if (this.value.pic === undefined || this.value.pic == null || this.value.pic === '') {
          return pics;
        }
        pics.push(this.value.pic);
        if (this.value.albumPics === undefined || this.value.albumPics == null || this.value.albumPics === '') {
          return pics;
        }
        let albumPics = this.value.albumPics.split(',');
        for (let i = 0; i < albumPics.length; i++) {
          pics.push(albumPics[i]);
        }
        return pics;
      },
      set: function (newValue) {
        if (newValue == null || newValue.length === 0) {
          this.value.pic = null;
          this.value.albumPics = null;
        } else {
          // 保存第一张图片作为商品主图
          this.value.pic = newValue[0];
          // 如果有多张图片，将其余图片作为画册图片
          if (newValue.length > 1) {
            // 构建画册图片字符串
            this.value.albumPics = newValue.slice(1).join(',');
          } else {
            this.value.albumPics = '';
          }
        }
      }
    }
  },
  created() {
    this.getProductAttrCateList();
  },
  watch: {
    productId: function (newValue) {
      if (!this.isEdit) return;
      if (this.hasEditCreated) return;
      if (newValue === undefined || newValue == null || newValue === 0) return;
      this.handleEditCreated();
    }
  },
  methods: {
          handleEditCreated() {
        //根据商品属性分类id获取属性和参数
      if (this.value.productAttributeCategoryId != null) {
          this.handleProductAttrChange(this.value.productAttributeCategoryId);
        }
        
      // 初始化门店库存汇总显示
      this.initStoreStockSummaryForEdit();
      this.hasEditCreated = true;
      },
    getProductAttrCateList() {
      let param = { pageNum: 1, pageSize: 100 };
      fetchProductAttrCateList(param).then(response => {
        this.productAttributeCategoryOptions = [];
        if (response && response.data && response.data.list) {
          let list = response.data.list;
          for (let i = 0; i < list.length; i++) {
            this.productAttributeCategoryOptions.push({ label: list[i].name, value: list[i].id });
          }
        }
      }).catch(error => {
        console.error('获取商品属性分类列表失败:', error);
        this.$message({
          message: '获取商品属性分类列表失败',
          type: 'error',
          duration: 3000
        });
      });
    },
    getProductAttrList(type, cid) {
      let param = { pageNum: 1, pageSize: 100, type: type };
      fetchProductAttrList(cid, param).then(response => {
        let list = response.data.list;
        if (type === 0) {
          this.selectProductAttr = [];
          for (let i = 0; i < list.length; i++) {
            let options = [];
            let values = [];
            if (this.isEdit) {
              if (list[i].handAddStatus === 1) {
                //编辑状态下获取手动添加编辑属性
                options = this.getEditAttrOptions(list[i].id);
              }
              //编辑状态下获取选中属性
              values = this.getEditAttrValues(i);
            }
            this.selectProductAttr.push({
              id: list[i].id,
              name: list[i].name,
              handAddStatus: list[i].handAddStatus,
              inputList: list[i].inputList,
              values: values,
              options: options
            });
          }
          if (this.isEdit) {
            //编辑模式下刷新商品属性图片
            this.refreshProductAttrPics();
          }
        } else {
          this.selectProductParam = [];
          for (let i = 0; i < list.length; i++) {
            let value = null;
            if (this.isEdit) {
              //编辑模式下获取参数属性
              value = this.getEditParamValue(list[i].id);
            }
            this.selectProductParam.push({
              id: list[i].id,
              name: list[i].name,
              value: value,
              inputType: list[i].inputType,
              inputList: list[i].inputList
            });
          }
        }
      });
    },
    //获取设置的可手动添加属性值
    getEditAttrOptions(id) {
      let options = [];
      try {
        if (!this.value.productAttributeValueList || !Array.isArray(this.value.productAttributeValueList)) {
          return options;
        }

        for (let i = 0; i < this.value.productAttributeValueList.length; i++) {
          let attrValue = this.value.productAttributeValueList[i];
          if (attrValue.productAttributeId === id && attrValue.value) {
            let strArr = attrValue.value.split(',');
            for (let j = 0; j < strArr.length; j++) {
              if (strArr[j].trim() !== '') {
                options.push(strArr[j].trim());
              }
            }
            break;
          }
        }
      } catch (error) {
        console.error('获取编辑属性选项出错:', error);
      }
      return options;
    },
        //获取选中的属性值
    getEditAttrValues(index) {
      let values = new Set();
      try {
        if (!this.value.skuStockList || this.value.skuStockList.length === 0) {
          return [];
        }
        
        for (let i = 0; i < this.value.skuStockList.length; i++) {
          let sku = this.value.skuStockList[i];
          if (!sku.spData) {
            continue;
          }
          
          try {
            let spData = JSON.parse(sku.spData);
            if (spData != null && Array.isArray(spData)) {
              if (index === 0 && spData.length >= 1) {
                values.add(spData[0].value);
              } else if (index === 1 && spData.length >= 2) {
                values.add(spData[1].value);
              } else if (index === 2 && spData.length >= 3) {
                values.add(spData[2].value);
              }
            }
          } catch (parseError) {
            console.error('解析SKU spData出错:', parseError, sku.spData);
          }
        }
      } catch (error) {
        console.error('获取编辑属性值出错:', error);
      }
      return Array.from(values);
    },
    //获取属性的值
    getEditParamValue(id) {
      try {
        if (!this.value.productAttributeValueList || !Array.isArray(this.value.productAttributeValueList)) {
          return null;
        }

        for (let i = 0; i < this.value.productAttributeValueList.length; i++) {
          let attrValue = this.value.productAttributeValueList[i];
          if (id === attrValue.productAttributeId) {
            return attrValue.value || null;
          }
        }
      } catch (error) {
        console.error('获取编辑参数值出错:', error);
      }
      return null;
    },
    handleProductAttrChange(value) {
      this.getProductAttrList(0, value);
      this.getProductAttrList(1, value);
    },
    getInputListArr(inputList) {
      if (!inputList || typeof inputList !== 'string') {
        return [];
      }
      return inputList.split(',').filter(item => item.trim() !== '');
    },
    handleAddProductAttrValue(idx) {
      let options = this.selectProductAttr[idx].options;
      if (this.addProductAttrValue == null || this.addProductAttrValue == '') {
        this.$message({
          message: '属性值不能为空',
          type: 'warning',
          duration: 1000
        });
        return
      }
      if (options.indexOf(this.addProductAttrValue) !== -1) {
        this.$message({
          message: '属性值不能重复',
          type: 'warning',
          duration: 1000
        });
        return;
      }
      const newValue = this.addProductAttrValue;
      this.selectProductAttr[idx].options.push(newValue);
      // 同时添加到values数组并增量更新SKU列表
      this.selectProductAttr[idx].values.push(newValue);
      this.addSkuForNewAttrValue(idx, newValue);
      this.addProductAttrValue = null;
    },
    handleRemoveProductAttrValue(idx, index) {
      const removedValue = this.selectProductAttr[idx].options[index];
      this.selectProductAttr[idx].options.splice(index, 1);
      // 同时从values数组中移除并更新SKU列表
      const valueIndex = this.selectProductAttr[idx].values.indexOf(removedValue);
      if (valueIndex !== -1) {
        this.selectProductAttr[idx].values.splice(valueIndex, 1);
      }
      this.removeSkuForAttrValue(idx, removedValue);
    },
    // 为新增的属性值增量添加SKU
    addSkuForNewAttrValue(attrIdx, newValue) {
      const skuList = this.value.skuStockList || [];
      const attrName = this.selectProductAttr[attrIdx].name;
      const oldSkuCount = skuList.length;
      
      if (this.selectProductAttr.length === 1) {
        // 只有一个属性，直接添加
        skuList.push({
          spData: JSON.stringify([{ key: attrName, value: newValue }])
        });
      } else if (this.selectProductAttr.length === 2) {
        const otherIdx = attrIdx === 0 ? 1 : 0;
        const otherAttr = this.selectProductAttr[otherIdx];
        const otherValues = otherAttr.values.length > 0 ? otherAttr.values : [];
        
        if (otherValues.length === 0) {
          // 另一个属性没有值，只添加当前属性
          if (attrIdx === 0) {
            skuList.push({
              spData: JSON.stringify([{ key: attrName, value: newValue }])
            });
          }
        } else {
          // 与另一个属性的所有值组合
          for (let val of otherValues) {
            let spData = attrIdx === 0
              ? [{ key: attrName, value: newValue }, { key: otherAttr.name, value: val }]
              : [{ key: otherAttr.name, value: val }, { key: attrName, value: newValue }];
            skuList.push({ spData: JSON.stringify(spData) });
          }
        }
      } else if (this.selectProductAttr.length >= 3) {
        // 三个属性的情况
        const attr0 = this.selectProductAttr[0];
        const attr1 = this.selectProductAttr[1];
        const attr2 = this.selectProductAttr[2];
        
        if (attrIdx === 0) {
          const vals1 = attr1.values.length > 0 ? attr1.values : [null];
          const vals2 = attr2.values.length > 0 ? attr2.values : [null];
          for (let v1 of vals1) {
            for (let v2 of vals2) {
              let spData = [{ key: attr0.name, value: newValue }];
              if (v1) spData.push({ key: attr1.name, value: v1 });
              if (v2) spData.push({ key: attr2.name, value: v2 });
              skuList.push({ spData: JSON.stringify(spData) });
            }
          }
        } else if (attrIdx === 1) {
          const vals0 = attr0.values.length > 0 ? attr0.values : [null];
          const vals2 = attr2.values.length > 0 ? attr2.values : [null];
          for (let v0 of vals0) {
            for (let v2 of vals2) {
              let spData = [];
              if (v0) spData.push({ key: attr0.name, value: v0 });
              spData.push({ key: attr1.name, value: newValue });
              if (v2) spData.push({ key: attr2.name, value: v2 });
              skuList.push({ spData: JSON.stringify(spData) });
            }
          }
        } else {
          const vals0 = attr0.values.length > 0 ? attr0.values : [null];
          const vals1 = attr1.values.length > 0 ? attr1.values : [null];
          for (let v0 of vals0) {
            for (let v1 of vals1) {
              let spData = [];
              if (v0) spData.push({ key: attr0.name, value: v0 });
              if (v1) spData.push({ key: attr1.name, value: v1 });
              spData.push({ key: attr2.name, value: newValue });
              skuList.push({ spData: JSON.stringify(spData) });
            }
          }
        }
      }
      this.value.skuStockList = skuList;
      // 更新属性图片（如果是第一个属性）
      if (attrIdx === 0) {
        this.selectProductAttrPics.push({ name: newValue, pic: null });
      }
      // 同步更新门店库存数据结构（为新增的SKU添加门店库存记录）
      this.syncStoreStockDataOnSkuAdd(oldSkuCount, skuList.length);
    },
    // 删除属性值时移除对应的SKU
    removeSkuForAttrValue(attrIdx, removedValue) {
      const attrName = this.selectProductAttr[attrIdx].name;
      // 记录被删除的SKU索引
      const removedSkuIndexes = [];
      (this.value.skuStockList || []).forEach((sku, index) => {
        try {
          const spData = JSON.parse(sku.spData);
          for (let sp of spData) {
            if (sp.key === attrName && sp.value === removedValue) {
              removedSkuIndexes.push(index);
              break;
            }
          }
        } catch (e) {
          // ignore
        }
      });
      
      this.value.skuStockList = (this.value.skuStockList || []).filter((sku, index) => {
        return !removedSkuIndexes.includes(index);
      });
      // 更新属性图片（如果是第一个属性）
      if (attrIdx === 0) {
        const picIndex = this.selectProductAttrPics.findIndex(p => p.name === removedValue);
        if (picIndex !== -1) {
          this.selectProductAttrPics.splice(picIndex, 1);
        }
      }
      // 同步更新门店库存数据结构（移除被删除SKU的门店库存记录）
      this.syncStoreStockDataOnSkuRemove(removedSkuIndexes);
    },
    // 新增SKU时同步门店库存数据
    syncStoreStockDataOnSkuAdd(oldCount, newCount) {
      if (this.allStores.length === 0 || Object.keys(this.storeStockData).length === 0) {
        return;
      }
      // 为新增的SKU索引初始化门店库存数据
      for (let skuIndex = oldCount; skuIndex < newCount; skuIndex++) {
        this.$set(this.storeStockData, skuIndex, {});
        this.allStores.forEach(store => {
          this.$set(this.storeStockData[skuIndex], store.id, {
            stock: 0,
            lowStock: 0,
            status: 1
          });
        });
      }
      // 同步更新currentEditingSkuList
      this.currentEditingSkuList = [...(this.value.skuStockList || [])];
      // 更新汇总
      this.updateStoreStockSummary();
    },
    // 删除SKU时同步门店库存数据
    syncStoreStockDataOnSkuRemove(removedIndexes) {
      if (Object.keys(this.storeStockData).length === 0) {
        return;
      }
      // 重建storeStockData，跳过被删除的索引
      const newStoreStockData = {};
      let newIndex = 0;
      const oldKeys = Object.keys(this.storeStockData).map(k => parseInt(k)).sort((a, b) => a - b);
      for (let oldIndex of oldKeys) {
        if (!removedIndexes.includes(oldIndex)) {
          newStoreStockData[newIndex] = this.storeStockData[oldIndex];
          newIndex++;
        }
      }
      this.storeStockData = newStoreStockData;
      // 同步更新currentEditingSkuList
      this.currentEditingSkuList = [...(this.value.skuStockList || [])];
      // 更新汇总
      this.updateStoreStockSummary();
    },
    getProductSkuSp(row, index) {
      try {
        if (!row.spData) {
          return null;
        }
        let spData = JSON.parse(row.spData);
        if (spData != null && Array.isArray(spData) && index < spData.length) {
          return spData[index].value;
        }
      } catch (error) {
        console.error('解析SKU规格数据出错:', error, row.spData);
      }
      return null;
    },
    handleRefreshProductSkuList() {
      this.$confirm('刷新列表将导致sku信息重新生成，是否要刷新', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.refreshProductAttrPics();
        this.refreshProductSkuList();
      });
    },
    handleSyncProductSkuPrice() {
      this.$confirm('将同步第一个sku的价格到所有sku,是否继续', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.value.skuStockList !== null && this.value.skuStockList.length > 0) {
          let price = this.value.skuStockList[0].price;
          for (let i = 0; i < this.value.skuStockList.length; i++) {
            this.value.skuStockList[i].price = price;
          }
        }
      });
    },
    handleSyncProductSkuStock() {
      this.$confirm('将同步第一个sku的库存到所有sku,是否继续', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.value.skuStockList !== null && this.value.skuStockList.length > 0) {
          let stock = this.value.skuStockList[0].stock;
          let lowStock = this.value.skuStockList[0].lowStock;
          for (let i = 0; i < this.value.skuStockList.length; i++) {
            this.value.skuStockList[i].stock = stock;
            this.value.skuStockList[i].lowStock = lowStock;
          }
        }
      });
    },
    refreshProductSkuList() {
      this.value.skuStockList = [];
      let skuList = this.value.skuStockList;
      
      //只有一个属性时
      if (this.selectProductAttr.length === 1) {
        let attr = this.selectProductAttr[0];
        for (let i = 0; i < attr.values.length; i++) {
          skuList.push({
            spData: JSON.stringify([{ key: attr.name, value: attr.values[i] }])
          });
        }
      } else if (this.selectProductAttr.length === 2) {
        let attr0 = this.selectProductAttr[0];
        let attr1 = this.selectProductAttr[1];
        for (let i = 0; i < attr0.values.length; i++) {
          if (attr1.values.length === 0) {
            skuList.push({
              spData: JSON.stringify([{ key: attr0.name, value: attr0.values[i] }])
            });
            continue;
          }
          for (let j = 0; j < attr1.values.length; j++) {
            let spData = [];
            spData.push({ key: attr0.name, value: attr0.values[i] });
            spData.push({ key: attr1.name, value: attr1.values[j] });
            skuList.push({
              spData: JSON.stringify(spData)
            });
          }
        }
      } else {
        let attr0 = this.selectProductAttr[0];
        let attr1 = this.selectProductAttr[1];
        let attr2 = this.selectProductAttr[2];
        for (let i = 0; i < attr0.values.length; i++) {
          if (attr1.values.length === 0) {
            skuList.push({
              spData: JSON.stringify([{ key: attr0.name, value: attr0.values[i] }])
            });
            continue;
          }
          for (let j = 0; j < attr1.values.length; j++) {
            if (attr2.values.length === 0) {
              let spData = [];
              spData.push({ key: attr0.name, value: attr0.values[i] });
              spData.push({ key: attr1.name, value: attr1.values[j] });
              skuList.push({
                spData: JSON.stringify(spData)
              });
              continue;
            }
            for (let k = 0; k < attr2.values.length; k++) {
              let spData = [];
              spData.push({ key: attr0.name, value: attr0.values[i] });
              spData.push({ key: attr1.name, value: attr1.values[j] });
              spData.push({ key: attr2.name, value: attr2.values[k] });
              skuList.push({
                spData: JSON.stringify(spData)
              });
            }
          }
        }
      }
          },
    refreshProductAttrPics() {
      this.selectProductAttrPics = [];
      if (this.selectProductAttr.length >= 1) {
        let values = this.selectProductAttr[0].values;
        for (let i = 0; i < values.length; i++) {
          let pic = null;
          if (this.isEdit) {
            //编辑状态下获取图片
            pic = this.getProductSkuPic(values[i]);
          }
          this.selectProductAttrPics.push({ name: values[i], pic: pic })
        }
      }
    },
    //获取商品相关属性的图片
    getProductSkuPic(name) {
      try {
        if (!this.value.skuStockList || this.value.skuStockList.length === 0) {
          return null;
        }

        for (let i = 0; i < this.value.skuStockList.length; i++) {
          let sku = this.value.skuStockList[i];
          if (!sku.spData) {
            continue;
          }

          try {
            let spData = JSON.parse(sku.spData);
            if (spData && Array.isArray(spData) && spData.length > 0 && name === spData[0].value) {
              return sku.pic;
            }
          } catch (parseError) {
            console.error('解析SKU spData出错:', parseError, sku.spData);
          }
        }
      } catch (error) {
        console.error('获取商品SKU图片出错:', error);
      }
      return null;
    },
    //合并商品属性
    mergeProductAttrValue() {
      this.value.productAttributeValueList = [];
      // 处理商品规格属性（type=0）
      for (let i = 0; i < this.selectProductAttr.length; i++) {
        let attr = this.selectProductAttr[i];
        if (attr.handAddStatus === 1 && attr.options != null && attr.options.length > 0) {
          // 手动添加的属性选项
          this.value.productAttributeValueList.push({
            productAttributeId: attr.id,
            value: this.getOptionStr(attr.options)
          });
        } else if (attr.handAddStatus === 0 && attr.values != null && attr.values.length > 0) {
          // 预定义的属性选项，保存选中的值
          this.value.productAttributeValueList.push({
            productAttributeId: attr.id,
            value: this.getOptionStr(attr.values)
          });
        }
      }
      // 处理商品参数属性（type=1）
      for (let i = 0; i < this.selectProductParam.length; i++) {
        let param = this.selectProductParam[i];
        // 只有当参数值不为空时才添加到列表中
        if (param.value !== null && param.value !== undefined && param.value !== '' && String(param.value).trim() !== '') {
          this.value.productAttributeValueList.push({
            productAttributeId: param.id,
            value: String(param.value).trim()
          });
        }
      }
    },
    //合并商品属性图片
    mergeProductAttrPics() {
      for (let i = 0; i < this.selectProductAttrPics.length; i++) {
        for (let j = 0; j < this.value.skuStockList.length; j++) {
          let spData = JSON.parse(this.value.skuStockList[j].spData);
          if (spData[0].value === this.selectProductAttrPics[i].name) {
            this.value.skuStockList[j].pic = this.selectProductAttrPics[i].pic;
          }
        }
      }
    },
    getOptionStr(arr) {
      let str = '';
      for (let i = 0; i < arr.length; i++) {
        str += arr[i];
        if (i != arr.length - 1) {
          str += ',';
        }
      }
      return str;
    },
    handleRemoveProductSku(index, row) {
      let list = this.value.skuStockList;
      if (list.length === 1) {
        list.pop();
      } else {
        list.splice(index, 1);
      }
      // 同步更新门店库存数据
      this.syncStoreStockDataOnSkuRemove([index]);
    },
    getParamInputList(inputList) {
      if (!inputList || typeof inputList !== 'string') {
        return [];
      }
      return inputList.split(',').filter(item => item.trim() !== '');
    },
    handlePrev() {
      this.$emit('prevStep')
    },
    handleNext() {
      // 新增校验：必须选择商品规格
      if (!this.selectProductAttr || this.selectProductAttr.length === 0) {
        this.$message({
          message: '请先选择商品规格',
          type: 'error',
          duration: 3000
        });
        return;
      }
      // 验证SKU库存信息
      if (this.value.skuStockList && this.value.skuStockList.length > 0) {
        let valid = true;
        // 重置所有错误状态
        this.value.skuStockList.forEach(sku => {
          sku.priceError = false;
          sku.stockError = false;
        });
        // 验证每个SKU是否都填写了必要信息
        for (let i = 0; i < this.value.skuStockList.length; i++) {
          let sku = this.value.skuStockList[i];
          if (sku.price === undefined || sku.price === null || sku.price === '' || Number(sku.price) <= 0) {
            sku.priceError = true;
            valid = false;
          }
          if (sku.stock === undefined || sku.stock === null || sku.stock === '' || Number(sku.stock) <= 0) {
            sku.stockError = true;
            valid = false;
          }
        }
        if (!valid) {
          this.$message({
            message: '请先完善商品规格信息（库存和销售价格必填且大于0）',
            type: 'error',
            duration: 3000
          });
          return;
        }
      } else {
        // 没有 SKU 时也不允许通过
        this.$message({
          message: '请先完善商品规格信息',
          type: 'error',
          duration: 3000
        });
        return;
      }

      // 强制更新图片顺序，确保拖拽后的顺序被保存
      // 用临时变量存储当前的selectProductPics，触发setter
      const currentPics = [...this.selectProductPics];
      this.selectProductPics = currentPics;

      this.mergeProductAttrValue();
      this.mergeProductAttrPics();
      this.$emit('nextStep')
    },

    // === 门店库存管理相关方法 ===

    // 初始化编辑模式下的门店库存汇总显示
    async initStoreStockSummaryForEdit() {
      try {
        // 检查是否有SKU和门店库存数据
        if (this.value.skuStockList && this.value.skuStockList.length > 0 &&
          this.value.storeSkuStockList && this.value.storeSkuStockList.length > 0) {

          // 获取所有门店数据
          await this.fetchStores();

          // 准备SKU列表
          this.currentEditingSkuList = [...this.value.skuStockList];

          // 初始化门店库存数据结构
          this.initStoreStockData();

          // 加载现有门店库存数据
          await this.loadExistingStoreStock();
        }
      } catch (error) {
        console.error('初始化门店库存汇总失败:', error);
      }
    },

    // 显示门店库存分配对话框
    async handleShowStoreStockDialog() {
      try {
        // 获取所有门店
        await this.fetchStores();

        // 准备当前编辑的SKU列表
        this.currentEditingSkuList = [...(this.value.skuStockList || [])];

        // 初始化门店库存数据结构
        this.initStoreStockData();

        // 加载现有的门店库存分配
        if (this.isEdit && this.value.id) {
          await this.loadExistingStoreStock();
        }

        // 更新汇总数据
        this.updateStoreStockSummary();

        this.storeStockDialogVisible = true;
      } catch (error) {
        console.error('加载门店库存管理失败:', error);
        this.$message.error('加载门店库存管理失败');
      }
    },

    // 获取所有门店
    async fetchStores() {
      const response = await fetchAllStores();
      if (response.code === 200) {
        this.allStores = response.data || [];
      } else {
        throw new Error('获取门店列表失败');
      }
    },

    // 初始化门店库存数据结构
    initStoreStockData() {
      this.storeStockData = {};
      this.currentEditingSkuList.forEach((sku, skuIndex) => {
        this.$set(this.storeStockData, skuIndex, {});
        this.allStores.forEach(store => {
          this.$set(this.storeStockData[skuIndex], store.id, {
            stock: 0,
            lowStock: 0,
            status: 1
          });
        });
      });
    },

    // 加载现有的门店库存分配
    async loadExistingStoreStock() {
      try {
        // 优先使用商品详情中直接返回的门店库存数据
        if (this.value.storeSkuStockList && this.value.storeSkuStockList.length > 0) {
          const existingStoreStocks = this.value.storeSkuStockList;
          existingStoreStocks.forEach(storeStock => {
            // 找到对应的SKU索引
            const skuIndex = this.currentEditingSkuList.findIndex(sku => sku.id === storeStock.skuId);
            if (skuIndex !== -1 && this.storeStockData[skuIndex]) {
              this.$set(this.storeStockData[skuIndex], storeStock.storeId, {
                stock: storeStock.stock || 0,
                lowStock: storeStock.lowStock || 0,
                status: storeStock.status || 1
              });
            }
          });
          // 更新汇总数据
          this.updateStoreStockSummary();
        } else {
          // 如果商品详情中没有门店库存数据，则通过API获取
          const response = await fetchStoreSkuStockByProduct(this.value.id);
          if (response.code === 200 && response.data) {
            const existingStoreStocks = response.data;
            existingStoreStocks.forEach(storeStock => {
              // 找到对应的SKU索引
              const skuIndex = this.currentEditingSkuList.findIndex(sku => sku.id === storeStock.skuId);
              if (skuIndex !== -1 && this.storeStockData[skuIndex]) {
                this.$set(this.storeStockData[skuIndex], storeStock.storeId, {
                  stock: storeStock.stock || 0,
                  lowStock: storeStock.lowStock || 0,
                  status: storeStock.status || 1
                });
              }
            });
          }
          // 更新汇总数据
          this.updateStoreStockSummary();
        }
      } catch (error) {
        console.error('加载现有门店库存失败:', error);
      }
    },

    // 获取门店库存值
    getStoreStock(skuIndex, storeId) {
      if (this.storeStockData[skuIndex] && this.storeStockData[skuIndex][storeId]) {
        return this.storeStockData[skuIndex][storeId].stock;
      }
      return 0;
    },

    // 设置门店库存值（主要用于均分操作）
    setStoreStock(skuIndex, storeId, value) {
      if (!this.storeStockData[skuIndex]) {
        this.$set(this.storeStockData, skuIndex, {});
      }
      if (!this.storeStockData[skuIndex][storeId]) {
        this.$set(this.storeStockData[skuIndex], storeId, { stock: 0, lowStock: 0, status: 1 });
      }
      
      // 直接更新库存值，保持其他属性不变
      this.$set(this.storeStockData[skuIndex][storeId], 'stock', value || 0);
    },

    // 获取门店预警库存值
    getStoreLowStock(skuIndex, storeId) {
      if (this.storeStockData[skuIndex] && this.storeStockData[skuIndex][storeId]) {
        return this.storeStockData[skuIndex][storeId].lowStock;
      }
      return 0;
    },

    // 设置门店预警库存值（主要用于均分操作）
    setStoreLowStock(skuIndex, storeId, value) {
      if (!this.storeStockData[skuIndex]) {
        this.$set(this.storeStockData, skuIndex, {});
      }
      if (!this.storeStockData[skuIndex][storeId]) {
        this.$set(this.storeStockData[skuIndex], storeId, { stock: 0, lowStock: 0, status: 1 });
      }
      this.$set(this.storeStockData[skuIndex][storeId], 'lowStock', value || 0);
    },

    // 获取门店状态
    getStoreStatus(skuIndex, storeId) {
      if (this.storeStockData[skuIndex] && this.storeStockData[skuIndex][storeId]) {
        return this.storeStockData[skuIndex][storeId].status;
      }
      return 1;
    },

    // 设置门店状态
    setStoreStatus(skuIndex, storeId, value) {
      if (!this.storeStockData[skuIndex]) {
        this.$set(this.storeStockData, skuIndex, {});
      }
      if (!this.storeStockData[skuIndex][storeId]) {
        this.$set(this.storeStockData[skuIndex], storeId, { stock: 0, lowStock: 0, status: 1 });
      }
      this.$set(this.storeStockData[skuIndex][storeId], 'status', value);
    },

    // 处理库存变化
    handleStoreStockChange() {
      this.updateStoreStockSummary();
    },

    // 获取已分配库存总数
    getDistributedStock(skuIndex) {
      if (!this.storeStockData[skuIndex]) return 0;
      let total = 0;
      Object.values(this.storeStockData[skuIndex]).forEach(storeData => {
        total += storeData.stock || 0;
      });
      return total;
    },

    // 获取SKU标签页标题
    getSkuTabLabel(sku, index) {
      const distributedStock = this.getDistributedStock(index);
      const isComplete = distributedStock === sku.stock;
      const statusIcon = isComplete ? '✓' : '!';
      return `${statusIcon} SKU${index + 1} (${distributedStock}/${sku.stock})`;
    },

    // 获取SKU显示名称
    getSkuDisplayName(skuId) {
      if (!skuId) return '';
      const skuList = this.value.skuStockList || [];
      const sku = skuList.find(s => s.id === skuId);
      if (!sku || !sku.spData) return `SKU-${skuId}`;

      try {
        const spData = JSON.parse(sku.spData);
        if (Array.isArray(spData) && spData.length > 0) {
          return spData.map(sp => `${sp.key}:${sp.value}`).join(', ');
        }
      } catch (error) {
        console.error('解析SKU规格数据失败:', error);
      }
      return `SKU-${skuId}`;
    },

    // 单个SKU自动均分库存
    handleAutoDistributeSingleSku(skuIndex) {
      const sku = this.currentEditingSkuList[skuIndex];
      const storeCount = this.allStores.length;
      if (storeCount === 0) {
        this.$message.warning('没有可用的门店');
        return;
      }

      const stockPerStore = Math.floor(sku.stock / storeCount);
      const remainingStock = sku.stock % storeCount;

      this.allStores.forEach((store, index) => {
        let allocatedStock = stockPerStore;
        if (index < remainingStock) {
          allocatedStock += 1;
        }
        this.setStoreStock(skuIndex, store.id, allocatedStock);
        this.setStoreLowStock(skuIndex, store.id, Math.max(1, Math.floor(allocatedStock / 10)));
      });

      // 触发响应性更新
      this.updateStoreStockSummary();
      
      // 使用nextTick确保DOM更新完成后再显示消息
      this.$nextTick(() => {
        const distributedTotal = this.getDistributedStock(skuIndex);
        this.$message.success(`SKU${skuIndex + 1}库存均分完成，已分配库存：${distributedTotal}/${sku.stock}`);
      });
    },

    // 所有SKU自动均分库存
    async handleAutoDistributeAllSkuStock() {
      if (!this.value.skuStockList || this.value.skuStockList.length === 0) {
        this.$message.warning('没有SKU信息');
        return;
      }

      try {
        // 确保门店列表已加载
        if (this.allStores.length === 0) {
          await this.fetchStores();
        }

        // 初始化数据结构
        this.currentEditingSkuList = [...this.value.skuStockList];
        this.initStoreStockData();

        // 为每个SKU均分库存
        this.currentEditingSkuList.forEach((sku, skuIndex) => {
          this.handleAutoDistributeSingleSku(skuIndex);
        });

        // 更新汇总
        this.updateStoreStockSummary();

        // 自动保存
        await this.saveStoreStockToBackend();

        this.$message.success('所有SKU库存均分并保存成功');
      } catch (error) {
        console.error('自动均分库存失败:', error);
        this.$message.error('自动均分库存失败');
      }
    },

    // 保存门店库存分配
    async handleSaveStoreStock() {
      // 验证库存分配
      let allValid = true;
      for (let skuIndex = 0; skuIndex < this.currentEditingSkuList.length; skuIndex++) {
        const sku = this.currentEditingSkuList[skuIndex];
        const distributedStock = this.getDistributedStock(skuIndex);
        if (distributedStock !== sku.stock) {
          allValid = false;
          this.$message.error(`SKU${skuIndex + 1}的门店库存总和(${distributedStock})不等于SKU总库存(${sku.stock})`);
          break;
        }
      }

      if (!allValid) {
        return;
      }

      try {
        this.saveStoreStockLoading = true;
        await this.saveStoreStockToBackend();
        this.storeStockDialogVisible = false;
        this.$message.success('门店库存分配保存成功');
      } catch (error) {
        console.error('保存门店库存失败:', error);
        this.$message.error('保存门店库存失败');
      } finally {
        this.saveStoreStockLoading = false;
      }
    },

    // 保存到后端
    async saveStoreStockToBackend() {
      if (!this.value.id) {
        console.warn('商品ID为空，跳过门店库存保存');
        return;
      }

      const storeStockParams = this.currentEditingSkuList.map((sku, skuIndex) => {
        const storeAllocations = this.allStores.map(store => ({
          storeId: store.id,
          storeName: store.addressName,
          stock: this.getStoreStock(skuIndex, store.id),
          lowStock: this.getStoreLowStock(skuIndex, store.id),
          status: this.getStoreStatus(skuIndex, store.id)
        })).filter(allocation => allocation.stock > 0); // 只保存有库存的门店

        return {
          skuId: sku.id,
          skuCode: sku.skuCode,
          productId: this.value.id,
          totalStock: sku.stock,
          storeAllocations: storeAllocations
        };
      });

      const response = await batchSaveStoreSkuStockByProduct(this.value.id, storeStockParams);
      if (response.code !== 200) {
        throw new Error(response.message || '保存失败');
      }
    },

    // 更新门店库存汇总
    updateStoreStockSummary() {
      this.storeStockSummary = this.currentEditingSkuList.map((sku, skuIndex) => {
        const allocatedStock = this.getDistributedStock(skuIndex);
        const storeDetails = this.allStores.map(store => ({
          storeName: store.addressName,
          stock: this.getStoreStock(skuIndex, store.id)
        })).filter(detail => detail.stock > 0);

        return {
          skuId: sku.id,
          totalStock: sku.stock,
          allocatedStock: allocatedStock,
          storeDetails: storeDetails
        };
      });
    }
  }
}
</script>

<style scoped>
.littleMarginLeft {
  margin-left: 10px;
}

.littleMarginTop {
  margin-top: 10px;
}

.paramInput {
  width: 250px;
}

.paramInputLabel {
  display: inline-block;
  width: 100px;
  text-align: right;
  padding-right: 10px
}

.cardBg {
  background: #F8F9FC;
}

.is-error>>>.el-input__inner {
  border-color: #F56C6C;
  background-color: #fef0f0;
}

/* 门店库存管理相关样式 */
.stock-warning {
  color: #F56C6C;
  font-weight: bold;
}

.store-stock-summary {
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  padding: 10px;
  margin-top: 10px;
  background-color: #F5F7FA;
}

.dialog-footer {
  text-align: right;
}

.el-tab-pane {
  padding: 0;
}

.store-stock-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #F0F9FF;
  border-radius: 6px;
  border-left: 4px solid #409EFF;
}
</style>
