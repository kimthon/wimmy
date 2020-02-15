package com.example.wimmy


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.jar.Attributes

/**
 * A simple [Fragment] subclass.
 */
class NameFragment : Fragment() {
    var photoList = arrayListOf<PhotoData>(
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false)
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SetView()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_name, container, false)
    }

    fun SetView() {

        val recyclerView = getView()?.findViewById<RecyclerView>(R.id.mRecycleView)
        val mainAdapter = MainAdapter(MainActivity(), photoList)
        if (recyclerView != null) {
            recyclerView.adapter = mainAdapter
        }
        val lm = GridLayoutManager(MainActivity(), 3)
        if (recyclerView != null) {
            recyclerView.layoutManager = lm
        }
    }




}
