package com.zfkj.paybyweichat.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zfkj.paybyweichat.java.TestConfigs;
import com.zfkj.paybyweichat.utils.LoggerUtils;

/**
 *
 */
public class WXPayEntryActivity1 extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, TestConfigs.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(final BaseResp resp) {
        LoggerUtils.e("onPayFinish, errCode = " + resp.errCode + "----errString:" + resp.errStr);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(TestConfigs.PAY_BY_WECHAT_RESULT).putExtra(TestConfigs.KEY_PAY_WECHAT, String.valueOf(resp.errCode)));
            finish();
        }
    }
}