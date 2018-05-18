package com.zfkj.paybyweichat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zfkj.paybyweichat.java.TestConfigs;
import com.zfkj.paybyweichat.utils.CommonUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_pay;
    private PayBroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        btn_pay = (Button) findViewById(R.id.btn_pay);
    }

    private void initData() {
        receiver = new PayBroadcastReceiver();
        IntentFilter fileter = new IntentFilter();
        fileter.addAction(TestConfigs.PAY_BY_WECHAT_RESULT);
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, fileter);
    }

    private void setListener() {
        btn_pay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 微信支付监听
     */
    private class PayBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TestConfigs.PAY_BY_WECHAT_RESULT.equals(intent.getAction())) {
                String wxpay = intent.getStringExtra(TestConfigs.KEY_PAY_WECHAT);
                switch (wxpay) {
                    case "0"://此处是支付成功的后的回调
                        CommonUtils.showToastShort(context, "支付成功");
                        break;
                    case "-1":
                        CommonUtils.showToastShort(context, "支付失败");
                        break;
                    case "-2":
                        CommonUtils.showToastShort(context, "支付取消");
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(receiver);
        }
        super.onDestroy();
    }
}
