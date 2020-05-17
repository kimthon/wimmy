package com.jtsoft.wimmy.Activity

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
import com.jtsoft.wimmy.Activity.MainActivity.Companion.folder_type
import com.jtsoft.wimmy.Adapter.RecyclerAdapterForder
import com.jtsoft.wimmy.DirectoryThread
import com.jtsoft.wimmy.MainHandler
import com.jtsoft.wimmy.R
import com.jtsoft.wimmy.db.*
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
        var inputnum = 0
        when(type) {
            "tag_name" -> { inputnum = 1 }
            "date_name" -> { inputnum = 2 }
            "location_name" -> { inputnum = 3 }
            "file_name" -> { inputnum = 4 }
        }
        recyclerView = findViewById(R.id.search_recyclerView)
        recyclerAdapter = RecyclerAdapterForder(this, thumbnailList, inputnum)
            {thumbnailData ->
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    if((MainActivity.location_type == 0) && type == "location_name") {
                        val intent = Intent(this, Main_Map::class.java)
                        intent.putExtra("location_name", thumbnailData.data)
                        startActivityForResult(intent, 800)
                    }
                    else if(type == "date_name"){
                        val cal: Calendar = Calendar.getInstance()
                        cal.set(thumbnailData.data.substring(0, 4).toInt(), thumbnailData.data.substring(6, 8).toInt() - 1, thumbnailData.data.substring(10, 12).toInt(), 0, 0, 0)
                        val intent = Intent(this, Main_PhotoView::class.java)
                        intent.putExtra(type, cal.time)
                        startActivityForResult(intent, 201)
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

        val lm = GridLayoutManager(MainActivity(), folder_type)
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
                            thumbnailList = vm.getTagDirSearch(query!!)
                            MainHandler.post {
                                recyclerAdapter.setThumbnailList(thumbnailList)
                                sizeCheck()
                            }
                        }
                    }   // 태그

                    1 -> {
                        setView("location_name")
                        DirectoryThread.execute {
                            thumbnailList = vm.getLocationDirSearch(query!!)
                            MainHandler.post {
                                recyclerAdapter.setThumbnailList(thumbnailList)
                                sizeCheck()
                            }
                        }
                    }   // 위치

                    2 -> {
                        setView("file_name")
                        DirectoryThread.execute {
                            thumbnailList = vm.getNameDirSearch(searchview.context, query!!)
                            MainHandler.post {
                                recyclerAdapter.setThumbnailList(thumbnailList)
                                sizeCheck()
                            }
                        }
                    }   // 이름

                    3 -> {
                        val cal: Calendar = Calendar.getInstance()
                        try {
                            if (query!!.length == 7) cal.set(query!!.substring(0, 4).toInt(), query.substring(5, 7).toInt() - 1, 1, 0, 0, 0)
                            else if (query!!.length == 6) cal.set(query!!.substring(0, 4).toInt(), query.substring(5, 6).toInt() - 1, 1, 0, 0, 0)
                            setView("date_name")

                            DirectoryThread.execute {
                                thumbnailList = vm.getDateDirSearch(searchview.context, cal)
                                MainHandler.post {
                                    recyclerAdapter.setThumbnailList(thumbnailList)
                                    sizeCheck()
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@SearchView, "올바른 날짜 정보를 입력해주세요. (ex. 2020 03)", Toast.LENGTH_SHORT).show()
                        }
                    }   // 날짜
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                //mAdapter!!.filter.filter(query)
                return true
            }
        })
    }

    fun sizeCheck() {
        if(recyclerAdapter.getItemCount() == 0) {
            Toast.makeText(this@SearchView, "결과가 없어요. 다시 검색해주세요" , Toast.LENGTH_SHORT).show()
        }
        else {
            setPhotoSize(folder_type, 10)
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchview.windowToken, 0)
        }
    }

    fun dialogCreate(view: View) {
        val pd: YearMonthPickerDialog<View> = YearMonthPickerDialog(view, "search")
        pd.show(supportFragmentManager, "YearMonthPickerTest")
    }

}
