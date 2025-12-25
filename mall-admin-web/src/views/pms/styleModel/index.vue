<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float: right"
          @click="searchStyleModelList()"
          type="primary"
          size="small">
          查询结果
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input style="width: 203px" v-model="listQuery.keyword" placeholder="风格名称/关键字"></el-input>
          </el-form-item>
          <el-form-item label="状态：">
            <el-select v-model="listQuery.status" placeholder="全部" clearable>
              <el-option label="启用" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button
        class="btn-add"
        @click="addStyleModel()"
        size="mini">
        添加
      </el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="styleModelTable"
                :data="list"
                style="width: 100%"
                @selection-change="handleSelectionChange"
                v-loading="listLoading"
                border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="风格名称" align="center">
          <template slot-scope="scope">{{scope.row.name}}</template>
        </el-table-column>
        <el-table-column label="功能类型" width="140" align="center">
          <template slot-scope="scope">
            <el-tag :type="getFunctionTypeTagType(scope.row.functionType)" size="small">
              {{ getFunctionTypeLabel(scope.row.functionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="风格介绍" align="center" show-overflow-tooltip>
          <template slot-scope="scope">{{scope.row.description}}</template>
        </el-table-column>
        <el-table-column label="封面图片" width="120" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row.coverImage" :src="scope.row.coverImage" style="height: 60px; width: 60px; object-fit: cover;">
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="排序" width="100" align="center">
          <template slot-scope="scope">{{scope.row.sort}}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.$index, scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.status">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="关联商品" width="120" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="manageProducts(scope.$index, scope.row)">管理商品
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleUpdate(scope.$index, scope.row)">编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="batch-operate-container">
      <el-select
        size="small"
        v-model="operateType" placeholder="批量操作">
        <el-option
          v-for="item in operates"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-button
        style="margin-left: 20px"
        class="search-button"
        @click="handleBatchOperate()"
        type="primary"
        size="small">
        确定
      </el-button>
    </div>
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :page-size="listQuery.pageSize"
        :page-sizes="[5,10,15]"
        :current-page.sync="listQuery.pageNum"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {fetchList, updateStatus, deleteStyleModel, batchOperate} from '@/api/styleModel'

export default {
  name: 'styleModelList',
  data() {
    return {
      operates: [
        {
          label: "启用风格模型",
          value: "enableStyleModel"
        },
        {
          label: "禁用风格模型",
          value: "disableStyleModel"
        }
      ],
      operateType: null,
      listQuery: {
        keyword: null,
        status: null,
        pageNum: 1,
        pageSize: 10
      },
      list: null,
      total: null,
      listLoading: false,
      multipleSelection: []
    }
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      fetchList(this.listQuery).then(response => {
        this.listLoading = false;
        this.list = response.data.list;
        this.total = response.data.total;
      });
    },
    searchStyleModelList() {
      this.listQuery.pageNum = 1;
      this.getList();
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleStatusChange(index, row) {
      this.$confirm('是否要修改该状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateStatus({id: row.id, status: row.status}).then(response => {
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
      this.$confirm('是否要删除该风格模型?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteStyleModel(row.id).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getList();
        });
      });
    },
    handleBatchOperate() {
      if (this.multipleSelection < 1) {
        this.$message({
          message: '请选择一条记录',
          type: 'warning'
        });
        return;
      }
      let ids = [];
      for (let i = 0; i < this.multipleSelection.length; i++) {
        ids.push(this.multipleSelection[i].id);
      }
      batchOperate({ids: ids, type: this.operateType}).then(response => {
        this.$message({
          type: 'success',
          message: '操作成功!'
        });
        this.getList();
      });
    },
    addStyleModel() {
      this.$router.push({path: '/diy/styleModel/add'});
    },
    handleUpdate(index, row) {
      this.$router.push({path: '/diy/styleModel/update', query: {id: row.id}});
    },
    manageProducts(index, row) {
      this.$router.push({path: '/diy/styleModel/products', query: {id: row.id, name: row.name}});
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
    // 获取功能类型标签
    getFunctionTypeLabel(functionType) {
      const typeMap = {
        'stylization_all': '全局风格化',
        'stylization_local': '局部风格化',
        'description_edit': '指令编辑',
        'description_edit_with_mask': '局部重绘',
        'remove_watermark': '去文字水印',
        'expand': '扩图',
        'super_resolution': '图像超分',
        'colorization': '图像上色',
        'doodle': '线稿生图',
        'control_cartoon_feature': '参考卡通形象'
      };
      return typeMap[functionType] || '未知';
    },
    // 获取功能类型标签颜色
    getFunctionTypeTagType(functionType) {
      const typeColorMap = {
        'stylization_all': 'success',
        'stylization_local': 'primary',
        'description_edit': '',
        'description_edit_with_mask': 'warning',
        'remove_watermark': 'info',
        'expand': 'success',
        'super_resolution': 'primary',
        'colorization': 'warning',
        'doodle': 'info',
        'control_cartoon_feature': 'success'
      };
      return typeColorMap[functionType] || '';
    }
  }
}
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.operate-container {
  margin-bottom: 20px;
}
.btn-add {
  float: right;
  margin-right: 20px;
}
.table-container {
  margin-bottom: 20px;
}
.batch-operate-container {
  margin-bottom: 20px;
}
.pagination-container {
  text-align: center;
}
</style>
