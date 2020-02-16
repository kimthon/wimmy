package com.example.wimmy


import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * A simple [Fragment] subclass.
 */
class NameFragment : Fragment() {
    private var recyclerAdapter : RecyclerAdapter ?= null
    var recyclerView: RecyclerView? = null
    var bottomNavigationView: BottomNavigationView? = null

    private var photoList = arrayListOf<PhotoData>(
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false),
        PhotoData("dummy", "dummy", "dummy", 0, false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        var view : View = inflater.inflate(R.layout.fragment_name, container, false)
        SetView(view)
        SetPhotoSize(view, 3, 10)
       // recyclerView?.addOnScrollListener(Scroll())

        // Inflate the layout for this fragment
        return view
    }

    fun SetView(view : View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.nameRecycleView)
        recyclerAdapter = RecyclerAdapter(activity, photoList)
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), 3)
        recyclerView?.layoutManager = lm as RecyclerView.LayoutManager?
    }

    fun SetPhotoSize(view : View, row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var size = width / row - 2*padding

        recyclerAdapter!!.SetPhotoSize(size, padding)
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



