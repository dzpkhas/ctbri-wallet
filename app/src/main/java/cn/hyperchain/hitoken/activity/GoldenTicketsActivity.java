package cn.hyperchain.hitoken.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rthtech.ble.Controller;
import com.rthtech.ble.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import butterknife.BindView;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.ble.Ble;
import cn.hyperchain.hitoken.ble.Util;
import cn.hyperchain.hitoken.ble.crypto.Hash;
import cn.hyperchain.hitoken.ble.key.RawTransaction;
import cn.hyperchain.hitoken.ble.key.TransactionEncoder;
import cn.hyperchain.hitoken.utils.NumberUtil;
import cn.hyperchain.hitoken.utils.SPHelper;
import cn.hyperchain.hitoken.view.WebViewWithAndroid;

import static cn.hyperchain.hitoken.view.WebViewWithAndroid.YES;


public class GoldenTicketsActivity extends BaseActivity implements com.rthtech.ble.Callback{

//    @BindView(R.id.webView)
    static WebView webView;

    private ValueCallback<Uri> mUploadCallbackBelow;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    String accountId;//账户id
    byte[] hash;//交易摘要哈希值

    //蓝牙设备和卡
   private  static     Controller mController = null;
   private Ble ble;

    //网页加载进度条
    private ProgressBar pg;

    //卡链接蓝牙次数
    private int cardTimes = 0;

    // 0 未寻卡 1 已寻卡
    private int findCard = 0;


    public static Handler handler =  new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == YES){
                Log.d("handler","执行前");
                webView.evaluateJavascript("javascript:returnResult('"
                        + msg.obj.toString() + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
                Log.d("handler","执行后");
                mController.disconnect();


            }
        }
    };

    String url = "http://132.232.101.233:8080/dist";//阿里云前端url
//  String url = "file:///android_asset/javaScript.html";//本地测试用url
//  String url = "http://132.232.101.233:8080/test/";//阿里云功能测试url


    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      setConfigCallback((WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        setContentView(R.layout.activity_golden_tickets);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//native软键盘不改变布局

        webView = findViewById(R.id.webView);
        pg = findViewById(R.id.progressBar1);
        webView.setVerticalScrollbarOverlay(true);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //设置WebView支持JavaScript
        settings.setJavaScriptEnabled(true);
        //允许js弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //设置可以访问文件
        settings.setAllowFileAccess(true);
        settings.setDefaultTextEncodingName("utf-8");
        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        //取消缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //允许webview调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }

        //内嵌页面无法在请求时携带外面页面的cookie
        // 原因在于内嵌页面和外部页面域名不同，导致cookie存在跨域；因此内部页面无法获取外部页面的cookie。
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            CookieManager.getInstance().setAcceptThirdPartyCookies( webView,true);
        }

        String cookieString  = (String) SPHelper.get(this,"token","");
        CookieSyncManager.createInstance(webView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, cookieString);
        CookieSyncManager.getInstance().sync();

        //在js中调用本地java方法
        mController = com.rthtech.ble.Factory.getController(GoldenTicketsActivity.this);
        webView.addJavascriptInterface(new WebViewWithAndroid(getApplicationContext(),webView,GoldenTicketsActivity.this,mController), "Android");


        //添加客户端支持
        webView.setWebChromeClient(new WebChromeClient(){

            //加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress==100){
                    pg.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    pg.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg.setProgress(newProgress);//设置进度值
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                // (1)该方法回b调时说明版本API >= 21，此时将结果赋值给 mUploadCallbackAoveL，使之 != null
                mUploadCallbackAboveL = filePathCallback;
                takePhoto();
                return true;
            }
        });

        //禁止跳到手机自带浏览器
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {

            }
        } );

        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webView.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键时的操作
                                webView.goBack();   //后退
                                //webview.goForward();//前进
                                return true;    //已处理
                            }
                        }
                        return false;
                    }
        });
        webView.loadUrl(url);
    }





    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onFoundDevice(String s, String s1) {

    }

    @Override
    public void onStateChange(int old_state, int new_state, int error) {
        if (new_state == Data.STATE_SCANNING) {
            ble.log("scanning device...");
        } else if (new_state == Data.STATE_CONNECTING_DEVICE) {
            ble.log("conn: " + mController.getDeviceName());
            ble.log("addr: " + mController.getDeviceAddress());
        } else if (new_state == Data.STATE_CONNECTING_SERVICE) {
        } else if (new_state == Data.STATE_END) {
            if (ble.mScanMode) {
                ble.log("scanning stop!");
                ble.mScanMode = false;
            } else {
                ble.log("disconnected! code=" + ble.errDesc(error));
//                showToast("设备断开连接 code=" + ble.errDesc(error));
//                Toast.makeText(activity,"设备断开连接 code=" + ble.errDesc(error),Toast.LENGTH_SHORT).show();
//                hideDialog();
//                finish();
            }
        } else if (new_state == Data.STATE_READY) {
            ble.log("connect ok!");
            //连接成功后做配对
            ble.pair();

            cardTimes = 0;
        }
    }

    @Override
    public void onWrite(byte[] bytes, int i) {

    }

    @Override
    public void onResult(byte[] bytes, int i) {

    }

    @Override
    public void onResult(Result result) {

    }





    //最后在OnActivityResult中接受返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            //针对5.0以上, 以下区分处理方法
            if (mUploadCallbackBelow != null) {
                chooseBelow(resultCode, data);
            } else if (mUploadCallbackAboveL != null) {
                chooseAbove(resultCode, data);
            } else {
                Toast.makeText(this, "发生错误", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**处理拍照/选择的文件*/
    private File handleFile(File file)
    {
        DisplayMetrics dMetrics = getResources().getDisplayMetrics();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        System.out.println("  imageWidth = " + imageWidth + " imageHeight = " + imageHeight);
        int widthSample = (int) (imageWidth / (dMetrics.density * 90));
        int heightSample = (int) (imageHeight / (dMetrics.density * 90));
        System.out.println("widthSample = " + widthSample + " heightSample = " + heightSample);
        options.inSampleSize = widthSample < heightSample ? heightSample : widthSample;
        options.inJustDecodeBounds = false;
        Bitmap newBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        System.out.println("newBitmap.size = " + newBitmap.getRowBytes() * newBitmap.getHeight());
        File handleFile = new File(file.getParentFile(), "upload.png");
        try
        {
            if (newBitmap.compress(Bitmap.CompressFormat.PNG, 50, new FileOutputStream(handleFile)))
            {
                System.out.println("保存图片成功");
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return handleFile;

    }



    private void takePhoto() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), 100);
    }

    /**
     * Android API >= 21(Android 5.0) 版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    private void chooseAbove(int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对从文件中选图片的处理, 区别是一个返回的URI, 一个是URI[]
                Uri[] results;
                Uri uriData = data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    mUploadCallbackAboveL.onReceiveValue(results);
                } else {
                    mUploadCallbackAboveL.onReceiveValue(null);
                }
            } else {
               // mUploadCallbackAboveL.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            mUploadCallbackAboveL.onReceiveValue(null);
        }
        mUploadCallbackAboveL = null;
    }

    private void updatePhotos() {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
       // intent.setData(imageUri);
        sendBroadcast(intent);
    }

    /**
     * Android API < 21(Android 5.0)版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    private void chooseBelow(int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对文件路径处理
                Uri uri = data.getData();
                if (uri != null) {
                    mUploadCallbackBelow.onReceiveValue(uri);
                } else {
                    mUploadCallbackBelow.onReceiveValue(null);
                }
            } else {
                // 以指定图像存储路径的方式调起相机，成功后返回data为空
             //   mUploadCallbackBelow.onReceiveValue(imageUri);
            }
        } else {
            mUploadCallbackBelow.onReceiveValue(null);
        }
        mUploadCallbackBelow = null;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewParent parent = webView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(webView);
        }
        webView.removeAllViews();
        webView.destroy();
        if(mController != null) {
            mController.term();
            mController.disconnect();
        }
//        setConfigCallback(null);
        finish();
    }


    //防止Webview 造成OOM
    public void setConfigCallback(WindowManager windowManager) {

        try {

            Field field = WebView.class.getDeclaredField("mWebViewCore");

            field = field.getType().getDeclaredField("mBrowserFrame");

            field = field.getType().getDeclaredField("sConfigCallback");

            field.setAccessible(true);

            Object configCallback = field.get(null);


            if (null == configCallback) {

                return;

            }

            field = field.getType().getDeclaredField("mWindowManager");

            field.setAccessible(true);

            field.set(configCallback, windowManager);

        } catch(Exception e) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        webView.loadUrl(url);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
