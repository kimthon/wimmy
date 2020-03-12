package com.example.wimmy.db

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.util.*





class PhotoData(
    var photo_id: Long,
    var name: String,
    var file_path: String,
    var location_info: String?,
    var date_info: Date?,
    var favorite: Boolean
)

class LatLngData(
    var index : Int,
    var latlng: LatLng) : ClusterItem {
    override fun getPosition(): LatLng {
        return latlng
    }
}

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


