package com.example.wimmy

import android.content.Intent
import android.hardware.display.DisplayManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
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

    private var recyclerViewer : RecyclerView ?= null
    private var mainAdapter : MainAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val go_intent = findViewById(R.id.menu_map) as View
        go_intent.setOnClickListener {
            val intent = Intent(this@MainActivity, MapActivity::class.java)
            startActivity(intent)
        }

        SetView()
        SetRecyclerViewRow(3, 10)
        SetHeader()
    }

    private fun SetView() {
        recyclerViewer = findViewById(R.id.mRecycleView)
        mainAdapter = MainAdapter(this, photoList)
        recyclerViewer!!.adapter = mainAdapter
        val lm = GridLayoutManager(this, 3)
        recyclerViewer!!.layoutManager = lm
    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(null)
    }

    private fun SetRecyclerViewRow(span : Int, padding : Int) {
        //get width
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        //set span
        var lm = recyclerViewer!!.layoutManager as GridLayoutManager
        lm.spanCount = span

        //calculate each_size
        var width = displayMetrics.widthPixels - (span + 1)*padding
        val eachSize = (width / span)

        mainAdapter?.SetPhotoSize(eachSize, padding)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            //R.id.favorate_menu =>
            //
        }
        return super .onOptionsItemSelected(item)
    }
}
