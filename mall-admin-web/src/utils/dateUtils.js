/**
 * 日期工具函数
 */

/**
 * 格式化日期为 YYYY-MM-DD
 * @param {Date} date - 日期对象
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/**
 * 根据时间范围选项计算开始和结束日期
 * @param {string} timeRange - 时间范围选项
 * @returns {Object} 包含startDate和endDate的对象
 */
export function calculateDateRange(timeRange) {
  const today = new Date()
  const endDate = formatDate(today)
  let startDate
  
  switch (timeRange) {
    case 'today':
      // 当日
      startDate = endDate
      break
      
    case 'last3days':
      // 近3日
      {
        const date = new Date(today)
        date.setDate(date.getDate() - 2)
        startDate = formatDate(date)
      }
      break
      
    case 'last7days':
      // 近7日
      {
        const date = new Date(today)
        date.setDate(date.getDate() - 6)
        startDate = formatDate(date)
      }
      break
      
    case 'last30days':
      // 近30日
      {
        const date = new Date(today)
        date.setDate(date.getDate() - 29)
        startDate = formatDate(date)
      }
      break
      
    case 'last3months':
      // 近3个月
      {
        const date = new Date(today)
        date.setMonth(date.getMonth() - 3)
        startDate = formatDate(date)
      }
      break
      
    case 'last6months':
      // 近半年
      {
        const date = new Date(today)
        date.setMonth(date.getMonth() - 6)
        startDate = formatDate(date)
      }
      break
      
    default:
      // 默认当日
      startDate = endDate
  }
  
  return {
    startDate,
    endDate
  }
}

/**
 * 解析日期字符串为Date对象
 * @param {string} dateStr - 日期字符串 (YYYY-MM-DD)
 * @returns {Date} Date对象
 */
export function parseDate(dateStr) {
  const [year, month, day] = dateStr.split('-').map(Number)
  return new Date(year, month - 1, day)
}

/**
 * 计算两个日期之间的天数差
 * @param {string} startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} endDate - 结束日期 (YYYY-MM-DD)
 * @returns {number} 天数差
 */
export function daysBetween(startDate, endDate) {
  const start = parseDate(startDate)
  const end = parseDate(endDate)
  const diffTime = Math.abs(end - start)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays
}

/**
 * 获取日期范围的描述文本
 * @param {string} timeRange - 时间范围选项
 * @returns {string} 描述文本
 */
export function getTimeRangeLabel(timeRange) {
  const labels = {
    today: '当日',
    last3days: '近3日',
    last7days: '近7日',
    last30days: '近30日',
    last3months: '近3个月',
    last6months: '近半年'
  }
  return labels[timeRange] || '当日'
}
