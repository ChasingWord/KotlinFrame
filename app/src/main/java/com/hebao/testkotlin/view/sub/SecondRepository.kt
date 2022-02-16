package com.hebao.testkotlin.view.sub

import android.content.Context
import com.hebao.testkotlin.db.database.BookDatabase
import com.hebao.testkotlin.db.database.ManDatabase
import com.hebao.testkotlin.db.entity.Book
import com.shrimp.network.RequestManager
import com.shrimp.network.callback.AbstractCallBack
import com.shrimp.network.callback.AbstractStringCallBack
import com.shrimp.network.entity.res.PresetWordDataInfo
import com.shrimp.network.entity.res.Tags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

/**
 * Created by chasing on 2021/10/26.
 */
class SecondRepository {
    private var job: Job? = null

    fun getData(
        callBack: AbstractCallBack<PresetWordDataInfo>,
        viewModelScope: CoroutineScope
    ) {
        if (job != null && job?.isActive == true && job?.isCompleted == false)
            job?.cancel()
        job = RequestManager.getData(callBack, viewModelScope)
    }

    fun getTags(callBack: AbstractCallBack<List<Tags>>, viewModelScope: CoroutineScope) {
        RequestManager.getTags(callBack, viewModelScope)
    }

    fun getLocalData(context: Context, viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            val manDao = ManDatabase.getDao(context.applicationContext)
            val loadMan = manDao.loadMan()

            val bookDao = BookDatabase.getDao(context.applicationContext)
            val book = Book(0, "book")
            val id = bookDao.insert(book)

            val i = 1
        }
    }
}