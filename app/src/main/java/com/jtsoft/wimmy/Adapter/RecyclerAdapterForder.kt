package com.jtsoft.wimmy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.jtsoft.wimmy.ImageLoder
import com.jtsoft.wimmy.R
import com.jtsoft.wimmy.ThumbnailLoad
import com.jtsoft.wimmy.db.thumbnailData

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

            text.text = data.data
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
        if(list.isNullOrEmpty()) this.list = arrayListOf()
        else {
            var thisIndex = 0
            for(pData in list) {
                do {
                    val pre = if(thisIndex < this.list.size) {
                        pData.data.compareTo(this.list[thisIndex].data)
                    }
                    else { Int.MIN_VALUE }

                    //pre > 0 : 이전 데이터가 사라진 경우
                    if(pre > 0) {
                        this.list.removeAt(thisIndex)
                        notifyItemRemoved(thisIndex)
                        //제자리에 머물러야함
                        continue
                    }
                    //그대로 일 경우
                    else if(pre == 0) {
                        if(this.list[thisIndex].photo_id != pData.photo_id) {
                            this.list[thisIndex].photo_id = pData.photo_id
                            notifyItemChanged(thisIndex)
                        }
                        ++thisIndex
                        break
                    }
                    //삽입
                    else {
                        this.list.add(thisIndex, pData)
                        notifyItemInserted(thisIndex)
                        ++thisIndex
                        break
                    }
                } while(true)
            }
        }
    }
}
