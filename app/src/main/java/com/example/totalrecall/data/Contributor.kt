package com.example.totalrecall.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE


@Entity(tableName = "contributors",
    foreignKeys = [ForeignKey(entity = Resource::class, parentColumns = arrayOf("resourceId"), childColumns = arrayOf("resourceId"), onDelete = CASCADE)],
    primaryKeys = ["resourceId", "position"]
)
data class Contributor (
    val name: String,
    val contribution: String,
    var resourceId: Int,
    val position: Int
)