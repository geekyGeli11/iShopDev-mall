<template> 
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float:right"
          type="primary"
          @click="handleSearchList()"
          size="small">
          查询搜索
        </el-button>
        <el-button
          style="float:right;margin-right: 15px"
          @click="handleResetSearch()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input v-model="listQuery.keyword" class="input-width" placeholder="帐号/姓名" clearable></el-input>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button size="mini" class="btn-add" @click="handleAdd()" style="margin-left: 20px">添加</el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="adminTable"
                :data="list"
                style="width: 100%;"
                v-loading="listLoading" border>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="帐号" align="center">
          <template slot-scope="scope">{{scope.row.username}}</template>
        </el-table-column>
        <el-table-column label="姓名" align="center">
          <template slot-scope="scope">{{scope.row.nickName}}</template>
        </el-table-column>
        <el-table-column label="邮箱" align="center">
          <template slot-scope="scope">{{scope.row.email}}</template>
        </el-table-column>
        <el-table-column label="账户类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="!scope.row.adminType" type="danger" size="small">管理账号</el-tag>
            <el-tag v-else type="success" size="small">门店账号</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="关联门店" width="120" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.storeName">{{scope.row.storeName}}</span>
            <span v-else style="color: #999;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="关联学校" width="140" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.schoolName">{{scope.row.schoolName}}</span>
            <span v-else style="color: #999;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="添加时间" width="160" align="center">
          <template slot-scope="scope">{{scope.row.createTime | formatDateTime}}</template>
        </el-table-column>
        <el-table-column label="最后登录" width="160" align="center">
          <template slot-scope="scope">{{scope.row.loginTime | formatDateTime}}</template>
        </el-table-column>
        <el-table-column label="是否启用" width="140" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.$index, scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.status">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="微信绑定" width="120" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.wxServiceOpenid" type="success" size="small">
              <i class="el-icon-check"></i> 已绑定
            </el-tag>
            <el-tag v-else type="info" size="small">未绑定</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template slot-scope="scope">
            <el-button size="mini"
                       type="text"
                       @click="handleSelectRole(scope.$index, scope.row)">分配角色
            </el-button>
            <el-button size="mini"
                       type="text"
                       @click="handleWechatBind(scope.$index, scope.row)">
              {{ scope.row.wxServiceOpenid ? '微信' : '绑定微信' }}
            </el-button>
            <el-button size="mini"
                       type="text"
                       @click="handleUpdate(scope.$index, scope.row)">
              编辑
            </el-button>
            <el-button size="mini"
                       type="text"
                       @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[10,15,20]"
        :total="total">
      </el-pagination>
    </div>
    <el-dialog
      :title="isEdit?'编辑用户':'添加用户'"
      :visible.sync="dialogVisible"
      width="40%">
      <el-form :model="admin"
               ref="adminForm"
               label-width="150px" size="small">
        <el-form-item label="帐号：">
          <el-input v-model="admin.username" style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="姓名：">
          <el-input v-model="admin.nickName" style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="邮箱：">
          <el-input v-model="admin.email" style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="密码：">
          <el-input v-model="admin.password"  type="password" style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="备注：">
          <el-input v-model="admin.note"
                    type="textarea"
                    :rows="5"
                    style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="账户类型：">
          <el-radio-group v-model="admin.adminType" @change="handleAdminTypeChange">
            <el-radio :label="false">管理账号</el-radio>
            <el-radio :label="true">门店账号</el-radio>
          </el-radio-group>
          <div style="font-size: 12px; color: #909399; margin-top: 5px;">
            管理账号可查看所有门店数据，门店账号只能查看关联门店数据
          </div>
        </el-form-item>
        <el-form-item label="关联学校：" v-if="admin.adminType === true">
          <el-select v-model="admin.schoolId" placeholder="请选择学校" style="width: 250px" clearable @change="handleSchoolChange">
            <el-option
              v-for="item in schoolList"
              :key="item.id"
              :label="item.schoolName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关联门店：" v-if="admin.adminType === true">
          <el-select v-model="admin.storeId" placeholder="请选择门店" style="width: 250px" clearable @change="handleStoreChange">
            <el-option
              v-for="item in filteredStoreList"
              :key="item.id"
              :label="item.addressName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用：">
          <el-radio-group v-model="admin.status">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="分配角色"
      :visible.sync="allocDialogVisible"
      width="30%">
      <el-select v-model="allocRoleIds" multiple placeholder="请选择" size="small" style="width: 80%">
        <el-option
          v-for="item in allRoleList"
          :key="item.id"
          :label="item.name"
          :value="item.id">
        </el-option>
      </el-select>
      <span slot="footer" class="dialog-footer">
        <el-button @click="allocDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleAllocDialogConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 微信绑定对话框 -->
    <el-dialog
      title="微信绑定"
      :visible.sync="wechatDialogVisible"
      width="400px"
      @close="handleWechatDialogClose">
      <div v-if="wechatBindingStatus.bound" class="wechat-bound-info">
        <div class="wechat-avatar">
          <img v-if="wechatBindingStatus.headImgUrl" :src="wechatBindingStatus.headImgUrl" alt="微信头像">
          <i v-else class="el-icon-user" style="font-size: 60px; color: #909399;"></i>
        </div>
        <div class="wechat-nickname">{{ wechatBindingStatus.nickname || '微信用户' }}</div>
        <div class="wechat-bindtime">绑定时间：{{ wechatBindingStatus.bindTime | formatDateTime }}</div>
        <el-button type="danger" size="small" @click="handleUnbindWechat" style="margin-top: 20px;">解除绑定</el-button>
      </div>
      <div v-else class="wechat-qrcode-container">
        <div v-if="qrcodeLoading" class="qrcode-loading">
          <i class="el-icon-loading"></i>
          <p>正在生成二维码...</p>
        </div>
        <div v-else-if="qrcodeUrl" class="qrcode-wrapper">
          <img :src="qrcodeUrl" alt="微信绑定二维码" class="qrcode-img">
          <p class="qrcode-tip">请使用微信扫描二维码关注公众号完成绑定</p>
          <p class="qrcode-tip-sub">二维码有效期5分钟</p>
          <el-button type="text" @click="refreshQRCode">刷新二维码</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
  import {fetchList,createAdmin,updateAdmin,updateStatus,deleteAdmin,getRoleByAdmin,allocRole,getWechatBindingStatus,generateWechatQRCode,unbindWechat} from '@/api/login';
  import {fetchAllRoleList} from '@/api/role';
  import {formatDate} from '@/utils/date';
  import {fetchEnabledSchools} from '@/api/school';
  import {getAllStores} from '@/api/storeAddress';

  const defaultListQuery = {
    pageNum: 1,
    pageSize: 10,
    keyword: null
  };
  const defaultAdmin = {
    id: null,
    username: null,
    password: null,
    nickName: null,
    email: null,
    note: null,
    status: 1,
    adminType: false,
    storeId: null,
    storeName: null,
    schoolId: null,
    schoolName: null
  };
  export default {
    name: 'adminList',
    data() {
      return {
        listQuery: Object.assign({}, defaultListQuery),
        list: null,
        total: null,
        listLoading: false,
        dialogVisible: false,
        admin: Object.assign({}, defaultAdmin),
        isEdit: false,
        allocDialogVisible: false,
        allocRoleIds:[],
        allRoleList:[],
        allocAdminId:null,
        // 学校和门店列表
        schoolList: [],
        storeList: [],
        // 微信绑定相关
        wechatDialogVisible: false,
        wechatAdminId: null,
        wechatBindingStatus: {
          bound: false,
          nickname: null,
          headImgUrl: null,
          bindTime: null
        },
        qrcodeUrl: null,
        qrcodeLoading: false,
        bindingCheckTimer: null
      }
    },
    created() {
      this.getList();
      this.getAllRoleList();
      this.getSchoolList();
      this.getStoreList();
    },
    computed: {
      // 根据选择的学校过滤门店列表
      filteredStoreList() {
        if (!this.admin.schoolId) {
          return this.storeList;
        }
        return this.storeList.filter(store => store.schoolId === this.admin.schoolId);
      }
    },
    filters: {
      formatDateTime(time) {
        if (time == null || time === '') {
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
      }
    },
    methods: {
      handleResetSearch() {
        this.listQuery = Object.assign({}, defaultListQuery);
      },
      handleSearchList() {
        this.listQuery.pageNum = 1;
        this.getList();
      },
      handleSizeChange(val) {
        this.listQuery.pageNum = 1;
        this.listQuery.pageSize = val;
        this.getList();
      },
      handleCurrentChange(val) {
        this.listQuery.pageNum = val;
        this.getList();
      },
      handleAdd() {
        this.dialogVisible = true;
        this.isEdit = false;
        this.admin = Object.assign({},defaultAdmin);
      },
      handleStatusChange(index, row) {
        this.$confirm('是否要修改该状态?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          updateStatus(row.id, {status: row.status}).then(response => {
            this.$message({
              type: 'success',
              message: '修改成功!'
            });
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '取消修改'
          });
          this.getList();
        });
      },
      handleDelete(index, row) {
        this.$confirm('是否要删除该用户?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteAdmin(row.id).then(response => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getList();
          });
        });
      },
      handleUpdate(index, row) {
        this.dialogVisible = true;
        this.isEdit = true;
        this.admin = Object.assign({},row);
      },
      handleDialogConfirm() {
        this.$confirm('是否要确认?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          if (this.isEdit) {
            updateAdmin(this.admin.id,this.admin).then(response => {
              this.$message({
                message: '修改成功！',
                type: 'success'
              });
              this.dialogVisible =false;
              this.getList();
            })
          } else {
            createAdmin(this.admin).then(response => {
              this.$message({
                message: '添加成功！',
                type: 'success'
              });
              this.dialogVisible =false;
              this.getList();
            })
          }
        })
      },
      handleAllocDialogConfirm(){
        this.$confirm('是否要确认?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let params = new URLSearchParams();
          params.append("adminId", this.allocAdminId);
          params.append("roleIds", this.allocRoleIds);
          allocRole(params).then(response => {
            this.$message({
              message: '分配成功！',
              type: 'success'
            });
            this.allocDialogVisible = false;
          })
        })
      },
      handleSelectRole(index,row){
        this.allocAdminId = row.id;
        this.allocDialogVisible = true;
        this.getRoleListByAdmin(row.id);
      },
      getList() {
        this.listLoading = true;
        fetchList(this.listQuery).then(response => {
          this.listLoading = false;
          this.list = response.data.list;
          this.total = response.data.total;
        });
      },
      getAllRoleList() {
        fetchAllRoleList().then(response => {
          this.allRoleList = response.data;
        });
      },
      getRoleListByAdmin(adminId) {
        getRoleByAdmin(adminId).then(response => {
          let allocRoleList = response.data;
          this.allocRoleIds=[];
          if(allocRoleList!=null&&allocRoleList.length>0){
            for(let i=0;i<allocRoleList.length;i++){
              this.allocRoleIds.push(allocRoleList[i].id);
            }
          }
        });
      },
      getSchoolList() {
        fetchEnabledSchools().then(response => {
          this.schoolList = response.data || [];
        });
      },
      getStoreList() {
        getAllStores().then(response => {
          this.storeList = response.data || [];
        });
      },
      handleAdminTypeChange(val) {
        if (val === false) {
          // 管理账号清空门店和学校关联
          this.admin.storeId = null;
          this.admin.storeName = null;
          this.admin.schoolId = null;
          this.admin.schoolName = null;
        }
      },
      handleSchoolChange(val) {
        // 学校变更时，清空门店选择并更新学校名称
        this.admin.storeId = null;
        this.admin.storeName = null;
        if (val) {
          const school = this.schoolList.find(s => s.id === val);
          this.admin.schoolName = school ? school.schoolName : null;
        } else {
          this.admin.schoolName = null;
        }
      },
      handleStoreChange(val) {
        // 更新门店名称
        if (val) {
          const store = this.storeList.find(s => s.id === val);
          this.admin.storeName = store ? store.addressName : null;
        } else {
          this.admin.storeName = null;
        }
      },
      // 微信绑定相关方法
      handleWechatBind(index, row) {
        this.wechatAdminId = row.id;
        this.wechatDialogVisible = true;
        this.loadWechatBindingStatus();
      },
      loadWechatBindingStatus() {
        getWechatBindingStatus(this.wechatAdminId).then(response => {
          this.wechatBindingStatus = response.data;
          if (!this.wechatBindingStatus.bound) {
            this.generateQRCode();
          }
        });
      },
      generateQRCode() {
        this.qrcodeLoading = true;
        generateWechatQRCode(this.wechatAdminId).then(response => {
          this.qrcodeUrl = response.data;
          this.qrcodeLoading = false;
          // 开始轮询检查绑定状态
          this.startBindingCheck();
        }).catch(() => {
          this.qrcodeLoading = false;
          this.$message.error('生成二维码失败');
        });
      },
      refreshQRCode() {
        this.stopBindingCheck();
        this.generateQRCode();
      },
      startBindingCheck() {
        this.stopBindingCheck();
        this.bindingCheckTimer = setInterval(() => {
          getWechatBindingStatus(this.wechatAdminId).then(response => {
            if (response.data.bound) {
              this.wechatBindingStatus = response.data;
              this.stopBindingCheck();
              this.$message.success('微信绑定成功！');
              this.getList();
            }
          });
        }, 3000);
      },
      stopBindingCheck() {
        if (this.bindingCheckTimer) {
          clearInterval(this.bindingCheckTimer);
          this.bindingCheckTimer = null;
        }
      },
      handleWechatDialogClose() {
        this.stopBindingCheck();
        this.qrcodeUrl = null;
        this.wechatBindingStatus = {
          bound: false,
          nickname: null,
          headImgUrl: null,
          bindTime: null
        };
      },
      handleUnbindWechat() {
        this.$confirm('确定要解除微信绑定吗？解除后将无法通过公众号接收系统通知。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          unbindWechat(this.wechatAdminId).then(() => {
            this.$message.success('解除绑定成功');
            this.wechatDialogVisible = false;
            this.getList();
          });
        });
      }
    }
  }
</script>
<style scoped>
.wechat-bound-info {
  text-align: center;
  padding: 20px;
}
.wechat-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto 15px;
  border-radius: 50%;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}
.wechat-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.wechat-nickname {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}
.wechat-bindtime {
  font-size: 12px;
  color: #999;
}
.wechat-qrcode-container {
  text-align: center;
  padding: 20px;
  min-height: 300px;
}
.qrcode-loading {
  padding: 80px 0;
  color: #909399;
}
.qrcode-loading i {
  font-size: 40px;
}
.qrcode-loading p {
  margin-top: 15px;
}
.qrcode-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.qrcode-img {
  width: 200px;
  height: 200px;
  border: 1px solid #eee;
}
.qrcode-tip {
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}
.qrcode-tip-sub {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}
</style>


