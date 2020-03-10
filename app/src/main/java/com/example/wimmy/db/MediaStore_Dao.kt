package com.example.wimmy.db

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
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
    private val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val noLocationData = "위치 정보 없음"

    fun getNameDir(context: Context) : ArrayList<thumbnailData>{
        val thumbList = arrayListOf<thumbnailData>()

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
        if(!cursorIsValid(cursor)) return thumbList

        do {
            val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            val allPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
            val file = File(allPath)
            val name = file.name
            val path = allPath.subSequence(0, (allPath.length - name.length - 1)).toString()

            thumbList.add(thumbnailData(id, path))
        } while (cursor!!.moveToNext())
        cursor.close()

        return thumbList
    }

    fun getLocation(context: Context, id : Long) : String{
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE
        )

        val selection = MediaStore.Images.ImageColumns._ID + " = " + id

        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        if(!cursorIsValid(cursor)) return noLocationData

        val lat = cursor!!.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
        val lon = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))
        val geo = Geocoder(context)

        var loc : String = noLocationData
        val locTmp = geo.getFromLocation(lat, lon, 1)
        if(locTmp != null && locTmp.isNotEmpty()) {
            loc = locTmp[0].countryName
            if(locTmp[0].locality != null) loc += " ${locTmp[0].locality}"
            if(locTmp[0].subLocality != null) loc += " ${locTmp[0].subLocality}"
        }

        cursor.close()

        return loc
    }

    fun getDateIdInfo(context: Context, cal : Calendar) : ArrayList<Long>{
        val IdList = arrayListOf<Long>()

        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID //photo_id
        )
        val selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + getDateStart(cal) + " AND " + getDateEnd(cal)

        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        if(!cursorIsValid(cursor)) return IdList

        do {
            val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            IdList.add(id)
        } while (cursor!!.moveToNext())
        cursor.close()

        return IdList
    }

    fun getNameDir(context: Context, path : String) : ArrayList<PhotoData>{
        val selection = MediaStore.Images.ImageColumns.DATA + " LIKE '" + path + "/%' AND " +
                MediaStore.Images.ImageColumns.DATA + " NOT LIKE '" + path + "/%/%'"
        return getDir(context, selection)
    }
    fun getLocationDir(context: Context, idList: List<Long>) : ArrayList<PhotoData>{
        return getDirByIdList(context, idList)
    }

    fun getDateDir(context: Context, cal : Calendar) : ArrayList<PhotoData>{
        val selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + getDateStart(cal) + " AND " + getDateEnd(cal)
        return getDir(context, selection)
    }

    fun getTagDir(context: Context, idList : List<Long>?) : ArrayList<PhotoData> {
        return getDirByIdList(context, idList)
    }

    fun getDir(context: Context, selection : String) : ArrayList<PhotoData>{
        val photoList = ArrayList<PhotoData>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DATA, // folder + name
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE,
            MediaStore.Images.ImageColumns.DATE_TAKEN //date
        )

        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        if(!cursorIsValid(cursor)) return photoList

        do {
            val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
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
        } while (cursor!!.moveToNext())
        cursor.close()

        return photoList
    }

    fun getDirByIdList(context: Context, idList: List<Long>?) : ArrayList<PhotoData> {
        if(idList.isNullOrEmpty()) return ArrayList()
        var selection = MediaStore.Images.ImageColumns._ID + " IN ("
        for( id in idList) {
            selection += "$id ,"
        }
        selection = selection.substring(0, selection.length - 1)
        selection += ")"

        return getDir(context, selection)
    }

    fun getLocationInfo(context: Context, id : Long) : String {
        var loc : String = noLocationData

        val projection = arrayOf(
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE
        )
        val selection =  MediaStore.Images.ImageColumns._ID + " = " + id

        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        if(!cursorIsValid(cursor)) return loc

        val lat = cursor!!.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
        val lon = cursor!!.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))

        val geo = Geocoder(context)
        val locTmp = geo.getFromLocation(lat, lon, 1)
        if(locTmp != null && locTmp.isNotEmpty()) {
            loc = locTmp[0].countryName
            if(locTmp[0].locality != null) loc += " ${locTmp[0].locality}"
            if(locTmp[0].subLocality != null) loc += " ${locTmp[0].subLocality}"
        }
        return loc
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

    private fun cursorIsValid(cursor: Cursor?) : Boolean {
        if (cursor == null) {
            Log.e("TAG", "cursor null or cursor is empty")
            return false
        }

        cursor.moveToFirst()
        if(cursor.count == 0) {
            cursor.close()
            return false
        }

        return true
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