package com.example.wimmy.db

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import java.util.*





class PhotoData(var photo_id: Long,
                var name : String,
                var file_path : String,
                var location_info : String?,
                var date_info : Date?,
                var favorite : Boolean)


@Entity(tableName = "extra_photo_data",
    primaryKeys = ["photo_id"])
class ExtraPhotoData(
    var photo_id: Long,
    var location : String,
    var favorite: Boolean
)

@Entity(tableName = "tag_data",
    primaryKeys = ["photo_id", "tag"]
)
class TagData(
    var photo_id: Long,
    var tag: String,
    @ColumnInfo var type: String)


data class thumbnailData( var photo_id : Long,
                          var data : String)
