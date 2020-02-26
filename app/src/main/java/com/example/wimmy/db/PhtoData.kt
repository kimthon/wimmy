package com.example.wimmy.db

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import java.util.*

@Entity(tableName = "photo_data")
class PhotoData(@PrimaryKey(autoGenerate = true) var photo_id: Long,
                @ColumnInfo(name = "name") var name : String,
                @ColumnInfo(name = "file_path") var file_path : String,
                @ColumnInfo(name = "thumbnail_path") var thumbnail_path : String,
                @ColumnInfo(name = "location_info") var location_info : String?,
                @ColumnInfo(name = "date_info") var date_info : Date?,
                @ColumnInfo(name = "favorite") var favorite : Boolean)


@Entity(tableName = "tag_data",
    primaryKeys = ["photo_id", "tag"],
    foreignKeys = [ForeignKey(entity = PhotoData::class,
        parentColumns = arrayOf("photo_id"),
        childColumns = arrayOf("photo_id")
    )]
)
class TagData(var photo_id: Long,
              var tag : String,
              @ColumnInfo var type : String)

data class thumbnailData( var thumbnail_path: String,
                             var data : String ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(thumbnail_path)
        parcel.writeString(data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<thumbnailData> {
        override fun createFromParcel(parcel: Parcel): thumbnailData {
            return thumbnailData(parcel)
        }

        override fun newArray(size: Int): Array<thumbnailData?> {
            return arrayOfNulls(size)
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value : Long?) : Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date : Date?) : Long? {
        return date?.time
    }
}