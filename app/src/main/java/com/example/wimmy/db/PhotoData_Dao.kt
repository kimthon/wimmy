package com.example.wimmy.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface PhotoData_Dao {
    @Insert(onConflict = REPLACE)
    fun insert(tagData: TagData)

    @Update
    fun update(tagData: TagData)

    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id")
    fun deleteTagById(photo_id: Long)

    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id AND tag = :tag")
    fun delete(photo_id : Long, tag : String)

    @Query("SELECT tag FROM tag_data WHERE photo_id IN (:idList) GROUP BY tag ORDER BY count(*) LIMIT 1")
    fun getDateInfo(idList : List<Long>) : String
    @Query("SELECT MAX(photo_id) as photo_id, tag as data FROM tag_data GROUP BY tag")
    fun getTagDir() : List<thumbnailData>

    @Query("SELECT photo_id FROM tag_data where tag = :tag")
    fun getTagDir(tag : String) : List<Long>

    @Query("SELECT tag FROM tag_data WHERE photo_id = :id")
    fun getTag(id : Long) : List<String>

}
