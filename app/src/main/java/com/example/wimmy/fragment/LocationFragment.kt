package com.example.wimmy.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.*
import com.example.wimmy.Activity.MainActivity
import com.example.wimmy.Activity.Main_Map
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.Activity.MainActivity.Companion.location_type
import com.example.wimmy.Activity.Main_PhotoView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.main_activity.view.*
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData

class LocationFragment(v: AppBarLayout) : Fragment() {
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
        setPhotoSize(this.view!!,3, 10)
    }

    override fun onPause() {
        super.onPause()
    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                202 -> {
                   /* if(data!!.getIntExtra("delete_check", 0) == 1) {
                        thumbnailList = MediaStore_Dao.getNameDir(thisview?.context!!)
                        setView(thisview!!)
                    }*/
                }
            }
        }
    }*/

    private fun setView(view : View?) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.fragment_RecycleView)
        recyclerAdapter =
            RecyclerAdapterForder(activity, ArrayList())
            {thumbnailData ->
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
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

        val lm = GridLayoutManager(MainActivity(), 3)
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
