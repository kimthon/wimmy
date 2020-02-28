package com.example.wimmy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.indexOf
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData

class Main_PhotoView: AppCompatActivity() {
    private var recyclerAdapter : RecyclerAdapterPhoto?= null
    var recyclerView: RecyclerView? = null
    private var thumbnailList = listOf<thumbnailData>()
    private var num: Int = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_photoview)
        val view: View = findViewById(R.id.photo_recyclerView)
        setView(view)
        SetHeader()
        setPhotoSize(3, 10)
        // Inflate the layout for this fragment
        var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        vm.getNameDir().observe(this,
            Observer<List<thumbnailData>> { t -> recyclerAdapter?.setThumbnailList(t) })

    }

    private fun setView(view : View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.photo_recyclerView)
        recyclerAdapter =
            RecyclerAdapterPhoto(this, thumbnailList, num) {
                thumbnailData, num ->  Toast.makeText(this,"인덱스: ${num} 이름: ${thumbnailData.data}",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, com.example.wimmy.PhotoViewPager::class.java)
                intent.putExtra("photo_num", num)
                intent.putExtra("photo_name", thumbnailData.data)
                startActivityForResult(intent, 100)
            }
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), 3)
        recyclerView?.layoutManager = lm as RecyclerView.LayoutManager?

    }

    private fun setPhotoSize(row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var size = width / row - 2*padding

        recyclerAdapter!!.setPhotoSize(size, padding)
    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.photo_toolbar)
        toolbar.bringToFront()
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    val doc = data!!.getIntExtra("index", 0)
                    recyclerView?.scrollToPosition(doc)
                }
            }
        }
    }
}
