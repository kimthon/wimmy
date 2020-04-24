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
import com.jtsoft.wimmy.*
import com.jtsoft.wimmy.Activity.MainActivity
import com.jtsoft.wimmy.Activity.MainActivity.Companion.folder_type
import com.jtsoft.wimmy.Activity.Main_Map
import com.jtsoft.wimmy.Adapter.RecyclerAdapterForder
import com.jtsoft.wimmy.Activity.MainActivity.Companion.location_type
import com.jtsoft.wimmy.Activity.Main_PhotoView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.main_activity.view.*
import com.jtsoft.wimmy.db.PhotoViewModel
import com.jtsoft.wimmy.db.thumbnailData

class LocationFragment(val v: AppBarLayout) : Fragment() {
    private var thisview: View? = null
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
        liveData = vm.getLocationDir()
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

    override fun onPause() {
        super.onPause()
    }

    private fun setView(view : View?) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.fragment_RecycleView)
        recyclerAdapter =
            RecyclerAdapterForder(activity, ArrayList())
            {thumbnailData ->
                if(SystemClock.elapsedRealtime() - mLastClickTime > 300) {
                    if(location_type == 1) {
                        val intent = Intent(activity, Main_PhotoView::class.java)
                        intent.putExtra("location_name", thumbnailData.data)
                        startActivityForResult(intent, 201)
                    }
                    else {
                        val intent = Intent(activity, Main_Map::class.java)
                        intent.putExtra("location_name", thumbnailData.data)
                        startActivityForResult(intent, 800)
                    }
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
                recyclerAdapter.setPhotoSize(size, padding)
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}
