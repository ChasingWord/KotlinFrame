package com.hebao.testkotlin.view.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivityMainBinding
import com.hebao.testkotlin.db.database.ManDatabase
import com.hebao.testkotlin.db.entity.Man
import com.hebao.testkotlin.view.sub.SecondActivity
import com.shrimp.base.utils.L
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        var job : Job? = null
        binding.fab.setOnClickListener {
//            SecondActivity.start(this)

            job = CoroutineScope(Dispatchers.IO).launch {
                    // 在后台启动一个新的协程并继续
                    try {
                        world()
                    } catch (e: Exception) {
                        if (e is CancellationException)
                            L.e("CancellationException")
                        else
                            L.e("OtherException")
                    } finally {
                        L.e("finally")
                    }
                }
                L.e("Hello,")
        }
        binding.fabTop.setOnClickListener {
            GlobalScope.launch {
//                val man = Man(1, 1, true)
//                ManDatabase.getDao(applicationContext)?.insert(man)

                job?.cancel()
            }
        }
    }

    suspend fun world(){
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