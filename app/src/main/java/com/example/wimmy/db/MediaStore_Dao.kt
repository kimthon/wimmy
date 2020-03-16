package com.example.wimmy.db

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Geocoder
import android.media.ExifInterface
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.Main_Map
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList

object MediaStore_Dao {
    private val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private const val noLocationData = "위치 정보 없음"

    fun getNameDir(context: Context) : ArrayList<thumbnailData>{
        val thumbList = arrayListOf<thumbnailData>()
        val projection = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DISPLAY_NAME
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
            val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
            val path = allPath.subSequence(0, (allPath.length - name.length - 1)).toString()

            thumbList.add(thumbnailData(id, path))
        } while (cursor!!.moveToNext())
        cursor.close()

        return thumbList
    }

    fun getLocation(context: Context, id : Long) : String?{
        val loc = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //TODO 파일 위치를 얻어 올 방법이 필요
            val projection = arrayOf(
                MediaStore.Images.ImageColumns.DATA
            )
            val selection = MediaStore.Images.ImageColumns._ID + " = " + id

            val cursor = context.contentResolver.query(uri, projection, selection, null, null)
            if (!cursorIsValid(cursor)) return noLocationData
            val path = cursor!!.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))

            val exif = ExifInterface(path)

            val attrLat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
            val attrLatRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
            val attrLon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
            val attrLonRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
            if(attrLat.isNullOrEmpty() || attrLatRef.isNullOrEmpty() || attrLon.isNullOrEmpty() || attrLonRef.isNullOrEmpty()) return noLocationData
            val lat = if(attrLatRef == "N") {
                convertToDegree(attrLat)
            } else { -convertToDegree(attrLat) }
            val lon = if(attrLonRef == "E") {
               convertToDegree(attrLon)
            } else { -convertToDegree(attrLon)}

            Pair(lat, lon)
        } else {
            val projection = arrayOf(
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE
            )

            val selection = MediaStore.Images.ImageColumns._ID + " = " + id

            val cursor = context.contentResolver.query(uri, projection, selection, null, null)
            if (!cursorIsValid(cursor)) return noLocationData

            val loc= Pair(cursor!!.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE)))
            cursor.close()
            loc
        }
        val geo = Geocoder(context)

        var locString : String = noLocationData
        try {
            val locTmp = geo.getFromLocation(loc.first, loc.second, 1)
            if (locTmp != null && locTmp.isNotEmpty()) {
                locString = locTmp[0].countryName
                if (locTmp[0].locality != null) locString += " ${locTmp[0].locality}"
                if (locTmp[0].subLocality != null) locString += " ${locTmp[0].subLocality}"
            }
        }catch (e : Exception) {
            e.printStackTrace()
            return null
        }

        return locString
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
        return getDir(adapter, selection)
    }

    fun getDateDir(adapter: RecyclerAdapterPhoto, cal : Calendar) : Cursor? {
        val selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " BETWEEN " + getDateStart(cal) + " AND " + getDateEnd(cal)
        return getDir(adapter, selection)
    }

    fun getDir(adapter: RecyclerAdapterPhoto, selection : String) : Cursor? {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DISPLAY_NAME
        )
        val cursor = adapter.context!!.contentResolver.query(uri, projection, selection, null, null)
        return cursor
    }

    fun getPhotoDataCursor(context: Context, selection : String) : Cursor? {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DATA, // folder + name
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN //date
        )

        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        return cursor
    }

    fun getLatLngCursor(context: Context,selection: String) : Cursor? {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE
        )
        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        return cursor
    }

    fun getPathById(context: Context, id: Long) : String?{
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA
        )
        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        return if(cursorIsValid(cursor)) {
            cursor!!.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
        } else null
    }
    fun getDataById(adapter: RecyclerAdapterPhoto, id : Long) : thumbnailData? {
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val cursor = getDir(adapter, selection)
        if(cursorIsValid(cursor)) {
            return getData(cursor!!)
        }
        else return null
    }

    fun getNameById(context: Context, id: Long) : String? {
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DISPLAY_NAME
        )
        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        return if(cursorIsValid(cursor)) {
            cursor!!.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
        } else null
    }

    fun getDateById(context: Context, id: Long) : Long? {
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATE_TAKEN
        )
        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        return if(cursorIsValid(cursor)) {
            cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))
        } else null
    }

    fun setLatLngDataById(context: Context, id : Long, map: Main_Map) {
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val cursor = getLatLngCursor(context, selection)
        if(cursorIsValid(cursor)) {
            val latLng = getLatLngById(context, id) ?: return
            map.addLatLNgData(id, latLng)
        }
    }

    private fun getLatLngById(context: Context, id : Long) : LatLng? {
        val selection = MediaStore.Images.ImageColumns._ID + " = " + id
        val cursor = getLatLngCursor(context, selection)
        if(cursorIsValid(cursor)) {
            val latLng = getLatLngByCursor(cursor!!)
            return latLng
        }
        else return null
    }

    fun getData(cursor: Cursor) : thumbnailData {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))

        return thumbnailData(id, name)
    }

    fun getPhotoData(cursor: Cursor) : PhotoData {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
        val allPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
        val path = allPath.subSequence(0, (allPath.length - name.length - 1)).toString()
        val dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))
        val date = Date(dateTaken)

        var loc : String? = noLocationData
        val photoData = PhotoData(id, name, path, loc, date, false)
        return photoData
    }

    private fun getLatLngByCursor(cursor: Cursor) : LatLng {
        val lat = cursor!!.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
        val lon = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))

        return LatLng(lat, lon)
    }

    fun getNewlySortedCursor(context: Context, date : Long) : Cursor? {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATE_ADDED
        )
        val selection = MediaStore.Images.ImageColumns.DATE_ADDED + " > " + date
        val sortOrder = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC"
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)

        return cursor
    }

    @Suppress("DEPRECATION")
    fun LoadThumbnailById(context: Context, id : Long) : Bitmap{
        val bitmap : Bitmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        return (cursorIsValid(cursor))
    }

    private fun convertToDegree (string: String) : Double {
        val dms = string.split(",")

        val dList = dms[0].split("/")
        val d = dList[0].toDouble() / dList[1].toDouble()

        val mList = dms[1].split("/")
        val m = mList[0].toDouble() / mList[1].toDouble()

        val sList = dms[2].split("/")
        val s = sList[0].toDouble() / sList[1].toDouble()

        return d + m/60 + s/3600
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