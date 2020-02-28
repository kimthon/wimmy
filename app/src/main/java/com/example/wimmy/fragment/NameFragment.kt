package com.example.wimmy


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.wimmy.db.PhotoDB
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData

/**
 * A simple [Fragment] subclass.
 */
class NameFragment : Fragment() {
    private var recyclerAdapter : RecyclerAdapterForder?= null
    var bottomNavigationView: BottomNavigationView? = null
    private var thumbnailList = listOf<thumbnailData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        var view : View = inflater.inflate(R.layout.fragment_name, container, false)
        setView(view)
        setPhotoSize(view,3, 10)
        // Inflate the layout for this fragment

        var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        vm.getNameDir().observe(this,
            Observer<List<thumbnailData>> { t -> recyclerAdapter!!.setThumbnailList(t)})
        
        return view
    }

    private fun setView(view : View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.nameRecycleView)
        recyclerAdapter =
            RecyclerAdapterForder(activity, thumbnailList)
            {thumbnailData ->
                val intent = Intent(activity, com.example.wimmy.Main_PhotoView::class.java)
                intent.putExtra("dir_name", thumbnailData.data)
                startActivity(intent)
            }
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), 3)
        recyclerView?.layoutManager = lm
    }

    private fun setPhotoSize(view : View, row : Int, padding : Int) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.nameRecycleView)
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



