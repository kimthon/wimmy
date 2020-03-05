package com.example.wimmy.db

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Geocoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object MediaStore_Dao {
    val noLocationData = "위치 정보 없음"
    //@Query("SELECT photo_id, file_path as data FROM photo_data WHERE photo_id IN (SELECT MAX(photo_id) FROM photo_data GROUP BY file_path) ORDER BY data")
    fun getNameDir(context: Context) : ArrayList<thumbnailData>{
        val dirList = arrayListOf<thumbnailData>()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DATA// folder + name
        )
        val selection = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.ImageColumns._ID + " IN (SELECT " + MediaStore.Images.ImageColumns._ID +
                    " FROM images GROUP BY " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + ")"
        }else {
            "1) GROUP BY (" + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
        }
        val sortOrder = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " ASC"

        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)


        if (cursor == null) {
            Log.e("TAG", "cursor null or cursor is empty")
            return dirList
        }
        cursor.moveToFirst()
        if(cursor.count == 0) {
            cursor.close()
            return dirList
        }
        do {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            val allPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
            val file = File(allPath)
            val name = file.name
            val path = allPath.subSequence(0, (allPath.length - name.length - 1)).toString()

            dirList.add(thumbnailData(id, path))
        } while (cursor.moveToNext())
        cursor.close()

        return dirList
    }

    //@Query("SELECT photo_id, location_info as data FROM photo_data WHERE photo_id IN (SELECT MAX(photo_id) FROM photo_data GROUP BY location_info) ORDER BY data")
    fun getLocationDir(context: Context) : List<thumbnailData>{
        val thumbList = arrayListOf<thumbnailData>()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE
        )

        val selection = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.ImageColumns._ID + " IN (SELECT " + MediaStore.Images.ImageColumns._ID +
                    " FROM images GROUP BY " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " OR " + MediaStore.Images.ImageColumns.LONGITUDE+ ")"
        }else {
            "1) GROUP BY (" + MediaStore.Images.ImageColumns.LATITUDE + " OR " + MediaStore.Images.ImageColumns.LONGITUDE
        }
        val cursor = context.contentResolver.query(uri, projection, selection, null, null)


        if (cursor == null) {
            Log.e("TAG", "cursor null or cursor is empty")
            return thumbList
        }

        cursor.moveToFirst()
        if(cursor.count == 0){
            cursor.close()
            return thumbList
        }

        do {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))

            val lat = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
            val lon = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))
            val geo = Geocoder(context)

            var loc : String = noLocationData
            val locTmp = geo.getFromLocation(lat, lon, 1)
            if(locTmp != null && locTmp.isNotEmpty()) {
                loc = locTmp[0].countryName
                if(locTmp[0].locality != null) loc += " ${locTmp[0].locality}"
                if(locTmp[0].subLocality != null) loc += " ${locTmp[0].subLocality}"
            }
            thumbList.add(thumbnailData(id, loc))
        } while (cursor.moveToNext())
        cursor.close()

        return thumbList
    }
    //@Query("SELECT tag FROM photo_data, tag_data WHERE date_info BETWEEN :from AND :to AND photo_data.photo_id = tag_data.photo_id GROUP BY tag ORDER BY count(*) LIMIT 1")
    fun getDateIdInfo(context: Context, cal : Calendar) : ArrayList<Long>{
        val IdList = arrayListOf<Long>()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DATA
        )
        val selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + getDateStart(cal) + " AND " + getDateEnd(cal)

        val cursor = context.contentResolver.query(uri, projection, selection, null, null)


        if (cursor == null) {
            Log.e("TAG", "cursor null or cursor is empty")
            return IdList
        }

        cursor.moveToFirst()
        if(cursor.count == 0){
            cursor.close()
            return IdList
        }

        do {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            IdList.add(id)
        } while (cursor.moveToNext())
        cursor.close()

        return IdList
    }

    //@Query("SELECT photo_data.photo_id, tag as data FROM photo_data, (SELECT MAX(photo_id) as photo_id, tag FROM tag_data GROUP BY tag) tag_data WHERE photo_data.photo_id = tag_data.photo_id ORDER BY data")
    //fun getTagDir(context: Context) : ArrayList<thumbnailData> room에 구현됨

    //@Query("SELECT * FROM photo_data where file_path = :name")
    fun getNameDir(context: Context, path : String) : ArrayList<PhotoData>{
        val selection = MediaStore.Images.ImageColumns.DATA + " LIKE '" + path + "/%' AND " +
                MediaStore.Images.ImageColumns.DATA + " NOT LIKE '" + path + "/%/%'"
        return getDir(context, selection)
    }
    //@Query("SELECT * FROM photo_data where location_info = :loc")
    fun getLocationDir(context: Context, loc : String) : ArrayList<PhotoData>{
        var selection : String?
        if(loc.compareTo(noLocationData) == 0) {
            selection = MediaStore.Images.ImageColumns.LATITUDE + " IS NULL AND " +
                    MediaStore.Images.ImageColumns.LONGITUDE + " IS NULL"
        } else {
            val geo = Geocoder(context)
            var lat: Double = 0.0
            var lon: Double = 0.0
            val adress = geo.getFromLocationName(loc, 1)
            if (adress.isNotEmpty()) {
                lat = adress[0].latitude
                lon = adress[0].longitude
            }

            selection = MediaStore.Images.ImageColumns.LATITUDE + "='" + lat + "' AND " +
                    MediaStore.Images.ImageColumns.LONGITUDE + " = '" + lon + "'"

        }

        return getDir(context, selection)
    }
    //@Query("SELECT * FROM photo_data where date_info = :date")
    fun getDateDir(context: Context, cal : Calendar) : ArrayList<PhotoData>{
        val selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + getDateStart(cal) + " AND " + getDateEnd(cal)
        return getDir(context, selection)
    }
    //@Query("SELECT photo_data.* FROM photo_data, tag_data where (photo_data.photo_id = tag_data.photo_id) AND (tag_data.tag = :tag)")
    fun getTagDir(context: Context, vm: PhotoViewModel, tag : String) : ArrayList<PhotoData> {
        val idList = vm.getTagDirIdList(tag)

        var selection = MediaStore.Images.ImageColumns._ID + " IN ("
        for( id in idList) {
            selection += "$id ,"
        }
        selection = selection.substring(0, selection.length - 1)
        selection += ")"

        return getDir(context, selection)
    }

    fun getDir(context: Context, selection : String) : ArrayList<PhotoData>{
        val photoList = ArrayList<PhotoData>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DATA, // folder + name
            MediaStore.Images.Thumbnails.DATA,
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE,
            MediaStore.Images.ImageColumns.DATE_TAKEN //date
        )

        val cursor = context.contentResolver.query(uri, projection, selection, null, null)

        if (cursor == null) {
            Log.e("TAG", "cursor null or cursor is empty")
            return photoList
        }

        cursor.moveToFirst()
        if(cursor.count == 0) {
            cursor.close()
            return photoList
        }

        do {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            val allPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
            val file = File(allPath)
            val name = file.name
            val path = allPath.subSequence(0, (allPath.length - name.length - 1)).toString()

            val dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))
            val date = Date(dateTaken)

            val lat = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
            val lon = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))
            val geo = Geocoder(context)
            var loc : String? = noLocationData
            val locTmp = geo.getFromLocation(lat, lon, 1)
            if(locTmp != null && locTmp.isNotEmpty()) {
                loc = locTmp[0].countryName
                if(locTmp[0].locality != null) loc += " ${locTmp[0].locality}"
                if(locTmp[0].subLocality != null) loc += " ${locTmp[0].subLocality}"
            }
            photoList.add(PhotoData(id, name, path, loc, date, false))
        } while (cursor.moveToNext())
        cursor.close()

        return photoList
    }

    fun LoadThumbnailById(context: Context, id : Long) : Bitmap{
        var bitmap : Bitmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            context.contentResolver.loadThumbnail( uri, Size(100, 100), null)
        }
        else {
            MediaStore.Images.Thumbnails.getThumbnail( context.contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
        }
        return modifyOrientaionById(context, id, bitmap)
    }

    fun modifyOrientaionById(context: Context, id: Long, bitmap: Bitmap) : Bitmap {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.ORIENTATION
        )
        val selection = MediaStore.Images.ImageColumns._ID + "=" + id
        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        cursor!!.moveToFirst()

        val orientation = cursor.getFloat(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION))
        return rotateBitmap(bitmap, orientation)
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees : Float) : Bitmap{
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getDateStart(cal : Calendar) : Long{
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time.time
    }

    private fun getDateEnd(cal : Calendar) : Long{
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.time.time
    }
}