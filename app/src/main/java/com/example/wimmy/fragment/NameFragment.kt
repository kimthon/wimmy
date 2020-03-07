package com.example.wimmy.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.DataBaseObserver
import com.example.wimmy.MainActivity
import com.example.wimmy.Main_PhotoView
import com.example.wimmy.R
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.thumbnailData
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.main_activity.view.*

class NameFragment(v: AppBarLayout) : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private var recyclerAdapter : RecyclerAdapterForder?= null
    private var thumbnailList = listOf<thumbnailData>()
    private lateinit var observer : DataBaseObserver
    private var mLastClickTime: Long = 0
    private val ab = v

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        ab.main_toolbar.visibility = View.VISIBLE
        ab.setExpanded(true,true)

        val view : View = inflater.inflate(R.layout.fragment_name, container, false)
        thumbnailList = MediaStore_Dao.getNameDir(view.context)
        setView(view)
        observer = DataBaseObserver(Handler(), recyclerAdapter!!)

        return view
    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(this.view!!,3, 3)
        this.context!!.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer)
    }

    override fun onPause() {
        super.onPause()
        this.context!!.contentResolver.unregisterContentObserver(observer)
    }

    private fun setView(view : View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.nameRecycleView)
        recyclerAdapter =
            RecyclerAdapterForder(activity, thumbnailList)
            {thumbnailData ->
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    val intent = Intent(activity, Main_PhotoView::class.java)
                    intent.putExtra("dir_name", thumbnailData.data)
                    startActivity(intent)
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), 3)
        recyclerView!!.layoutManager = lm
    }

    private fun setPhotoSize(view : View, row : Int, padding : Int) {
        recyclerView = view.findViewById<RecyclerView>(R.id.nameRecycleView)
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener( object : ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                val width = recyclerView.width
                val size = width / row - 2 * padding
                recyclerAdapter!!.setPhotoSize(size, padding)
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}
/*
    inner class Scroll : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
            bottomNavigationView = view!!.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            if (dy > 0 && bottomNavigationView!!.isShown()) {
                bottomNavigationView!!.setVisibility(View.GONE);
            } else if (dy < 0 ) {
                bottomNavigationView!!.setVisibility(View.VISIBLE);
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }
    }
}
 */



