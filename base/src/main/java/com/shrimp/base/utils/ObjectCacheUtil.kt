package com.shrimp.base.utils

import android.content.Context
import android.text.TextUtils
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import kotlin.experimental.or

/**
 * Created by chasing on 2021/10/22.
 */
object ObjectCacheUtil {

    private const val fileName = "objectCache"

    /**
     * desc:保存对象
     *
     * @param obj 要保存的对象，只能保存实现了serializable的对象
     */
    fun saveObject(context: Context?, key: String?, obj: Any?) {
        if (context == null) return
        var bos: ByteArrayOutputStream? = null
        var os: ObjectOutputStream? = null
        try {
            // 保存对象
            val shareData = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit()
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            bos = ByteArrayOutputStream()
            os = ObjectOutputStream(bos)
            //将对象序列化写入byte缓存
            os.writeObject(obj)
            //将序列化的数据转为16进制保存
            val bytesToHexString = bytesToHexString(bos.toByteArray())
            //保存该16进制数组
            shareData.putString(key, bytesToHexString)
            shareData.apply()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bos?.close()
                os?.close()
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * desc:将数组转为16进制
     */
    private fun bytesToHexString(bArray: ByteArray?): String? {
        if (bArray == null) {
            return null
        }
        if (bArray.isEmpty()) {
            return ""
        }
        val sb = StringBuilder(bArray.size)
        var sTemp: String
        for (b in bArray) {
            sTemp = Integer.toHexString(0xFF and b.toInt())
            if (sTemp.length < 2) sb.append(0)
            sb.append(sTemp.uppercase(Locale.getDefault()))
        }
        return sb.toString()
    }

    fun readObject(context: Context?, key: String?): Any? {
        return readObject(context, key, null)
    }

    /**
     * desc:获取保存的Object对象
     */
    fun readObject(context: Context?, key: String?, value: Any?): Any? {
        if (context == null) return value
        try {
            val shareData = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
            if (shareData.contains(key)) {
                val string = shareData.getString(key, "")
                return if (TextUtils.isEmpty(string)) {
                    value
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    val stringToBytes = stringToBytes(string)
                    val bis = ByteArrayInputStream(stringToBytes)
                    val `is` = ObjectInputStream(bis)
                    //返回反序列化得到的对象
                    `is`.readObject()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        //所有异常返回null
        return value
    }

    fun remove(context: Context?, key: String?) {
        if (context == null) return
        val shareData = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val edit = shareData.edit()
        edit.remove(key)
        edit.apply()
    }

    fun clear(context: Context?) {
        if (context == null) return
        val shareData = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val edit = shareData.edit()
        edit.clear()
        edit.apply()
    }

    /*
     * 把16进制字符串转换成字节数组 @param hex @return
     */
    private fun stringToBytes(hex: String?): ByteArray {
        val len = hex!!.length / 2
        val result = ByteArray(len)
        val aChar = hex.toCharArray()
        for (i in 0 until len) {
            val pos = i * 2
            result[i] = (toByte(aChar[pos]).toInt().shl(4).toByte() or toByte(aChar[pos + 1]))
        }
        return result
    }

    private fun toByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }
}
