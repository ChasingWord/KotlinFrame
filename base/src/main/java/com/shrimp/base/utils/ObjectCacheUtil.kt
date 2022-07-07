package com.shrimp.base.utils

import android.content.Context
import android.text.TextUtils
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.shrimp.base.utils.thread.ComparableRunnable
import com.shrimp.base.utils.thread.ThreadPoolUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*
import kotlin.reflect.KClass

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_data_store")

/**
 * Created by chasing on 2021/11/8.
 * 使用DataStore进行缓存对象
 */
object ObjectCacheUtil {
    suspend inline fun <reified T> read(context: Context, key: String, crossinline func: (T) -> Unit) =
        coroutineScope {
            try {
                context.dataStore.data
                    .catch {
                        // 当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
                        // 但是如果是其他的异常，最好将它抛出去，不要隐藏问题
                        if (it is IOException) {
                            it.printStackTrace()
                            emit(emptyPreferences())
                        } else {
                            throw it
                        }
                    }
                    .map { value ->
                        when (T::class) {
                            Int::class -> value[intPreferencesKey(key)] ?: 0
                            Double::class -> value[doublePreferencesKey(key)] ?: .0
                            String::class -> value[stringPreferencesKey(key)] ?: ""
                            Boolean::class -> value[booleanPreferencesKey(key)] ?: false
                            Float::class -> value[floatPreferencesKey(key)] ?: .0f
                            Long::class -> value[longPreferencesKey(key)] ?: 0L
                            else -> {
                            }
                        }
                    }
                    .take(1) //限制只取一个，避免一直监听获取
                    .collect {
                        withContext(Dispatchers.Main) {
                            if (it is T) func.invoke(it as T)
                        }
                    }
            } catch (e: Exception) {
                when (T::class) {
                    Int::class -> func.invoke(0 as T)
                    Double::class -> func.invoke(.0 as T)
                    String::class -> func.invoke("" as T)
                    Boolean::class -> func.invoke(false as T)
                    Float::class -> func.invoke(.0f as T)
                    Long::class -> func.invoke(0L as T)
                }
            }
        }

    suspend fun save(context: Context, key: String, value: Any) {
        context.dataStore.edit { mutablePreferences ->
            when (value) {
                is Int -> mutablePreferences[intPreferencesKey(key)] = value
                is Double -> mutablePreferences[doublePreferencesKey(key)] = value
                is String -> mutablePreferences[stringPreferencesKey(key)] = value
                is Boolean -> mutablePreferences[booleanPreferencesKey(key)] = value
                is Float -> mutablePreferences[floatPreferencesKey(key)] = value
                is Long -> mutablePreferences[longPreferencesKey(key)] = value
            }
        }
    }

    suspend fun remove(context: Context, key: String, kClass: KClass<*>) {
        context.dataStore.edit { mutablePreferences ->
            when (kClass) {
                Int::class -> mutablePreferences.remove(intPreferencesKey(key))
                Double::class -> mutablePreferences.remove(doublePreferencesKey(key))
                String::class -> mutablePreferences.remove(stringPreferencesKey(key))
                Boolean::class -> mutablePreferences.remove(booleanPreferencesKey(key))
                Float::class -> mutablePreferences.remove(floatPreferencesKey(key))
                Long::class -> mutablePreferences.remove(longPreferencesKey(key))
            }
        }
    }

    suspend fun clear(context: Context) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences.clear()
        }
    }

    const val FILENAME = "objectCache"

    /**
     * desc:保存对象
     *
     * @param obj 要保存的对象，只能保存实现了serializable的对象
     */
    fun saveObjectBySP(context: Context, key: String, obj: Any) {
        ThreadPoolUtil.execute(object : ComparableRunnable() {
            override fun run() {
                var bos: ByteArrayOutputStream? = null
                var os: ObjectOutputStream? = null
                try {
                    // 保存对象
                    val sharedata =
                        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
                            .edit()
                    //先将序列化结果写到byte缓存中，其实就分配一个内存空间
                    bos = ByteArrayOutputStream()
                    os = ObjectOutputStream(bos)
                    //将对象序列化写入byte缓存
                    os.writeObject(obj)
                    //将序列化的数据转为16进制保存
                    val bytesToHexString = bytesToHexString(bos.toByteArray())
                    //保存该16进制数组
                    sharedata.putString(key, bytesToHexString)
                    sharedata.apply()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        bos?.close()
                        os?.close()
                    } catch (ignored: java.lang.Exception) {
                    }
                }
            }
        })
    }

    /**
     * desc:将数组转为16进制
     */
    private fun bytesToHexString(bArray: ByteArray?): String? {
        if (bArray == null) {
            return null
        }
        if (bArray.size == 0) {
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

    fun readObjectBySP(context: Context, key: String): Any? {
        return readObjectBySP(context, key, null)
    }

    /**
     * desc:获取保存的Object对象
     */
    fun readObjectBySP(context: Context, key: String, value: Any?): Any? {
        try {
            val sharedata =
                context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
            if (sharedata.contains(key)) {
                val string = sharedata.getString(key, "")
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

    fun removeBySP(context: Context, key: String) {
        val shareData = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
        val edit = shareData.edit()
        edit.remove(key)
        edit.apply()
    }

    fun clearBySP(context: Context) {
        val shareData = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
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
            result[i] = (getIndex(aChar[pos]) shl 4 or getIndex(aChar[pos + 1])).toByte()
        }
        return result
    }

    private fun getIndex(c: Char) = "0123456789ABCDEF".indexOf(c)
}