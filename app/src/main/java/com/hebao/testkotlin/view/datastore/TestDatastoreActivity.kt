package com.hebao.testkotlin.view.datastore

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivityTestDatastoreBinding
import com.shrimp.base.decoration.DividerGridItemDecoration
import com.shrimp.base.utils.ActivityUtil
import com.shrimp.base.utils.ObjectCacheUtil
import com.shrimp.base.utils.checkAndRequestReadStoragePermission
import com.shrimp.base.utils.media.MediaLoader
import com.shrimp.base.view.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        isFullScreen = true
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

                val root = TreeNode(2)
                root.left = TreeNode(1)
                root.right = TreeNode(3)
                isValidBST(root)
            }
        }

        imgAdapter = ImgAdapter(context)
        dataBinding.rcvImg.layoutManager = GridLayoutManager(context, 3)
        dataBinding.rcvImg.adapter = imgAdapter
        dataBinding.rcvImg.addItemDecoration(DividerGridItemDecoration(context).colorResId(R.color.transparent)
            .widthResId(R.dimen.dp_4).widthOfVerticalResId(R.dimen.dp_4))

        if (checkAndRequestReadStoragePermission() == 0) {
            queryImage()
        } else {
            ActivityUtil.openPermissionSettingUI(this)
        }
    }

    fun isValidBST(root: TreeNode?): Boolean {
        return isValid(root, Int.MIN_VALUE, Int.MAX_VALUE)
    }

    fun isValid(root: TreeNode?, minVal: Int, maxVal: Int): Boolean {
        if (root == null) return true
        val leftValid = root.left == null || root.left!!.`val` in (minVal + 1 until root.`val`)
        val rightValid = root.right == null || root.right!!.`val` in (root.`val` + 1 until maxVal)
        return leftValid && rightValid && isValid(root.left,
            minVal,
            root.`val`) && isValid(root.right, root.`val`, maxVal)
    }

    class TreeNode(var `val`: Int) {
        var left: TreeNode? = null
        var right: TreeNode? = null
    }

    override fun initDataObserve() {
        viewModel.folderList.observe(this) {
            imgAdapter?.insertAll(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            queryImage()
        }
    }

    private fun queryImage(){
        val loader = MediaLoader(this, viewModel)
        loader.setShowVideo(true)
        loader.load()
    }
}