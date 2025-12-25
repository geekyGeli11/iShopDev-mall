// date.js
export function formatDate(date, fmt) {
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  }
  let o = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds()
  };
  for (let k in o) {
    if (new RegExp(`(${k})`).test(fmt)) {
      let str = o[k] + '';
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : padLeftZero(str));
    }
  }
  return fmt;
}

function padLeftZero(str) {
  return ('00' + str).substr(str.length);
}

export function str2Date(dateStr, separator) {
  if (!separator) {
    separator = "-";
  }
  let dateArr = dateStr.split(separator);
  let year = parseInt(dateArr[0]);
  let month;
  //处理月份为04这样的情况
  if (dateArr[1].indexOf("0") == 0) {
    month = parseInt(dateArr[1].substring(1));
  } else {
    month = parseInt(dateArr[1]);
  }
  let day = parseInt(dateArr[2]);
  let date = new Date(year, month - 1, day);
  return date;
}

/**
 * 格式化日期时间为常用格式
 * @param {Date|String|Number} date - 日期对象、日期字符串或时间戳
 * @param {String} format - 格式字符串，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns {String} 格式化后的日期字符串
 */
export function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return '-';
  
  // 转换为Date对象
  let dateObj;
  if (date instanceof Date) {
    dateObj = date;
  } else if (typeof date === 'string') {
    // 处理各种日期字符串格式
    if (date.includes('T')) {
      // ISO 格式
      dateObj = new Date(date);
    } else if (date.includes('-')) {
      // YYYY-MM-DD 格式
      dateObj = new Date(date.replace(/-/g, '/'));
    } else {
      dateObj = new Date(date);
    }
  } else if (typeof date === 'number') {
    // 时间戳
    dateObj = new Date(date);
  } else {
    return '-';
  }
  
  // 检查日期是否有效
  if (isNaN(dateObj.getTime())) {
    return '-';
  }
  
  const year = dateObj.getFullYear();
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const day = String(dateObj.getDate()).padStart(2, '0');
  const hours = String(dateObj.getHours()).padStart(2, '0');
  const minutes = String(dateObj.getMinutes()).padStart(2, '0');
  const seconds = String(dateObj.getSeconds()).padStart(2, '0');
  
  // 支持多种格式
  const formatMap = {
    'YYYY': year,
    'MM': month,
    'DD': day,
    'HH': hours,
    'mm': minutes,
    'ss': seconds,
    'M': dateObj.getMonth() + 1,
    'D': dateObj.getDate(),
    'H': dateObj.getHours(),
    'm': dateObj.getMinutes(),
    's': dateObj.getSeconds()
  };
  
  let result = format;
  Object.keys(formatMap).forEach(key => {
    result = result.replace(new RegExp(key, 'g'), formatMap[key]);
  });
  
  return result;
}

/**
 * 格式化为标准日期时间格式 YYYY-MM-DD HH:mm:ss
 * @param {Date|String|Number} date - 日期
 * @returns {String} 格式化后的日期字符串
 */
export function formatStandardDateTime(date) {
  return formatDateTime(date, 'YYYY-MM-DD HH:mm:ss');
}

/**
 * 格式化为日期格式 YYYY-MM-DD
 * @param {Date|String|Number} date - 日期
 * @returns {String} 格式化后的日期字符串
 */
export function formatDateOnly(date) {
  return formatDateTime(date, 'YYYY-MM-DD');
}

/**
 * 格式化为时间格式 HH:mm:ss
 * @param {Date|String|Number} date - 日期
 * @returns {String} 格式化后的时间字符串
 */
export function formatTimeOnly(date) {
  return formatDateTime(date, 'HH:mm:ss');
}

/**
 * 获取相对时间描述
 * @param {Date|String|Number} date - 日期
 * @returns {String} 相对时间描述
 */
export function getRelativeTime(date) {
  if (!date) return '-';
  
  const dateObj = new Date(date);
  const now = new Date();
  const diff = now.getTime() - dateObj.getTime();
  
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const month = 30 * day;
  const year = 365 * day;
  
  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前';
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前';
  } else if (diff < month) {
    return Math.floor(diff / day) + '天前';
  } else if (diff < year) {
    return Math.floor(diff / month) + '个月前';
  } else {
    return Math.floor(diff / year) + '年前';
  }
}

/**
 * 判断是否为今天
 * @param {Date|String|Number} date - 日期
 * @returns {Boolean} 是否为今天
 */
export function isToday(date) {
  if (!date) return false;
  
  const dateObj = new Date(date);
  const today = new Date();
  
  return dateObj.getFullYear() === today.getFullYear() &&
         dateObj.getMonth() === today.getMonth() &&
         dateObj.getDate() === today.getDate();
}

/**
 * 判断是否为昨天
 * @param {Date|String|Number} date - 日期
 * @returns {Boolean} 是否为昨天
 */
export function isYesterday(date) {
  if (!date) return false;
  
  const dateObj = new Date(date);
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  
  return dateObj.getFullYear() === yesterday.getFullYear() &&
         dateObj.getMonth() === yesterday.getMonth() &&
         dateObj.getDate() === yesterday.getDate();
}

/**
 * 获取时间范围描述
 * @param {Date|String|Number} startDate - 开始日期
 * @param {Date|String|Number} endDate - 结束日期
 * @returns {String} 时间范围描述
 */
export function getDateRange(startDate, endDate) {
  if (!startDate || !endDate) return '-';
  
  const start = formatDateOnly(startDate);
  const end = formatDateOnly(endDate);
  
  if (start === end) {
    return start;
  } else {
    return `${start} 至 ${end}`;
  }
}
