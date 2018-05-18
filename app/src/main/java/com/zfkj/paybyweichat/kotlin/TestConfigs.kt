package com.zfkj.paybyweichat.kotlin

/**
 * 项目名称：PayByWeiChat
 * 类描述：TestConfigs 描述:
 * 创建人：songlijie
 * 创建时间：2018/5/18 14:00
 * 邮箱:814326663@qq.com
 */
object TestConfigs {
    //    微信,支付宝支付的Action
    val PAY_BY_WECHAT_RESULT : String = "PAY_BY_WECHAT_RESULT"
    val PAY_BY_ALIPAY_RESULT: String  = "PAY_BY_ALIPAY_RESULT"
    val KEY_PAY_WECHAT: String  = "KEY_PAY_WECHAT"
    //申请的开发appid
    val WX_APP_ID: String  = "wx237117eb126a1d43"
    val WX_APP_SECRET: String  = "a38d49888990cfcd334cf80c3a96767a"
    //关于微信的信息授权地址
    val WX_APP_OAUTH2_URL: String  = "https://api.weixin.qq.com/sns/oauth2/access_token"
    //关于微信的获取用户地址
    val WX_APP_USERINFO_URL: String  = "https://api.weixin.qq.com/sns/userinfo"
    val PAY_BY_WECHAT = "xxxxxxxxx" + "hanxuanWeixinPay"//微信充值
}