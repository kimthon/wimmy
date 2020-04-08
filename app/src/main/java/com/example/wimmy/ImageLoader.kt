package com.example.wimmy

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

class ImageLoad(context: Context, imageView: ImageView, id : Long, type: Int) : Runnable {
    private val imageView = imageView
    private val id = id
    private val context = context
    private val type = type

    @Suppress("DEPRECATION")
    override fun run() {
        try {
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            lateinit var img: Drawable
            if(type == 0) { img = context.resources.getDrawable(R.drawable.dummy_image) }   // 뷰페이저
            else { img = context.resources.getDrawable(R.drawable.dummy_image_white) }  // 맵, 유사이미지

            handler.post {
                Glide.with(context)
                    .load(uri)
                    //ToDo 왜 되는 지 확인할 것
                    .placeholder(img) //더미 이미지
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(imageView)
                //imageView.setImageBitmap(image)
            }
        } catch (e : Error) {
            e.printStackTrace()
        }
    }
}

fun getImage(context: Context, id: Long) : Bitmap {
    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
    val image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val src = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(src)
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
    return image
}