<template>
  <div class="app-container">
    <!-- 搜索区域 -->
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
          <el-form-item label="祝福类型：">
            <el-select v-model="listQuery.type" placeholder="全部" clearable class="input-width">
              <el-option v-for="item in typeOptions"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="分类：">
            <el-select v-model="listQuery.category" placeholder="全部" clearable class="input-width">
              <el-option v-for="item in categoryOptions"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="内容搜索：">
            <el-input v-model="listQuery.content" class="input-width" placeholder="内容关键字"></el-input>
          </el-form-item>
          <el-form-item label="状态：">
            <el-select v-model="listQuery.status" placeholder="全部" clearable class="input-width">
              <el-option v-for="item in statusOptions"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    
    <!-- 数据列表 -->
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button
        class="btn-add"
        size="mini"
        style="float: right;margin-right: 20px"
        @click="handleAdd()">
        添加
      </el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="giftWishTable"
              :data="list"
              style="width: 100%"
              v-loading="listLoading"
              border>
        <el-table-column label="ID" width="80" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="祝福类型" width="120" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type" type="success">图片</el-tag>
            <el-tag v-else type="info">文字</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="120" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.category" type="primary">送礼祝福</el-tag>
            <el-tag v-else type="warning">收礼感谢语</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="内容预览" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row.type" :src="scope.row.imageUrl" style="height: 80px">
            <span v-else>{{scope.row.content}}</span>
          </template>
        </el-table-column>
        <el-table-column label="排序" width="100" align="center">
          <template slot-scope="scope">{{scope.row.sort}}</template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.row)"
              v-model="scope.row.status">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template slot-scope="scope">{{scope.row.createTime | formatDateTime}}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleUpdate(scope.row)">编辑</el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)">删除</el-button>
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
        :page-sizes="[5, 10, 15]"
        :page-size="listQuery.pageSize"
        :current-page.sync="listQuery.pageNum"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { fetchList, updateGiftWish, deleteGiftWish } from '@/api/giftWish';
import { formatDate } from '@/utils/date';

// 默认查询参数
const defaultListQuery = {
  pageNum: 1,
  pageSize: 10,
  type: null,
  category: null,
  content: null,
  status: null
};

export default {
  name: 'giftWishList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: [],
      total: 0,
      listLoading: false,
      // 祝福类型选项
      typeOptions: [
        {
          label: '图片',
          value: true
        },
        {
          label: '文字',
          value: false
        }
      ],
      // 分类选项
      categoryOptions: [
        {
          label: '送礼祝福',
          value: true
        },
        {
          label: '收礼感谢语',
          value: false
        }
      ],
      // 状态选项
      statusOptions: [
        {
          label: '启用',
          value: true
        },
        {
          label: '禁用',
          value: false
        }
      ]
    };
  },
  created() {
    this.getList();
  },
  filters: {
    formatDateTime(time) {
      if (time == null || time === '') {
        return 'N/A';
      }
      return formatDate(new Date(time), 'yyyy-MM-dd hh:mm:ss');
    }
  },
  methods: {
    // 获取心愿列表数据
    getList() {
      this.listLoading = true;
      fetchList(this.listQuery).then(response => {
        this.listLoading = false;
        this.list = response.data.list;
        this.total = response.data.total;
      });
    },
    // 处理添加心愿
    handleAdd() {
      this.$router.push({path: '/oms/addGiftWish'});
    },
    // 处理编辑心愿
    handleUpdate(row) {
      this.$router.push({path: '/oms/updateGiftWish', query: {id: row.id}});
    },
    // 处理删除心愿
    handleDelete(row) {
      this.$confirm('是否要删除该礼物心愿?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteGiftWish(row.id).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getList();
        });
      });
    },
    // 处理状态变更
    handleStatusChange(row) {
      const data = {
        id: row.id,
        status: row.status
      };
      updateGiftWish(row.id, data).then(() => {
        this.$message({
          message: '状态修改成功',
          type: 'success'
        });
      }).catch(() => {
        // 更新失败时恢复原状态
        row.status = !row.status;
        this.$message({
          message: '状态修改失败',
          type: 'error'
        });
      });
    },
    // 处理搜索
    handleSearchList() {
      this.listQuery.pageNum = 1;
      this.getList();
    },
    // 重置搜索
    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery);
    },
    // 分页大小变更
    handleSizeChange(val) {
      this.listQuery.pageNum = 1;
      this.listQuery.pageSize = val;
      this.getList();
    },
    // 分页变更
    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
    }
  }
};
</script>

<style scoped>
.input-width {
  width: 203px;
}

.filter-container {
  margin-bottom: 20px;
}

.table-container {
  margin-top: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}

.operate-container {
  margin-top: 20px;
}
</style> 