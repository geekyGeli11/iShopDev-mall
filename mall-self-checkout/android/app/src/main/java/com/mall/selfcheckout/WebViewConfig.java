package com.mall.selfcheckout;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.os.Build;

public class WebViewConfig {
    
    public static void configureWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        
        // 启用JavaScript
        settings.setJavaScriptEnabled(true);
        
        // 启用DOM存储
        settings.setDomStorageEnabled(true);
        
        // 启用数据库存储
        settings.setDatabaseEnabled(true);
        
        // 设置缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // 设置用户代理
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent + " MallSelfCheckout/1.0");
        
        // 启用硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
        }
        
        // 设置WebView的布局算法为自适应
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

        // 禁用用户缩放，使用自动缩放
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        // 启用自动缩放到合适比例
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // 设置文本缩放为100%
        settings.setTextZoom(100);
        
        // 启用混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        
        // 禁用文件访问
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        
        // 启用地理位置
        settings.setGeolocationEnabled(true);
        
        // 设置媒体播放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
    }
}
