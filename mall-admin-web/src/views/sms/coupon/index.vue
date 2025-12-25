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
          <el-form-item label="优惠券名称：">
            <el-input v-model="listQuery.name" class="input-width" placeholder="优惠券名称"></el-input>
          </el-form-item>
          <el-form-item label="优惠券类型：">
            <el-select v-model="listQuery.type" placeholder="全部" clearable class="input-width">
              <el-option v-for="item in typeOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关联学校：">
            <el-select v-model="listQuery.schoolId" placeholder="请选择学校" clearable class="input-width">
              <el-option label="全平台通用" :value="null"></el-option>
              <el-option
                v-for="school in schoolOptions"
                :key="school.id"
                :label="school.schoolName"
                :value="school.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button size="mini" class="btn-add" @click="handleAdd()">添加</el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="couponTable"
                :data="list"
                style="width: 100%;"
                @selection-change="handleSelectionChange"
                v-loading="listLoading" border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="优惠劵名称" align="center">
          <template slot-scope="scope">{{scope.row.name}}</template>
        </el-table-column>
        <el-table-column label="优惠券类型" width="100" align="center">
          <template slot-scope="scope">{{scope.row.type | formatType}}</template>
        </el-table-column>
        <el-table-column label="可使用商品" width="100" align="center">
          <template slot-scope="scope">{{scope.row.useType | formatUseType}}</template>
        </el-table-column>
        <el-table-column label="使用门槛" width="140" align="center">
          <template slot-scope="scope">满{{scope.row.minPoint}}元可用</template>
        </el-table-column>
        <el-table-column label="优惠方式" width="120" align="center">
          <template slot-scope="scope">{{scope.row.couponType | formatCouponType}}</template>
        </el-table-column>
        <el-table-column label="优惠信息" width="120" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.couponType === 1">{{(scope.row.discountRate * 10).toFixed(1)}}折</span>
            <span v-else>{{scope.row.amount}}元</span>
          </template>
        </el-table-column>
        <el-table-column label="适用平台" width="100" align="center">
          <template slot-scope="scope">{{scope.row.platform | formatPlatform}}</template>
        </el-table-column>
        <el-table-column label="有效期" width="180" align="center">
          <template slot-scope="scope">{{scope.row.startTime|formatDate}}至{{scope.row.endTime|formatDate}}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">{{scope.row.endTime | formatStatus}}</template>
        </el-table-column>
        <el-table-column label="关联学校" width="150" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.schoolNames">{{scope.row.schoolNames}}</span>
            <span v-else>全平台通用</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template slot-scope="scope">
            <el-button size="mini"
                       type="text"
                       @click="handleView(scope.$index, scope.row)">查看</el-button>
            <el-button size="mini"
                       type="text"
                       @click="handleUpdate(scope.$index, scope.row)">
              编辑</el-button>
            <el-button size="mini"
                       type="text"
                       @click="handleShare(scope.$index, scope.row)">分享</el-button>
            <el-button size="mini"
                       type="text"
                       @click="handleDelete(scope.$index, scope.row)">删除</el-button>
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
        :page-sizes="[5,10,15]"
        :total="total">
      </el-pagination>
    </div>

    <!-- 分享弹窗 -->
    <el-dialog
      title="优惠券分享"
      :visible.sync="shareDialogVisible"
      width="600px"
      @close="handleShareDialogClose">
      <div v-if="shareInfo">
        <el-row :gutter="20">
          <el-col :span="24">
            <h4>{{ shareInfo.couponName }}</h4>
            <p>{{ shareInfo.shareDescription }}</p>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <div class="share-item">
              <h5>分享链接</h5>
              <el-input
                v-model="shareInfo.shareLink"
                readonly
                type="textarea"
                :rows="3"
                placeholder="分享链接">
                <template slot="append">
                  <el-button @click="copyToClipboard(shareInfo.shareLink)">复制</el-button>
                </template>
              </el-input>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="share-item">
              <h5>小程序码</h5>
              <div class="qrcode-container">
                <img
                  v-if="shareInfo.miniProgramCodeBase64"
                  :src="shareInfo.miniProgramCodeBase64"
                  alt="小程序码"
                  style="width: 150px; height: 150px;" />
                <div v-else class="qrcode-placeholder">
                  <i class="el-icon-picture"></i>
                  <p>{{ shareInfo.miniProgramCodeUrl || '小程序码生成失败' }}</p>
                </div>
                <div style="margin-top: 10px;">
                  <el-button
                    size="small"
                    @click="downloadQRCode"
                    :disabled="!shareInfo.miniProgramCodeBase64">
                    下载小程序码
                  </el-button>
                  <el-button
                    size="small"
                    @click="copyQRCodeBase64"
                    :disabled="!shareInfo.miniProgramCodeBase64">
                    复制图片
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div v-if="shareLoading" style="text-align: center; padding: 40px;">
        <i class="el-icon-loading"></i>
        <p>正在生成分享信息...</p>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="shareDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import {fetchList,deleteCoupon,generateShareInfo} from '@/api/coupon';
  import {fetchEnabledSchools} from '@/api/school';
  import {formatDate} from '@/utils/date';
  const defaultListQuery = {
    pageNum: 1,
    pageSize: 10,
    name: null,
    type: null,
    schoolId: null
  };
  const defaultTypeOptions=[
    {
      label: '全场赠券',
      value: 0
    },
    {
      label: '营销赠券',
      value: 1
    }
  ];
  export default {
    name:'couponList',
    data() {
      return {
        listQuery:Object.assign({},defaultListQuery),
        typeOptions:Object.assign({},defaultTypeOptions),
        list:null,
        total:null,
        listLoading:false,
        multipleSelection:[],
        shareDialogVisible: false,
        shareInfo: null,
        shareLoading: false,
        schoolOptions: []
      }
    },
    created(){
      this.getList();
      this.getSchoolOptions();
    },
    filters:{
      formatType(type){
        for(let i=0;i<defaultTypeOptions.length;i++){
          if(type===defaultTypeOptions[i].value){
            return defaultTypeOptions[i].label;
          }
        }
        return '';
      },
      formatUseType(useType){
        if(useType===0){
          return '全场通用';
        }else if(useType===1){
          return '指定分类';
        }else{
          return '指定商品';
        }
      },
      formatPlatform(platform){
        if(platform===1){
          return '移动平台';
        }else if(platform===2){
          return 'PC平台';
        }else{
          return '全平台';
        }
      },
      formatDate(time){
        if(time==null||time===''){
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd')
      },
      formatStatus(endTime){
        let now = new Date().getTime();
        let endDate = new Date(endTime);
        if(endDate>now){
          return '未过期'
        }else{
          return '已过期';
        }
      },
      formatCouponType(couponType){
        if(couponType === 1){
          return '打折券';
        }else{
          return '满减券';
        }
      }
    },
    methods:{
      getSchoolOptions() {
        fetchEnabledSchools().then(response => {
          this.schoolOptions = response.data;
        }).catch(error => {
          console.error('获取学校列表失败:', error);
        });
      },
      handleResetSearch() {
        this.listQuery = Object.assign({}, defaultListQuery);
      },
      handleSearchList() {
        this.listQuery.pageNum = 1;
        this.getList();
      },
      handleSelectionChange(val){
        this.multipleSelection = val;
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
      handleAdd(){
        this.$router.push({path: '/sms/addCoupon'})
      },
      handleView(index, row) {
        this.$router.push({path: '/sms/couponHistory', query: {id: row.id}})
      },
      handleUpdate(index, row) {
        this.$router.push({path: '/sms/updateCoupon', query: {id: row.id}})
      },
      handleShare(index, row) {
        this.shareDialogVisible = true;
        this.shareLoading = true;
        this.shareInfo = null;

        generateShareInfo(row.id).then(response => {
          this.shareLoading = false;
          if (response.code === 200) {
            this.shareInfo = response.data;
            // 调试信息：检查base64数据是否正确接收
            if (this.shareInfo.miniProgramCodeBase64) {
              console.log('小程序码base64数据长度:', this.shareInfo.miniProgramCodeBase64.length);
              console.log('小程序码base64数据前缀:', this.shareInfo.miniProgramCodeBase64.substring(0, 50));
            } else {
              console.log('未接收到小程序码base64数据');
            }
          } else {
            this.$message.error(response.message || '生成分享信息失败');
            this.shareDialogVisible = false;
          }
        }).catch(error => {
          this.shareLoading = false;
          this.$message.error('生成分享信息失败: ' + (error.message || error));
          this.shareDialogVisible = false;
          console.error('分享信息生成错误:', error);
        });
      },
      handleDelete(index, row) {
        this.$confirm('是否进行删除操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteCoupon(row.id).then(response=>{
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getList();
          });
        })
      },
      getList(){
        this.listLoading=true;
        fetchList(this.listQuery).then(response=>{
          this.listLoading = false;
          this.list = response.data.list;
          this.total = response.data.total;
        });
      },
      handleShareDialogClose() {
        this.shareInfo = null;
        this.shareLoading = false;
      },
      copyToClipboard(text) {
        // 创建临时文本域
        const textArea = document.createElement('textarea');
        textArea.value = text;
        document.body.appendChild(textArea);
        textArea.select();

        try {
          document.execCommand('copy');
          this.$message.success('复制成功');
        } catch (err) {
          this.$message.error('复制失败，请手动复制');
        }

        document.body.removeChild(textArea);
      },
      downloadQRCode() {
        if (this.shareInfo && this.shareInfo.miniProgramCodeBase64) {
          try {
            // 将base64转换为blob
            const base64Data = this.shareInfo.miniProgramCodeBase64.replace(/^data:image\/png;base64,/, '');
            const byteCharacters = atob(base64Data);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
              byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            const blob = new Blob([byteArray], { type: 'image/png' });

            // 创建下载链接
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = `coupon-${this.shareInfo.couponId}-qrcode.png`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(link.href);

            this.$message.success('小程序码下载成功');
          } catch (error) {
            this.$message.error('下载失败：' + error.message);
          }
        }
      },
      copyQRCodeBase64() {
        if (this.shareInfo && this.shareInfo.miniProgramCodeBase64) {
          this.copyToClipboard(this.shareInfo.miniProgramCodeBase64);
        }
      }
    }
  }
</script>
<style scoped>
  .input-width {
    width: 203px;
  }

  .share-item {
    margin-bottom: 20px;
  }

  .share-item h5 {
    margin-bottom: 10px;
    color: #333;
    font-weight: bold;
  }

  .qrcode-container {
    text-align: center;
    padding: 10px;
    border: 1px dashed #ddd;
    border-radius: 4px;
  }

  .qrcode-container img {
    border: 1px solid #eee;
    border-radius: 4px;
  }

  .qrcode-placeholder {
    width: 150px;
    height: 150px;
    border: 1px dashed #ddd;
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #999;
    font-size: 12px;
    text-align: center;
  }

  .qrcode-placeholder i {
    font-size: 24px;
    margin-bottom: 8px;
  }

  .qrcode-placeholder p {
    margin: 0;
    padding: 0 10px;
    line-height: 1.4;
  }
</style>


