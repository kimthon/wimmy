package com.example.wimmy

import SwipeGesture
import YearMonthPickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.Adapter.DateAdapter
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_cal.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */

open class DateFragment() : Fragment() {
    private lateinit var header : LinearLayout
    private lateinit var gridView : GridView
    private lateinit var vm : PhotoViewModel
    private var size : Pair<Int, Int>? = null
    private var count = 0
    companion object {
        var calDate: Calendar = Calendar.getInstance()
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        val view : View = inflater.inflate(R.layout.fragment_cal, container, false)
        val calendar_allheader: View = view.findViewById(R.id.calendar_allheader) as View
        // Inflate the layout for this fragment
        setView(view)
        setHeader(view)
        setGridLayout(view)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        updateCalendar(view, calDate.clone() as Calendar)

        // 상단 스와이프 제스처
        val gestureListener: SwipeGesture = SwipeGesture(calendar_allheader)
        val gesturedetector = GestureDetector(calendar_allheader.context, gestureListener)
        calendar_allheader.setOnTouchListener { v, event ->
            return@setOnTouchListener gesturedetector.onTouchEvent(event)

        }
        return view
    }

    fun setView(view : View) {
        header = view.findViewById(R.id.calendar_week)
        gridView = view.findViewById(R.id.cal_grid)
    }

    fun setHeader(view : View) {
        val month_left_button = view.findViewById<AppCompatImageButton>(R.id.cal_month_left)
        val month_right_button = view.findViewById<AppCompatImageButton>(R.id.cal_month_right)
        val month_text = view.findViewById<TextView>(R.id.cal_month_text)


        month_left_button.setOnClickListener {
            calDate.add(Calendar.MONTH, -1)
            updateCalendar(view, calDate.clone() as Calendar)
            setHeaderDate(month_text)
        }

        month_right_button.setOnClickListener {
            calDate.add(Calendar.MONTH, 1)
            updateCalendar(view, calDate.clone() as Calendar)
            setHeaderDate(month_text)
        }

        // 다이얼로그
        month_text.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val pd: YearMonthPickerDialog<View> = YearMonthPickerDialog(view)
                //pd.setListener()
                pd.show(childFragmentManager, "YearMonthPickerTest")
            }
        })
        setHeaderDate(month_text)
    }

    private fun updateCalendar(view: View, inputCalendar : Calendar) {
        val cells = ArrayList<Date>()

        //해당 달의 1일으로 설정
        inputCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val month = inputCalendar.get(Calendar.MONTH)

        //월의 시작 요일 계산
        val monthBeginningCell = inputCalendar.get(Calendar.DAY_OF_WEEK) -1
        inputCalendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        var count = 0
        do {
            for (i in 1..7) {
                cells.add(inputCalendar.time)
                inputCalendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            ++count
        } while(inputCalendar.get(Calendar.MONTH) == month)

        if(gridView.adapter == null ) {
            gridView.adapter = DateAdapter(activity!!, size, cells, month) { Date ->
                val intent = Intent(activity, Main_PhotoView::class.java)
                intent.putExtra("date_name", Date.time)
                startActivity(intent)
            }
        }
        else {
            val gridAdapter = gridView.adapter as DateAdapter
            gridAdapter.Update(cells, month)
        }

        //이전 달과 주 갯수가 같으면 실행할 필요없음
        if(this.count != count) {
            setColumnSize(view, count)
            this.count = count
        }
    }
    private fun setGridLayout(view : View) {
        val gridViewWrapper = view.findViewById<LinearLayout>(R.id.cal_grid_wrapper)
        val header = view.findViewById<LinearLayout>(R.id.calendar_header)
        val statusBar = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = resources.getDimensionPixelSize(statusBar)

        val displayMetrics = DisplayMetrics()
        super.getActivity()!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val bnv = super.getActivity()!!.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        gridViewWrapper.viewTreeObserver.addOnGlobalLayoutListener( object : ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                gridViewWrapper.viewTreeObserver.removeOnGlobalLayoutListener(this)
                //padding
                val padding = header.paddingTop + calendar_week.paddingTop + gridViewWrapper.paddingTop
                gridViewWrapper.layoutParams.height = displayMetrics.heightPixels - (header.height + calendar_week.height + bnv.height + statusBarHeight + padding)
                gridViewWrapper.requestLayout()
            }
        })
    }

    private fun setColumnSize(view : View, count : Int) {
        val gridViewWrapper = view.findViewById<LinearLayout>(R.id.cal_grid_wrapper)

        gridViewWrapper.viewTreeObserver.addOnGlobalLayoutListener( object : ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                gridViewWrapper.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val density = context!!.resources.displayMetrics.density
                val width = gridViewWrapper.width / 7 - 1*density.toInt()
                val height = (gridViewWrapper.layoutParams.height - 1*density.toInt()) / count - 1*density.toInt()

                val gridAdapter = gridView.adapter as DateAdapter
                size = Pair(width, height)
                gridAdapter.setDateSize(size as Pair<Int, Int>)
            }
        })
    }

    fun setHeaderDate(month_text: TextView) {
        val year = calDate.get(Calendar.YEAR).toString()
        val month = (calDate.get(Calendar.MONTH) + 1).toString()
        month_text.text = "$year 년 $month 월"

    }
}




