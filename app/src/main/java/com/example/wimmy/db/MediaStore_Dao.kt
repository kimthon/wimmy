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
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.Main_Map.Companion.latlngdata
import com.google.android.gms.maps.model.LatLng
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

    fun getNameDir(adapter : RecyclerAdapterPhoto, path : String) : Cursor?{
        val selection = MediaStore.Images.ImageColumns.DATA + " LIKE '" + path + "/%' AND " +
                MediaStore.Images.ImageColumns.DATA + " NOT LIKE '" + path + "/%/%'"
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DATA, // folder + name
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE,
            MediaStore.Images.ImageColumns.DATE_TAKEN //date
        )

        val cursor = adapter.context!!.contentResolver.query(uri, projection, selection, null, null)
        return cursor
    }
    fun getLocationDir(adapter: RecyclerAdapterPhoto, idList: List<Long>?) : Cursor?{
        return getDirByIdList(adapter, idList)
    }

    fun getDateDir(adapter: RecyclerAdapterPhoto, cal : Calendar) : Cursor? {
        val selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + getDateStart(cal) + " AND " + getDateEnd(cal)
        return getDir(adapter, selection)
    }

    fun getTagDir(adapter: RecyclerAdapterPhoto, idList : List<Long>?) : Cursor? {
        return getDirByIdList(adapter, idList)
    }

    fun getDir(adapter: RecyclerAdapterPhoto, selection : String) : Cursor? {
        val photoList = ArrayList<PhotoData>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DATA, // folder + name
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE,
            MediaStore.Images.ImageColumns.DATE_TAKEN //date
        )

        val cursor = adapter.context!!.contentResolver.query(uri, projection, selection, null, null)
        return cursor
    }

    fun getDataById(adapter: RecyclerAdapterPhoto, id : Long) : PhotoData? {
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val cursor = getDir(adapter, selection)
        if(cursorIsValid(cursor)) {
            return getPhotoData(cursor!!)
        }
        else return null
    }

    fun getDirByIdList(adapter: RecyclerAdapterPhoto, idList: List<Long>?) : Cursor? {
        if(idList.isNullOrEmpty()) return null
        var selection = MediaStore.Images.ImageColumns._ID + " IN ("
        for( id in idList) {
            selection += "$id ,"
        }
        selection = selection.substring(0, selection.length - 1)
        selection += ")"

        return getDir(adapter, selection)
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

    fun getPhotoData(cursor: Cursor) : PhotoData {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
        val allPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
        val file = File(allPath)
        val name = file.name
        val path = allPath.subSequence(0, (allPath.length - name.length - 1)).toString()

        // 위/경도 저장
        val lat = cursor!!.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
        Log.d("TAG12", "${lat}")
        val lon = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))
        latlngdata.add(LatLngData(LatLng(lat,lon)))

        val dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))
        val date = Date(dateTaken)

        var loc : String? = noLocationData

        val photoData = PhotoData(id, name, path, loc, date, false)
        return photoData
    }

    fun cursorIsValid(cursor: Cursor?) : Boolean {
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