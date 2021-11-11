package com.shrimp.network.entity.res

/**
 * Created by chasing on 2021/11/11.
 */
open class TagItem {
    var TagId: Int = 0
    var ParentTagId: Int = 0
    var Name: String = ""
    var TagType: Byte = 0
    var TagWebJumpUrl: String = ""
    var Description: String = ""
    var ChildrenTag:List<TagItem>? = null
}