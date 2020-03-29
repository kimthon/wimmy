package com.example.wimmy.Activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.DBThread
import com.example.wimmy.MainHandler
import com.example.wimmy.R
import com.example.wimmy.db.*
import kotlinx.android.synthetic.main.main_photoview.*
import org.w3c.dom.ls.LSException
import java.text.SimpleDateFormat
import java.util.*
import java.io.File

class Main_PhotoView: AppCompatActivity() {
    private lateinit var recyclerAdapter : RecyclerAdapterPhoto
    private lateinit var recyclerView: RecyclerView
    private var mLastClickTime: Long = 0
    private var delete_check: Int = 0

    companion object {
        var list = arrayListOf<thumbnailData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_photoview)

        val view: View = findViewById(R.id.photo_recyclerView)

        SetHeader()
        setView(view)
        getExtra()

        updown_Listener(recyclerView)
        updownEvent()
    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(3, 3)
    }

    private fun setView(view : View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.photo_recyclerView)
        recyclerAdapter =
            RecyclerAdapterPhoto(this, arrayListOf()) {
                    thumbnailData, num ->  if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                val intent = Intent(this, PhotoViewPager::class.java)
                intent.putExtra("index", num)

                startActivityForResult(intent, 100)
            }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        recyclerView.adapter = recyclerAdapter
        list = recyclerAdapter.getThumbnailList()
        val lm = GridLayoutManager(Main_PhotoView(), 3)

        recyclerView.layoutManager = lm
    }

    private fun setPhotoSize(row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val size = width / row - 2*padding

        recyclerAdapter.setPhotoSize(size, padding)
    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.photo_toolbar)
        toolbar.bringToFront()
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    /*
                    if(data!!.hasExtra("delete_check")) {
                        setView(photo_recyclerView)
                        setPhotoSize(3, 3)
                        delete_check = 1
                    }
                     */
                    val doc = data!!.getIntExtra("index", 0)
                    recyclerView.scrollToPosition(doc)
                }
            }
        }
    }

    fun getExtra() {
        val getname: String?
        val title_type: ImageView = findViewById(R.id.title_type)
        val title: TextView = findViewById(R.id.title_name)
        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        when {
            intent.hasExtra("dir_name") -> {
                getname = intent.getStringExtra("dir_name")!!

                DBThread.execute {
                    getOpenDirByCursor(vm, vm.getOpenNameDirCursor(applicationContext, getname))
                }

                title_type.setImageResource(R.drawable.ic_folder)
                title.text = File(getname).name
            }

            intent.hasExtra("location_name") -> {
                getname = intent.getStringExtra("location_name")!!

                val liveData = vm.getOpenLocationDirIdList(getname)
                liveData.observe(this, androidx.lifecycle.Observer { idList ->
                    DBThread.execute {
                        getOpenDirByIdList(vm, idList)
                    }
                })

                title_type.setImageResource(R.drawable.ic_location)
                title.text = getname
            }

            intent.hasExtra("date_name") -> {
                val cal = intent.getSerializableExtra("date_name") as Date
                val calendar = Calendar.getInstance()

                calendar.time = cal
                DBThread.execute {
                    getOpenDirByCursor(vm, vm.getOpenDateDirCursor(applicationContext, calendar))
                }

                val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                getname = formatter.format(calendar.time)

                title_type.setImageResource(R.drawable.ic_cal)

                title.text = getname
            }

            intent.hasExtra("tag_name") -> {
                getname = intent.getStringExtra("tag_name")!!

                val liveData = vm.getOpenTagDirIdList(getname)
                liveData.observe(this, androidx.lifecycle.Observer { idList ->
                    DBThread.execute {
                        getOpenDirByIdList(vm, idList)
                    }
                })

                title_type.setImageResource(R.drawable.ic_tag)
                title.text = getname
            }

            intent.hasExtra("file_name") -> {
                var filename = intent.getStringExtra("file_name")!!

                DBThread.execute {
                    getOpenDirByCursor(vm, vm.getOpenFileDirCursor(applicationContext, filename))
                }

                title_type.setImageResource(R.drawable.ic_name)
                if(filename.length >= 23) {
                    filename = filename.substring(0, 23)
                    filename += ".."
                }
                title.text = filename
            }

            intent.hasExtra("favorite") -> {
                val liveData = vm.getOpenFavoriteDirIdList()
                liveData.observe(this, androidx.lifecycle.Observer { idList ->
                    DBThread.execute {
                        getOpenDirByIdList(vm, idList)
                    }
                })

                title_type.setImageResource(R.drawable.ic_favorite_checked)
                title.text = "즐겨찾기"
            }

            intent.hasExtra("search_date") -> {
                val date = intent.getStringExtra("search_date")!!
                val cal: Calendar = Calendar.getInstance()
                cal.set(date.substring(0, 4).toInt(), date.substring(6, 8).toInt() - 1, date.substring(10, 12).toInt(), 0, 0, 0)

                DBThread.execute {
                    getOpenDirByCursor(vm, vm.getOpenDateDirCursor(applicationContext, cal))
                }
                title_type.setImageResource(R.drawable.ic_cal)

                title.text = date
            }
        }
    }

    fun updown_Listener(view: RecyclerView?) {
        up_button.setOnClickListener {
            view?.smoothScrollToPosition(0)
        }

        down_button.setOnClickListener {
            view?.smoothScrollToPosition(recyclerAdapter.getSize())
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

    fun updownEvent() {
        updown_Listener(recyclerView)
        val onScrollListener = object : RecyclerView.OnScrollListener() {
            var temp: Int = 0
            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (temp == 1) {
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

        this.recyclerView.setOnScrollListener(onScrollListener)
    }

    private fun getOpenDirByCursor(vm : PhotoViewModel, cursor : Cursor?) {
        if (vm.CursorIsValid(cursor)) {
            do {
                val data = vm.getThumbnailDataByCursor(cursor!!)
                recyclerAdapter.addThumbnailList(data)
            } while (cursor!!.moveToNext())
            cursor.close()
        }
    }

    private fun getOpenDirByIdList(vm : PhotoViewModel, idList : List<Long>) {
        val list = vm.getThumbnailListByIdList(this, idList)
        recyclerAdapter.setThumbnailList(list)
        Main_PhotoView.list = list
    }
}

