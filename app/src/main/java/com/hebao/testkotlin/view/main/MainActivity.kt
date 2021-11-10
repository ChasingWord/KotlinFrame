package com.hebao.testkotlin.view.main

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivityMainBinding
import com.shrimp.base.utils.ActivityUtil
import com.shrimp.base.utils.L
import com.shrimp.base.utils.ObjectCacheUtil
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var _objectCacheUtil: ObjectCacheUtil

    private val objectCacheUtil :ObjectCacheUtil
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
            main()

//            CoroutineScope(Dispatchers.IO).launch {
////                val man = Man(1, 1, true)
////                ManDatabase.getDao(applicationContext)?.insert(man)
//
//                job?.cancel()
//            }

//            CoroutineScope(Dispatchers.IO).launch {
//                objectCacheUtil.read<String>("key") {
//                    if (!TextUtils.isEmpty(it))
//                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }

    fun main() = runBlocking { // this: CoroutineScope
        val result = withTimeoutOrNull(1300L) {
            repeat(40) { i ->
                L.e("I'm sleeping $i ...")
                delay(400L)
            }
            "Done" // 运行完所有代码则返回“Done”，超时则返回null
        }
        coroutineScope {
            delay(3000)
        }
        L.e("Result is $result")
        nmi()
        launch {
            L.e("after delay")
        }
    }

    suspend fun nmi() = coroutineScope {
        async {
            delay(3000)
        }
        L.e("nmi delay")
        async {
            L.e("nmi asy")
        }
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
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}