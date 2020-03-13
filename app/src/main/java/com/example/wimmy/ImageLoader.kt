package com.example.wimmy

import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoData
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
        if(holder?.adapterPosition == holderPosition) {
            val image = MediaStore_Dao.LoadThumbnailById(imageView.context, id)
            handler.post {
               if(holder?.adapterPosition == holderPosition) {
                   imageView.setImageBitmap(image)
               }
            }
        }
    }
}

class ImageLoad(imageView: ImageView, id : Long) : Runnable {
    val imageView = imageView
    val id = id

    override fun run() {
        val path = MediaStore_Dao.getPathById(imageView.context, id)
        if(path != null) {
            val image = BitmapFactory.decodeFile(path)
            handler.post {
                imageView.setImageBitmap(image)
            }
        }
    }
}
