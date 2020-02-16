package com.example.wimmy


import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [Fragment] subclass.
 */
class NameFragment : Fragment() {
    private var recyclerAdapter : RecyclerAdapter ?= null

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
        // Inflate the layout for this fragment
        return view
    }

    fun SetView(view : View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.nameRecycleView)
        recyclerAdapter = RecyclerAdapter(activity, photoList)
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), 3)
        recyclerView?.layoutManager = lm
    }

    fun SetPhotoSize(view : View, row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var size = width / row - 2*padding

        recyclerAdapter!!.SetPhotoSize(size, padding)
    }



}
