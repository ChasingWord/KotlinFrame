package com.hebao.testkotlin.view.model3d.vr

import android.graphics.BitmapFactory
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivityVrBinding
import com.shrimp.base.view.BaseActivity

/**
 * Created by chasing on 2022/7/8.
 */
class VRActivity : BaseActivity<VRViewModel, ActivityVrBinding>() {
    override fun inflateDataBinding(): ActivityVrBinding = ActivityVrBinding.inflate(layoutInflater)

    override fun getViewModelClass(): Class<VRViewModel> = VRViewModel::class.java

    override fun initView() {
        dataBinding.vrView.setEventListener(object: VrPanoramaEventListener(){})
        val options = VrPanoramaView.Options()
        dataBinding.vrView.setInfoButtonEnabled(false)// 设置隐藏最左边信息的按钮
        dataBinding.vrView.setStereoModeButtonEnabled(false)// 设置隐藏立体模型的按钮
        dataBinding.vrView.setFullscreenButtonEnabled(false)// 隐藏全屏模式按钮
//vrView.setTouchTrackingEnabled(true) //false 只能通过传感器旋转 true设置传感器旋转加上左右方向可以通过触摸滑动旋转
        dataBinding.vrView.setPureTouchTracking(true) //设置手动上下左右滑动，不能通过传感器旋转

        options.inputType = VrPanoramaView.Options.TYPE_MONO
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.yyds)
        dataBinding.vrView.loadImageFromBitmap(bitmap,options)
    }

    override fun initDataObserve() {
    }
}