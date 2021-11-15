package com.shrimp.base.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.reflect.KClass

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_data_store")

/**
 * Created by chasing on 2021/11/8.
 */
class ObjectCacheUtil(val context: Context) {

    suspend inline fun <reified T> read(key: String, crossinline func: (T) -> Unit) =
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

    suspend fun save(key: String, value: Any) {
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

    suspend fun remove(key: String, kClass: KClass<*>) {
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

    suspend fun clear() {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences.clear()
        }
    }
}