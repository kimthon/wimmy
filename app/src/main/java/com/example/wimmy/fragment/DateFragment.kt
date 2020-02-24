package com.example.wimmy.fragment


import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.MainActivity
import com.example.wimmy.R
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * A simple [Fragment] subclass.
 */
class DateFragment : Fragment() {
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
        setPhotoSize(3, 10)
        // Inflate the layout for this fragment

        var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        vm.getDateDir().observe(this,
            Observer<List<thumbnailData>> { t -> recyclerAdapter!!.setThumbnailList(t)})

        return view
    }

    private fun setView(view : View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.nameRecycleView)
        recyclerAdapter =
            RecyclerAdapterForder(
                activity,
                thumbnailList
            )
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), 3)
        recyclerView?.layoutManager = lm as RecyclerView.LayoutManager?
    }

    private fun setPhotoSize(row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var size = width / row - 2*padding

        recyclerAdapter!!.setPhotoSize(size, padding)
    }
}