package com.hebao.testkotlin.view.datastore

import android.content.Context
import android.widget.Toast
import com.hebao.testkotlin.databinding.ActivityTestDatastoreBinding
import com.shrimp.base.utils.ObjectCacheUtil
import com.shrimp.base.view.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by chasing on 2021/11/10.
 */
class TestDatastoreActivity : BaseActivity<TestDatastoreViewModel, ActivityTestDatastoreBinding>() {
    private lateinit var _objectCacheUtil: ObjectCacheUtil
    private val objectCacheUtil: ObjectCacheUtil
        get() {
            if (!::_objectCacheUtil.isInitialized) {
                synchronized(this) {
                    if (!::_objectCacheUtil.isInitialized)
                        _objectCacheUtil = ObjectCacheUtil(this)
                }
            }
            return _objectCacheUtil
        }

    companion object {
        fun start(context: Context) {
            start(context, TestDatastoreActivity::class.java)
        }
    }

    override fun initViewModel(): Class<TestDatastoreViewModel> = TestDatastoreViewModel::class.java

    override fun initContentView(): ActivityTestDatastoreBinding =
        ActivityTestDatastoreBinding.inflate(layoutInflater)

    override fun initView() {
        dataBinding.first.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                objectCacheUtil.save("key", "say hello")
                objectCacheUtil.save("key_int", 1)
            }
        }

        dataBinding.second.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                objectCacheUtil.save("key", "say hello too")
                objectCacheUtil.save("key_int", 2)
            }
        }

        dataBinding.firstRead.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                objectCacheUtil.read<Int>("key") {
                    Toast.makeText(this@TestDatastoreActivity, it.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        dataBinding.firstRead.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                objectCacheUtil.read<Int>("key_int") {
                    Toast.makeText(this@TestDatastoreActivity, it.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        dataBinding.firstDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                objectCacheUtil.remove("key", String::class)
            }
        }

        dataBinding.secondDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                objectCacheUtil.remove("key_int", Int::class)
            }
        }
    }
}