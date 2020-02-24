package com.example.wimmy.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PhotoData::class, TagData::class], version = 1)
@TypeConverters(Converters::class)
abstract class PhotoDB: RoomDatabase() {
    abstract fun PhotoData_Dao() : PhotoData_Dao

    companion object {
        private var INSTANCE : PhotoDB? = null

        //singleton patton
        fun getInstance(context: Context) : PhotoDB? {
            if(INSTANCE == null) {
                //synchronized : 중복 방지
                synchronized(PhotoDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        PhotoDB::class.java, "photo.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}