package com.example.wimmy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var photoList = arrayListOf<PhotoData>(
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
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.mRecycleView)
        val mainAdapter = MainAdapter(this, photoList)
        recyclerView.adapter = mainAdapter
        val lm = GridLayoutManager(this, 3)
        recyclerView.layoutManager = lm
    }
}
