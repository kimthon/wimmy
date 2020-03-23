package com.example.wimmy.db

import android.database.Cursor
import androidx.lifecycle.LiveData
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

    @Query("UPDATE extra_photo_data SET favorite =:favorite WHERE photo_id = :id")
    fun update(id: Long, favorite : Boolean)

    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id")
    fun deleteTagById(photo_id: Long)
    @Query("DELETE FROM extra_photo_data WHERE photo_id = :photo_id")
    fun deleteExtraById(photo_id: Long)

    @Query("DELETE FROM tag_data WHERE photo_id = :photo_id AND tag = :tag")
    fun delete(photo_id : Long, tag : String)

    @Query("SELECT tag FROM tag_data WHERE photo_id IN (:idList) GROUP BY tag ORDER BY count(*) ASC LIMIT 5")
    fun getDateInfo(idList : List<Long>) : List<String>
    @Query("SELECT MAX(photo_id) as photo_id, tag as data FROM tag_data GROUP BY tag")
    fun getTagDir() : LiveData<List<thumbnailData>>
    @Query("SELECT MAX(photo_id) as photo_id, location as data FROM extra_photo_data GROUP BY location HAVING NOT location = '위치 정보 없음'")
    fun getLocationDir() : LiveData<List<thumbnailData>>

    @Query("SELECT MAX(photo_id) as photo_id, location as data FROM extra_photo_data GROUP BY location HAVING location LIKE '%' || :location || '%'")
    fun getLocationDirSearch(location: String) : List<thumbnailData>
    @Query("SELECT photo_id as photo_id, tag as data FROM tag_data GROUP BY tag HAVING tag LIKE '%' || :tags || '%'")
    fun getTagDirSearch(tags: String) : List<thumbnailData>

    @Query("SELECT photo_id FROM tag_data where tag = :tag")
    fun getTagDir(tag : String) : Cursor
    @Query("SELECT photo_id FROM extra_photo_data where location = :location")
    fun getLocationDir(location : String) : Cursor
    @Query("SELECT photo_id FROM extra_photo_data where favorite = 1")
    fun getFavoriteDir() : Cursor


    @Query("SELECT location FROM extra_photo_data WHERE photo_id = :id")
    fun getLocationById(id : Long) : String?
    @Query("SELECT tag FROM tag_data WHERE photo_id = :id")
    fun getTagsById(id : Long) : List<String>
    @Query("SELECT favorite FROM extra_photo_data WHERE photo_id = :id")
    fun getFavoriteById(id : Long) : Boolean?
    @Query("SELECT * FROM extra_photo_data WHERE photo_id = :id")
    fun getExtraPhotoDataById(id : Long) : ExtraPhotoData?


    @Query("SELECT photo_id FROM extra_photo_data ")
    fun getIdCursor() : Cursor

    @Query("DELETE FROM extra_photo_data")
    fun dropExtraTable()

    @Query("DELETE FROM tag_data")
    fun dropTagTable()
}
