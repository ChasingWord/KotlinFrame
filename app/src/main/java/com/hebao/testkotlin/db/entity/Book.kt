package com.hebao.testkotlin.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by chasing on 2021/10/26.
 */
@Entity
data class Book(@PrimaryKey(autoGenerate = true) var id: Int, var name: String)