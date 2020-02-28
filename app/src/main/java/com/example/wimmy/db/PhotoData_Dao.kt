package com.example.wimmy.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import java.util.*

@Dao
interface PhotoData_Dao {
    @Insert(onConflict = REPLACE)
    fun insert(photoData: PhotoData) : Long
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

    @Query("SELECT photo_id as thumbnail_path, file_path as data FROM photo_data WHERE photo_id IN (SELECT MAX(photo_id) FROM photo_data GROUP BY file_path) ORDER BY data")
    fun getNameDir() : LiveData<List<thumbnailData>>
    @Query("SELECT thumbnail_path, location_info as data FROM photo_data WHERE photo_id IN (SELECT MAX(photo_id) FROM photo_data GROUP BY location_info) ORDER BY data")
    fun getLocationDir() : LiveData<List<thumbnailData>>
    @Query("SELECT tag FROM photo_data, tag_data WHERE date_info BETWEEN :from AND :to AND photo_data.photo_id = tag_data.photo_id GROUP BY tag ORDER BY count(*) LIMIT 1")
    fun getDateInfo(from: Date, to : Date) : String
    @Query("SELECT thumbnail_path, tag as data FROM photo_data, (SELECT MAX(photo_id) as photo_id, tag FROM tag_data GROUP BY tag) tag_data WHERE photo_data.photo_id = tag_data.photo_id ORDER BY data")
    fun getTagDir() : LiveData<List<thumbnailData>>

    @Query("SELECT * FROM photo_data where file_path = :name")
    fun getNameDir(name : String) : LiveData<List<PhotoData>>
    @Query("SELECT * FROM photo_data where location_info = :loc")
    fun getLocationDir(loc : String) : LiveData<List<PhotoData>>
    @Query("SELECT * FROM photo_data where date_info = :date")
    fun getDateDir(date : Int) : LiveData<List<PhotoData>>
    @Query("SELECT photo_data.* FROM photo_data, tag_data where (photo_data.photo_id = tag_data.photo_id) AND (tag_data.tag = :tag)")
    fun getTagDir(tag : String) : LiveData<List<PhotoData>>

    @Query("SELECT tag_data.photo_id, tag_data.tag, tag_data.type FROM photo_data, tag_data WHERE ((photo_data.file_path = :name) AND (photo_data.photo_id = tag_data.photo_id)) ORDER BY tag")
    fun getNameTag(name : String) : LiveData<List<TagData>>
    @Query("SELECT tag_data.photo_id, tag_data.tag, tag_data.type FROM photo_data, tag_data WHERE ((photo_data.location_info = :loc) AND (photo_data.photo_id = tag_data.photo_id)) ORDER BY tag")
    fun getLocationTag(loc : String) : LiveData<List<TagData>>
    @Query("SELECT tag_data.photo_id, tag_data.tag, tag_data.type FROM photo_data, tag_data WHERE ((photo_data.date_info = :date) AND (photo_data.photo_id = tag_data.photo_id)) ORDER BY tag")
    fun getDateTag(date : Int) : LiveData<List<TagData>>
    @Query("SELECT tag_data.photo_id, tag_data.tag, tag_data.type FROM photo_data, tag_data WHERE ((tag_data.tag = :tag) AND (photo_data.photo_id = tag_data.photo_id)) ORDER BY tag")
    fun getTagTag(tag : String) : LiveData<List<TagData>>

    @Query("SELECT count(*) FROM photo_data")
    fun getSize() : Int
}
