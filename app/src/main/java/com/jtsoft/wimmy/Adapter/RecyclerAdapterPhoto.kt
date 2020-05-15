package com.jtsoft.wimmy.Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jtsoft.wimmy.Activity.Main_PhotoView.Companion.checkboxList
import com.jtsoft.wimmy.ImageLoder
import com.jtsoft.wimmy.MainHandler
import com.jtsoft.wimmy.R
import com.jtsoft.wimmy.ThumbnailLoad
import com.jtsoft.wimmy.db.checkboxData
import com.jtsoft.wimmy.db.thumbnailData
import kotlinx.android.synthetic.main.thumbnail_imgview.view.*

class RecyclerAdapterPhoto(val context: Activity?, var list: ArrayList<thumbnailData>, val itemClick: (thumbnailData, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapterPhoto.Holder>()
{
    private var size : Int = 200
    private var padding_size = 200
    private lateinit var view: View
    private var ck = 0
    private var ck2 = 0

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var thumbnail: ImageView = itemView!!.findViewById<ImageView>(R.id.thumbnail_img)
        var checkbox: CheckBox = itemView!!.findViewById<CheckBox>(R.id.checkbox)

        fun bind(data : thumbnailData, num: Int) {
            val layoutParam = thumbnail.layoutParams as ViewGroup.MarginLayoutParams
            thumbnail.layoutParams.width = size
            thumbnail.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)

            if(ck == 1) {
                checkbox.visibility = View.VISIBLE
            }
            else
                checkbox.visibility = View.GONE

            if(num >= checkboxList.size)
                checkboxList.add(num, checkboxData(data.photo_id, false))


            thumbnail.setImageResource(0)
            ImageLoder.execute(ThumbnailLoad(this, thumbnail, data.photo_id))


            checkbox.isChecked = checkboxList[num].checked


            checkbox.setOnClickListener {
                if(checkbox.isChecked) {
                    checkboxList[num].checked = true
                }
                else {
                    checkboxList[num].checked = false
                }
            }

            itemView.setOnClickListener { itemClick(data, num) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        view = LayoutInflater.from(context).inflate(R.layout.thumbnail_imgview, parent, false)
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
                        checkboxList.removeAt(thisIndex)
                        MainHandler.post { notifyItemRemoved(thisIndex) }
                        //제자리에 머물러야함
                        continue
                    }
                    //그대로 일 경우
                    else if(pre == 0) {
                        if(this.list[thisIndex].photo_id != pData.photo_id) {
                            this.list[thisIndex].photo_id = pData.photo_id
                            checkboxList[thisIndex].id = pData.photo_id
                            MainHandler.post{ notifyItemChanged(thisIndex) }
                        }
                        ++thisIndex
                        break
                    }
                    //삽입
                    else {
                        this.list.add(thisIndex, pData)

                        checkboxList.add(thisIndex, checkboxData(pData.photo_id, false))
                        MainHandler.post{ notifyItemInserted(thisIndex) }
                        ++thisIndex
                        break
                    }
                } while(true)
            }
        }
    }

    fun setCheckAll(boolean: Boolean) {
        for(ckbox in checkboxList){
            if(ckbox.checked == !boolean)
                ckbox.checked = boolean
        }
        MainHandler.post{ notifyDataSetChanged() }
    }

    fun getThumbnailList() : ArrayList<thumbnailData> {
        return list
    }

    fun addThumbnailList(data : thumbnailData) {

        list.add(data)
        checkboxList.add(checkboxData(data.photo_id, false))
    }

    fun getSize() : Int {
        return list.size
    }

    fun updateCheckbox(n: Int) {
        ck = n
    }

    fun updateCheckbox2(n: Int) {
        ck2 = n
    }

}
