package com.hebao.testkotlin.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.hebao.testkotlin.db.base.BaseDao
import com.hebao.testkotlin.db.entity.Man
import com.hebao.testkotlin.db.entity.Person

/**
 * Created by chasing on 2021/10/26.
 */
@Dao
interface ManDao : BaseDao<Man> {

    @Query("SELECT * FROM Man")
    fun loadMan(): Array<Man>

    @Query("SELECT * FROM Man WHERE age > :minAge")
    fun loadAllMansOlderThan(minAge: Int): Array<Man>

    @Query("SELECT * FROM Man WHERE age BETWEEN :minAge AND :maxAge")
    fun loadAllMansBetweenAges(minAge: Int, maxAge: Int): Array<Man>

    // 传递参数的集合
    @Query("SELECT * FROM Man WHERE age IN (:ages)")
    fun loadUsersFromRegions(ages: List<Int>): Array<Man>

    // 查询子集
    @Query("SELECT name,isMale,description FROM Man")
    fun loadPerson(): Array<Person>

    @Query("DELETE FROM Man")
    fun deleteAll()
}