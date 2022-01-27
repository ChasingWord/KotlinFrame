package com.shrimp.base.utils.thread

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by chasing on 2021/10/22.
 */
object ThreadPoolUtil {
    //先进先出的线程池，具有优先级功能
    private var threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(5)

    //单个运行线程的线程池，控制顺序，一个线程执行完后才会继续下一个线程
    private var singeThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    //主线程运行的Handler，处理在子线程回调之后需要在主线程刷新UI的操作哦
    private var mainThreadHandler: Handler = object : Handler(Looper.getMainLooper()) {}

    fun execute(runnable: ComparableRunnable) {
        threadPoolExecutor.execute(runnable)
    }

    //逐个执行Runnable，上个Runnable执行完后才会开始下个Runnable
    //单个线程操作类似bitmap时，内存开销太大，所以使用单个线程依次运行
    fun executeOneByOne(runnable: Runnable) {
        singeThreadExecutor.execute(runnable)
    }

    fun executeOnMainThread(runnable: Runnable) {
        mainThreadHandler.post(runnable)
    }

    fun executeOnMainThread(runnable: Runnable, delay: Int) {
        mainThreadHandler.postDelayed(runnable, delay.toLong())
    }
}