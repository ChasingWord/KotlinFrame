package com.hebao.testkotlin.view.datastore

import android.content.Context
import androidx.databinding.ViewDataBinding
import coil.load
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ItemImgBinding
import com.shrimp.base.adapter.recycler.BaseRecyclerAdapter
import com.shrimp.base.utils.L
import com.shrimp.base.utils.image_load.ImageLoadUtil
import com.shrimp.base.utils.media.FolderBean
import com.shrimp.base.utils.media.MediaBean

/**
 * Created by chasing on 2022/1/27.
 */
class ImgAdapter(context : Context) : BaseRecyclerAdapter<MediaBean>(context, R.layout.item_img){
    override fun convert(position:Int, viewType: Int, dataBinding: ViewDataBinding, item: MediaBean) {
        dataBinding as ItemImgBinding
        ImageLoadUtil.loadFile(dataBinding.img, item.path)
    }
}