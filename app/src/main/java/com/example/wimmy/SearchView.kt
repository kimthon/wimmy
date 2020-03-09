package com.example.wimmy

import YearMonthPickerDialog
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.db.*
import kotlinx.android.synthetic.main.search_view.*


class SearchView: AppCompatActivity() {

    private var thumbnailList = listOf<thumbnailData>()

    private var recyclerAdapter : RecyclerAdapterForder?= null
    var recyclerView: RecyclerView? = null
    private var mLastClickTime: Long = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_view)

        searchview.queryHint = "WIMMY 검색"
        searchview.onActionViewExpanded()
        searchview.isIconified = false

        dateQuery()
        searchResult()

    }

    private fun setView() {
        recyclerView = findViewById<RecyclerView>(R.id.search_recyclerView)
        recyclerAdapter =
            RecyclerAdapterForder(this, thumbnailList)
            {thumbnailData ->
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    val intent = Intent(this, Main_PhotoView::class.java)
                    intent.putExtra("dir_name", thumbnailData.data)
                    startActivityForResult(intent, 205)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
    }
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

       /* searchview.onActionViewExpandListener(object : MenuItem.OnActionExpandListener() {

                override val fun onMenuItemActionExpand(item: MenuItem) {
                    TextView text =(TextView) findViewById (R.id.txtstatus);
                    text.setText("현재 상태 : 확장됨");
                    return true;
                }
            }*/
    }

    fun searchResult(){
        searchview.setOnQueryTextListener (object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                thumbnailList = emptyList()
                when(searchview_spinner.selectedItemPosition) {

                    0 -> {
                        thumbnailList = MediaStore_Dao.getNameDir(this@SearchView)
                    }   // 태그

                    1 -> {
                        //thumbnailList = MediaStore_Dao.getNameDir(view.context, query)
                    }   // 위치

                    2 -> {
                        //thumbnailList = MediaStore_Dao.getNameDir(view.context, query)
                    }   // 이름

                    3 -> {
                        //thumbnailList = MediaStore_Dao.getNameDir(view.context, query)
                    }   // 날짜

                }

                if(thumbnailList.size == 0) {
                    Toast.makeText(this@SearchView, "결과가 없어요. 다시 검색해주세요" , Toast.LENGTH_SHORT).show()
                    return true
                }
                setView()
                setPhotoSize(3, 3)
                val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager;
                imm.hideSoftInputFromWindow(searchview.getWindowToken(), 0)
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
