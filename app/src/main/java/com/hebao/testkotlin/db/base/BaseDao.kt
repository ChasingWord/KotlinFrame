package com.hebao.testkotlin.db.base

import androidx.room.*

/**
 * Created by chasing on 2021/10/26.
 * 默认的更新删除都是以主键PrimaryKey为条件进行的
 */
@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(t: T): Long

    @Insert
    suspend fun insert(tList: List<T>): List<Long>

    // 它使用与每个实体的主键匹配的查询。
    @Update
    suspend fun update(t: T)

    @Update
    suspend fun update(tList: List<T>)

    // 它使用与每个实体的主键匹配的查询。
    @Delete
    suspend fun delete(t: T)

    @Delete
    suspend fun delete(tList: List<T>)
}