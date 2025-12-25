package com.macro.mall.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用的Excel导出工具，基于Apache POI（SXSSF）实现。
 * <p>
 * 使用示例：
 * <pre>
 * LinkedHashMap<String, String> headerMap = new LinkedHashMap<>();
 * headerMap.put("username", "用户名");
 * ...
 * List<Map<String, Object>> data = new ArrayList<>();
 * ExcelExportUtil.exportExcel("用户列表", "sheet1", headerMap, data, response);
 * </pre>
 * <p>
 * 注意：
 * 1. 此工具采用 SXSSFWorkbook 写入，支持大数据量导出；
 * 2. 自动处理空值、自动列宽；
 * 3. 默认导出 .xlsx 格式。
 *
 * @author macro
 */
public class ExcelExportUtil {

    private ExcelExportUtil() {
        // 工具类不需要实例化
    }

    /**
     * 导出 Excel 文件到响应流。
     *
     * @param fileName    下载的文件名（不包含扩展名）
     * @param sheetName   工作表名称
     * @param headerAlias 列标题映射：key 为 Map 中的字段名，value 为列标题
     * @param dataList    数据内容，每条数据用 Map 表示，key 对应 headerAlias 中的 key
     * @param response    HttpServletResponse
     */
    public static void exportExcel(String fileName,
                                   String sheetName,
                                   LinkedHashMap<String, String> headerAlias,
                                   List<? extends Map<String, Object>> dataList,
                                   HttpServletResponse response) throws IOException {
        // 创建工作簿，使用SXSSF提高大数据性能
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        int rowIndex = 0;
        // 1. 写入标题行
        Row headerRow = sheet.createRow(rowIndex++);
        int colIndex = 0;
        for (String header : headerAlias.values()) {
            Cell cell = headerRow.createCell(colIndex++);
            cell.setCellValue(header);
        }

        // 2. 写入数据行
        if (dataList != null) {
            for (Map<String, Object> data : dataList) {
                Row dataRow = sheet.createRow(rowIndex++);
                colIndex = 0;
                for (String key : headerAlias.keySet()) {
                    Cell cell = dataRow.createCell(colIndex++);
                    Object value = data.get(key);
                    if (value == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(String.valueOf(value));
                    }
                }
            }
        }

        // 3. 自动列宽
        colIndex = 0;
        for (int i = 0; i < headerAlias.size(); i++) {
            sheet.autoSizeColumn(i);
            // 调整列宽（避免过窄）
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, currentWidth + 1024);
        }

        // 4. 设置响应信息并写出
        String finalFileName = fileName + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(finalFileName, "UTF-8"));
        try (ServletOutputStream os = response.getOutputStream()) {
            workbook.write(os);
            os.flush();
        } finally {
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
        }
    }
} 