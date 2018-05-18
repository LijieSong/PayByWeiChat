package com.zfkj.paybyweichat.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.zfkj.paybyweichat.kotlin.TestConfigs
import com.zfkj.paybyweichat.utils.LoggerUtils

/**
 * 项目名称：PayByWeiChat
 * 类描述：WXPayEntryActivity1 描述:
 * 创建人：songlijie
 * 创建时间：2018/5/18 14:22
 * 邮箱:814326663@qq.com
 */
class WXPayEntryActivity : Activity(), IWXAPIEventHandler {
    private var api: IWXAPI? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, TestConfigs.WX_APP_ID)
        api!!.handleIntent(intent, this@WXPayEntryActivity)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api!!.handleIntent(intent, this@WXPayEntryActivity)
    }

    override fun onReq(req: BaseReq) {

    }

    override fun onResp(resp: BaseResp) {
        LoggerUtils.e("onPayFinish, errCode = " + resp.errCode + "----errString:" + resp.errStr)
        if (resp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent(TestConfigs.PAY_BY_WECHAT_RESULT).putExtra(TestConfigs.KEY_PAY_WECHAT, resp.errCode.toString()))
            finish()
        }
    }
}