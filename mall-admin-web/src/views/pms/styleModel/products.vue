<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>风格模型：{{ styleModelName }} - 商品管理</span>
        <el-button
          style="float: right"
          @click="searchProductList()"
          type="primary"
          size="small">
          查询结果
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input style="width: 203px" v-model="listQuery.keyword" placeholder="商品名称/关键字"></el-input>
          </el-form-item>
          <el-form-item label="商品分类：">
            <el-cascader
              clearable
              v-model="selectProductCateValue"
              :options="productCateOptions"
              @change="handleCascaderChange">
            </el-cascader>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>商品列表</span>
      <el-button
        class="btn-add"
        @click="addProducts()"
        size="mini">
        添加商品
      </el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="productTable"
                :data="list"
                style="width: 100%"
                @selection-change="handleSelectionChange"
                v-loading="listLoading"
                border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="商品图片" width="120" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row.pic" :src="scope.row.pic" style="height: 60px; width: 60px; object-fit: cover;">
          </template>
        </el-table-column>
        <el-table-column label="商品名称" align="center">
          <template slot-scope="scope">{{scope.row.name}}</template>
        </el-table-column>
        <el-table-column label="价格" width="100" align="center">
          <template slot-scope="scope">¥{{scope.row.price}}</template>
        </el-table-column>
        <el-table-column label="排序" width="100" align="center">
          <template slot-scope="scope">{{scope.row.sort}}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="danger"
              @click="handleRemove(scope.$index, scope.row)">移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="batch-operate-container">
      <el-button
        class="search-button"
        @click="handleBatchRemove()"
        type="danger"
        size="small">
        批量移除
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

    <!-- 添加商品对话框 -->
    <el-dialog
      title="选择商品"
      :visible.sync="addProductDialogVisible"
      width="80%">
      <el-form :inline="true" size="small" label-width="140px">
        <el-form-item label="输入搜索：">
          <el-input style="width: 203px" v-model="productSearchQuery.keyword" placeholder="商品名称"></el-input>
        </el-form-item>
        <el-button @click="searchAvailableProducts()" type="primary" size="small">查询</el-button>
      </el-form>
      <el-table
        ref="availableProductTable"
        :data="availableProductList"
        @selection-change="handleAvailableProductSelectionChange"
        v-loading="availableProductLoading"
        border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="商品图片" width="120" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row.pic" :src="scope.row.pic" style="height: 60px; width: 60px; object-fit: cover;">
          </template>
        </el-table-column>
        <el-table-column label="商品名称" align="center">
          <template slot-scope="scope">{{scope.row.name}}</template>
        </el-table-column>
        <el-table-column label="价格" width="100" align="center">
          <template slot-scope="scope">¥{{scope.row.price}}</template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleAvailableProductSizeChange"
          @current-change="handleAvailableProductCurrentChange"
          layout="total, sizes,prev, pager, next,jumper"
          :page-size="productSearchQuery.pageSize"
          :page-sizes="[5,10,15]"
          :current-page.sync="productSearchQuery.pageNum"
          :total="availableProductTotal">
        </el-pagination>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addProductDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmAddProducts">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {getStyleModelProducts, addProductToStyleModel, removeProductFromStyleModel} from '@/api/styleModel'
import {fetchList as fetchProductList} from '@/api/product'
import {fetchListWithChildren} from '@/api/productCate'

export default {
  name: 'styleModelProducts',
  data() {
    return {
      styleModelId: null,
      styleModelName: '',
      listQuery: {
        keyword: null,
        productCategoryId: null,
        pageNum: 1,
        pageSize: 10
      },
      list: null,
      total: null,
      listLoading: false,
      multipleSelection: [],
      selectProductCateValue: null,
      productCateOptions: [],
      addProductDialogVisible: false,
      productSearchQuery: {
        keyword: null,
        pageNum: 1,
        pageSize: 10
      },
      availableProductList: [],
      availableProductTotal: 0,
      availableProductLoading: false,
      availableProductSelection: []
    }
  },
  created() {
    this.styleModelId = this.$route.query.id;
    this.styleModelName = this.$route.query.name || '';
    this.getProductCateList();
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      getStyleModelProducts(this.styleModelId, this.listQuery).then(response => {
        this.listLoading = false;
        this.list = response.data.list;
        this.total = response.data.total;
      });
    },
    searchProductList() {
      this.listQuery.pageNum = 1;
      this.getList();
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleRemove(index, row) {
      this.$confirm('是否要移除该商品?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeProductFromStyleModel(this.styleModelId, [row.id]).then(response => {
          this.$message({
            type: 'success',
            message: '移除成功!'
          });
          this.getList();
        });
      });
    },
    handleBatchRemove() {
      if (this.multipleSelection.length < 1) {
        this.$message({
          message: '请选择要移除的商品',
          type: 'warning'
        });
        return;
      }
      let ids = this.multipleSelection.map(item => item.id);
      this.$confirm('是否要移除选中的商品?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeProductFromStyleModel(this.styleModelId, ids).then(response => {
          this.$message({
            type: 'success',
            message: '移除成功!'
          });
          this.getList();
        });
      });
    },
    addProducts() {
      this.addProductDialogVisible = true;
      this.searchAvailableProducts();
    },
    searchAvailableProducts() {
      this.availableProductLoading = true;
      fetchProductList(this.productSearchQuery).then(response => {
        this.availableProductLoading = false;
        this.availableProductList = response.data.list;
        this.availableProductTotal = response.data.total;
      });
    },
    handleAvailableProductSelectionChange(val) {
      this.availableProductSelection = val;
    },
    confirmAddProducts() {
      if (this.availableProductSelection.length < 1) {
        this.$message({
          message: '请选择要添加的商品',
          type: 'warning'
        });
        return;
      }
      let ids = this.availableProductSelection.map(item => item.id);
      addProductToStyleModel(this.styleModelId, ids).then(response => {
        this.$message({
          type: 'success',
          message: '添加成功!'
        });
        this.addProductDialogVisible = false;
        this.getList();
      });
    },
    getProductCateList() {
      fetchListWithChildren().then(response => {
        let list = response.data;
        this.productCateOptions = [];
        for (let i = 0; i < list.length; i++) {
          let children = [];
          if (list[i].children != null && list[i].children.length > 0) {
            for (let j = 0; j < list[i].children.length; j++) {
              children.push({label: list[i].children[j].name, value: list[i].children[j].id});
            }
          }
          this.productCateOptions.push({label: list[i].name, value: list[i].id, children: children});
        }
      });
    },
    handleCascaderChange(value) {
      this.listQuery.productCategoryId = value[value.length - 1];
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
    handleAvailableProductSizeChange(val) {
      this.productSearchQuery.pageNum = 1;
      this.productSearchQuery.pageSize = val;
      this.searchAvailableProducts();
    },
    handleAvailableProductCurrentChange(val) {
      this.productSearchQuery.pageNum = val;
      this.searchAvailableProducts();
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
  margin-top: 20px;
}
</style>
