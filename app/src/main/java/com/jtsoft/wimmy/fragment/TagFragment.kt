package com.jtsoft.wimmy.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jtsoft.wimmy.Adapter.RecyclerAdapterForder
import com.jtsoft.wimmy.Activity.MainActivity
import com.jtsoft.wimmy.Activity.MainActivity.Companion.folder_type
import com.jtsoft.wimmy.Activity.Main_PhotoView
import com.jtsoft.wimmy.R
import com.jtsoft.wimmy.db.PhotoViewModel
import com.jtsoft.wimmy.db.thumbnailData
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.main_activity.view.*

class TagFragment(val v: AppBarLayout) : Fragment() {
    private lateinit var thisview: View
    private lateinit var recyclerAdapter : RecyclerAdapterForder
    private lateinit var liveData : LiveData<List<thumbnailData>>
    private var mLastClickTime: Long = 0
    val ab = v

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        ab.main_toolbar.visibility = View.VISIBLE
        ab.setExpanded(true,true)

        thisview = inflater.inflate(R.layout.fragment_view, container, false)
        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        setView(thisview)
        liveData = vm.getTagDir()
        liveData.observe(this, Observer { list ->
            val arrayList = ArrayList(list)
            recyclerAdapter.setThumbnailList(arrayList)
        })

        return thisview
    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(this.view!!,folder_type, 10)
    }

    private fun setView(view : View?) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.fragment_RecycleView)
        recyclerAdapter =
            RecyclerAdapterForder(activity, ArrayList())
            {thumbnailData ->
                if(SystemClock.elapsedRealtime() - mLastClickTime > 300) {
                    val intent = Intent(activity, Main_PhotoView::class.java)
                    intent.putExtra("tag_name", thumbnailData.data)
                    startActivityForResult(intent, 203)
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), folder_type)
        recyclerView?.layoutManager = lm
    }

    private fun setPhotoSize(view : View, row : Int, padding : Int) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_RecycleView)
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
