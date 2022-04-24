package com.example.totalrecall.data

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag (
        @PrimaryKey(autoGenerate = true)
        val tagId: Int = 0,
        @ColumnInfo(index = true)
        val name: String,
        val color: String
)
