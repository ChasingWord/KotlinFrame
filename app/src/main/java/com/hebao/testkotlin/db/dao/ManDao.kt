package com.hebao.testkotlin.db.dao

import androidx.room.*
import com.hebao.testkotlin.db.base.BaseDao
import com.hebao.testkotlin.db.entity.Man
import com.hebao.testkotlin.db.entity.Person

/**
 * Created by chasing on 2021/10/26.
 */
@Dao
interface ManDao:BaseDao<Man> {
    @Query("SELECT * FROM Man")
    suspend fun loadMan(): Array<Man>

    @Query("SELECT * FROM Man WHERE age > :minAge")
    suspend fun loadAllMansOlderThan(minAge: Int): Array<Man>

    @Query("SELECT * FROM Man WHERE age BETWEEN :minAge AND :maxAge")
    suspend fun loadAllMansBetweenAges(minAge: Int, maxAge: Int): Array<Man>

    // 查询子集
    @Query("SELECT name,isMale,description FROM Man")
    suspend fun loadPerson(): Array<Person>

    // 传递参数的集合
    @Query("SELECT * FROM Man WHERE age IN (:ages)")
    suspend fun loadUsersFromRegions(ages: List<Int>): List<Man>

    @Query("DELETE FROM Man")
    suspend fun deleteAll()
}