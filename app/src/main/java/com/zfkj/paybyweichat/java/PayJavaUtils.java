package com.zfkj.paybyweichat.java;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zfkj.paybyweichat.utils.CommonUtils;
import com.zfkj.paybyweichat.utils.LoggerUtils;
import com.zfkj.paybyweichat.utils.OkHttpUtils;
import com.zfkj.paybyweichat.utils.Param;

import java.util.ArrayList;
import java.util.List;

public class PayJavaUtils {

    /**
     * 微信支付网络支付调用接口 获取到服务器返回的订单号
     *
     * @param aty    activity
     * @param object 从服务器请求下来的数据
     */
    private static void pay(Activity aty, JSONObject object) {
        PayReq req = new PayReq();
        req.appId = TestConfigs.WX_APP_ID;//APPID
        req.partnerId = object.getString("partnerid");//商户号
        req.prepayId = object.getString("prepayid");
        req.packageValue = object.getString("package");
        req.nonceStr = object.getString("noncestr");
        req.timeStamp = object.getString("timestamp");
        req.extData = object.getString("out_trade_no"); // optional
        req.sign = object.getString("sign");
        sendPayReq(aty, req);
    }

    /**
     * 请求微信支付
     *
     * @param aty    当前的activity
     * @param amount 钱数
     */
    public static void payByWeChat(final Activity aty, final String amount) {
        if (isWeixinAvilible(aty)) {//判断是否安装了微信
            List<Param> params = new ArrayList<>();
            params.add(new Param("userId", "1008611"));
            params.add(new Param("amount", amount));
//            params.add(new Param("phoneIp", CommonUtils.getHostIP()));
            new OkHttpUtils(aty).post(params, TestConfigs.PAY_BY_WECHAT, new OkHttpUtils.HttpCallBack() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    LoggerUtils.e("请求服务器的结果:" + jsonObject.toJSONString());
                    int code = jsonObject.getIntValue("code");
                    switch (code) {
                        case 1:
                            JSONObject data = jsonObject.getJSONObject("data");
                            pay(aty, data);
                            break;
                        default:
                            CommonUtils.showToastShort(aty, "申请失败");
                            break;

                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    CommonUtils.showToastShort(aty, "申请失败");
                }
            });
        } else {
            CommonUtils.showToastShort(aty, "您暂未安装微信,请先安装微信再试");
        }
    }

    /**
     * 发起请求
     *
     * @param activity 发起的activity
     * @param req      payReq
     */
    private static void sendPayReq(Activity activity, PayReq req, String appId) {
        if (TextUtils.isEmpty(appId)) {
            appId = TestConfigs.WX_APP_ID;
        }
        IWXAPI msgApi = WXAPIFactory.createWXAPI(activity, null);
        msgApi.registerApp(appId);
        msgApi.sendReq(req);
    }

    /**
     * 发起请求
     *
     * @param activity 发起的activity
     * @param req      payReq
     */
    private static void sendPayReq(Activity activity, PayReq req) {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(activity, null);
        msgApi.registerApp(TestConfigs.WX_APP_ID);
        msgApi.sendReq(req);
    }

    /**
     * 判断是否安装了微信
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    public interface PayBackListener {
        void paySuccess();

        void payFailed();

        void payCancled();
    }
}
