'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  // BASE_API: '"https://guanghengzou.hello4am.com/mall-admin"'
  BASE_API: '"http://localhost:8201/mall-admin"'
})
