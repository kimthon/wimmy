package com.example.wimmy

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.db.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Main_PhotoView: AppCompatActivity() {
    private var PhotoArrayList = ArrayList<PhotoData>()
    private var TagArrayList = ArrayList<TagData>()
    private var PhotoList = listOf<PhotoData>()

    private var recyclerAdapter : RecyclerAdapterPhoto?= null
    var recyclerView: RecyclerView? = null
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_photoview)
        val view: View = findViewById(R.id.photo_recyclerView)
        getExtra(view)

        setView(view)
        SetHeader()
        setPhotoSize(3, 10)
        // Inflate the layout for this fragment
    }

    private fun setView(view : View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.photo_recyclerView)
        recyclerAdapter =
            RecyclerAdapterPhoto(this, PhotoList) {
                    PhotoData, num, image ->  if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                Toast.makeText(this, "인덱스: ${num} 이름: ${PhotoData.name}", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, PhotoViewPager::class.java)
                intent.putExtra("photo_num", num)
                PhotoArrayList.addAll(PhotoList)

                Log.d("처음사이즈", "${TagArrayList.size}")
                intent.putParcelableArrayListExtra("photo_list", PhotoArrayList)
                intent.putParcelableArrayListExtra("tag_list", TagArrayList)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        image, "pair_thumb"
                    )
                    startActivityForResult(intent, 100, options.toBundle())

                } else {
                    startActivityForResult(intent, 100)
                }
            }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), 3)
        recyclerView?.layoutManager = lm

    }

    private fun setPhotoSize(row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val size = width / row - 2*padding

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

    fun getExtra(view: View){
        var getname: String?
        val title_type: ImageView = findViewById(R.id.title_type)
        val title: TextView = findViewById(R.id.title_name)
        if (intent.hasExtra("dir_name")) {
            getname = intent.getStringExtra("dir_name")

            PhotoList = MediaStore_Dao.getNameDir(view.context, getname)

            title_type.setImageResource(R.drawable.ic_folder)
            title.text = getname
        }
        else if (intent.hasExtra("location_name")) {
            getname = intent.getStringExtra("location_name")
            PhotoList = MediaStore_Dao.getLocationDir(view.context, getname)

            title_type.setImageResource(R.drawable.ic_location)
            title.text = getname
        }
        else if(intent.hasExtra("date_name")) {
            val date = intent.getLongExtra("date_name", 0)
            val cal = Calendar.getInstance()

            cal.time = Date(date)
            PhotoList = MediaStore_Dao.getDateDir(view.context, cal)
            val formatter = SimpleDateFormat("yyyy년 MM월 dd일")
            getname = formatter.format(Date(date))

            title_type.setImageResource(R.drawable.ic_cal)

            title.text = getname
        }
        else if (intent.hasExtra("tag_name")) {
            getname = intent.getStringExtra("tag_name")
            val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
            PhotoList = MediaStore_Dao.getTagDir(view.context, vm, getname)

            title_type.setImageResource(R.drawable.ic_tag)
            title.text = getname
        }
    }
}
