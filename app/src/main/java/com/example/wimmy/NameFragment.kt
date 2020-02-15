package com.example.wimmy


import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [Fragment] subclass.
 */
class NameFragment : Fragment() {
    private var mainAdapter : MainAdapter ?= null

    var photoList = arrayListOf<PhotoData>(
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SetView()
        SetPhotoSize(3, 10)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_name, container, false)
    }

    fun SetView() {

        val recyclerView = getView()?.findViewById<RecyclerView>(R.id.mRecycleView)
        mainAdapter = MainAdapter(MainActivity(), photoList)
        if (recyclerView != null) {
            recyclerView.adapter = mainAdapter
        }
        val lm = GridLayoutManager(MainActivity(), 3)
        if (recyclerView != null) {
            recyclerView.layoutManager = lm
        }
    }

    fun SetPhotoSize(row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var size = width / row - 2*padding

        mainAdapter!!.SetPhotoSize(size, padding)
    }



}
