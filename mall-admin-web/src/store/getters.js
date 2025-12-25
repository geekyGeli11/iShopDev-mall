const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  roles: state => state.user.roles,
  addRouters: state => state.permission.addRouters,
  routers: state => state.permission.routers,
  // 列表状态相关 getters
  getListState: state => state.listState.getListState,
  hasListState: state => state.listState.hasListState
}
export default getters
