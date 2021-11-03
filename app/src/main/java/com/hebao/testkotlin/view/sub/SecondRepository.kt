package com.hebao.testkotlin.view.sub

import android.content.Context
import com.hebao.testkotlin.db.database.BookDatabase
import com.hebao.testkotlin.db.database.ManDatabase
import com.hebao.testkotlin.db.entity.Book
import com.shrimp.network.RequestManager
import com.shrimp.network.callback.AbstractStringCallBack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Created by chasing on 2021/10/26.
 */
class SecondRepository {
    fun getData(
        context: Context,
        callBack: AbstractStringCallBack,
        viewModelScope: CoroutineScope
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val manDao = ManDatabase.getDao(context.applicationContext)
            val loadMan = manDao?.loadMan()

            val bookDao = BookDatabase.getDao(context.applicationContext)
            val book = Book(0, "book")
            val id = bookDao?.insert(book)

            val i = 1
        }

        val job = RequestManager.getData(callBack, viewModelScope)
        job.cancel()
    }
}