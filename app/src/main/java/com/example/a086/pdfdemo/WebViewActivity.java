package com.example.a086.pdfdemo;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by 086 on 2018/1/25.
 */

public class WebViewActivity extends AppCompatActivity {
    WebView pdfViewerWeb;
//    private String downloadUrl="http://file.chmsp.com.cn/colligate/file/00100000224821.pdf";
    private String downloadUrl="http://wyyt-test.oss-cn-huhehaote.aliyuncs.com/wyyt-doc/2019/01/03/null_jf3i3g897i_1901021114057216857.pdf_1434692814287948830.pdf";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        pdfViewerWeb=findViewById(R.id.webview);

        WebSettings settings = pdfViewerWeb.getSettings();
        settings.setSavePassword(false);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        pdfViewerWeb.addJavascriptInterface(new AndroidtoJs(), "android");//AndroidtoJS类对象映射到js的test对象
        pdfViewerWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;

            }

        });
        pdfViewerWeb.setWebChromeClient(new WebChromeClient());


        new Thread(new Runnable() {
            @Override
            public void run() {
                final String download = download();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//api >= 19

                            pdfViewerWeb.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + download);

                        }
                    }
                });
            }
        }).start();
    }
    public class AndroidtoJs extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void back() {
//            WebViewActivity.this.finish();
        }
    }
    //下载具体操作
    private String download() {
        try {
            URL url = new URL(downloadUrl);
            //打开连接
            URLConnection conn = url.openConnection();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            int contentLength = conn.getContentLength();
            //创建文件夹 MyDownLoad，在存储卡下
            String dirName = Environment.getExternalStorageDirectory() + "/MyDownLoad/";
            File file = new File(dirName);
            //不存在创建
            if (!file.exists()) {
                file.mkdir();
            }
            //下载后的文件名
            final String fileName = dirName + "invoice.pdf";
            File file1 = new File(fileName);
            if (file1.exists()) {
                file1.delete();
            }
            //创建字节流
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(fileName);
            //写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            //完成后关闭流
            os.close();
            is.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
