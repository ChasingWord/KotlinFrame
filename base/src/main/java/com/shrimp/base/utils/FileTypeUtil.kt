package com.shrimp.base.utils

import android.text.TextUtils
import androidx.annotation.NonNull
import java.util.*

/**
 * Created by chasing on 2021/10/22.
 */
object FileTypeUtil {

    // region 声明各种类型文件的dataType
    private const val DATA_TYPE_ALL = "*/*" //未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择

    private const val DATA_TYPE_PSD = "image/x-photoshop"
    private const val DATA_TYPE_AEP = "application/vnd.audiograph"
    private const val DATA_TYPE_SVG = "image/svg+xml"
    private const val DATA_TYPE_ASP = "application/octet-stream" //text/asp
    private const val DATA_TYPE_AI = "application/postscript"
    private const val DATA_TYPE_CDR = "image/x-coreldraw"
    private const val DATA_TYPE_EPS = "application/postscript"
    private const val DATA_TYPE_APK = "application/vnd.android.package-archive"
    private const val DATA_TYPE_PPT = "application/vnd.ms-powerpoint"
    private const val DATA_TYPE_EXCEL = "application/vnd.ms-excel"
    private const val DATA_TYPE_CHM = "application/x-chm"
    private const val DATA_TYPE_TXT = "text/plain"
    private const val DATA_TYPE_PDF = "application/pdf"
    private const val DATA_TYPE_GP = "video/3gpp"
    private const val DATA_TYPE_ASF = "video/x-ms-asf"
    private const val DATA_TYPE_AVI = "video/x-msvideo"
    private const val DATA_TYPE_BIN = "application/octet-stream"
    private const val DATA_TYPE_BMP = "image/bmp"
    private const val DATA_TYPE_C = "text/plain"
    private const val DATA_TYPE_CLASS = "application/octet-stream"
    private const val DATA_TYPE_CONF = "text/plain"
    private const val DATA_TYPE_CPP = "text/plain"
    private const val DATA_TYPE_DOC = "application/msword"
    private const val DATA_TYPE_EXE = "application/octet-stream"
    private const val DATA_TYPE_GIF = "image/gif"
    private const val DATA_TYPE_GTAR = "application/x-gtar"
    private const val DATA_TYPE_GZ = "application/x-gzip"
    private const val DATA_TYPE_H = "text/plain"
    private const val DATA_TYPE_HTM = "text/html"
    private const val DATA_TYPE_HTML = "text/html"
    private const val DATA_TYPE_JAR = "application/java-archive"
    private const val DATA_TYPE_JAVA = "text/plain"
    private const val DATA_TYPE_JPG = "image/jpeg"
    private const val DATA_TYPE_JS = "application/x-javascript"
    private const val DATA_TYPE_LOG = "text/plain"
    private const val DATA_TYPE_M3U = "audio/x-mpegurl"
    private const val DATA_TYPE_M4A = "audio/mp4a-latm"
    private const val DATA_TYPE_M4B = "audio/mp4a-latm"
    private const val DATA_TYPE_M4P = "audio/mp4a-latm"
    private const val DATA_TYPE_M4U = "video/vnd.mpegurl"
    private const val DATA_TYPE_M4V = "video/x-m4v"
    private const val DATA_TYPE_MOV = "video/quicktime"
    private const val DATA_TYPE_MP2 = "audio/x-mpeg"
    private const val DATA_TYPE_MP3 = "audio/x-mpeg"
    private const val DATA_TYPE_MP4 = "video/mp4"
    private const val DATA_TYPE_MPC = "application/vnd.mpohun.certificate"
    private const val DATA_TYPE_MPE = "video/mpeg"
    private const val DATA_TYPE_MPG = "video/mpeg"
    private const val DATA_TYPE_MPG4 = "video/mp4"
    private const val DATA_TYPE_MPGA = "audio/mpeg"
    private const val DATA_TYPE_MSG = "application/vnd.ms-outlook"
    private const val DATA_TYPE_OGG = "audio/ogg"
    private const val DATA_TYPE_PNG = "image/png"
    private const val DATA_TYPE_PPS = "application/vnd.ms-powerpoint"
    private const val DATA_TYPE_PROP = "text/plain"
    private const val DATA_TYPE_RAR = "application/rar"
    private const val DATA_TYPE_RC = "text/plain"
    private const val DATA_TYPE_RMVB = "audio/x-pn-realaudio"
    private const val DATA_TYPE_RP = "image/vnd.rn-realpix"
    private const val DATA_TYPE_RTF = "application/rtf"
    private const val DATA_TYPE_SH = "text/plain"
    private const val DATA_TYPE_TAR = "application/x-tar"
    private const val DATA_TYPE_TGZ = "application/x-compressed"
    private const val DATA_TYPE_WAV = "audio/x-wav"
    private const val DATA_TYPE_WMA = "audio/x-ms-wma"
    private const val DATA_TYPE_WMV = "audio/x-ms-wmv"
    private const val DATA_TYPE_WPS = "application/vnd.ms-works"
    private const val DATA_TYPE_XML = "text/plain"
    private const val DATA_TYPE_Z = "application/x-compress"
    private const val DATA_TYPE_ZIP = "application/zip"
    // endregion

    // endregion
    fun getMIMEType(@NonNull filePath: String): String {
        var dataType = DATA_TYPE_ALL
        var end = filePath
        if (filePath.contains(".")) end =
            filePath.substring(filePath.lastIndexOf(".") + 1).uppercase(Locale.getDefault())
        when (end) {
            "PSD" -> dataType = DATA_TYPE_PSD
            "AEP" -> dataType = DATA_TYPE_AEP
            "SVG" -> dataType = DATA_TYPE_SVG
            "ASP" -> dataType = DATA_TYPE_ASP
            "AI" -> dataType = DATA_TYPE_AI
            "CDR" -> dataType = DATA_TYPE_CDR
            "EPS" -> dataType = DATA_TYPE_EPS
            "RP" -> dataType = DATA_TYPE_RP
            "APK" -> dataType = DATA_TYPE_APK
            "PPT" -> dataType = DATA_TYPE_PPT
            "CHM" -> dataType = DATA_TYPE_CHM
            "TXT" -> dataType = DATA_TYPE_TXT
            "PDF" -> dataType = DATA_TYPE_PDF
            "GP" -> dataType = DATA_TYPE_GP
            "ASF" -> dataType = DATA_TYPE_ASF
            "AVI" -> dataType = DATA_TYPE_AVI
            "BIN" -> dataType = DATA_TYPE_BIN
            "BMP" -> dataType = DATA_TYPE_BMP
            "C" -> dataType = DATA_TYPE_C
            "CLASS" -> dataType = DATA_TYPE_CLASS
            "CONF" -> dataType = DATA_TYPE_CONF
            "CPP" -> dataType = DATA_TYPE_CPP
            "DOC", "DOCX" -> dataType = DATA_TYPE_DOC
            "EXE" -> dataType = DATA_TYPE_EXE
            "XLS", "XLSX" -> dataType = DATA_TYPE_EXCEL
            "GIF" -> dataType = DATA_TYPE_GIF
            "GTAR" -> dataType = DATA_TYPE_GTAR
            "GZ" -> dataType = DATA_TYPE_GZ
            "H" -> dataType = DATA_TYPE_H
            "HTM" -> dataType = DATA_TYPE_HTM
            "HTML" -> dataType = DATA_TYPE_HTML
            "JAR" -> dataType = DATA_TYPE_JAR
            "JAVA" -> dataType = DATA_TYPE_JAVA
            "JPG" -> dataType = DATA_TYPE_JPG
            "JS" -> dataType = DATA_TYPE_JS
            "LOG" -> dataType = DATA_TYPE_LOG
            "M3U" -> dataType = DATA_TYPE_M3U
            "M4A" -> dataType = DATA_TYPE_M4A
            "M4B" -> dataType = DATA_TYPE_M4B
            "M4P" -> dataType = DATA_TYPE_M4P
            "M4U" -> dataType = DATA_TYPE_M4U
            "M4V" -> dataType = DATA_TYPE_M4V
            "MOV" -> dataType = DATA_TYPE_MOV
            "MP2" -> dataType = DATA_TYPE_MP2
            "MP3" -> dataType = DATA_TYPE_MP3
            "MP4" -> dataType = DATA_TYPE_MP4
            "MPC" -> dataType = DATA_TYPE_MPC
            "MPE", "MPEG" -> dataType = DATA_TYPE_MPE
            "MPG" -> dataType = DATA_TYPE_MPG
            "MPG4" -> dataType = DATA_TYPE_MPG4
            "MPGA" -> dataType = DATA_TYPE_MPGA
            "MSG" -> dataType = DATA_TYPE_MSG
            "OGG" -> dataType = DATA_TYPE_OGG
            "PNG" -> dataType = DATA_TYPE_PNG
            "PPS" -> dataType = DATA_TYPE_PPS
            "PROP" -> dataType = DATA_TYPE_PROP
            "RAR" -> dataType = DATA_TYPE_RAR
            "RC" -> dataType = DATA_TYPE_RC
            "RMVB" -> dataType = DATA_TYPE_RMVB
            "RTF" -> dataType = DATA_TYPE_RTF
            "SH" -> dataType = DATA_TYPE_SH
            "TAR" -> dataType = DATA_TYPE_TAR
            "TGZ" -> dataType = DATA_TYPE_TGZ
            "WAV" -> dataType = DATA_TYPE_WAV
            "WMA" -> dataType = DATA_TYPE_WMA
            "WMV" -> dataType = DATA_TYPE_WMV
            "WPS" -> dataType = DATA_TYPE_WPS
            "XML" -> dataType = DATA_TYPE_XML
            "Z" -> dataType = DATA_TYPE_Z
            "ZIP" -> dataType = DATA_TYPE_ZIP
        }
        return dataType
    }

    fun isFitType(filePath: String, conditions: Array<String>): Boolean {
        if (TextUtils.isEmpty(filePath)) return false
        for (condition in conditions) {
            if (filePath.lowercase(Locale.getDefault()).endsWith(condition)) return true
        }
        return false
    }
}