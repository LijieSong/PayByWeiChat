package com.zfkj.paybyweichat.kotlin

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Message
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.zfkj.paybyweichat.utils.CommonUtils
import com.zfkj.paybyweichat.utils.Param
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLConnection
import java.util.concurrent.TimeUnit

/**
 * 项目名称：PayByWeiChat
 * 类描述：OkHttpUitls 描述:
 * 创建人：songlijie
 * 创建时间：2018/5/18 14:51
 * 邮箱:814326663@qq.com
 */
object OkHttpUitls {
    private var mContext: Context? = null
    private var okHttpClient: OkHttpClient? = null
    private val RESULT_ERROR = 1000
    private val RESULT_SUCESS = 2000
    private val RESULT_SUCESS_FROM_GET = 3000
    private var httpCallBack: HttpCallBack? = null

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val reusltCode = msg.what
            when (reusltCode) {
                RESULT_ERROR -> {
                    httpCallBack!!.onFailure(msg.obj as String)
                    LoggerUtils.e("result----->" + msg.obj as String)
                }
                RESULT_SUCESS -> {
                    val result = msg.obj as String
                    LoggerUtils.e("result----->" + result)
                    try {
                        val jsonObject = JSONObject.parseObject(result)
                        httpCallBack!!.onResponse(jsonObject)
                    } catch (e: JSONException) {
                        httpCallBack!!.onFailure(msg.obj as String)
                    }

                }
                RESULT_SUCESS_FROM_GET -> {
                    val resultfromget = msg.obj as String
                    LoggerUtils.e("resultfromget----->" + resultfromget)
                    try {
                        val jsonObject = JSONObject.parseObject(resultfromget)
                        httpCallBack!!.onResponse(jsonObject)
                    } catch (e: JSONException) {
                        httpCallBack!!.onFailure(msg.obj as String)
                    }

                }
            }

        }
    }

    fun OkHttpUitls(context: Context) {
        this.mContext = context
        okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15000L, TimeUnit.MILLISECONDS)
                .readTimeout(15000L, TimeUnit.MILLISECONDS)
                .build()

    }


    //纯粹键值对post请求
    fun post(params: List<Param>, url: String, httpCallBack: HttpCallBack) {
        this.httpCallBack = httpCallBack
        val bodyBulder = FormBody.Builder()
        for (param in params) {
            bodyBulder.add(param.key, param.value)
            LoggerUtils.e("param.getKey()----->" + param.key)
            LoggerUtils.e("param.getValue()----->" + param.value)
        }
        val requestBody = bodyBulder.build()
        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
        startRequest(request)

    }

    /**
     * get请求
     * @param url
     * *
     * @param callBack
     */
    fun requestFromGet(url: String, callBack: HttpCallBack) {
        this.httpCallBack = callBack
        val request = Request.Builder()
                .url(url)
                .build()
        if (CommonUtils.isNetWorkConnected(mContext)) {
            okHttpClient!!.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val message = handler.obtainMessage()
                    message.what = RESULT_ERROR
                    message.obj = e.message.toString()
                    message.sendToTarget()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val jsonObject = JSONObject()
                    jsonObject.put("result", response.body().string())
                    val message = handler.obtainMessage()
                    message.what = RESULT_SUCESS_FROM_GET
                    message.obj = jsonObject.toJSONString()
                    message.sendToTarget()
                }
            })
        } else {
            CommonUtils.showToastShort(mContext, "网络出错,请检查网络设置")
        }
    }


    private fun startRequest(request: Request) {
        if (CommonUtils.isNetWorkConnected(mContext)) {
            okHttpClient!!.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val message = handler.obtainMessage()
                    message.what = RESULT_ERROR
                    message.obj = e.message.toString()
                    message.sendToTarget()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val message = handler.obtainMessage()
                    message.what = RESULT_SUCESS
                    message.obj = response.body().string()
                    message.sendToTarget()
                }
            })
        } else {
            CommonUtils.showToastShort(mContext, "网络出错,请检查网络设置")
        }
    }

    interface HttpCallBack {

        fun onResponse(jsonObject: JSONObject)

        fun onFailure(errorMsg: String)
    }

    /**
     * 下载不带进度
     */
    interface DownloadCallBack {
        fun onSuccess()

        fun onFailure(message: String)
    }


    private fun guessMimeType(path: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        var contentTypeFor: String? = fileNameMap.getContentTypeFor(path)
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream"
        }
        return contentTypeFor
    }

    fun loadFile(url: String, savePath: String, callBack: DownloadCallBack) {
        val request = Request.Builder()
                //下载地址
                .url(url)
                .build()
        okHttpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callBack.onFailure(e.message!!)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                var len: Int
                val buf = ByteArray(2048)
                val inputStream = response.body().byteStream()
                //可以在这里自定义路径
                val file1 = File(savePath)
                val fileOutputStream = FileOutputStream(file1)
                len = inputStream.read(buf)
                while (len != -1) {
                    fileOutputStream.write(buf, 0, len)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                inputStream.close()
                callBack.onSuccess()
            }
        })
    }

    /**
     * 下载的带进度的callback
     */
    interface ProgressDownloadCallBack {
        fun onSuccess()

        fun onProgress(progress: Int)

        fun onFailure()
    }

    /**
     * 下载文件带进度

     * @param url      下载地址
     * *
     * @param savePath 保存地址
     * *
     * @param callBack ProgressDownloadCallBack
     */
    fun loadFileHasProgress(url: String, savePath: String, callBack: ProgressDownloadCallBack) {
        val request = Request.Builder()
                //下载地址
                .url(url)
                .build()
        okHttpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callBack.onFailure()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                var len: Int
                val buf = ByteArray(2048)
                val inputStream = response.body().byteStream()
                val requestLength = response.body().contentLength()
                var total: Long = 0
                //可以在这里自定义路径
                val file1 = File(savePath)
                val fileOutputStream = FileOutputStream(file1)
                len = inputStream.read(buf)
                while (len != -1) {
                    total += len.toLong()
                    // publishing the progress....
                    if (requestLength > 0)
                    // only if total length is known
                        callBack.onProgress((total * 100 / requestLength).toInt())
                    fileOutputStream.write(buf, 0, len)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                inputStream.close()
                callBack.onSuccess()
            }
        })
    }

    /**
     * Json数据请求数据
     * @param url   地址
     * *
     * @param object    Josn数据
     * *
     * @param callBack  回调
     */
    fun postByJSONObject(url: String, `object`: JSONObject, callBack: HttpCallBack) {
        this.httpCallBack = callBack
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, `object`.toJSONString())
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        startRequest(request)
    }

    /**
     * Json数据格式的字符串请求。
     * @param url  地址
     * *
     * @param objString  json数据格式的字符串
     * *
     * @param callBack   回调监听
     */
    fun postByJsonString(url: String, objString: String, callBack: HttpCallBack) {
        this.httpCallBack = callBack
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, objString)
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        startRequest(request)
    }

}