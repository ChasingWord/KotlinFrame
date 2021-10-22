package com.shrimp.base.view

import android.content.Intent
import androidx.lifecycle.ViewModel

/**
 * Created by chasing on 2021/10/20.
 */
abstract class BaseViewModel: ViewModel() {
    fun handleIntent(intent:Intent){}

}