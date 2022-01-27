package com.shrimp.base.utils.media

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Created by chasing on 2022/1/27.
 */
class MediaBean() : Parcelable {
    var path: String = ""
    var name: String = ""
    var type: String = ""
    var time: Long = 0
    var duration: Long = 0

    var isSelected = false

    fun isVideo(): Boolean {
        return type === MineType.VIDEO
    }

    fun isGif(): Boolean {
        return type === MineType.GIF
    }

    constructor(path: String, name: String, type: String, time: Long) : this() {
        this.path = path
        this.name = name
        this.type = type
        this.time = time
    }

    constructor(path: String, name: String, type: String, time: Long, duration: Long) : this() {
        this.path = path
        this.name = name
        this.type = type
        this.time = time
        this.duration = duration
    }

    constructor(parcel: Parcel) : this() {
        path = parcel.readString() ?: ""
        name = parcel.readString() ?: ""
        time = parcel.readLong()
        duration = parcel.readLong()
        isSelected = parcel.readByte() != 0.toByte()
        type = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeString(name)
        parcel.writeLong(time)
        parcel.writeLong(duration)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<MediaBean> {
        override fun createFromParcel(parcel: Parcel): MediaBean {
            return MediaBean(parcel)
        }

        override fun newArray(size: Int): Array<MediaBean?> {
            return arrayOfNulls(size)
        }
    }


}