/**
 * 格式化倒计时时间
 * @param {Number} timestamp - 目标时间戳
 * @returns {Object} 包含小时、分钟、秒的对象
 */
export function formatCountdown(timestamp) {
  const now = new Date().getTime();
  const diff = timestamp - now;
  
  if (diff <= 0) {
    return { hours: '00', minutes: '00', seconds: '00', isEnd: true };
  }
  
  const seconds = Math.floor((diff / 1000) % 60);
  const minutes = Math.floor((diff / (1000 * 60)) % 60);
  const hours = Math.floor((diff / (1000 * 60 * 60)) % 24);
  
  return { 
    hours: hours < 10 ? '0' + hours : '' + hours, 
    minutes: minutes < 10 ? '0' + minutes : '' + minutes, 
    seconds: seconds < 10 ? '0' + seconds : '' + seconds,
    isEnd: false
  };
}

/**
 * 获取当天结束时间戳
 * @returns {Number} 当天23:59:59的时间戳
 */
export function getTodayEndTimestamp() {
  const now = new Date();
  const todayEnd = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59, 999);
  return todayEnd.getTime();
} 