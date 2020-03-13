package com.example.wimmy.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.ImageLoder
import com.example.wimmy.R
import com.example.wimmy.ThumbnailLoad
import com.example.wimmy.db.PhotoData

class RecyclerAdapterPhoto(val context: Activity?, var list: ArrayList<PhotoData>, val itemClick: (PhotoData, Int, ImageView) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapterPhoto.Holder>()
{
    private var size : Int = 200
    private var padding_size = 200
    var count = 0

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var thumbnail: ImageView = itemView!!.findViewById<ImageView>(R.id.thumbnail_img)
        var text = itemView?.findViewById<TextView>(R.id.thumbnail_img_text)

        fun bind(data : PhotoData, num: Int) {
            val layoutParam = thumbnail.layoutParams as ViewGroup.MarginLayoutParams
            thumbnail.layoutParams.width = size
            thumbnail.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)

            thumbnail.setImageResource(0)
            ImageLoder.execute(ThumbnailLoad(this, thumbnail, data.photo_id))

            text!!.text = data.name
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

    fun setThumbnailList(list : ArrayList<PhotoData>?) {
        if(list.isNullOrEmpty()) this.list = ArrayList<PhotoData>()
        else {
            this.list = list
            notifyDataSetChanged()
        }
    }

    fun getThumbnailList() : ArrayList<PhotoData> {
        return list
    }

    fun addThumbnailList(photoData : PhotoData) {
        list.add(photoData)
    }

    fun getSize() : Int {
        return list.size
    }
}
