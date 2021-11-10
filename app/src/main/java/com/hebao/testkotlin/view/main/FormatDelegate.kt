package com.hebao.testkotlin.view.main

import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by chasing on 2021/11/10.
 */
class FormatDelegate{
    private var formattedString: String = ""

    operator fun getValue(thisRef: Any, property: KProperty<*>): String {
        return formattedString
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        formattedString = value.lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}