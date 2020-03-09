package com.example.wimmy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.db.*
import kotlinx.android.synthetic.main.main_photoview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.io.File
import java.lang.Thread.sleep

class Main_PhotoView: AppCompatActivity() {
    private var tagList = ArrayList<TagData>()
    private var recyclerAdapter : RecyclerAdapterPhoto?= null
    private var recyclerView: RecyclerView? = null
    private var mLastClickTime: Long = 0
    private var delete_check: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_photoview)
        val view: View = findViewById(R.id.photo_recyclerView)

        SetHeader()
        setView(view)
        getExtra(view)

        updown_Listener(recyclerView)


        val onScrollListener = object:RecyclerView.OnScrollListener() {
            var temp: Int = 0
            override fun onScrolled(@NonNull recyclerView:RecyclerView, dx:Int, dy:Int) {
                if(temp == 1) {
                    super.onScrolled(recyclerView, dx, dy)
                    up_button.visibility = View.GONE
                    down_button.visibility = View.GONE
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                up_button.visibility = View.VISIBLE
                down_button.visibility = View.VISIBLE
                temp = 1
            }

        }
        recyclerView?.setOnScrollListener(onScrollListener)

    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(3, 3)
    }

    private fun setView(view : View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.photo_recyclerView)
        recyclerAdapter =
            RecyclerAdapterPhoto(this, arrayListOf()) {
                    PhotoData, num, image ->  if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                Toast.makeText(this, "인덱스: ${num} 이름: ${PhotoData.name}", Toast.LENGTH_SHORT)
                    .show()

                val intent = Intent(this, PhotoViewPager::class.java)
                intent.putExtra("photo_num", num)
                intent.putExtra("thumbnail", PhotoData.photo_id)

                intent.putParcelableArrayListExtra("photo_list", recyclerAdapter!!.getThumbnailList())
                intent.putParcelableArrayListExtra("tag_list", tagList)

                startActivityForResult(intent, 100)

            }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(Main_PhotoView(), 3)
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
                    if(data!!.hasExtra("delete_list")) {
                        PhotoList = data!!.getSerializableExtra("delete_list") as ArrayList<PhotoData>
                        setView(photo_recyclerView)
                        setPhotoSize(3, 3)
                        delete_check = 1
                    }
                    val doc = data!!.getIntExtra("index", 0)
                    recyclerView?.smoothScrollToPosition(doc)
                }
            }
        }
    }

    fun getExtra(view: View){
        val getname: String?
        val title_type: ImageView = findViewById(R.id.title_type)
        val title: TextView = findViewById(R.id.title_name)
        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        if (intent.hasExtra("dir_name")) {
            getname = intent.getStringExtra("dir_name")

            vm.setOpenNameDir(recyclerAdapter!!, getname)

            title_type.setImageResource(R.drawable.ic_folder)
            title.text = File(getname).name
        }
        else if (intent.hasExtra("location_name")) {
            getname = intent.getStringExtra("location_name")
            vm.setOpenLocationDir(recyclerAdapter!!, getname)

            title_type.setImageResource(R.drawable.ic_location)
            title.text = getname
        }
        else if(intent.hasExtra("date_name")) {
            val date = intent.getLongExtra("date_name", 0)
            val cal = Calendar.getInstance()
            cal.time = Date(date)

            vm.setOpenDateDir(recyclerAdapter!!, cal)
            val formatter = SimpleDateFormat("yyyy년 MM월 dd일")
            getname = formatter.format(Date(date))

            title_type.setImageResource(R.drawable.ic_cal)

            title.text = getname
        }
        else if (intent.hasExtra("tag_name")) {
            getname = intent.getStringExtra("tag_name")
            vm.setOpenTagDir(recyclerAdapter!!, getname)

            title_type.setImageResource(R.drawable.ic_tag)
            title.text = getname
        }
    }

    fun updown_Listener(view: RecyclerView?) {
        up_button.setOnClickListener {
            view?.smoothScrollToPosition(0)
        }

        down_button.setOnClickListener {
            view?.smoothScrollToPosition(recyclerAdapter!!.getSize())
        }
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun finishActivity() {
        val intent = Intent()
        if(delete_check == 1)
            intent.putExtra("delete_check", delete_check)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}

