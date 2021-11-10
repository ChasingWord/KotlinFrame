package com.hebao.testkotlin.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hebao.testkotlin.db.dao.ManDao
import com.hebao.testkotlin.db.entity.Man

/**
 * Created by chasing on 2021/10/26.
 */
@Database(version = 1, exportSchema = false, entities = [Man::class])
abstract class ManDatabase : RoomDatabase() {
    abstract val dao: ManDao

    companion object {
        @Volatile
        private var instance: ManDatabase? = null
        private val MIGRATION_LIST = arrayOf(Migration1To2)

        fun getDao(applicationContext: Context): ManDao? {
            if (instance == null) {
                synchronized(ManDatabase::class) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            applicationContext, ManDatabase::class.java,
                            "self.db"
                        )
                            .addCallback(CreatedCallBack)
                            .addMigrations(*MIGRATION_LIST)
                            .build()
                    }
                }
            }
            return instance?.dao
        }

        private object CreatedCallBack : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                //在新装app时会调用，调用时机为数据库build()之后，数据库升级时不调用此函数
                MIGRATION_LIST.map {
                    it.migrate(db)
                }
            }
        }

        private object Migration1To2 : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 数据库的升级语句
                // database.execSQL("")
            }
        }
    }
}