package com.shrimp.base.utils.media

/**
 * Created by chasing on 2022/1/27.
 */
data class FolderBean(val path: String, val cover: MediaBean) {
    var name: String? = null
    var mediaList: MutableList<MediaBean>? = null
    var selectedCount = 0
}