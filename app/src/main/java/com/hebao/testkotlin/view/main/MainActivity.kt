package com.hebao.testkotlin.view.main

import android.animation.Animator
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivityMainBinding
import com.shrimp.base.utils.L
import com.shrimp.base.utils.ObjectCacheUtil
import kotlinx.coroutines.*

import android.widget.FrameLayout

import android.view.animation.Animation
import android.animation.ObjectAnimator
import android.graphics.Path
import android.text.TextUtils
import android.view.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var _objectCacheUtil: ObjectCacheUtil
    private var name by FormatDelegate()

    private val objectCacheUtil: ObjectCacheUtil
        get() {
            if (!::_objectCacheUtil.isInitialized)
                _objectCacheUtil = ObjectCacheUtil(this)
            return _objectCacheUtil
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        var job: Job? = null
        binding.fab.setOnClickListener {
//             ActivityUtil.takePhoto(this)

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
//
            CoroutineScope(Dispatchers.IO).launch {
                objectCacheUtil.remove("key", String::class)
                objectCacheUtil.read<Int>("key_int") {
                    Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.fabTop.setOnClickListener {
//            main()
//            L.e("Task after main")

            name = "abc"
            L.e(name)

            CoroutineScope(Dispatchers.IO).launch {
                objectCacheUtil.save("key_int", 2)
            }

//            CoroutineScope(Dispatchers.IO).launch {
////                val man = Man(1, 1, true)
////                ManDatabase.getDao(applicationContext)?.insert(man)
//
//                job?.cancel()
//            }

            CoroutineScope(Dispatchers.IO).launch {
                objectCacheUtil.read<String>("key") {
                    if (!TextUtils.isEmpty(it))
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.fab.tag = "fabTop"
        binding.fab.setOnLongClickListener {
            val item = ClipData.Item(it.tag as CharSequence);

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"
            val dragData = ClipData(
                it.tag as CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            );

            // Instantiates the drag shadow builder.
            val myShadow = MyDragShadowBuilder(binding.fab);

            // Starts the drag

            it.startDrag(
                dragData,  // the data to be dragged
                myShadow,  // the drag shadow builder
                null,      // no need to use local data
                0          // flags (not currently used, set to 0)
            );
            return@setOnLongClickListener true
        }

        val path = Path()
        path.arcTo(0f, 0f, 1000f, 1000f, 270f, -180f, true)
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(binding.fabTop, View.X, View.Y, path)
        animator.duration = 2000
        animator.start()

        binding.root.setOnDragListener { v, event ->
            binding.fabTop.x = event.x
            binding.fabTop.y = event.y

            //获取事件
            val action = event.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> Log.d("aaa", "开始拖拽")
                DragEvent.ACTION_DRAG_ENDED -> Log.d("aaa", "结束拖拽")
                DragEvent.ACTION_DRAG_ENTERED -> Log.d("aaa", "拖拽的view进入监听的view时")
                DragEvent.ACTION_DRAG_EXITED -> Log.d("aaa", "拖拽的view退出监听的view时")
                DragEvent.ACTION_DRAG_LOCATION -> {
                    val x = event.x
                    val y = event.y
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

    private suspend fun world() {
        delay(3000L)
        L.e("World!")
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