package com.example.wimmy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.Adapter.PagerRecyclerAdapter
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData


class PhotoViewPager : AppCompatActivity() {
    private var recyclerAdapter : PagerRecyclerAdapter?= null
    internal lateinit var viewPager: ViewPager
    private var thumbnailList = listOf<thumbnailData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: View = findViewById(R.id.imgViewPager)
        setContentView(R.layout.photoview_frame)
        setView(view)
        // Inflate the layout for this fragment


    }

    private fun setView(view: View) {
        viewPager = view.findViewById<RecyclerView>(R.id.imgViewPager) as ViewPager
        recyclerAdapter =
            PagerRecyclerAdapter(
                this,
                thumbnailList
            )
        viewPager?.adapter = recyclerAdapter


    }
}
