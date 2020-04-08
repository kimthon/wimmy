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
import androidx.exifinterface.media.ExifInterface
import com.google.android.gms.maps.model.LatLng
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object MediaStore_Dao {
    private val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    const val noLocationData = "위치 정보 없음"
    const val sortdate = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"

    fun getNameDir(context: Context) : ArrayList<thumbnailData>{
        val thumbList = arrayListOf<thumbnailData>()
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
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
            val folder = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))

            thumbList.add(thumbnailData(id, folder))
        } while (cursor!!.moveToNext())
        cursor.close()

        return thumbList
    }

    fun getNameDirSearch(context: Context, name: String) : ArrayList<thumbnailData>{
        val thumbList = arrayListOf<thumbnailData>()
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.TITLE
        )
        val selection = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.ImageColumns._ID + " IN (SELECT " + MediaStore.Images.ImageColumns._ID +
                    " FROM images GROUP BY " + MediaStore.Images.ImageColumns.TITLE + " LIKE '%" + name
        }else {
            MediaStore.Images.ImageColumns.TITLE + " LIKE '%" + name + "%') GROUP BY (" + MediaStore.Images.ImageColumns.TITLE
        }
        val sortOrder = MediaStore.Images.ImageColumns.TITLE + " ASC"

        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)
        if(!cursorIsValid(cursor)) return thumbList

        do {
            val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            val title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE))

            thumbList.add(thumbnailData(id, title))
        } while (cursor!!.moveToNext())
        cursor.close()

        return thumbList
    }


    fun getDateDirSearch(context: Context, cal: Calendar) : ArrayList<thumbnailData>{
        var checkdays: Int = 0
        val thumbList = arrayListOf<thumbnailData>()
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATE_TAKEN
        )
        val selection = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.ImageColumns._ID + " IN (SELECT " + MediaStore.Images.ImageColumns._ID +
                    " FROM images GROUP BY " + MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + cal.time.time + " AND " + getDateEndSearch(cal)
        }else {
            MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + cal.time.time + " AND " + getDateEndSearch(cal)
        }
        val sortOrder = MediaStore.Images.ImageColumns.DATE_TAKEN + " ASC"

        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)
        if(!cursorIsValid(cursor)) return thumbList

        do {
            val date= cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))
            val days = SimpleDateFormat("d", Locale.getDefault()).format(date).toInt()

            if(days > checkdays) {
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                val formatter = SimpleDateFormat("YYYY년 MM월 dd일", Locale.getDefault())
                thumbList.add(thumbnailData(id, formatter.format(date)))
                checkdays = days
            }
        } while (cursor!!.moveToNext())
        cursor.close()

        return thumbList
    }

    fun getLocation(context: Context, id : Long) : String?{
        val latLng = getLatLngById(context, id) ?: return noLocationData

        val geo = Geocoder(context)
        var locString = ""
        try {
            val locTmp = geo.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (locTmp != null && locTmp.isNotEmpty()) {
                if (locTmp[0].adminArea != null) locString += locTmp[0].adminArea
                if (locTmp[0].subAdminArea != null) locString += " ${locTmp[0].subAdminArea}"
                if (locTmp[0].locality != null) locString += " ${locTmp[0].locality}"
                if (locTmp[0].subLocality != null) locString += " ${locTmp[0].subLocality}"
                if (locTmp[0].countryName != null && locString == "") locString = locTmp[0].countryName
            }
            else locString = noLocationData
        }catch (e : Exception) {
            e.printStackTrace()
            return null
        }
        return locString
    }

    fun getDateIdInfo(context: Context, cal : Calendar) : ArrayList<Long>{
        val idList = arrayListOf<Long>()

        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID //photo_id
        )
        val selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + getDateStart(cal) + " AND " + getDateEnd(cal)
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortdate)
        if(!cursorIsValid(cursor)) return idList

        do {
            val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            idList.add(id)
        } while (cursor!!.moveToNext())
        cursor.close()

        return idList
    }

    fun getNameDir(context: Context, path : String) : Cursor? {
        val selection = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = '" + path + "'"
        return getDir(context, selection)
    }

    fun getFileDir(context: Context, name : String) : Cursor? {
        val selection = MediaStore.Images.ImageColumns.TITLE + " = '" + name + "'"
        return getDir(context, selection)
    }

    fun getDateDir(context: Context, cal : Calendar) : Cursor? {
        val selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + getDateStart(cal) + " AND " + getDateEnd(cal)
        return getDir(context, selection)
    }

    private fun getDir(context: Context, selection : String) : Cursor? {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DISPLAY_NAME
        )
        return context.contentResolver.query(uri, projection, selection, null, sortdate)
    }

    fun getDirByIdList(context: Context, idList : List<Long>) : ArrayList<thumbnailData> {
        val list = ArrayList<thumbnailData>()
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME
        )
        var selection = MediaStore.Images.ImageColumns._ID + " IN ("
        for(id in idList) {
            selection += "$id,"
        }
        selection = selection.substring(0, selection.length - 1)
        selection += ")"

        val cursor = context.contentResolver.query(uri, projection, selection, null, sortdate)
        if(!cursorIsValid(cursor)) return list
        do {
            val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
            list.add(thumbnailData(id, name))
        } while (cursor!!.moveToNext())

        return list
    }

    fun getSimilarByIdList(context: Context, idList : List<Long>, cal: Calendar) : ArrayList<thumbnailData> {
        val list = ArrayList<thumbnailData>()
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME
        )
        var selection = MediaStore.Images.ImageColumns._ID + " IN ("
        for(id in idList) {
            selection += "$id,"
        }
        selection = selection.substring(0, selection.length - 1)
        selection += ")" + " AND " + MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + (cal.time.time - 30000) + " AND " + (cal.time.time + 30000)

        val cursor = context.contentResolver.query(uri, projection, selection, null, sortdate)
        if(!cursorIsValid(cursor)) return list
        do {
            val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
            list.add(thumbnailData(id, name))
        } while (cursor!!.moveToNext())

        return list
    }

    fun getNameById(context: Context, id: Long) : String? {
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DISPLAY_NAME
        )
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortdate)
        return if(cursorIsValid(cursor)) {
            cursor!!.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
        } else null
    }

    fun getDateById(context: Context, id: Long) : Long? {
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATE_TAKEN
        )
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortdate)
        return if(cursorIsValid(cursor)) {
            cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))
        } else null
    }

    fun getLatLngById(context: Context, id : Long) : LatLng? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var photoUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            photoUri = MediaStore.setRequireOriginal(photoUri)

            val stream = context.contentResolver.openInputStream(photoUri) ?: return null
            val exif = ExifInterface(stream)
            val latLng = exif.latLong ?: return null

            stream.close()
            LatLng(latLng[0], latLng[1])
        } else {
            val projection = arrayOf(
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE
            )

            val selection = MediaStore.Images.ImageColumns._ID + " = " + id

            val cursor = context.contentResolver.query(uri, projection, selection, null, sortdate)
            if (!cursorIsValid(cursor)) return null


            val loc = LatLng(cursor!!.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE)))

            cursor.close()
            loc
        }
    }

    fun getThumbnailDataByCursor(cursor: Cursor) : thumbnailData {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))

        return thumbnailData(id, name)
    }

    private fun getLatLngByCursor(cursor: Cursor) : LatLng {
        val lat = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
        val lon = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))

        return LatLng(lat, lon)
    }

    fun getNewlySortedCursor(context: Context, date : Long) : Cursor? {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATE_ADDED
        )
        val selection = MediaStore.Images.ImageColumns.DATE_ADDED + " >= " + date

        return context.contentResolver.query(uri, projection, selection, null, sortdate)
    }

    @Suppress("DEPRECATION")
    fun LoadThumbnailById(context: Context, id : Long) : Bitmap?{
        try {
            val bitmap = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                context.contentResolver.loadThumbnail(uri, Size(100, 100), null)
            } else {
                MediaStore.Images.Thumbnails.getThumbnail(
                    context.contentResolver,
                    id,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
                )
            }) ?: return null
            return modifyOrientaionById(context, id, bitmap)
        } catch ( e: FileNotFoundException) {
            e.printStackTrace()
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            context.contentResolver.delete(uri, null, null)
            return null
        }
    }

    fun modifyOrientaionById(context: Context, id: Long, bitmap: Bitmap) : Bitmap {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.ORIENTATION
        )
        val selection = MediaStore.Images.ImageColumns._ID + "=" + id
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortdate)
        cursor!!.moveToFirst()

        val orientation = cursor.getFloat(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION))
        cursor.close()
        return rotateBitmap(bitmap, orientation)
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees : Float) : Bitmap{
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
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

    fun IsItValidId(context: Context, id : Long) : Boolean {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID
        )
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortdate)
        return (cursorIsValid(cursor))
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


    private fun getDateEndSearch(cal : Calendar) : Long{
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 59)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.time.time
    }
}