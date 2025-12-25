/**
 * 富文本内容处理工具
 * 用于处理富文本内容，确保图片和内容不会超出屏幕
 */

/**
 * 处理HTML内容，确保图片宽度不超出屏幕
 * @param {String} html 原始HTML内容
 * @returns {String} 处理后的HTML内容
 */
export function processHtmlContent(html) {
  if (!html) return '';
  
  // 先将所有图片标签属性修改为响应式
  let processedHtml = html.replace(/<img(.*?)>/gi, (match, attributes) => {
    // 移除所有可能导致溢出的宽度或高度属性
    let cleanAttrs = attributes
      .replace(/width=["']\d+["']/gi, '')
      .replace(/height=["']\d+["']/gi, '')
      .replace(/style=["'].*?["']/gi, '');
      
    // 添加严格控制尺寸的样式
    return `<img${cleanAttrs} style="max-width:100% !important; width:auto !important; height:auto !important; display:block !important; margin:0 auto !important;">`; 
  });
  
  // 将所有图片包裹在限宽容器内
  processedHtml = processedHtml.replace(/<img(.*?)>/gi, '<div style="width:100%; overflow:hidden; text-align:center;"><img$1></div>');
  
  // 所有段落也添加宽度限制
  processedHtml = processedHtml.replace(/<p(.*?)>/gi, '<p$1 style="max-width:100%; overflow-wrap:break-word; overflow:hidden; box-sizing:border-box;">');
  
  return processedHtml;
}

/**
 * 包含CSS样式的富文本样式
 * 用于在页面样式中引入
 */
export const richTextStyle = `
.rich-text-wrapper {
  width: 100%;
  overflow: hidden;
  box-sizing: border-box;
  max-width: 690rpx;
}

.rich-text-content {
  width: 100%;
  overflow-x: hidden;
  word-break: break-word;
  box-sizing: border-box;
}

/* 针对富文本内容的样式，不使用深度选择器 */
.rich-text-content image,
.rich-text-content img {
  max-width: 100% !important;
  height: auto !important;
  display: block !important;
  margin: 0 auto !important;
  width: auto !important;
  box-sizing: border-box !important;
}

.rich-text-content p {
  margin: 0 !important;
  padding: 0 !important;
  width: 100% !important;
  overflow-x: hidden !important;
  box-sizing: border-box !important;
}

.rich-text-content * {
  max-width: 100% !important;
  box-sizing: border-box !important;
  overflow-wrap: break-word !important;
}
`; 