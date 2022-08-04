package com.hebao.testkotlin.view.model3d

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hebao.testkotlin.databinding.FragmentTest3dModelBinding
import com.hebao.testkotlin.view.model3d.cube.CubeActivity
import com.hebao.testkotlin.view.model3d.myobj.ObjActivity
import com.hebao.testkotlin.view.model3d.plane.PlaneActivity
import com.hebao.testkotlin.view.model3d.vr.VRActivity
import com.shrimp.base.view.BaseFragment

/**
 * Created by chasing on 2022/7/8.
 */
class Test3DModelFragment : BaseFragment<Test3DModelViewModel, FragmentTest3dModelBinding>() {
    override fun inflateDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentTest3dModelBinding = FragmentTest3dModelBinding.inflate(inflater, container, false)

    override fun getViewModelClass(): Class<Test3DModelViewModel> = Test3DModelViewModel::class.java

    override fun initView() {
        dataBinding.cube.setOnClickListener{
            startActivity(Intent(activity, CubeActivity::class.java))
        }
        dataBinding.myobj.setOnClickListener{
            startActivity(Intent(activity, ObjActivity::class.java))
        }
        dataBinding.plane.setOnClickListener{
            startActivity(Intent(activity, PlaneActivity::class.java))
        }
        dataBinding.vr.setOnClickListener{
            startActivity(Intent(activity, VRActivity::class.java))
        }

    }

    override fun initDataObserve() {

    }

}