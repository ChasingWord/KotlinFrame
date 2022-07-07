package com.hebao.testkotlin.db.base

import androidx.room.*

const val DB_NAME = "room.db"

/**
 * Created by chasing on 2021/10/26.
 * 默认的更新删除都是以主键PrimaryKey为条件进行的
 */
@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: T): Long

    @Insert
    fun insert(tList: List<T>): Array<Long>

    // 它使用与每个实体的主键匹配的查询。
    @Update
    fun update(t: T)

    @Update
    fun update(tList: List<T>)

    // 它使用与每个实体的主键匹配的查询。
    @Delete
    fun delete(t: T)

    @Delete
    fun delete(tList: List<T>)
}