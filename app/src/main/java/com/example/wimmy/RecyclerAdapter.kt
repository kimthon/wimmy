package com.example.wimmy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(val context: FragmentActivity?, val list: ArrayList<PhotoData>) :
    RecyclerView.Adapter<RecyclerAdapter.Holder>()
{
    private var size : Int = 200
    private var padding_size = 200

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView!!) {
        //photo_view 변수 받아오기
        var photo = itemView?.findViewById<ImageView>(R.id.photo)

        fun bind(data : PhotoData) {

            //photo_view의 내부 값 설정
            val layoutParam = photo.layoutParams as ViewGroup.MarginLayoutParams
            photo.layoutParams.width = size
            photo.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.photo_view, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun SetPhotoSize(size : Int, padding_size : Int) {
        this.size = size
        this.padding_size = padding_size
        notifyDataSetChanged()
    }
}
