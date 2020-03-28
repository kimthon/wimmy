package com.example.wimmy

import android.content.ContentUris
import android.content.Context
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.db.MediaStore_Dao
import java.lang.Error
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

val ImageLoder = ThreadPoolExecutor(0, Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS, SynchronousQueue<Runnable>())
private val handler = Handler(Looper.getMainLooper())

class ThumbnailLoad(holder: RecyclerView.ViewHolder, imageView: ImageView, id : Long) : Runnable {
    private val holder = holder
    private val holderPosition = holder.adapterPosition
    private val imageView: ImageView = imageView
    private val id = id

    override fun run() {
        if(holder.adapterPosition == holderPosition) {
            val image = MediaStore_Dao.LoadThumbnailById(imageView.context, id)
            handler.post {
               if(holder.adapterPosition == holderPosition) {
                   imageView.setImageBitmap(image)
               }
            }
        }
    }
}

class ImageLoad(context: Context, imageView: ImageView, id : Long) : Runnable {
    private val imageView = imageView
    private val id = id
    private val context = context

    @Suppress("DEPRECATION")
    override fun run() {
        try {
            handler.post { imageView.setImageBitmap(MediaStore_Dao.LoadThumbnailById(imageView.context, id)) }

            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            var image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val src = ImageDecoder.createSource(imageView.context.contentResolver, uri)
                ImageDecoder.decodeBitmap(src)
            } else {
                MediaStore.Images.Media.getBitmap(imageView.context.contentResolver, uri)
            } ?: return
            image = MediaStore_Dao.modifyOrientaionById(context, id, image)

            handler.post {
                imageView.setImageBitmap(image)
            }
        } catch (e : Error) {
            e.printStackTrace()
        }
    }
}

