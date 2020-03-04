package com.example.wimmy.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.R
import com.example.wimmy.db.PhotoData

class RecyclerAdapterPhoto(val context: FragmentActivity?, var list: ArrayList<PhotoData>, val itemClick: (PhotoData, Int, ImageView) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapterPhoto.Holder>()
{
    private var size : Int = 200
    private var padding_size = 200

    inner class setThumbnailAsyncTask(holder: Holder, imageView: ImageView, id : Long) : AsyncTask<Context, Void, Bitmap>() {
        private val holder = holder
        private val holderPosition = holder.adapterPosition
        private val imageView : ImageView = imageView
        private val id = id

        override fun doInBackground(vararg params: Context?): Bitmap? {
            return if(holder.adapterPosition == holderPosition) {
                MediaStore_Dao.LoadThumbnail(params[0]!!, id)
            } else null
        }

        override fun onPostExecute(result: Bitmap?) {
            if(holder.adapterPosition == holderPosition) {
                imageView.setImageBitmap(result)
                imageView.layoutParams.width = size
                imageView.layoutParams.height = size
            }
        }
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        //thumbnail_imgview 변수 받아오기

        var thumbnail: ImageView = itemView!!.findViewById<ImageView>(R.id.thumbnail_img)
        var text = itemView?.findViewById<TextView>(R.id.thumbnail_img_text)

        fun bind(data : PhotoData, num: Int) {
            //photo_view의 내부 값 설정
            val layoutParam = thumbnail.layoutParams as ViewGroup.MarginLayoutParams
            thumbnail.layoutParams.width = size
            thumbnail.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)

            text!!.text = data.name
            thumbnail.setImageResource(R.drawable.loding_image)
            setThumbnailAsyncTask(this, thumbnail,data.photo_id).execute(context!!.applicationContext)

            itemView.setOnClickListener { itemClick(data, num, thumbnail) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.thumbnail_imgview, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position], position)
    }

    fun setPhotoSize(size : Int, padding_size : Int) {
        this.size = size
        this.padding_size = padding_size
        notifyDataSetChanged()
    }

    fun setThumbnailList(list : ArrayList<PhotoData>) : ArrayList<PhotoData>{
        this.list = list
        notifyDataSetChanged()
        return list
    }

    fun setThumbnailList(list : List<PhotoData>) {
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}
