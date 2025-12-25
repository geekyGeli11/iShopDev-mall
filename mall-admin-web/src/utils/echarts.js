/**
 * ECharts工具函数
 * 用于统一管理ECharts的配置和初始化
 */

import * as echarts from 'echarts/core'
import {
  BarChart,
  LineChart,
  PieChart,
  RadarChart,
  ScatterChart,
  EffectScatterChart,
  GaugeChart
} from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  DataZoomComponent,
  ToolboxComponent,
  MarkPointComponent,
  MarkLineComponent,
  MarkAreaComponent,
  TimelineComponent,
  VisualMapComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import darkTheme from './echarts-dark-theme'

// 注册必需的组件
echarts.use([
  BarChart,
  LineChart,
  PieChart,
  RadarChart,
  ScatterChart,
  EffectScatterChart,
  GaugeChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  DataZoomComponent,
  ToolboxComponent,
  MarkPointComponent,
  MarkLineComponent,
  MarkAreaComponent,
  TimelineComponent,
  VisualMapComponent,
  CanvasRenderer
])

// 注册深色主题
echarts.registerTheme('dark', darkTheme)

/**
 * 初始化ECharts实例
 * @param {HTMLElement} dom - DOM元素
 * @param {String} theme - 主题名称，默认为空（浅色主题）
 * @param {Object} opts - 初始化选项
 * @returns {Object} ECharts实例
 */
export function initChart(dom, theme = '', opts = {}) {
  return echarts.init(dom, theme, opts)
}

/**
 * 获取默认的图表配置
 * @param {Boolean} isDark - 是否使用深色主题
 * @returns {Object} 默认配置
 */
export function getDefaultOption(isDark = false) {
  return {
    animation: true,
    animationDuration: 1000,
    animationEasing: 'cubicOut',
    textStyle: {
      fontFamily: 'Microsoft YaHei, Arial, sans-serif'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      backgroundColor: isDark ? 'rgba(26, 39, 68, 0.9)' : 'rgba(255, 255, 255, 0.9)',
      borderColor: isDark ? '#2e4a7c' : '#ddd',
      borderWidth: 1,
      textStyle: {
        color: isDark ? '#EEF1FA' : '#333'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    }
  }
}

/**
 * 创建折线图配置
 * @param {Object} params - 参数对象
 * @returns {Object} 图表配置
 */
export function createLineChartOption(params) {
  const {
    title = '',
    xAxisData = [],
    series = [],
    isDark = false,
    smooth = true,
    showArea = false
  } = params

  const defaultOption = getDefaultOption(isDark)

  return {
    ...defaultOption,
    title: {
      text: title,
      left: 'center'
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      boundaryGap: false
    },
    yAxis: {
      type: 'value'
    },
    series: series.map(item => ({
      ...item,
      type: 'line',
      smooth,
      areaStyle: showArea ? {} : undefined
    }))
  }
}

/**
 * 创建柱状图配置
 * @param {Object} params - 参数对象
 * @returns {Object} 图表配置
 */
export function createBarChartOption(params) {
  const {
    title = '',
    xAxisData = [],
    series = [],
    isDark = false,
    showGradient = true
  } = params

  const defaultOption = getDefaultOption(isDark)

  return {
    ...defaultOption,
    title: {
      text: title,
      left: 'center'
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: {
      type: 'value'
    },
    series: series.map((item, index) => ({
      ...item,
      type: 'bar',
      itemStyle: showGradient ? {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: item.color || defaultOption.color[index] },
          { offset: 1, color: item.color2 || defaultOption.color[index] }
        ])
      } : undefined
    }))
  }
}

/**
 * 创建饼图配置
 * @param {Object} params - 参数对象
 * @returns {Object} 图表配置
 */
export function createPieChartOption(params) {
  const {
    title = '',
    data = [],
    isDark = false,
    isRing = true,
    radius = ['40%', '70%']
  } = params

  const defaultOption = getDefaultOption(isDark)

  return {
    ...defaultOption,
    title: {
      text: title,
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: title,
        type: 'pie',
        radius: isRing ? radius : '70%',
        data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          formatter: '{b}: {d}%'
        }
      }
    ]
  }
}

/**
 * 创建双轴折线图配置
 * @param {Object} params - 参数对象
 * @returns {Object} 图表配置
 */
export function createDualAxisLineChartOption(params) {
  const {
    title = '',
    xAxisData = [],
    leftSeries = [],
    rightSeries = [],
    leftAxisName = '',
    rightAxisName = '',
    isDark = false
  } = params

  const defaultOption = getDefaultOption(isDark)

  return {
    ...defaultOption,
    title: {
      text: title,
      left: 'center'
    },
    legend: {
      data: [...leftSeries.map(s => s.name), ...rightSeries.map(s => s.name)],
      top: 30
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: [
      {
        type: 'value',
        name: leftAxisName,
        position: 'left'
      },
      {
        type: 'value',
        name: rightAxisName,
        position: 'right'
      }
    ],
    series: [
      ...leftSeries.map(item => ({
        ...item,
        type: 'line',
        yAxisIndex: 0,
        smooth: true
      })),
      ...rightSeries.map(item => ({
        ...item,
        type: 'line',
        yAxisIndex: 1,
        smooth: true
      }))
    ]
  }
}

/**
 * 响应式调整图表大小
 * @param {Object} chart - ECharts实例
 */
export function resizeChart(chart) {
  if (chart && !chart.isDisposed()) {
    chart.resize()
  }
}

/**
 * 销毁图表实例
 * @param {Object} chart - ECharts实例
 */
export function disposeChart(chart) {
  if (chart && !chart.isDisposed()) {
    chart.dispose()
  }
}

export default echarts
