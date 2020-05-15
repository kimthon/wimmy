package com.jtsoft.wimmy.db

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.util.*

class LatLngData(
    var id : Long,
    val latlng: LatLng) : ClusterItem {
    override fun getPosition(): LatLng {
        return latlng
    }
}

class checkboxData(
    var id: Long,
    var checked: Boolean)

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
    var tag: String)

@Entity(tableName = "cal_data",
    primaryKeys = ["date"]
)
class CalendarData(
    var date : Date,
    var title : String,
    var memo : String?)

data class thumbnailData( var photo_id : Long,
                          var data : String)

class Converter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}