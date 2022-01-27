package com.hebao.testkotlin.view.datastore

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivityTestDatastoreBinding
import com.shrimp.base.decoration.DividerGridItemDecoration
import com.shrimp.base.utils.ObjectCacheUtil
import com.shrimp.base.utils.StatusBarUtil
import com.shrimp.base.utils.media.MediaLoader
import com.shrimp.base.view.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by chasing on 2021/11/10.
 * 测试DataStore存储，本地图片读取
 */
class TestDatastoreActivity : BaseActivity<TestDatastoreViewModel, ActivityTestDatastoreBinding>() {
    private var imgAdapter: ImgAdapter? = null

    companion object {
        fun start(context: Context) {
            start(context, TestDatastoreActivity::class.java)
        }
    }

    override fun getViewModelClass(): Class<TestDatastoreViewModel> =
        TestDatastoreViewModel::class.java

    override fun inflateDataBinding(): ActivityTestDatastoreBinding =
        ActivityTestDatastoreBinding.inflate(layoutInflater)

    override fun changeConfig() {
        needChangeStatusBar = false
        StatusBarUtil.setFullScreen(this)
    }

    override fun initView() {
        dataBinding.first.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ObjectCacheUtil.save(context, "key", "say hello")
                ObjectCacheUtil.save(context, "key_int", 1)
            }
        }

        dataBinding.second.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ObjectCacheUtil.save(context, "key", "say hello too")
                ObjectCacheUtil.save(context, "key_int", 2)
            }
        }

        dataBinding.firstRead.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ObjectCacheUtil.read<String>(context, "key") {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        dataBinding.secondRead.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ObjectCacheUtil.read<Int>(context, "key_int") {
                    Toast.makeText(this@TestDatastoreActivity, it.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        dataBinding.firstDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ObjectCacheUtil.remove(context, "key", String::class)
            }
        }

        dataBinding.secondDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ObjectCacheUtil.remove(context, "key_int", Int::class)
            }
        }

        imgAdapter = ImgAdapter(context)
        dataBinding.rcvImg.layoutManager = GridLayoutManager(context, 3)
        dataBinding.rcvImg.adapter = imgAdapter
        dataBinding.rcvImg.addItemDecoration(DividerGridItemDecoration(context).colorResId(R.color.transparent)
            .widthResId(R.dimen.dp_4).widthOfVerticalResId(R.dimen.dp_4))
    }

    override fun initDataObserve() {
        viewModel.folderList.observe(this) {
            imgAdapter?.insertAll(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loader = MediaLoader(this, viewModel)
        loader.load()
    }
}