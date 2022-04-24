package com.example.totalrecall.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "resource_tag_join",
        primaryKeys = ["resourceId", "tagId"],
        foreignKeys = [
            ForeignKey(entity = Resource::class, parentColumns = arrayOf("resourceId"), childColumns = arrayOf("resourceId"), onDelete = CASCADE),
            ForeignKey(entity = Tag::class, parentColumns = arrayOf("tagId"), childColumns = arrayOf("tagId"))
        ])
data class ResourceTagRel(
        val resourceId: Int,
        @ColumnInfo(index = true)
        val tagId: Int
)
