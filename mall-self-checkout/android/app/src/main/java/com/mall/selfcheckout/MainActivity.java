package com.mall.selfcheckout;

import android.os.Bundle;
import android.webkit.WebView;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 配置WebView以实现自适应显示
        configureWebView();
    }

    private void configureWebView() {
        // 获取Capacitor的WebView
        WebView webView = getBridge().getWebView();
        if (webView != null) {
            // 应用自定义WebView配置
            WebViewConfig.configureWebView(webView);

            // 计算并设置自动缩放比例
            setAutoScale(webView);
        }
    }

    private void setAutoScale(WebView webView) {
        // 获取屏幕尺寸
        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int designWidth = 1080; // 设计稿宽度

        // 计算缩放比例
        float scale = (float) screenWidth / designWidth;

        // 设置初始缩放比例
        webView.setInitialScale((int) (scale * 100));
    }
}
