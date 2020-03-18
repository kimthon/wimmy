package com.example.wimmy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.ImageLoder
import com.example.wimmy.R
import com.example.wimmy.ThumbnailLoad
import com.example.wimmy.db.thumbnailData
import java.io.File

class RecyclerAdapterForder(val context: FragmentActivity?, var list: ArrayList<thumbnailData>, val itemClick: (thumbnailData) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapterForder.Holder>()
{
    private var size : Int = 200
    private var padding_size = 200

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var thumbnail = itemView.findViewById<ImageView>(R.id.thumbnail)
        private var text = itemView.findViewById<TextView>(R.id.thumbnail_text)

        fun bind(data : thumbnailData) {
            val layoutParam = thumbnail.layoutParams as ViewGroup.MarginLayoutParams
            thumbnail.layoutParams.width = size
            thumbnail.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)

            thumbnail.setImageResource(0)
            ImageLoder.execute(ThumbnailLoad(this, thumbnail, data.photo_id))

            text.text = File(data.data).name
            itemView.setOnClickListener { itemClick(data) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.thumbnail_forderview, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setPhotoSize(size : Int, padding_size : Int) {
        this.size = size
        this.padding_size = padding_size
        notifyDataSetChanged()
    }

    fun setThumbnailList(list : ArrayList<thumbnailData>?) {
        if(list.isNullOrEmpty()) this.list = arrayListOf<thumbnailData>()
        else {
            for( i in list.withIndex()) {
                //바뀐 것 만 변경
                if(this.list.size > i.index) {
                    if (this.list[i.index].photo_id != i.value.photo_id) {
                        this.list[i.index] = i.value
                        notifyItemInserted(i.index)
                    }
                } else {
                    this.list.add(i.value)
                    notifyItemInserted(i.index)
                }
            }
        }
    }
}
