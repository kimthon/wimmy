package com.example.wimmy.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import java.util.*

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
    fun deleteById(photo_id : Long)
    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id")
    fun deleteTagById(photo_id: Long)

    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id AND tag = :tag")
    fun delete(photo_id : Long, tag : String)

    @Query("SELECT photo_id, file_path as data FROM photo_data WHERE photo_id IN (SELECT MAX(photo_id) FROM photo_data GROUP BY file_path) ORDER BY data")
    fun getNameDir() : LiveData<List<thumbnailData>>
    @Query("SELECT photo_id, location_info as data FROM photo_data WHERE photo_id IN (SELECT MAX(photo_id) FROM photo_data GROUP BY location_info) ORDER BY data")
    fun getLocationDir() : LiveData<List<thumbnailData>>
    @Query("SELECT tag FROM photo_data, tag_data WHERE date_info BETWEEN :from AND :to AND photo_data.photo_id = tag_data.photo_id GROUP BY tag ORDER BY count(*) LIMIT 1")
    fun getDateInfo(from: Date, to : Date) : String
    @Query("SELECT photo_data.photo_id, tag as data FROM photo_data, (SELECT MAX(photo_id) as photo_id, tag FROM tag_data GROUP BY tag) tag_data WHERE photo_data.photo_id = tag_data.photo_id ORDER BY data")
    fun getTagDir() : LiveData<List<thumbnailData>>

    @Query("SELECT * FROM photo_data where name = :name")
    fun getNameDir(name : String) : LiveData<List<PhotoData>>
    @Query("SELECT * FROM photo_data where name = :loc")
    fun getLocationDir(loc : String) : LiveData<List<PhotoData>>
    @Query("SELECT * FROM photo_data where date_info = :date")
    fun getDateDir(date : Date) : LiveData<List<PhotoData>>
    @Query("SELECT * FROM photo_data where name = :tag")
    fun getTagDir(tag : String) : LiveData<List<PhotoData>>

    @Query("SELECT photo_id FROM photo_data WHERE photo_id = :photo_id")
    fun IsInserted(photo_id: Long) : Long?
    @Query("SELECT count(*) FROM photo_data")
    fun getSize() : Int
}
