import html2canvas from 'html2canvas';

// 适配UniApp的html2canvas包装函数
export default function uniappHtml2Canvas(element, options = {}) {
  return new Promise((resolve, reject) => {
    try {
      // UniApp环境下的元素转换
      const convertedElement = element;
      
      // 设置默认选项
      const defaultOptions = {
        useCORS: true,
        scale: 2,
        logging: false,
        ...options
      };
      
      // 执行html2canvas
      html2canvas(convertedElement, defaultOptions)
        .then(canvas => {
          // 转换canvas为临时文件路径
          const tempFilePath = canvas.toDataURL('image/png');
          resolve(tempFilePath);
        })
        .catch(error => {
          console.error('HTML2Canvas error:', error);
          reject(error);
        });
    } catch (error) {
      console.error('UniApp HTML2Canvas wrapper error:', error);
      reject(error);
    }
  });
} 