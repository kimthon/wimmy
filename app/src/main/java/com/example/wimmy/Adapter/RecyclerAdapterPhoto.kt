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
import com.example.wimmy.db.thumbnailData

class RecyclerAdapterPhoto(val context: Activity?, var list: ArrayList<thumbnailData>, val itemClick: (thumbnailData, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapterPhoto.Holder>()
{
    private var size : Int = 200
    private var padding_size = 200

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var thumbnail: ImageView = itemView!!.findViewById<ImageView>(R.id.thumbnail_img)
        var text = itemView?.findViewById<TextView>(R.id.thumbnail_img_text)

        fun bind(data : thumbnailData, num: Int) {
            val layoutParam = thumbnail.layoutParams as ViewGroup.MarginLayoutParams
            thumbnail.layoutParams.width = size
            thumbnail.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)

            thumbnail.setImageResource(0)
            ImageLoder.execute(ThumbnailLoad(this, thumbnail, data.photo_id))

            text!!.text = data.data
            itemView.setOnClickListener { itemClick(data, num) }
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

    fun setThumbnailList(list : ArrayList<thumbnailData>?) {
        if(list.isNullOrEmpty()) this.list = ArrayList<thumbnailData>()
        else {
            this.list = list
            notifyDataSetChanged()
        }
    }

    fun getThumbnailList() : ArrayList<thumbnailData> {
        return list
    }

    fun addThumbnailList(data : thumbnailData) {
        list.add(data)
    }

    fun getSize() : Int {
        return list.size
    }
}
