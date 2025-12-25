<template>
  <div class="app-container">
    <!-- 操作按钮 -->
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-location"></i>
      <span>门店地址列表</span>
      <el-button
        class="btn-add"
        size="mini"
        style="margin-left: 20px"
        @click="handleAddStore"
      >
        添加门店
      </el-button>
    </el-card>

    <!-- 搜索栏 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.addressName"
        placeholder="门店名称"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleSearchList"
      />
      <el-input
        v-model="listQuery.phone"
        placeholder="联系电话"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleSearchList"
      />
      <el-select
        v-model="listQuery.schoolId"
        placeholder="所属学校"
        clearable
        style="width: 200px"
        class="filter-item"
      >
        <el-option
          v-for="school in schoolList"
          :key="school.id"
          :label="school.schoolName"
          :value="school.id">
        </el-option>
      </el-select>
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleSearchList"
      >
        搜索
      </el-button>
      <el-button
        class="filter-item"
        type="default"
        icon="el-icon-refresh"
        @click="handleResetSearch"
      >
        重置
      </el-button>
    </div>

    <!-- 数据列表 -->
    <div class="table-container">
      <el-table ref="storeTable" 
                :data="list" 
                style="width: 100%;" 
                v-loading="listLoading" 
                border>
        <el-table-column label="ID" width="80" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="门店名称" align="center">
          <template slot-scope="scope">{{scope.row.addressName}}</template>
        </el-table-column>
        <el-table-column label="联系人" width="120" align="center">
          <template slot-scope="scope">{{scope.row.name}}</template>
        </el-table-column>
        <el-table-column label="电话号码" width="150" align="center">
          <template slot-scope="scope">{{scope.row.phone}}</template>
        </el-table-column>
        <el-table-column label="省市区" width="200" align="center">
          <template slot-scope="scope">
            {{scope.row.province + scope.row.city + scope.row.region}}
          </template>
        </el-table-column>
        <el-table-column label="详细地址" align="center">
          <template slot-scope="scope">{{scope.row.detailAddress}}</template>
        </el-table-column>
        <el-table-column label="营业时间" width="180" align="center">
          <template slot-scope="scope">{{scope.row.businessHours || '09:00-20:00'}}</template>
        </el-table-column>
        <el-table-column label="门店Logo" width="120" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row.logo" :src="scope.row.logo" alt="门店Logo" style="width: 60px; height: 60px; object-fit: cover;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="门店相册" width="150" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.storeGallery && scope.row.storeGallery.length > 0" class="gallery-preview">
              <img v-for="(pic, index) in getGalleryPics(scope.row.storeGallery).slice(0, 3)"
                   :key="index"
                   :src="pic"
                   alt="门店相册"
                   style="width: 30px; height: 30px; object-fit: cover; margin-right: 5px; border-radius: 4px;" />
              <span v-if="getGalleryPics(scope.row.storeGallery).length > 3" class="more-pics">+{{getGalleryPics(scope.row.storeGallery).length - 3}}</span>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="位置坐标" width="180" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.longitude && scope.row.latitude">
              {{scope.row.longitude}},{{scope.row.latitude}}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="所属学校" width="150" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.schoolId">
              {{ getSchoolName(scope.row.schoolId) }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="是否默认" width="120" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.row)"
              v-model="scope.row.isDefault">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleUpdate(scope.row)">
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next, jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[5,10,15]"
        :total="total">
      </el-pagination>
    </div>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="isEdit?'编辑门店':'添加门店'"
      :visible.sync="dialogVisible"
      width="40%">
      <el-form :model="storeAddress"
               ref="storeAddressForm"
               :rules="rules"
               label-width="100px">
        <el-form-item label="门店名称" prop="addressName">
          <el-input v-model="storeAddress.addressName" placeholder="请输入门店名称"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="name">
          <el-input v-model="storeAddress.name" placeholder="请输入联系人"></el-input>
        </el-form-item>
        <el-form-item label="电话号码" prop="phone">
          <el-input v-model="storeAddress.phone" placeholder="请输入电话号码"></el-input>
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="storeAddress.province" placeholder="请输入省份"></el-input>
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="storeAddress.city" placeholder="请输入城市"></el-input>
        </el-form-item>
        <el-form-item label="区域" prop="region">
          <el-input v-model="storeAddress.region" placeholder="请输入区域"></el-input>
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="storeAddress.detailAddress" placeholder="请输入详细地址"></el-input>
        </el-form-item>
        <el-form-item label="营业时间" prop="businessHours">
          <el-input v-model="storeAddress.businessHours" placeholder="例如：09:00-20:00"></el-input>
        </el-form-item>
        <el-form-item label="门店Logo" prop="logo">
          <single-upload v-model="storeAddress.logo"></single-upload>
        </el-form-item>
        <el-form-item label="门店相册" prop="storeGallery">
          <multi-upload v-model="storeGalleryPics" :max-count="10"></multi-upload>
        </el-form-item>
        <el-form-item label="所属学校" prop="schoolId">
          <el-row :gutter="10">
            <el-col :span="20">
              <el-select
                v-model="storeAddress.schoolId"
                placeholder="请选择学校"
                clearable
                style="width: 100%">
                <el-option
                  v-for="school in schoolList"
                  :key="school.id"
                  :label="school.schoolName"
                  :value="school.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4">
              <el-button
                type="primary"
                size="small"
                @click="handleQuickAddSchool"
                style="width: 100%">
                快速添加
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="地址定位" prop="location">
          <el-input 
            v-model="locationSearchText" 
            placeholder="请输入地址进行搜索定位"
            @keyup.enter.native="searchLocation">
            <el-button slot="append" icon="el-icon-search" @click="searchLocation"></el-button>
          </el-input>
          <div style="margin-top: 10px;">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="经度" prop="longitude" style="margin-bottom: 0;">
                  <el-input-number 
                    v-model="storeAddress.longitude" 
                    :precision="6" 
                    :step="0.000001"
                    :min="-180" 
                    :max="180"
                    placeholder="请输入经度"
                    style="width: 100%;">
                  </el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="纬度" prop="latitude" style="margin-bottom: 0;">
                  <el-input-number 
                    v-model="storeAddress.latitude" 
                    :precision="6" 
                    :step="0.000001"
                    :min="-90" 
                    :max="90"
                    placeholder="请输入纬度"
                    style="width: 100%;">
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div v-if="storeAddress.longitude && storeAddress.latitude" style="margin-top: 10px; color: #666; font-size: 12px;">
            当前坐标：经度 {{storeAddress.longitude}}, 纬度 {{storeAddress.latitude}}
            <el-button type="text" size="mini" @click="getCurrentLocation">获取当前位置</el-button>
          </div>
        </el-form-item>
        <el-form-item label="是否默认" prop="isDefault">
          <el-switch
            v-model="storeAddress.isDefault">
          </el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 快速添加学校对话框 -->
    <el-dialog
      title="快速添加学校"
      :visible.sync="schoolDialogVisible"
      width="30%"
    >
      <el-form
        ref="schoolForm"
        :model="newSchool"
        :rules="schoolRules"
        label-width="100px"
        size="small"
      >
        <el-form-item label="学校名称" prop="schoolName">
          <el-input v-model="newSchool.schoolName" placeholder="请输入学校名称"/>
        </el-form-item>
        <el-form-item label="学校校徽" prop="schoolLogo">
          <single-upload v-model="newSchool.schoolLogo"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="newSchool.status">
            <el-radio :label="true">启用</el-radio>
            <el-radio :label="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="schoolDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleSchoolDialogConfirm" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchList,
  getVisibleStores,
  createStoreAddress,
  updateStoreAddress,
  deleteStoreAddress
} from '@/api/storeAddress';
import { fetchEnabledSchools } from '@/api/school';
import SingleUpload from '@/components/Upload/singleUpload';
import MultiUpload from '@/components/Upload/multiUpload';

const defaultStoreAddress = {
  id: null,
  addressName: '',
  name: '',
  phone: '',
  province: '',
  city: '',
  region: '',
  detailAddress: '',
  businessHours: '09:00-20:00',
  isDefault: false,
  logo: '',
  storeGallery: '',
  longitude: undefined,
  latitude: undefined,
  schoolId: null
};

export default {
  name: 'storeAddress',
  components: {
    SingleUpload,
    MultiUpload
  },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        addressName: null,
        phone: null,
        schoolId: null
      },
      storeAddress: Object.assign({}, defaultStoreAddress),
      dialogVisible: false,
      isEdit: false,
      locationSearchText: '',
      schoolList: [], // 学校列表
      schoolDialogVisible: false, // 快速添加学校对话框
      newSchool: { // 新学校数据
        schoolName: '',
        schoolLogo: '',
        status: true
      },
      schoolRules: { // 学校表单验证规则
        schoolName: [
          { required: true, message: '请输入学校名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ]
      },
      rules: {
        addressName: [
          { required: true, message: '请输入门店名称', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入联系人', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入电话号码', trigger: 'blur' }
        ],
        province: [
          { required: true, message: '请输入省份', trigger: 'blur' }
        ],
        city: [
          { required: true, message: '请输入城市', trigger: 'blur' }
        ],
        region: [
          { required: true, message: '请输入区域', trigger: 'blur' }
        ],
        detailAddress: [
          { required: true, message: '请输入详细地址', trigger: 'blur' }
        ],
        longitude: [
          { type: 'number', message: '经度必须是数字', trigger: 'blur' }
        ],
        latitude: [
          { type: 'number', message: '纬度必须是数字', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    // 门店相册图片数组，用于多文件上传组件
    storeGalleryPics: {
      get: function () {
        if (this.storeAddress.storeGallery == null || this.storeAddress.storeGallery === '') {
          return [];
        }
        return this.storeAddress.storeGallery.split(',').filter(pic => pic.trim() !== '');
      },
      set: function (newValue) {
        if (newValue == null || newValue.length === 0) {
          this.storeAddress.storeGallery = '';
        } else {
          this.storeAddress.storeGallery = newValue.join(',');
        }
      }
    }
  },
  created() {
    this.getList();
    this.getSchoolList();
  },
  methods: {
    // 获取门店相册图片数组（用于表格显示）
    getGalleryPics(storeGallery) {
      if (!storeGallery || storeGallery === '') {
        return [];
      }
      return storeGallery.split(',').filter(pic => pic.trim() !== '');
    },
    // 获取数据列表（不包含地库）
    getList() {
      this.listLoading = true;
      getVisibleStores().then(response => {
        this.listLoading = false;
        // 确保将后端返回的isDefault转换为布尔类型
        if (response.data && response.data.length > 0) {
          response.data.forEach(item => {
            // 强制转换为布尔类型
            item.isDefault = Boolean(item.isDefault);
          });
        }
        this.list = response.data;
        this.total = response.data.length;
      }).catch(() => {
        this.listLoading = false;
      });
    },
    // 搜索地址定位
    searchLocation() {
      if (!this.locationSearchText.trim()) {
        this.$message.warning('请输入地址信息');
        return;
      }
      
      this.$message.info('地址搜索功能需要配置地图API密钥，请手动输入经纬度坐标');
      // 如果需要启用地图API搜索，请配置相应的API密钥
      // this.getCoordinatesByAddress(this.locationSearchText);
    },
    
    // 获取当前位置
    getCurrentLocation() {
      if (!navigator.geolocation) {
        this.$message.error('您的浏览器不支持地理位置功能');
        return;
      }
      
      this.$message.info('正在获取当前位置...');
      
             navigator.geolocation.getCurrentPosition(
         (position) => {
           this.storeAddress.longitude = parseFloat(position.coords.longitude.toFixed(6));
           this.storeAddress.latitude = parseFloat(position.coords.latitude.toFixed(6));
           this.$message.success('当前位置获取成功');
         },
        (error) => {
          let errorMessage = '获取位置失败';
          switch (error.code) {
            case error.PERMISSION_DENIED:
              errorMessage = '用户拒绝了地理位置请求';
              break;
            case error.POSITION_UNAVAILABLE:
              errorMessage = '位置信息不可用';
              break;
            case error.TIMEOUT:
              errorMessage = '获取位置超时';
              break;
          }
          this.$message.error(errorMessage);
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0
        }
      );
    },
    
    // 通过地址获取坐标（预留接口，需要配置API密钥）
    getCoordinatesByAddress(address) {
      // 这里可以集成地图API进行地址解析
      // 例如：腾讯地图API、高德地图API等
      this.$message.info('地址解析功能需要配置地图API服务');
    },
    
    // 处理分页大小改变
    handleSizeChange(val) {
      this.listQuery.pageNum = 1;
      this.listQuery.pageSize = val;
      this.getList();
    },
    // 处理页码改变
    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
    },
    // 添加门店
    handleAddStore() {
      this.dialogVisible = true;
      this.isEdit = false;
      this.storeAddress = Object.assign({}, defaultStoreAddress);
      this.locationSearchText = '';
    },
    // 编辑门店
    handleUpdate(row) {
      this.dialogVisible = true;
      this.isEdit = true;
      const storeData = Object.assign({}, row);
      // 确保isDefault是布尔类型
      storeData.isDefault = Boolean(storeData.isDefault);
      this.storeAddress = storeData;
      
      // 设置地址搜索文本
      this.locationSearchText = `${storeData.province}${storeData.city}${storeData.region}${storeData.detailAddress}`;
    },
    // 删除门店
    handleDelete(row) {
      this.$confirm('是否要删除该门店?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteStoreAddress(row.id).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getList();
        });
      });
    },
    // 改变默认状态
    handleStatusChange(row) {
      // 当前row的isDefault已经被修改为新的值
      
      // 如果设置为默认(true)，需要将其他门店设为非默认
      if (row.isDefault === true) {
        // 将所有其他门店设为非默认
        this.list.forEach(item => {
          if (item.id !== row.id && item.isDefault === true) {
            item.isDefault = false;
            // 更新其他门店的状态到数据库
            updateStoreAddress(item.id, { id: item.id, isDefault: false })
              .catch(() => {
                this.$message({
                  message: `更新${item.addressName}的默认状态失败`,
                  type: 'error'
                });
              });
          }
        });
      } else if (row.isDefault === false) {
        // 如果当前门店取消了默认状态，可能需要确保至少有一个默认门店
        // 检查是否还有其他默认门店
        const hasOtherDefault = this.list.some(item => 
          item.id !== row.id && item.isDefault === true
        );
        
        // 如果没有其他默认门店且列表中有门店，可以选择第一个作为默认
        if (!hasOtherDefault && this.list.length > 1) {
          // 可选：自动选择另一个门店作为默认
          // 或者在这里提示用户必须有一个默认门店
          this.$confirm('系统需要至少一个默认门店，是否选择另一个门店作为默认?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            // 用户确认，将当前门店重新设为默认
            row.isDefault = true;
            this.updateStoreStatus(row);
          }).catch(() => {
            // 用户取消，选择其他门店作为默认（例如第一个）
            for (let i = 0; i < this.list.length; i++) {
              if (this.list[i].id !== row.id) {
                this.list[i].isDefault = true;
                this.updateStoreStatus(this.list[i]);
                break;
              }
            }
          });
          return; // 提前返回，等待用户选择
        }
      }
      
      // 更新当前门店状态到数据库
      this.updateStoreStatus(row);
    },
    
    // 更新门店状态到数据库
    updateStoreStatus(row) {
      const data = {
        id: row.id,
        isDefault: row.isDefault
      };
      updateStoreAddress(row.id, data).then(response => {
        this.$message({
          message: '修改成功',
          type: 'success'
        });
      }).catch(() => {
        // 更新失败时恢复原来的状态
        row.isDefault = !row.isDefault;
        this.$message({
          message: '修改失败',
          type: 'error'
        });
      });
    },
    // 确认对话框
    handleDialogConfirm() {
      this.$refs.storeAddressForm.validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            updateStoreAddress(this.storeAddress.id, this.storeAddress).then(response => {
              this.$message({
                message: '修改成功',
                type: 'success'
              });
              this.dialogVisible = false;
              this.getList();
            });
          } else {
            createStoreAddress(this.storeAddress).then(response => {
              this.$message({
                message: '添加成功',
                type: 'success'
              });
              this.dialogVisible = false;
              this.getList();
            });
          }
        } else {
          return false;
        }
      });
    },

    // 获取学校列表
    getSchoolList() {
      fetchEnabledSchools().then(response => {
        this.schoolList = response.data;
      }).catch(() => {
        this.$message.error('获取学校列表失败');
      });
    },

    // 快速添加学校
    handleQuickAddSchool() {
      this.schoolDialogVisible = true;
      this.newSchool = {
        schoolName: '',
        schoolLogo: '',
        status: true
      };
      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs['schoolForm']) {
          this.$refs['schoolForm'].clearValidate();
        }
      });
    },

    // 确认添加学校
    handleSchoolDialogConfirm() {
      if (!this.newSchool.schoolName.trim()) {
        this.$message.warning('请输入学校名称');
        return;
      }

      // 调用创建学校的API
      this.$refs['schoolForm'].validate((valid) => {
        if (valid) {
          // 导入学校API
          import('@/api/school').then(schoolApi => {
            schoolApi.createSchool(this.newSchool).then(response => {
              const newSchoolItem = response.data;

              // 添加到本地学校列表
              this.schoolList.push(newSchoolItem);

              // 设置门店的学校ID为真实的数据库ID
              this.storeAddress.schoolId = newSchoolItem.id;

              this.schoolDialogVisible = false;
              this.$message.success('学校添加成功');

              // 重新获取学校列表以确保数据同步
              this.getSchoolList();
            }).catch(error => {
              console.error('创建学校失败:', error);
              this.$message.error('创建学校失败，请重试');
            });
          });
        }
      });
    },

    // 根据学校ID获取学校名称
    getSchoolName(schoolId) {
      const school = this.schoolList.find(item => item.id === schoolId);
      return school ? school.schoolName : '未知学校';
    },

    // 搜索
    handleSearchList() {
      this.listQuery.pageNum = 1;
      this.getList();
    },

    // 重置搜索
    handleResetSearch() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        addressName: null,
        phone: null,
        schoolId: null
      };
      this.getList();
    }
  }
}
</script>

<style scoped>
.operate-container {
  margin-bottom: 20px;
}

.operate-container .btn-add {
  float: right;
}

.filter-container {
  padding: 20px;
  background: #fff;
  margin-bottom: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,.12), 0 0 6px rgba(0,0,0,.04);
}

.filter-item {
  display: inline-block;
  vertical-align: middle;
  margin-bottom: 10px;
  margin-right: 10px;
}

.table-container {
  margin-top: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}
</style>