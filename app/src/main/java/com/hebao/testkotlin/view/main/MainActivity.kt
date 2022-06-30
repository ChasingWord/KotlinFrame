package com.hebao.testkotlin.view.main

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivityMainBinding
import com.shrimp.base.utils.ActivityUtil
import com.shrimp.base.utils.L
import com.shrimp.base.utils.StatusBarUtil
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var name by FormatDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtil.setColorDiff(
            this,
            ContextCompat.getColor(this, R.color.purple_200)
        )

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            ActivityUtil.takePhoto(this)

//            var job: Job? = null
//            job = CoroutineScope(Dispatchers.IO).launch {
//                // 在后台启动一个新的协程并继续
//                try {
//                    world()
//                } catch (e: Exception) {
//                    if (e is CancellationException)
//                        L.e("CancellationException")
//                    else
//                        L.e("OtherException")
//                } finally {
//                    L.e("finally")
//                }
//            }
//            L.e("Hello,")
//            job.cancel()
        }

        binding.fabTop.setOnClickListener {
//            main()
//            L.e("Task after main")

            name = "abc"
            L.e(name)

            //按路线进行位移动画
            val path = Path()
            path.arcTo(0f, 0f, 1000f, 1000f, 270f, -180f, true)
            val animator: ObjectAnimator =
                ObjectAnimator.ofFloat(binding.fabTop, View.X, View.Y, path)
            animator.duration = 2000
            animator.start()
        }

        // 拖拽相关逻辑
        binding.fabTop.tag = "fabTop"
        binding.fabTop.setOnLongClickListener {
            val item = ClipData.Item(it.tag as CharSequence);

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"
            val dragData = ClipData(
                it.tag as CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )

            // Instantiates the drag shadow builder.
            val myShadow = MyDragShadowBuilder(it)

            // Starts the drag
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(
                    dragData,  // the data to be dragged
                    myShadow,  // the drag shadow builder
                    null,      // no need to use local data
                    0          // flags (not currently used, set to 0)
                )
            } else {
                it.startDrag(
                    dragData,  // the data to be dragged
                    myShadow,  // the drag shadow builder
                    null,      // no need to use local data
                    0          // flags (not currently used, set to 0)
                )
            }
            return@setOnLongClickListener true
        }
        binding.root.setOnDragListener { v, event ->
            //获取事件
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> Log.d("aaa", "开始拖拽")
                DragEvent.ACTION_DRAG_ENDED -> Log.d("aaa", "结束拖拽")
                DragEvent.ACTION_DRAG_ENTERED -> Log.d("aaa", "拖拽的view进入监听的view时")
                DragEvent.ACTION_DRAG_EXITED -> Log.d("aaa", "拖拽的view退出监听的view时")
                DragEvent.ACTION_DRAG_LOCATION -> {
                    val x = event.x
                    val y = event.y
                    binding.fabTop.x = event.x - binding.fabTop.width / 2
                    binding.fabTop.y = event.y - binding.fabTop.height / 2
                    Log.e("aaa", "拖拽的view在监听view中的位置:x =$x,y=$y")
                }
                DragEvent.ACTION_DROP -> Log.i("aaa", "释放拖拽的view")
            }
            return@setOnDragListener true
        }
    }

    fun main() = CoroutineScope(Dispatchers.IO).launch { // t
        // his: CoroutineScope
        coroutineScope {
            delay(200L)
            L.e("Task from runBlocking")
        }
        L.e("Coroutine scope is over") // 这一行在内嵌 launch 执行完毕后才输出
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}