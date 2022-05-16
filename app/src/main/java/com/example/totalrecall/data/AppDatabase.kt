package com.example.totalrecall.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.totalrecall.data.Resource
import com.example.totalrecall.data.ResourceDAO
import com.example.totalrecall.data.ResourceTagRel
import com.example.totalrecall.data.Tag

private const val NAME = "app-database"

@Database(entities = [Resource::class, Tag::class, ResourceTagRel::class, Contributor::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resourceDao(): ResourceDAO
    //abstract fun tagDao(): TagDAO

    companion object { //Part of a singleton design

        @Volatile private var instance: AppDatabase? = null

        //Check if instance exists, if not, create it
        //synchronized() places a lock to ensure only one database is built
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        fun destroyInstance( ) {
            instance = null
        }

        private fun buildDatabase(context: Context):AppDatabase {
            return Room.databaseBuilder(
                context, AppDatabase::class.java, NAME
            ).build()
        }

    }
}