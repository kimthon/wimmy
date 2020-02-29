package com.example.wimmy.Adapter

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

class RecyclerAdapterPhoto(val context: FragmentActivity?, var list: List<PhotoData>, val itemClick: (PhotoData, Int, ImageView) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapterPhoto.Holder>()
{
    private var size : Int = 200
    private var padding_size = 200

    inner class Holder(itemView: View?, itemClick: (PhotoData, Int, ImageView) -> Unit) : RecyclerView.ViewHolder(itemView!!) {
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
            thumbnail.setImageBitmap(MediaStore_Dao.LoadThumbnail(context!!.applicationContext, data.photo_id))

            itemView.setOnClickListener { itemClick(data, num, thumbnail) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.thumbnail_imgview, parent, false)
        return Holder(view, itemClick)
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

    fun setThumbnailList(list : List<PhotoData>) : List<PhotoData>{
        this.list = list
        notifyDataSetChanged()
        return list
    }
}
