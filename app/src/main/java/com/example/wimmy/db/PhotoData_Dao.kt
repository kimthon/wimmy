package com.example.wimmy.db

import android.database.Cursor
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface PhotoData_Dao {
    @Insert(onConflict = REPLACE)
    fun insert(tagData: TagData)

    @Insert(onConflict = REPLACE)
    fun insert(extraPhotoData: ExtraPhotoData)

    @Update
    fun update(tagData: TagData)

    @Update
    fun update(tagData: ExtraPhotoData)

    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id")
    fun deleteTagById(photo_id: Long)
    @Query("DELETE FROM extra_photo_data WHERE photo_id = :photo_id")
    fun deleteExtraById(photo_id: Long)

    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id AND tag = :tag")
    fun delete(photo_id : Long, tag : String)

    @Query("SELECT tag FROM tag_data WHERE photo_id IN (:idList) GROUP BY tag ORDER BY count(*) LIMIT 1")
    fun getDateInfo(idList : List<Long>) : String
    @Query("SELECT MAX(photo_id) as photo_id, tag as data FROM tag_data GROUP BY tag")
    fun getTagDir() : List<thumbnailData>
    @Query("SELECT MAX(photo_id) as photo_id, location as data FROM extra_photo_data GROUP BY location")
    fun getLocationDir() : List<thumbnailData>

    @Query("SELECT photo_id FROM tag_data where tag = :tag")
    fun getTagDir(tag : String) : Cursor
    @Query("SELECT photo_id FROM extra_photo_data where location = :location")
    fun getLocationDir(location : String) : Cursor
    @Query("SELECT photo_id FROM extra_photo_data where favorite = 'true'")
    fun getFavoriteDir() : Cursor

    @Query("SELECT location FROM extra_photo_data WHERE photo_id = :id")
    fun getLocation(id : Long) : String
    @Query("SELECT tag FROM tag_data WHERE photo_id = :id")
    fun getTags(id : Long) : List<String>
    @Query("SELECT favorite FROM extra_photo_data WHERE photo_id = :id")
    fun getFavorite(id : Long) : Boolean
    @Query("SELECT * FROM extra_photo_data WHERE photo_id = :id")
    fun getExtraPhotoData(id : Long) : ExtraPhotoData?

    @Query("SELECT photo_id FROM extra_photo_data WHERE photo_id = :id")
    fun IsItInserted(id : Long) : Long?

    @Query("SELECT photo_id FROM extra_photo_data ")
    fun getIdCursor() : Cursor

    @Query("DELETE FROM extra_photo_data")
    fun dropTable()
}
