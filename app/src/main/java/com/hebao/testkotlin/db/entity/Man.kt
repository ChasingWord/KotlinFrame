package com.hebao.testkotlin.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by chasing on 2021/10/26.
 */
open class Person {
    var name: String? = null
    var isMale: Boolean = false
    var description: String? = null
}

//@Entity(ignoredColumns = ["description"])
@Entity
data class Man(
    @PrimaryKey var userId: Int,
    var age: Int,
    var sex: Boolean,
) : Person()