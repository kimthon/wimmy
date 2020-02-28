package com.example.wimmy

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import java.io.File
import java.util.*
import java.util.jar.Manifest

object PhotoScanner {
    fun addAllImages(activity: Activity, vm : PhotoViewModel) {
        //마시멜로 이전엔 권한 체크 필요 x
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val REQUEST_STORAGE_ACCESS = 1000
            var permission = ContextCompat.checkSelfPermission(
                activity.applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_ACCESS
                )

                //재 검사 후 권한이 없을 시 종료
                permission = ContextCompat.checkSelfPermission(
                    activity.applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                if (permission == PackageManager.PERMISSION_DENIED) { return }
            }
        }

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID, //photo_id
            MediaStore.Images.ImageColumns.DATA, // folder + name
            MediaStore.Images.Thumbnails.DATA,
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE,
            MediaStore.Images.ImageColumns.DATE_TAKEN //date
        )

        val cursor = activity.contentResolver.query(uri, projection, null, null, null)

        if (cursor == null) {
            Log.e("TAG", "cursor null or cursor is empty")
            return;
        }
        cursor.moveToFirst()

        do {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            if(!vm.IsInserted(id)) {
                val allPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                val file = File(allPath)
                val name = file.name
                val path = allPath.subSequence(0, (allPath.length - name.length - 1)).toString()

                val dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))
                val date = Date(dateTaken)

                val lat = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
                val lon = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))
                val geo = Geocoder(activity.applicationContext)
                var loc : String? = null
                val locTmp = geo.getFromLocation(lat, lon, 1)
                if(locTmp != null && locTmp.isNotEmpty()) {
                    loc = locTmp[0].toString()
                }

                vm.Insert(PhotoData(id, name, path, loc, date, false))
            }
        } while (cursor.moveToNext())
    }

    fun LoadThumbnail(context: Context, id : Long) : Bitmap{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            return context.contentResolver.loadThumbnail(
                uri,
                Size(100, 100),
                null)
        }
        else {
            return MediaStore.Images.Thumbnails.getThumbnail(
                context.contentResolver,
                id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null)
        }
    }
}