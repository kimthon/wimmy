package com.example.wimmy.db

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import java.util.*


fun Parcel.writeDate(date: Date?) {
    writeLong(date?.time ?: -1)
}

fun Parcel.readDate(): Date? {
    val long = readLong()
    return if (long != -1L) Date(long) else null
}


class PhotoData(var photo_id: Long,
                var name : String,
                var file_path : String,
                var location_info : String?,
                var date_info : Date?,
                var favorite : Boolean): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDate(),
        parcel.readByte() != 0.toByte()
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(photo_id)
        parcel.writeString(name)
        parcel.writeString(file_path)
        parcel.writeString(location_info)
        parcel.writeDate(date_info)
        parcel.writeByte(if (favorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotoData> {
        override fun createFromParcel(parcel: Parcel): PhotoData {
            return PhotoData(parcel)
        }

        override fun newArray(size: Int): Array<PhotoData?> {
            return arrayOfNulls(size)
        }
    }
}

@Entity(tableName = "extra_photo_data",
    primaryKeys = ["photo_id"])
class ExtraPhotoData(
    var photo_id: Long,
    var location : String?,
    var favorite: Boolean
)

@Entity(tableName = "tag_data",
    primaryKeys = ["photo_id", "tag"]
)
class TagData(
    var photo_id: Long,
    var tag: String,
    @ColumnInfo var type: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(photo_id)
        parcel.writeString(tag)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TagData> {
        override fun createFromParcel(parcel: Parcel): TagData {
            return TagData(parcel)
        }

        override fun newArray(size: Int): Array<TagData?> {
            return arrayOfNulls(size)
        }
    }
}


data class thumbnailData( var photo_id : Long,
                          var data : String)
