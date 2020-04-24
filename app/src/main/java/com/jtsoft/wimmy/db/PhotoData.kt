package com.jtsoft.wimmy.db

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class LatLngData(
    var id : Long,
    val latlng: LatLng) : ClusterItem {
    override fun getPosition(): LatLng {
        return latlng
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
    var tag: String)


data class thumbnailData( var photo_id : Long,
                          var data : String)

