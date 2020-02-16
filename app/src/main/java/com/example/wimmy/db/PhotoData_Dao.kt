package com.example.wimmy.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface PhotoData_Dao {
    @Insert(onConflict = REPLACE)
    fun insert(photoData: PhotoData)

    @Insert(onConflict = REPLACE)
    fun insert(tagData: TagData)

    //폴더 별로 분류 기능 추가해야함
    @Query("SELECT * FROM photo_data group by file_path")
    fun getNameDir()

    @Query("SELECT * FROM photo_data group by location_info")
    fun getLocationDir()

    @Query("SELECT * FROM photo_data group by date_info")
    fun getDateDir()

    @Query("SELECT * FROM tag_data group by tag")
    fun getDateTag()
}
