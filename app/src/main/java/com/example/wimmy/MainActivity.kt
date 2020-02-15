package com.example.wimmy

import android.content.Intent
import android.hardware.display.DisplayManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*

import android.view.View
import android.widget.Button
import android.widget.TabHost
import android.widget.TabWidget
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar



class MainActivity : AppCompatActivity() {
    var photoList = arrayListOf<PhotoData>(
        PhotoData("dummy", "dummy", "dummy",0,  false),
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

        var db = DBHelper(this)

        SetView()
        SetPhotoSize(3, 10)
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
        toolbar.bringToFront()
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(null)
    }

    private fun SetPhotoSize(row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var size = width / row - 2*padding

        mainAdapter!!.SetPhotoSize(size, padding)
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
