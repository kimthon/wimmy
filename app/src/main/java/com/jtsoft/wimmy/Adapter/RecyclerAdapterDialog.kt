package com.jtsoft.wimmy.Adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jtsoft.wimmy.ImageLoder
import com.jtsoft.wimmy.MainHandler
import com.jtsoft.wimmy.R
import com.jtsoft.wimmy.ThumbnailLoad
import com.jtsoft.wimmy.db.thumbnailData
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class RecyclerAdapterDialog(val context: Activity?, var list: ArrayList<thumbnailData>, val itemClick: (thumbnailData) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapterDialog.Holder>()
{
    private var size : Int = 200
    private var padding_size = 200
    private val checkboxSet: HashSet<Long> = hashSetOf()
    private var checktempList = arrayListOf<Boolean>()

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var thumbnail: ImageView = itemView!!.findViewById<ImageView>(R.id.thumbnail_similarimg)
        var checkbox: CheckBox = itemView!!.findViewById<CheckBox>(R.id.checkbox_similarimg)


        fun bind(data : thumbnailData, position: Int) {
            val layoutParam = thumbnail.layoutParams as ViewGroup.MarginLayoutParams
            thumbnail.layoutParams.width = size
            thumbnail.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)

            thumbnail.setImageResource(0)
            ImageLoder.execute(ThumbnailLoad(this, thumbnail, data.photo_id))

            checkbox.isChecked = checktempList[position]

            itemView.setOnClickListener { itemClick(data) }

            checkbox.setOnClickListener {
                if(checkbox.isChecked) {
                    checkboxSet.add(data.photo_id)
                    checktempList[position] = true
                }
                else {
                    checkboxSet.remove(data.photo_id)
                    checktempList[position] = false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.thumbnail_similarview, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if(position >= checktempList.size)
            checktempList.add(position, false)
        holder.bind(list[position], position)
    }

    fun setPhotoSize(size : Int, padding_size : Int) {
        this.size = size
        this.padding_size = padding_size
        notifyDataSetChanged()
    }

    fun setThumbnailList(list : ArrayList<thumbnailData>?): HashSet<Long> {
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
                        MainHandler.post { notifyItemRemoved(thisIndex) }
                        //제자리에 머물러야함
                        continue
                    }
                    //그대로 일 경우
                    else if(pre == 0) {
                        if(this.list[thisIndex].photo_id != pData.photo_id) {
                            this.list[thisIndex].photo_id = pData.photo_id
                            MainHandler.post{ notifyItemChanged(thisIndex) }
                        }
                        ++thisIndex
                        break
                    }
                    //삽입
                    else {
                        this.list.add(thisIndex, pData)
                        MainHandler.post{ notifyItemInserted(thisIndex) }
                        ++thisIndex
                        break
                    }
                } while(true)
            }
        }
        return checkboxSet
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
