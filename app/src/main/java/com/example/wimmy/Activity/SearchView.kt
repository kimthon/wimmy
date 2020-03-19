package com.example.wimmy.Activity

import YearMonthPickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.DirectoryThread
import com.example.wimmy.MainHandler
import com.example.wimmy.R
import com.example.wimmy.db.*
import kotlinx.android.synthetic.main.search_view.*
import java.util.*

class SearchView: AppCompatActivity() {

    private var thumbnailList = arrayListOf<thumbnailData>()
    private lateinit var recyclerAdapter : RecyclerAdapterForder
    private lateinit var recyclerView : RecyclerView
    private var mLastClickTime: Long = 0
    private lateinit var vm: PhotoViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_view)
        searchview.queryHint = "WIMMY 검색"
        searchview.onActionViewExpanded()
        searchview.isIconified = false
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        dateQuery()
        searchResult()
    }

    private fun setView(type : String) {
        recyclerView = findViewById(R.id.search_recyclerView)
        recyclerAdapter = RecyclerAdapterForder(this, thumbnailList)
            {thumbnailData ->
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    if((MainActivity.location_type == 0) && type == "location_name") {
                        val intent = Intent(this, Main_Map::class.java)
                        intent.putExtra("location_name", thumbnailData.data)
                        startActivityForResult(intent, 800)
                    }
                    else {
                        val intent = Intent(this, Main_PhotoView::class.java)
                        intent.putExtra(type, thumbnailData.data)
                        startActivityForResult(intent, 201)
                    }
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        recyclerView.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), 3)
        recyclerView.layoutManager = lm
    }

    private fun setPhotoSize(row : Int, padding : Int) {
        recyclerView = findViewById<RecyclerView>(R.id.search_recyclerView)
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener( object : ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                val width = recyclerView.width
                val size = width / row - 2 * padding
                recyclerAdapter.setPhotoSize(size, padding)
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                205 -> {
                    if(data!!.getIntExtra("delete_check", 0) == 1) {
                        dateQuery()
                        searchResult()
                    }
                }
            }
        }
    }*/

    fun dateQuery(){
        searchview_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                if(position == 3)
                    dialogCreate(searchview)
                else
                    searchview.isIconified = false
            }
        }

    }

    fun searchResult(){
        searchview.setOnQueryTextListener (object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                thumbnailList = arrayListOf()
                when(searchview_spinner.selectedItemPosition) {

                    0 -> {
                        setView("tag_name")
                        DirectoryThread.execute {
                            val list = vm.getTagDirSearch(query!!)
                            MainHandler.post {
                                recyclerAdapter.setThumbnailList(list)
                            }
                        }
                    }   // 태그

                    1 -> {
                        setView("location_name")
                        DirectoryThread.execute {
                            val list = vm.getLocationDirSearch(query!!)
                            MainHandler.post { recyclerAdapter.setThumbnailList(list) }
                        }
                    }   // 위치

                    2 -> {
                        setView("file_name")
                        DirectoryThread.execute {
                            val list = vm.getNameDirSearch(searchview.context, query!!)
                            MainHandler.post {
                                recyclerAdapter.setThumbnailList(list)
                            }
                        }
                    }   // 이름

                    3 -> {
                        val cal: Calendar = Calendar.getInstance()
                        try {
                            cal.set(query!!.substring(0, 4).toInt(), query.substring(5, 7).toInt() - 1, 1, 0, 0, 0)
                            setView("date_name")
                            DirectoryThread.execute {
                                val list = vm.getDateDirSearch(searchview.context, cal)
                                MainHandler.post {
                                    recyclerAdapter.setThumbnailList(list)
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@SearchView, "올바른 날짜 정보를 입력해주세요. (ex. 2020 03)", Toast.LENGTH_SHORT).show()
                        }
                    }   // 날짜

                }

                /*if(thumbnailList.size == 0) {
                    Toast.makeText(this@SearchView, "결과가 없어요. 다시 검색해주세요" , Toast.LENGTH_SHORT).show()
                    return true
                }*/

                setPhotoSize(3, 10)
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchview.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                //mAdapter!!.filter.filter(query)
                return true
            }
        })
    }

    fun dialogCreate(view: View) {
        val pd: YearMonthPickerDialog<View> = YearMonthPickerDialog(view, "search")
        pd.show(supportFragmentManager, "YearMonthPickerTest")
    }

}
