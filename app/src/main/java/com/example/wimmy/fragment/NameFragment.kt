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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.*
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.Activity.MainActivity
import com.example.wimmy.Activity.MainActivity.Companion.folder_type
import com.example.wimmy.Activity.Main_PhotoView
import com.example.wimmy.db.PhotoViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.main_activity.view.*

class NameFragment(v: AppBarLayout) : Fragment() {
    private lateinit var thisview: View
    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerAdapter : RecyclerAdapterForder
    private lateinit var vm : PhotoViewModel
    private lateinit var observer : DataBaseObserver
    private var mLastClickTime: Long = 0
    private val ab = v

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        ab.main_toolbar.visibility = View.VISIBLE
        ab.setExpanded(true,true)

        thisview = inflater.inflate(R.layout.fragment_view, container, false)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        setView(thisview)
        observer = DataBaseObserver(Handler(), recyclerAdapter)

        return thisview
    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(this.view!!,folder_type, 10)
        DirectoryThread.execute {
            val list = vm.getNameDir(this.context!!)
            MainHandler.post { recyclerAdapter.setThumbnailList(list) }
        }
        this.context!!.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer)
    }

    override fun onPause() {
        super.onPause()
        this.context!!.contentResolver.unregisterContentObserver(observer)
    }

    private fun setView(view : View?) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.fragment_RecycleView)
        recyclerAdapter =
            RecyclerAdapterForder(activity, ArrayList())
            {thumbnailData ->
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    val intent = Intent(activity, Main_PhotoView::class.java)
                    intent.putExtra("dir_name", thumbnailData.data)
                    startActivityForResult(intent, 201)
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        recyclerView?.adapter = recyclerAdapter
        val lm = GridLayoutManager(MainActivity(), folder_type)
        recyclerView!!.layoutManager = lm
    }

    private fun setPhotoSize(view : View, row : Int, padding : Int) {
        recyclerView = view.findViewById<RecyclerView>(R.id.fragment_RecycleView)
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




