import Vue from 'vue'

import 'normalize.css/normalize.css'// A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/zh-CN' // lang i18n
// 注释掉 v-charts，因为与 Vue 2.7 存在兼容性问题
// import VCharts from 'v-charts'

// 注释掉 DataV 数据可视化组件库，因为与 Vue 2.7 存在兼容性问题
// import dataV from '@jiaminghi/data-view'

// 引入vue-count-to数字滚动组件
import countTo from 'vue-count-to'

// 引入Vant移动端组件
import {
  DatetimePicker,
  Popup,
  Card,
  Cell,
  CellGroup,
  Tag,
  Rate,
  Image as VanImage,
  Divider,
  Button as VanButton,
  Steps,
  Step
} from 'vant'
import 'vant/lib/index.css'

// 引入高德地图
import VueAMap from 'vue-amap'

import '@/styles/index.scss' // global css

import App from './App'
import router from './router'
import store from './store'

import '@/icons' // icon
import '@/permission' // permission control

// 导入日期格式化函数
import { formatDateTime } from '@/utils/date'

Vue.use(ElementUI, { locale })
// 注释掉 v-charts，因为与 Vue 2.7 存在兼容性问题
// Vue.use(VCharts)
// 注释掉 DataV 组件，因为与 Vue 2.7 存在兼容性问题
// Vue.use(dataV)
// 注册vue-count-to组件
Vue.component('countTo', countTo)
// 使用Vant组件
Vue.use(DatetimePicker)
Vue.use(Popup)
Vue.use(Card)
Vue.use(Cell)
Vue.use(CellGroup)
Vue.use(Tag)
Vue.use(Rate)
Vue.use(VanImage)
Vue.use(Divider)
Vue.use(VanButton)
Vue.use(Steps)
Vue.use(Step)
// 使用高德地图
Vue.use(VueAMap)

// 挂载全局日期格式化方法
Vue.prototype.$formatDate = formatDateTime

// 初始化高德地图
VueAMap.initAMapApiLoader({
  // 高德地图key，需要在高德地图开发者平台申请
  key: '你的高德地图API密钥',
  // 插件集合
  plugin: ['AMap.Autocomplete', 'AMap.PlaceSearch', 'AMap.Scale', 'AMap.OverView', 'AMap.ToolBar', 'AMap.MapType', 'AMap.PolyEditor', 'AMap.CircleEditor', 'AMap.Geolocation'],
  // 高德地图版本
  v: '1.4.4',
  uiVersion: '1.0'
})

// 兼容 v-charts 对 _watchers 的早期访问（Vue 2.7+ 中此属性在 created 钩子前可能未初始化）
// 已禁用 v-charts 和 DataV，此兼容代码保留以防其他组件需要
Vue.mixin({
  beforeCreate() {
    if (!this._watchers) {
      this._watchers = [];
    }
  }
});

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
