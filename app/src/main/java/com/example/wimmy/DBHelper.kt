package com.example.wimmy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context : Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val db = this.writableDatabase

    companion object {
        private val DB_NAME = "WimmyDB"
        private val DB_VERSION = 1
    }

    //DB 생성시 실행
    override fun onCreate(db: SQLiteDatabase?) {
        val photoInfoSQL = "create table photo_info(" +
                "photo_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
                "filePath TEXT NOT NULL," +
                "thumbnailPath TEXT NOT NULL," +
                "location_info TEXT," +
                "date_info NUMERIC, " +
                "favorite BOOLEAN NOT NULL)"

        val tagInfoSQL =  "create table tag_info(" +
                "FOREIGN KEY(photo_id) INTEGER NOT NULL PRIMARY KEY REFERENCES photo_info(photo_id)," +
                "tag TEXT NOT NULL PRIMARY KEY," +
                "tag_type TEXT NOT NULL)" //서브타입 어떻게 하는 걸까

        //db?.execSQL(photoInfoSQL)
        //db?.execSQL(tagInfoSQL)
    }

    //DB 버전 변경시 실행 할 코드
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table photo_info") //photo_info 제거
        onCreate(db) //재생성
    }

    fun InsertPhoto(photo : PhotoData) : Int {
        var photo_id = 0

        val data = ContentValues().apply {
            put("filePath", photo.file_path)
            put("thumbnailPath", photo.thumbnail_path)
            put("location_info", photo.location_info)
            put("date_info", photo.date_info)
            put("favorite", photo.favorite)
        }

        db.insert("photo_info", null, data)
        db.close()

        return photo_id
    }

    fun InsertTag(phtoID : Int, tag : String, tagType : String) {
        val data = ContentValues().apply {
            put("photo_id", phtoID)
            put("tag", tag)
            put("tag_type", tagType)
        }

        db.insert("tag_info", null, data)
        db.close()
    }

    fun UpdatePhoto(photo : PhotoData) {
        val data = ContentValues().apply {
            put("filePath", photo.file_path)
            put("thumbnailPath", photo.thumbnail_path)
            put("location_info", photo.location_info)
            put("date_info", photo.date_info)
            put("favorite", photo.favorite)
        }

        //db.update("photoData", data, )

    }

    fun ModifyTag(photoID : Int, tag : String) {

    }
}
