package com.example.wimmy.db

import androidx.lifecycle.LiveData
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

    @Query("DELETE FROM photo_data WHERE photo_id = :photo_id")
    fun deleteById(photo_id : Int)
    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id")
    fun deleteTagById(photo_id: Int)

    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id AND tag = :tag")
    fun delete(photo_id : Int, tag : String)

    @Query("SELECT thumbnail_path, file_path as data FROM photo_data WHERE photo_id IN (SELECT MAX(photo_id) FROM photo_data GROUP BY file_path)")
    fun getNameDir() : LiveData<List<thumbnailData>>
    @Query("SELECT thumbnail_path, location_info as data FROM photo_data WHERE photo_id IN (SELECT MAX(photo_id) FROM photo_data GROUP BY location_info)")
    fun getLocationDir() : LiveData<List<thumbnailData>>
    @Query("SELECT thumbnail_path, date_info as data FROM photo_data WHERE photo_id IN (SELECT MAX(photo_id) FROM photo_data GROUP BY date_info)")
    fun getDateDir() : LiveData<List<thumbnailData>>
    @Query("SELECT thumbnail_path, tag as data FROM photo_data, (SELECT MAX(photo_id) as photo_id, tag FROM tag_data GROUP BY tag) tag_data WHERE photo_data.photo_id = tag_data.photo_id")
    fun getTagDir() : LiveData<List<thumbnailData>>

    @Query("SELECT * FROM photo_data where name = :name")
    fun getNameDir(name : String) : LiveData<List<PhotoData>>
    @Query("SELECT * FROM photo_data where name = :loc")
    fun getLocationDir(loc : String) : LiveData<List<PhotoData>>
    @Query("SELECT * FROM photo_data where date_info = :date")
    fun getDateDir(date : Int) : LiveData<List<PhotoData>>
    @Query("SELECT * FROM photo_data where name = :tag")
    fun getTagDir(tag : String) : LiveData<List<PhotoData>>
}
