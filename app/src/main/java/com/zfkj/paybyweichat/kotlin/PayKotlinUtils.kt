package com.zfkj.paybyweichat.java


import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.zfkj.paybyweichat.kotlin.LoggerUtils
import com.zfkj.paybyweichat.kotlin.OkHttpUtils
import com.zfkj.paybyweichat.kotlin.Param
import com.zfkj.paybyweichat.kotlin.TestConfigs
import com.zfkj.paybyweichat.utils.CommonUtils
import java.util.ArrayList

object PayKotlinUtils {

    /**
     * 微信支付网络支付调用接口 获取到服务器返回的订单号

     * @param aty    activity
     * *
     * @param object 从服务器请求下来的数据
     */
    private fun pay(aty: Activity, result: JSONObject) {
        val req = PayReq()
        req.appId = TestConfigs.WX_APP_ID//APPID
        req.partnerId = result.getString("partnerid")//商户号
        req.prepayId = result.getString("prepayid")
        req.packageValue = result.getString("package")
        req.nonceStr = result.getString("noncestr")
        req.timeStamp = result.getString("timestamp")
        req.extData = result.getString("out_trade_no") // optional
        req.sign = result.getString("sign")
        sendPayReq(aty, req)
    }

    /**
     * 请求微信支付

     * @param aty 当前的activity
     * *
     * @param amount 钱数
     */
    fun payByWeChat(aty: Activity, amount: String) {
        if (isWeixinAvilible(aty)) {//判断是否安装了微信
            val params = ArrayList<Param>()
            params.add(Param("userId", "1008611"))
            params.add(Param("amount", amount))
            OkHttpUtils.init(aty)
            OkHttpUtils.post(params, TestConfigs.PAY_BY_WECHAT, object : OkHttpUtils.HttpCallBack {
                override fun onResponse(jsonObject: JSONObject) {
                    LoggerUtils.e("请求服务器下来的结果:" + jsonObject.toJSONString())
                    val code = jsonObject.getIntValue("code")
                    when (code) {
                        1 -> {
                            val data = jsonObject.getJSONObject("data")
                            pay(aty, data)
                        }
                        else -> CommonUtils.showToastShort(aty, "申请失败")
                    }
                }

                override fun onFailure(errorMsg: String) {
                    CommonUtils.showToastShort(aty, "申请失败")
                }
            })
        } else {
            CommonUtils.showToastShort(aty, "您暂未安装微信,请先安装微信再试")
        }
    }

    /**
     * 发起请求

     * @param activity 发起的activity
     * *
     * @param req      payReq
     */
    private fun sendPayReq(activity: Activity, req: PayReq, appId: String) {
        var appId = appId
        if (TextUtils.isEmpty(appId)) {
            appId = TestConfigs.WX_APP_ID
        }
        val msgApi = WXAPIFactory.createWXAPI(activity, null)
        msgApi.registerApp(appId)
        msgApi.sendReq(req)
    }

    /**
     * 发起请求

     * @param activity 发起的activity
     * *
     * @param req      payReq
     */
    private fun sendPayReq(activity: Activity, req: PayReq) {
        val msgApi = WXAPIFactory.createWXAPI(activity, null)
        msgApi.registerApp(TestConfigs.WX_APP_ID)
        msgApi.sendReq(req)
    }

    /**
     * 判断是否安装了微信

     * @param context
     * *
     * @return
     */
    fun isWeixinAvilible(context: Context): Boolean {
        val packageManager = context.packageManager// 获取packagemanager
        val pinfo = packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn == "com.tencent.mm") {
                    return true
                }
            }
        }
        return false
    }
}
