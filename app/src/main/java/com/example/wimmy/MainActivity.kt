package com.example.wimmy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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
        setContentView(R.layout.activity_main)

        val go_intent = findViewById(R.id.menu_map) as View
        go_intent.setOnClickListener {
            val intent = Intent(this@MainActivity, MapActivity::class.java)
            startActivity(intent)
        }

        SetView()
        SetHeader()
    }

    fun SetView() {

        val recyclerView = findViewById<RecyclerView>(R.id.mRecycleView)
        val mainAdapter = MainAdapter(this, photoList)
        recyclerView.adapter = mainAdapter
        val lm = GridLayoutManager(this, 3)
        recyclerView.layoutManager = lm
    }

    fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(null)
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
