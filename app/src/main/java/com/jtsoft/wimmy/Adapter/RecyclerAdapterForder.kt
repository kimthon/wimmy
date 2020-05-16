package com.jtsoft.wimmy.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.jtsoft.wimmy.*
import com.jtsoft.wimmy.db.PhotoViewModel
import com.jtsoft.wimmy.db.thumbnailData
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapterForder(val context: FragmentActivity?, var list: ArrayList<thumbnailData>, val fragmentNum: Int, val itemClick: (thumbnailData) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapterForder.Holder>()
{
    private var size : Int = 200
    private var padding_size = 200

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var thumbnail = itemView.findViewById<ImageView>(R.id.thumbnail)
        private var text = itemView.findViewById<TextView>(R.id.thumbnail_text)
        private var count = itemView.findViewById<TextView>(R.id.thumbnail_count)
        private var vm = ViewModelProviders.of(context!!).get(PhotoViewModel::class.java)
        var cal: Calendar = Calendar.getInstance()
        var imgcount: String = ""

        fun bind(data : thumbnailData) {
            Log.d("값11", context.toString())
            val layoutParam = thumbnail.layoutParams as ViewGroup.MarginLayoutParams
            thumbnail.layoutParams.width = size
            thumbnail.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)

            thumbnail.setImageResource(0)
            ImageLoder.execute(ThumbnailLoad(this, thumbnail, data.photo_id))
            DBThread.execute {
                when(fragmentNum) {
                    0 -> imgcount = vm.getOpenNameDirCursor(context!!, data.data)?.count.toString()   // 폴더
                    1 -> imgcount = vm.getTagAmount(data.data).toString()
                    2 -> { cal.set(data.data.substring(0, 4).toInt(), data.data.substring(6, 8).toInt() - 1, data.data.substring(10, 12).toInt(), 0, 0, 0)
                        imgcount = vm.getDateAmount(context!!, cal).toString() }  // 날짜
                    3 -> imgcount = vm.getLocationAmount(data.data).toString()  // 위치
                    4 -> imgcount = vm.getOpenFileDirCursor(context!!, data.data)?.count.toString()  // 파일
                }
                MainHandler.post { count.text = imgcount }
            }
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
