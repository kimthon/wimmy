package com.example.wimmy

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.ExifInterface
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.db.PhotoViewModel
import java.io.File
import java.util.*

class PhotoScanner() {
    fun addAllImages(activity: Activity, vm : PhotoViewModel) {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, //forder path + filename
            MediaStore.Images.ImageColumns.DATE_TAKEN //date
        )

        val cursor = activity.contentResolver.query(uri, projection, null, null, null)

        if (cursor == null || !cursor.moveToFirst()) {
            Log.e("TAG", "cursor null or cursor is empty")
            return;
        }

        do {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            if(!vm.IsInserted(id)) {
                val allPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
                val file = File(allPath)
                val filePath = file.path
                val name = file.name

                val dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))
                val date = Date(dateTaken)

                val loc = getLocation(allPath, activity.applicationContext)
            }
        } while (cursor.moveToNext())
    }

    private fun getLocation(path : String, context: Context) : String?{
        val exif = ExifInterface(path)
        val attrLat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) //위도
        val attrLat_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) //남반구 북반구
        val attrLon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) //경도
        val attrLon_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF) //서반구 동반구

        if((attrLat == null) || (attrLat_REF != null) || (attrLon != null) || (attrLat_REF != null)) return null

        var lat = convertToDegree(attrLat)
        var lon = convertToDegree(attrLon)

        if((attrLat_REF == "S") && (lat != null)) {
            lat = -lat
        }

        if((attrLon_REF == "W") && (lon != null)) {
            lon = -lon
        }

        var adress : List<Address>? = null
        try {
            if((lat != null) && (lon !=null)) {
                val geo = Geocoder(context)
                adress = geo.getFromLocation(lat, lon, 1)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }

        if(adress != null && adress.isNotEmpty()) {
            return adress.get(0).toString()
        }
        else return null
    }

    private fun convertToDegree(str : String) : Double?{
        var result : Double? = null
        var strDMS = str
        try {
            strDMS = strDMS.replace("/1", ":")
            strDMS = strDMS.replace("/60", ":")
            strDMS = strDMS.replace("/3600", ":")
            result = Location.convert(strDMS)
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return result
    }
}