<template>
  <div class="menu-wrapper">
    <template v-for="item in routes" v-if="!item.hidden&&item.children">

      <router-link v-if="hasOneShowingChildren(item.children) && !item.children[0].children&&!item.alwaysShow" :to="item.path+'/'+item.children[0].path"
        :key="item.children[0].name">
        <el-menu-item :index="item.path+'/'+item.children[0].path" :class="{'submenu-title-noDropdown':!isNest}">
          <div class="menu-icon-wrapper">
            <svg-icon v-if="item.children[0].meta&&item.children[0].meta.icon" :icon-class="item.children[0].meta.icon"></svg-icon>
            <el-badge v-if="shouldShowBadge(item.children[0]) && $store.getters.sidebar.opened === false" :value="getBadgeValue(item.children[0])" class="menu-icon-badge" />
          </div>
          <span v-if="item.children[0].meta&&item.children[0].meta.title" slot="title">
            {{item.children[0].meta.title}}
            <el-badge v-if="shouldShowBadge(item.children[0]) && $store.getters.sidebar.opened === true" :value="getBadgeValue(item.children[0])" class="menu-badge" />
          </span>
        </el-menu-item>
      </router-link>

      <el-submenu v-else :index="item.name||item.path" :key="item.name">
        <template slot="title">
          <div class="menu-icon-wrapper">
            <svg-icon v-if="item.meta&&item.meta.icon" :icon-class="item.meta.icon"></svg-icon>
            <el-badge v-if="shouldShowSubmenuBadge(item) && $store.getters.sidebar.opened === false" :value="getSubmenuBadgeValue(item)" class="menu-icon-badge" />
          </div>
          <span v-if="item.meta&&item.meta.title" slot="title">
            {{item.meta.title}}
            <el-badge v-if="shouldShowSubmenuBadge(item) && $store.getters.sidebar.opened === true" :value="getSubmenuBadgeValue(item)" class="menu-badge" />
          </span>
        </template>

        <template v-for="child in item.children" v-if="!child.hidden">
          <sidebar-item :is-nest="true" class="nest-menu" v-if="child.children&&child.children.length>0" :routes="[child]" :key="child.path"></sidebar-item>
          <!--支持外链功能-->
          <a v-else-if="child.path.startsWith('http')" v-bind:href="child.path" target="_blank" :key="child.name">
            <el-menu-item :index="item.path+'/'+child.path">
              <svg-icon v-if="child.meta&&child.meta.icon" :icon-class="child.meta.icon"></svg-icon>
              <span v-if="child.meta&&child.meta.title" slot="title">{{child.meta.title}}</span>
            </el-menu-item>
          </a>
          <router-link v-else :to="item.path+'/'+child.path" :key="child.name">
            <el-menu-item :index="item.path+'/'+child.path">
              <div class="menu-icon-wrapper">
                <svg-icon v-if="child.meta&&child.meta.icon" :icon-class="child.meta.icon"></svg-icon>
                <el-badge v-if="shouldShowBadge(child) && $store.getters.sidebar.opened === false" :value="getBadgeValue(child)" class="menu-icon-badge" />
              </div>
              <span v-if="child.meta&&child.meta.title" slot="title">
                {{child.meta.title}}
                <el-badge v-if="shouldShowBadge(child) && $store.getters.sidebar.opened === true" :value="getBadgeValue(child)" class="menu-badge" />
              </span>
            </el-menu-item>
          </router-link>
        </template>
      </el-submenu>

    </template>
  </div>
</template>

<script>
import { getPendingReturnApplyCount } from '@/api/returnApply'

export default {
  name: 'SidebarItem',
  props: {
    routes: {
      type: Array
    },
    isNest: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      pendingReturnApplyCount: 0
    }
  },
  created() {
    this.fetchPendingReturnApplyCount()
    // 每30秒刷新一次未处理售后申请数量
    this.timer = setInterval(() => {
      this.fetchPendingReturnApplyCount()
    }, 30000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    hasOneShowingChildren(children) {
      const showingChildren = children.filter(item => {
        return !item.hidden
      })
      if (showingChildren.length === 1) {
        return true
      }
      return false
    },
    // 获取未处理售后申请数量
    async fetchPendingReturnApplyCount() {
      try {
        const response = await getPendingReturnApplyCount()
        this.pendingReturnApplyCount = response.data || 0
      } catch (error) {
        console.error('获取未处理售后申请数量失败:', error)
      }
    },
    // 判断是否显示红点
    shouldShowBadge(menuItem) {
      // 只在退货申请处理菜单显示红点
      return this.isReturnApplyMenu(menuItem) && this.pendingReturnApplyCount > 0
    },
    // 判断是否显示子菜单红点
    shouldShowSubmenuBadge(menuItem) {
      // 检查子菜单中是否有退货申请处理菜单项
      if (!menuItem.children) return false
      return menuItem.children.some(child => this.isReturnApplyMenu(child)) && this.pendingReturnApplyCount > 0
    },
    // 获取红点数值
    getBadgeValue(menuItem) {
      return this.isReturnApplyMenu(menuItem) ? this.pendingReturnApplyCount : 0
    },
    // 获取子菜单红点数值
    getSubmenuBadgeValue(menuItem) {
      return this.shouldShowSubmenuBadge(menuItem) ? this.pendingReturnApplyCount : 0
    },
    // 判断是否为退货申请处理菜单
    isReturnApplyMenu(menuItem) {
      if (!menuItem || !menuItem.meta) return false
      // 检查菜单标题或路径是否为退货申请处理
      const title = menuItem.meta.title || ''
      const path = menuItem.path || ''
      return title.includes('退货申请处理') || path.includes('returnApply')
    }
  }
}
</script>

<style scoped>
.menu-icon-wrapper {
  position: relative;
  display: inline-block;
}

.menu-badge {
  margin-left: 8px;
}

.menu-badge >>> .el-badge__content {
  background-color: #f56c6c;
  border: 1px solid #f56c6c;
  font-size: 10px;
  height: 16px;
  line-height: 14px;
  min-width: 16px;
  padding: 0 4px;
  border-radius: 8px;
}

.menu-badge >>> .el-badge__content.is-fixed {
  top: 2px;
  right: -8px;
}

/* 图标红点样式 - 用于侧边栏收起时显示 */
.menu-icon-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  z-index: 10;
}

.menu-icon-badge >>> .el-badge__content {
  background-color: #f56c6c;
  border: 1px solid #f56c6c;
  font-size: 10px;
  height: 16px;
  line-height: 14px;
  min-width: 16px;
  padding: 0 4px;
  border-radius: 8px;
}

.menu-icon-badge >>> .el-badge__content.is-fixed {
  top: 0;
  right: 0;
  transform: translate(50%, -50%);
}
</style>
