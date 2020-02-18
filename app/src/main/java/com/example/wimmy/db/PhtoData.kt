package com.example.wimmy.db

import androidx.annotation.ColorLong
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "photo_data")
class PhotoData(@PrimaryKey(autoGenerate = true) var photo_id: Int,
                @ColumnInfo(name = "name") var name : String,
                @ColumnInfo(name = "file_path") var file_path : String,
                @ColumnInfo(name = "thumbnail_path") var thumbnail_path : String,
                @ColumnInfo(name = "location_info") var location_info : String,
                @ColumnInfo(name = "date_info") var date_info : Int,
                @ColumnInfo(name = "favorite") var favorite : Boolean)


@Entity(tableName = "tag_data",
    primaryKeys = ["photo_id", "tag"],
    foreignKeys = arrayOf(
        ForeignKey(entity = PhotoData::class,
            parentColumns = arrayOf("photo_id"),
            childColumns = arrayOf("photo_id")
            )
    )
)
class TagData(var photo_id: Int,
              var tag : String,
              @ColumnInfo var type : String)

data class thumbnailData( var thumbnail_path: String,
                             var data : String )

