package com.zfkj.paybyweichat.kotlin

/**
 * 项目名称：PayByWeiChat
 * 类描述：Param 描述:
 * 创建人：songlijie
 * 创建时间：2018/5/18 15:10
 * 邮箱:814326663@qq.com
 */
data class Param(val key: String,val value: String) {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun toString(): String {
        return super.toString()
    }
}