package com.hebao.testkotlin.db.dao

import androidx.room.Dao
import com.hebao.testkotlin.db.base.BaseDao
import com.hebao.testkotlin.db.entity.Book

/**
 * Created by chasing on 2021/10/26.
 */
@Dao
interface BookDao: BaseDao<Book> {

}