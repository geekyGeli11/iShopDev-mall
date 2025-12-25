package com.macro.mall.selfcheck;

import com.macro.mall.selfcheck.service.impl.SelfcheckProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

/**
 * 条码格式测试
 */
@SpringBootTest
public class BarcodeFormatTest {

    // 条码格式正则表达式（复制自SelfcheckProductServiceImpl）
    private static final Pattern EAN13_PATTERN = Pattern.compile("^\\d{13}$");
    private static final Pattern EAN8_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern EAN9_PATTERN = Pattern.compile("^\\d{9}$");
    private static final Pattern EAN10_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern UPC_PATTERN = Pattern.compile("^\\d{12}$");

    @Test
    public void testBarcodeFormats() {
        // 测试各种条码格式
        String[] testBarcodes = {
            "020306001",      // 9位条码（图片中的条码）
            "6920036001",     // 10位条码
            "12345678",       // 8位条码
            "1234567890123",  // 13位条码
            "123456789012",   // 12位条码
            "abc123",         // 无效格式
            "",               // 空字符串
            "12345"           // 5位数字
        };

        for (String barcode : testBarcodes) {
            String detectedType = validateBarcodeFormat(barcode);
            System.out.println("条码: " + barcode + " -> 类型: " + detectedType);
        }
    }

    /**
     * 验证条码格式（复制自SelfcheckProductServiceImpl）
     */
    private String validateBarcodeFormat(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return "UNKNOWN";
        }

        barcode = barcode.trim();

        // 检查EAN-13商品条码
        if (EAN13_PATTERN.matcher(barcode).matches()) {
            return "BARCODE";
        }

        // 检查EAN-8商品条码
        if (EAN8_PATTERN.matcher(barcode).matches()) {
            return "BARCODE";
        }

        // 检查EAN-9商品条码
        if (EAN9_PATTERN.matcher(barcode).matches()) {
            return "BARCODE";
        }

        // 检查EAN-10商品条码
        if (EAN10_PATTERN.matcher(barcode).matches()) {
            return "BARCODE";
        }

        // 检查UPC-A条码
        if (UPC_PATTERN.matcher(barcode).matches()) {
            return "BARCODE";
        }

        return "UNKNOWN";
    }
}
