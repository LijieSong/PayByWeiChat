package com.zfkj.paybyweichat.java;

/**
 * 项目名称：PayByWeiChat
 * 类描述：TestConfigs 描述:
 * 创建人：songlijie
 * 创建时间：2018/5/18 14:15
 * 邮箱:814326663@qq.com
 */
public class TestConfigs {
    //申请的开发appid
    public static final String WX_APP_ID = "wx237117eb126a1d43";
    public static final String WX_APP_SECRET = "a38d49888990cfcd334cf80c3a96767a";
    //关于微信的信息授权地址
    public static final String WX_APP_OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    //关于微信的获取用户地址
    public static final String WX_APP_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    //微信支付的结果
    public static final String KEY_PAY_WECHAT = "KEY_PAY_WECHAT";
    //    微信,支付宝支付的Action
    public static final String PAY_BY_WECHAT_RESULT = "PAY_BY_WECHAT_RESULT";
    public static final String PAY_BY_WECHAT = "xxxxxxxxx" + "hanxuanWeixinPay";//微信充值
}
