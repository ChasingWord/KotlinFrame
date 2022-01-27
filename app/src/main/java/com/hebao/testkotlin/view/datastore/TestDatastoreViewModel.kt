package com.hebao.testkotlin.view.datastore

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.shrimp.base.utils.media.FolderBean
import com.shrimp.base.utils.media.IMediaLoaderCallback
import com.shrimp.base.utils.media.MediaBean
import com.shrimp.base.utils.media.MediaLoader
import com.shrimp.base.utils.thread.ThreadPoolUtil
import com.shrimp.base.view.BaseActivityModel

/**
 * Created by chasing on 2021/11/10.
 */
class TestDatastoreViewModel(application: Application) : BaseActivityModel(application),
    IMediaLoaderCallback {

    val folderList: MutableLiveData<List<MediaBean>> = MutableLiveData(ArrayList())

    override fun onLoadFinish(folderList: List<FolderBean>?) {
        ThreadPoolUtil.executeOnMainThread{
            this@TestDatastoreViewModel.folderList.value = folderList?.get(0)?.mediaList
        }
    }
}