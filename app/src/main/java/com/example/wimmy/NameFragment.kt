package com.example.wimmy


import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.wimmy.db.PhotoDB
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData

/**
 * A simple [Fragment] subclass.
 */
class NameFragment : Fragment() {
    private var recyclerAdapter : RecyclerAdapter ?= null
    var bottomNavigationView: BottomNavigationView? = null
    private var thumbnailList = listOf<thumbnailData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        var view : View = inflater.inflate(R.layout.fragment_name, container, false)
        setView(view)
        setPhotoSize(view, 3, 10)
        // Inflate the layout for this fragment

        var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        vm.getNameDir().observe(this,
            Observer<List<thumbnailData>> { t -> recyclerAdapter!!.setThumbnailList(t)})

        vm.Insert(PhotoData(0, "dump", "dump1", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump1", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump1", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump2", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump2", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump2", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump3", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump3", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump4", "dump", "dump", 0, false))
        return view
    }

    private fun setView(view : View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.nameRecycleView)
        recyclerAdapter = RecyclerAdapter(activity, thumbnailList)
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), 3)
        recyclerView?.layoutManager = lm as RecyclerView.LayoutManager?
    }

    private fun setPhotoSize(view : View, row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var size = width / row - 2*padding

        recyclerAdapter!!.setPhotoSize(size, padding)
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
    }*/
}



