package com.zfkj.paybyweichat.kotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import com.zfkj.paybyweichat.R
import com.zfkj.paybyweichat.java.PayKotlinUtils
import com.zfkj.paybyweichat.utils.CommonUtils

class Main2Activity : AppCompatActivity(), OnClickListener {
    private var btn_pay: Button? = null
    private var reciver: PayBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
        setListener()
    }

    private fun initView() {
        btn_pay = findViewById(R.id.btn_pay) as Button
    }

    private fun initData() {
        reciver = PayBroadcastReceiver()
        val fileter = IntentFilter()
        fileter.addAction(TestConfigs.PAY_BY_WECHAT_RESULT)
        LocalBroadcastManager.getInstance(baseContext).registerReceiver(reciver, fileter)
    }

    private fun setListener() {
        btn_pay!!.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        val id = p0?.id
        when (id) {
            R.id.btn_pay ->
                PayKotlinUtils.payByWeChat(this@Main2Activity, "0.01")
        }
    }

    internal class PayBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action.equals(TestConfigs.PAY_BY_WECHAT_RESULT)) {
                val wxpay = p1?.getStringExtra(TestConfigs.KEY_PAY_WECHAT)
                when (wxpay) {
                    "0" -> {
                        CommonUtils.showToastShort(p0, "支付成功")
                    }
                    "1" -> {
                        CommonUtils.showToastShort(p0, "支付失败")
                    }
                    "2" -> {
                        CommonUtils.showToastShort(p0, "支付取消")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (reciver != null) {
            LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(reciver)
        }
        super.onDestroy()
    }
}
