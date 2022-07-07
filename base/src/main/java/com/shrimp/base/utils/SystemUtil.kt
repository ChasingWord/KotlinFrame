package com.shrimp.base.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.NonNull

/**
 * Created by chasing on 2021/10/22.
 */
object SystemUtil {
    fun getScreenBrightness(@NonNull activity: Activity): Int {
        val contentResolver = activity.contentResolver
        val defVal = 125
        return Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS, defVal
        )
    }

    fun getActivityBrightness(@NonNull activity: Activity): Float {
        var brightness = activity.window.attributes.screenBrightness
        if (brightness == -1f) {
            brightness = getScreenBrightness(activity) / 255f
        }
        return brightness
    }

    //设置屏幕亮度(0-1)
    fun setBrightness(@NonNull activity: Activity, lightPercent: Float) {
        val window = activity.window
        val lpa = window.attributes
        lpa.screenBrightness = lightPercent
        if (lpa.screenBrightness > 1.0f) lpa.screenBrightness =
            1.0f else if (lpa.screenBrightness < 0.01f) lpa.screenBrightness = 0.01f
        window.attributes = lpa
    }

    //获取音量
    fun getVolume(@NonNull activity: Activity): Int {
        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    //获取最大音量
    fun getMaxVolume(@NonNull activity: Activity): Int {
        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    //设置音量
    fun setVolume(@NonNull activity: Activity, volume: Int) {
        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    //判断是否处于静音状态
    fun isVolumeMute(@NonNull context: Context): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0
    }

    private var PRE_PLAY_RINGTONE_TIME: Long = 0

    fun playRingtone(context: Context?) {
        val curTime = System.currentTimeMillis()
        if (curTime - PRE_PLAY_RINGTONE_TIME <= 1000) return
        PRE_PLAY_RINGTONE_TIME = curTime
        try {
            val ringtone = RingtoneManager.getRingtone(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            )
            ringtone.play()
        } catch (ignored: Exception) {
        }
    }

    /**
     * 屏幕是否黑屏（完全变黑那种，屏幕变暗不算）
     *
     * @param context 上下文
     * @return 屏幕变黑，则返回true；屏幕变亮，则返回false
     */
    fun isScreenOff(context: Context): Boolean {
        val manager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return manager.isKeyguardLocked
    }

    /**
     * 忽略电池优化
     */
    fun ignoreBatteryOptimization(activity: Activity) {
        val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        val hasIgnored: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.packageName)
            //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
            if (!hasIgnored) {
                @SuppressLint("BatteryLife") val intent =
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivity(intent)
            } else showToast("已经处于保持活跃状态！")
        }
    }
}