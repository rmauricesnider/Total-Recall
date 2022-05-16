package com.example.totalrecall.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resources")
data class Resource (
    @PrimaryKey(autoGenerate = true)
    var resourceId: Int = 0,
    val title: String,
    val link: String,
    val dateAdded: String,
    val type: ResourceType,
    val description: String
)