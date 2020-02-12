package com.example.wimmy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(val context: Context, val list : ArrayList<PhotoData>) :
    RecyclerView.Adapter<MainAdapter.Holder>()
{
    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        //photo_view 변수 받아오기

        fun bind(data : PhotoData, context: Context) {
            //photo_view의 내부 값 설정
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
        holder.bind(list[position], context)
    }
}
