package com.macro.mall.util;

import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV 导出工具，支持流式大批量写入。
 */
public class CsvExportUtil {

    private CsvExportUtil() {
    }

    public static void exportCsv(String filePrefix,
                                 LinkedHashMap<String, String> headerAlias,
                                 List<Map<String, Object>> firstBatch,
                                 DataSupplier supplier,
                                 HttpServletResponse response) throws IOException {
        String fileName = filePrefix + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".csv";
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        // 写入 UTF-8 BOM，避免 Excel 打开出现中文乱码
        var os = response.getOutputStream();
        os.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            // header
            writer.writeNext(headerAlias.values().toArray(new String[0]));
            // first batch
            writeBatch(writer, headerAlias, firstBatch);
            // next batch via supplier
            List<Map<String, Object>> batch;
            while ((batch = supplier.get()) != null && !batch.isEmpty()) {
                writeBatch(writer, headerAlias, batch);
                writer.flush();
            }
        }
    }

    private static void writeBatch(CSVWriter writer, LinkedHashMap<String, String> headerAlias, List<Map<String, Object>> batch) {
        for (Map<String, Object> row : batch) {
            String[] line = new String[headerAlias.size()];
            int i = 0;
            for (String key : headerAlias.keySet()) {
                Object val = row.get(key);
                line[i++] = val == null ? "" : String.valueOf(val);
            }
            writer.writeNext(line, false);
        }
    }

    /**
     * 供 CSV 写入器继续拉取数据的回调。
     */
    @FunctionalInterface
    public interface DataSupplier {
        List<Map<String, Object>> get();
    }
} 