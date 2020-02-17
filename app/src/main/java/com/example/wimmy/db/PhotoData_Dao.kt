package com.example.wimmy.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface PhotoData_Dao {
    @Insert(onConflict = REPLACE)
    fun insert(photoData: PhotoData)
    @Insert(onConflict = REPLACE)
    fun insert(tagData: TagData)

    @Update
    fun update(photoData: PhotoData)
    @Update
    fun update(tagData: TagData)

    @Delete
    fun delete(photoData: PhotoData)
    @Delete
    fun delete(tagData: TagData)

    @Query("SELECT thumbnail_path, file_path FROM photo_data LIMIT 1")
    fun getNameDir()
    @Query("SELECT thumbnail_path, location_info FROM photo_data group by location_info")
    fun getLocationDir()
    @Query("SELECT thumbnail_path, date_info FROM photo_data group by date_info")
    fun getDateDir()
    @Query("SELECT thumbnail_path, tag FROM photo_data, tag_data group by tag")
    fun getTagDir()

    @Query("SELECT * FROM photo_data where name = :name")
    fun getNameDir(name : String)
    @Query("SELECT * FROM photo_data where date_info = :date")
    fun getDateDir(date : Int)
    @Query("SELECT * FROM photo_data where name = :tag")
    fun getTagDir(tag : String)
}
