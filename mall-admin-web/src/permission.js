import router from './router'
import store from './store'
import NProgress from 'nprogress' // Progress 进度条
import 'nprogress/nprogress.css'// Progress 进度条样式
import { Message } from 'element-ui'
import { getToken } from '@/utils/auth' // 验权

const whiteList = ['/login'] // 不重定向白名单

// 获取第一个有权限的路由路径
function getFirstAccessibleRoute(routers) {
  for (let i = 0; i < routers.length; i++) {
    const route = routers[i];
    if (route.hidden) continue;
    if (route.children && route.children.length > 0) {
      for (let j = 0; j < route.children.length; j++) {
        const child = route.children[j];
        if (!child.hidden) {
          // 拼接完整路径
          const parentPath = route.path || '';
          const childPath = child.path || '';
          if (parentPath === '') {
            return '/' + childPath;
          }
          return parentPath + '/' + childPath;
        }
      }
    }
  }
  return '/404'; // 如果没有任何可访问的路由，跳转到404
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done() // if current page is dashboard will not trigger	afterEach hook, so manually handle it
    } else {
      if (store.getters.roles.length === 0) {
        store.dispatch('GetInfo').then(res => { // 拉取用户信息
          let menus=res.data.menus;
          let username=res.data.username;
          let roles=res.data.roles || [];
          store.dispatch('GenerateRoutes', { menus, username, roles }).then(() => { // 生成可访问的路由表
            router.addRoutes(store.getters.addRouters); // 动态添加可访问路由表
            // 如果访问根路径，跳转到第一个有权限的页面
            if (to.path === '/' || to.path === '/home') {
              const firstRoute = getFirstAccessibleRoute(store.getters.addRouters);
              next({ path: firstRoute, replace: true })
            } else {
              next({ ...to, replace: true })
            }
          })
        }).catch((err) => {
          store.dispatch('FedLogOut').then(() => {
            Message.error(err || 'Verification failed, please login again')
            next({ path: '/' })
          })
        })
      } else {
        // 已登录且有角色信息，检查是否访问根路径
        if (to.path === '/') {
          const firstRoute = getFirstAccessibleRoute(store.getters.addRouters);
          next({ path: firstRoute, replace: true })
        } else {
          next()
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next('/login')
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done() // 结束Progress
})
