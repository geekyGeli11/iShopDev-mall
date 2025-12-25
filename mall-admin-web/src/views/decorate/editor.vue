<template>
  <div class="decorate-editor">
    <!-- 左侧组件库 -->
    <div class="sidebar components">
      <div class="sidebar-header">
        <el-input v-model="search" size="mini" placeholder="搜索组件" clearable />
      </div>
      <draggable :list="filteredComponents" group="{ name: 'components', pull: 'clone', put: false }" :clone="cloneComponent" :sort="false" :move="() => false" item-key="id" class="components-list">
        <div v-for="item in filteredComponents" :key="item.id" class="component-item" @dblclick="appendComponent(item)">
          <img v-if="item.icon" :src="item.icon" />
          <span>{{ item.name }}</span>
        </div>
      </draggable>
    </div>

    <!-- 中间画布 -->
    <div class="canvas-wrapper">
      <div class="toolbar">
        <el-button size="mini" type="primary" @click="handleSave">保存</el-button>
        <el-button size="mini" @click="handlePreview">预览</el-button>
      </div>
      <div class="phone-stage" ref="stage">
        <draggable v-model="blocks" :group="{ name: 'components', pull: false, put: true }" class="canvas" item-key="blockKey">
          <div v-for="(block, idx) in blocks" :key="block.blockKey" class="canvas-block" @click="selectBlock(idx)" :class="{ active: selectedIndex===idx }">
            <div class="block-preview">
              <template v-if="block.type==='product-grid' && block.props.dataSource && block.props.dataSource.ids && block.props.dataSource.ids.length">
                <div class="product-placeholder" v-for="id in block.props.dataSource.ids.slice(0,3)" :key="id">ID{{id}}</div>
              </template>
              <template v-else-if="block.type==='banner' && block.props.images && block.props.images.length">
                <img :src="(typeof block.props.images[0]==='string'?block.props.images[0]:block.props.images[0].url)" class="preview-img" />
              </template>
              <template v-else>
                <img v-if="block.icon" :src="block.icon" class="preview-img" />
                <span v-else>{{ block.type }}</span>
              </template>
            </div>
            <div class="block-ops">
              <el-button type="text" icon="el-icon-copy-document" @click.stop="copyBlock(idx)"></el-button>
              <el-button type="text" icon="el-icon-delete" @click.stop="deleteBlock(idx)"></el-button>
            </div>
          </div>
        </draggable>
      </div>
    </div>

    <!-- 右侧属性面板 -->
    <div class="sidebar props">
      <div class="sidebar-header">属性</div>
      <div v-if="currentSchema">
        <VueForm
          :key="blocks[selectedIndex].blockKey"
          v-model="blocks[selectedIndex].props"
          :schema="currentSchema"
          :ui-schema="{}"
          @submit="autoSaveProps"
        />
        <el-button v-if="showChooseResource" size="mini" @click="chooseResource">选择关联资源</el-button>
        <div v-if="isBanner" class="banner-editor">
          <div v-for="(item,i) in blocks[selectedIndex].props.images" :key="i" class="banner-row">
            <el-upload
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="(res)=>onBannerImgSucc(i,res)"
              :class="{'no-img': !item.url}"
            >
              <img v-if="item.url" :src="item.url" class="banner-thumb" />
              <el-button v-else size="mini" icon="el-icon-plus">上传图片</el-button>
            </el-upload>
            <el-input v-model="item.link" placeholder="跳转地址" size="mini" class="link-input" />
            <el-button type="text" icon="el-icon-delete" @click="delImg(i)" />
          </div>
          <el-button size="mini" icon="el-icon-plus" @click="addBannerItem">添加图片</el-button>
        </div>
      </div>
      <div v-else class="empty">请选择一个组件</div>
    </div>
  </div>
</template>

<script>
import { fetchComponentList, fetchPageDetail, updatePage, fetchPagePreview } from '@/api/decorate';
import draggable from 'vuedraggable';
import VueForm from '@lljj/vue-json-schema-form';
import { v4 as uuidv4 } from 'uuid';
import SelectionDialog from '@/components/SelectionDialog.vue';

export default {
  name: 'DecorateEditor',
  components: { draggable, VueForm, SelectionDialog },
  data() {
    return {
      componentList: [],
      blocks: [],
      selectedIndex: -1,
      pageId: this.$route.query.pageId,
      storeId: this.$route.query.storeId,
      search: '',
      uploadUrl: process.env.BASE_API + '/tencent/cos/upload'
    };
  },
  computed: {
    currentSchema() {
      if (this.selectedIndex === -1) return null;
      const blk = this.blocks[this.selectedIndex];
      if (!blk) return null;
      if (blk.schema) {
        if (typeof blk.schema === 'string') {
          try {
            return JSON.parse(blk.schema);
          } catch (e) {
            console.warn('Schema parse error', e);
            return null;
          }
        }
        return blk.schema;
      }
      const found = this.componentList.find(c => c.type === blk.type);
      return found ? found.configSchema : null;
    },
    filteredComponents() {
      if (!this.search) return this.componentList;
      return this.componentList.filter(c => c.name.includes(this.search) || c.type.includes(this.search));
    },
    showChooseResource() {
      if (this.selectedIndex === -1) return false;
      const blk = this.blocks[this.selectedIndex];
      return blk && blk.type === 'product-grid';
    },
    isBanner() {
      return this.selectedIndex !== -1 && this.blocks[this.selectedIndex].type === 'banner';
    }
  },
  created() {
    this.loadComponents();
    this.loadPage();
  },
  methods: {
    loadComponents() {
      fetchComponentList().then(res => {
        if (res.code === 200) {
          // 后端字段命名适配，并对 banner 组件扩展 schema 增加 link 字段
          this.componentList = (res.data || []).map(it => {
            const cfg = it.configSchema ? JSON.parse(it.configSchema) : {};
            if (it.type === 'banner') {
              cfg.type = 'object';
              cfg.required = ['images'];
              cfg.properties = {
                images: {
                  type: 'array',
                  title: '图片列表',
                  items: {
                    type: 'object',
                    required: ['url'],
                    properties: {
                      url: { type: 'string', format: 'uri', title: '图片URL' },
                      link: { type: 'string', title: '跳转地址' }
                    }
                  }
                }
              };
            }
            return {
              id: it.id,
              name: it.name,
              type: it.type,
              icon: it.icon,
              configSchema: cfg
            };
          });
        }
      });
    },
    loadPage() {
      if (!this.pageId) return;
      fetchPageDetail(this.pageId).then(async res => {
        if (res.code === 200 && res.data && res.data.content) {
          try{
            const contentObj = JSON.parse(res.data.content);
            this.blocks = contentObj.blocks || [];
            return;
          }catch(e){
            console.warn('解析content失败，尝试预览接口',e);
          }
        }
        // fallback: 调用预览接口获取 DSL
        const prev = await fetchPagePreview(this.pageId);
        if(prev.code===200 && prev.data){
          try{
            const obj = JSON.parse(prev.data);
            this.blocks = obj.blocks || [];
          }catch(err){ console.error('预览DSL解析失败',err);}        
        }
      });
    },
    selectBlock(idx) {
      this.selectedIndex = idx;
    },
    handleSave() {
      const payload = {
        content: JSON.stringify({ blocks: this.blocks })
      };
      updatePage(this.pageId, payload).then(res => {
        if (res.code === 200) {
          this.$message.success('保存成功');
        }
      });
    },
    handlePreview() {
      const url = `/decorate/page/${this.pageId}/preview`;
      window.open(url, '_blank');
    },
    cloneComponent(item) {
      return {
        blockKey: uuidv4(),
        type: item.type,
        icon: item.icon,
        schema: item.configSchema,
        props: {},
      };
    },
    copyBlock(idx) {
      const cloned = JSON.parse(JSON.stringify(this.blocks[idx]));
      cloned.blockKey = uuidv4();
      this.blocks.splice(idx + 1, 0, cloned);
    },
    deleteBlock(idx) {
      this.blocks.splice(idx, 1);
      if (this.selectedIndex === idx) this.selectedIndex = -1;
    },
    appendComponent(item) {
      const blk = this.cloneComponent(item);
      this.blocks.push(blk);
    },
    previewText(block) {
      // Implement the logic to return a different representation based on the block type
      // This is a placeholder and should be replaced with the actual implementation
      return block.type;
    },
    chooseResource() {
      const blk = this.blocks[this.selectedIndex];
      if (!blk) return;
      const typeMap = { 'product-grid': 'product', 'coupon-bar': 'coupon' };
      const dlgType = typeMap[blk.type];
      if (!dlgType) return;
      const Dialog = this.$dialog || this.$root.$mount;
      this.$dialog({
        component: SelectionDialog,
        props: { type: dlgType, multiple: true, value: [] }
      }).then(list => {
        const ids = list.map(i => i.id);
        blk.props.dataSource = blk.props.dataSource || { mode: 'static' };
        blk.props.dataSource.ids = ids;
      });
    },
    computeScale() {
      const stage = this.$refs.stage;
      if (!stage) return;
      const maxW = this.$el.clientWidth - 480;
      const scale = Math.min(1, maxW / 430);
      stage.style.transform = `scale(${scale})`;
    },
    onBannerImgSucc(idx, res) {
      const url = res.data.url || res.data;
      const imgs = this.blocks[this.selectedIndex].props.images || [];
      if (idx >= 0 && idx < imgs.length) {
        imgs[idx].url = url;
      }
    },
    addBannerItem() {
      const imgs = this.blocks[this.selectedIndex].props.images || [];
      imgs.push({ url: '', link: '' });
      this.$set(this.blocks[this.selectedIndex].props, 'images', imgs);
    },
    delImg(i) {
      this.blocks[this.selectedIndex].props.images.splice(i, 1);
    },
    autoSaveProps() {
      this.handleSave();
    }
  },
  watch: {
    selectedIndex(idx) {
      console.log('Selected block index:', idx, this.blocks[idx]);
      console.log('Current schema:', this.currentSchema);
    },
    blocks: {
      handler() { this.$nextTick(() => this.computeScale()); },
      deep: true
    }
  },
  mounted() {
    window.addEventListener('resize', this.computeScale);
    this.computeScale();
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.computeScale);
  }
};
</script>

<style scoped>
.decorate-editor {
  display: flex;
  height: calc(100vh - 60px);
}
.sidebar {
  width: 240px;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}
.sidebar.props {
  border-left: 1px solid #ebeef5;
}
.sidebar-header {
  padding: 10px;
  font-weight: bold;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}
.components-list {
  flex: 1;
  overflow-y: auto;
}
.component-item {
  padding: 8px 12px;
  border-bottom: 1px dashed #ebeef5;
  cursor: grab;
  display: flex;
  align-items: center;
}
.component-item img {
  width: 24px;
  height: 24px;
  margin-right: 6px;
}
.canvas-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.toolbar {
  padding: 6px 10px;
  border-bottom: 1px solid #ebeef5;
}
.canvas {
  flex: 1;
  background: #fff;
  overflow-y: auto;
  width: 100%;
  height: 100%;
  padding: 10px;
}
.canvas-block {
  border: 1px dashed #dcdfe6;
  padding: 16px;
  margin-bottom: 10px;
  position: relative;
  cursor: pointer;
  min-height: 60px;
}
.canvas-block.active {
  border-color: #409eff;
}
.block-preview {
  display: flex;
  align-items: center;
}
.preview-img {
  width: 32px;
  height: 32px;
  margin-right: 6px;
}
.block-ops {
  position: absolute;
  top: 4px;
  right: 4px;
}
.block-ops .el-button {
  padding: 4px;
}
.empty {
  padding: 20px;
  text-align: center;
  color: #909399;
}
.phone-stage {
  flex: 1;
  position: relative;
  width: 430px;
  height: 932px;
  border: 1px solid #dcdfe6;
  background: #fafafa;
  transform-origin: top left;
  margin: 0 auto;
}
.product-placeholder {
  width: 60px;
  height: 60px;
  border: 1px solid #ccc;
  display: inline-block;
  font-size: 10px;
  line-height: 60px;
  text-align: center;
  margin-right: 4px;
}
.banner-editor {
  margin-top: 10px;
}
.banner-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.banner-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  margin-right: 6px;
  border: 1px solid #dcdfe6;
}
.link-input {
  flex: 1;
  margin-right: 6px;
}
.banner-row .el-upload.no-img {
  border: 1px dashed #dcdfe6;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 6px;
}
</style> 