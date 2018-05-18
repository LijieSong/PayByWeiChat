package com.zfkj.paybyweichat.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zfkj.paybyweichat.R;

import java.io.File;
import java.io.FileOutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class CommonUtils {
    private static final String TAG = "CommonUtils";
    private static Toast toast;
    private static Dialog progressDialog;

    /**
     * 获取直播ID
     *
     * @return
     */
    public static String getAnyRTCId() {
        return (int) ((Math.random() * 9 + 1) * 100000) + "";
    }

    /**
     * check if network avalable
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    /**
     * 插入一个字符
     *
     * @param editText
     * @param data
     */
    public static void addChar(EditText editText, String data) {
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        editable.insert(index, data);
    }

    /**
     * 删除一个字符
     *
     * @param editText
     */
    public static void deleteChar(EditText editText) {
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        editable.delete(index - 1, index);
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        for (RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getClassName().contains(className)) { // 说明它已经启动了
                return true;
            }
        }
        return false;
    }

    public static boolean isInLauncher(Context context, String className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        if (name.equals(className)) {
            return true;
        }
        return false;
    }

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * array2jsonList
     *
     * @param jsonArray
     * @param objectList
     * @return
     */
    public static void arrayToJsonList(JSONArray jsonArray, List<JSONObject> objectList) {
        if (jsonArray == null && jsonArray.size() == 0) {
            return;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (!objectList.contains(object)) {
                objectList.add(object);
            }
        }
    }

    /**
     * get top activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    public static int getSupportSoftInputHeight(Activity activity) {
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight(activity);
            Log.d("observeSoftKeyboard---9", String.valueOf(getSoftButtonsBarHeight(activity)));
        }
        if (softInputHeight < 0) {
            Log.w("EmotionInputDetector", "Warning: value of softInputHeight is below zero!");
        }

        return softInputHeight;
    }

    /**
     * @param bMute 值为true时为关闭背景音乐。
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        if (context == null) {
            Log.d(TAG, "-----context is null");
            return false;
        }
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            if (bMute) {
                int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            } else {
                int result = am.abandonAudioFocus(null);
                bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            }
        }
        return bool;
    }

    private static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        }
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    //键盘显示监听
    public static void observeSoftKeyboard(final Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            int previousKeyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - rect.bottom;

                if (Build.VERSION.SDK_INT >= 20) {
                    // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
                    keyboardHeight = keyboardHeight - getSoftButtonsBarHeight(activity);

                }

                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    listener.onSoftKeyBoardChange(keyboardHeight, !hide, this);
                }

                previousKeyboardHeight = height;

            }
        });
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean visible, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener);
    }

    public static boolean isChinese(String str) {

        char[] chars = str.toCharArray();
        boolean isGB2312 = false;
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes = ("" + chars[i]).getBytes();
            if (bytes.length == 2) {
                int[] ints = new int[2];
                ints[0] = bytes[0] & 0xff;
                ints[1] = bytes[1] & 0xff;
                if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40 && ints[1] <= 0xFE) {
                    isGB2312 = true;
                    break;
                }
            }
        }
        return isGB2312;
    }

    public static boolean test(String s) {
        char c = s.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean check(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, String msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, Object msg) {
        if (context == null) {
            return;
        }
        if (msg instanceof String) {
            showToastShort(context, (String) msg);
        } else if (msg instanceof Integer) {
            showToastShort(context, (int) msg);
        }
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, int msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 长吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastLong(Context context, String msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastLong(Context context, Object msg) {
        if (context == null) {
            return;
        }
        if (msg instanceof String) {
            showToastLong(context, (String) msg);
        } else if (msg instanceof Integer) {
            showToastLong(context, (int) msg);
        }
    }

    /**
     * 长吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastLong(Context context, int msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }


    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {
        String hostIp = "127.0.0.1";
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i(TAG, "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }


    /**
     * 保存图片到相册
     *
     * @param context
     * @param bmp
     * @return
     */
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        LoggerUtils.e("---传进来的:" + context + "---图片:" + bmp.getWidth() + "---高:" + bmp.getHeight());
        if (context == null) {
            return false;
        }
        if (bmp == null) {
            return false;
        }
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "red");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String filePath = appDir.getAbsolutePath();
        String fileName = "red_" + System.currentTimeMillis() + ".png";
        File file = new File(filePath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return false;
        }
        //通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
        return true;
    }

    /**
     * 强制弹起键盘
     *
     * @param context
     * @param editText
     */
    public static void showSoftInput(Context context, View editText) {
        if (context == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);//强制显示键盘
    }

    /**
     * 强制隐藏键盘
     *
     * @param context
     * @param view
     */
    public static void hintSoftInput(Context context, View view) {
        if (context == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }
}
