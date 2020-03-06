package com.example.wimmy

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.db.MediaStore_Dao

class ThumbnailAsyncTask(holder: RecyclerView.ViewHolder, imageView: ImageView, id : Long) : AsyncTask<Context, Void, Bitmap>() {
    private val holder = holder
    private val holderPosition = holder.adapterPosition
    private val imageView: ImageView = imageView
    private val id = id

    override fun doInBackground(vararg params: Context?): Bitmap? {
        return if(holder.adapterPosition == holderPosition) {
            MediaStore_Dao.LoadThumbnailById(params[0]!!, id)
        } else null
    }

    override fun onPostExecute(result: Bitmap?) {
        if(holder.adapterPosition == holderPosition) {
            imageView.setImageBitmap(result)
        }
    }
}
