package com.zfkj.paybyweichat.kotlin

import android.text.TextUtils
import android.util.Log
import com.zfkj.paybyweichat.utils.LoggerUtils

/**
 * 项目名称：PayByWeiChat
 * 类描述：LoggerUtils 描述:
 * 创建人：songlijie
 * 创建时间：2018/5/18 14:47
 * 邮箱:814326663@qq.com
 */
object LoggerUtils {
    private val customTagPrefix = LoggerUtils::class.java.`package`.name //自定义tag 项目名字
    private var isbug = false

    fun isDebug(isDebug: Boolean) {
        isbug = isDebug
    }

    private fun generateTag(caller: StackTraceElement): String {
        var tag = "%s.%s(Line:%d)" // 占位符
        var callerClazzName = caller.className // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
        tag = String.format(tag, callerClazzName, caller.methodName, caller.lineNumber) // 替换
        tag = if (TextUtils.isEmpty(customTagPrefix)) tag else customTagPrefix + ":" + tag
        return tag
    }

    private fun getCallerStackTraceElement(): StackTraceElement {
        return Thread.currentThread().stackTrace[4]
    }

    fun d(content: String) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.d(tag, content)
    }

    fun v(content: String) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.v(tag, content)
    }

    fun e(content: String) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.e(tag, content)
    }

    fun i(content: String) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.i(tag, content)
    }

    fun w(content: String) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.w(tag, content)
    }

    fun e(content: String, throwable: Throwable) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.e(tag, content, throwable)
    }

    fun d(content: String, throwable: Throwable) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.d(tag, content, throwable)
    }

    fun i(content: String, throwable: Throwable) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.i(tag, content, throwable)
    }

    fun v(content: String, throwable: Throwable) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.v(tag, content, throwable)
    }

    fun w(content: String, throwable: Throwable) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.w(tag, content, throwable)
    }

    fun wtf(content: String, throwable: Throwable) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.wtf(tag, content, throwable)
    }

    fun wtf(content: String) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.wtf(tag, content)
    }

    fun wtf(throwable: Throwable) {
        if (!isbug) {
            return
        }
        val caller = getCallerStackTraceElement()
        val tag = generateTag(caller)
        Log.wtf(tag, throwable)
    }
}