package com.example.wimmy

import SwipeGesture
import YearMonthPickerDialog
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.Activity.Main_PhotoView
import com.example.wimmy.Adapter.DateAdapter
import com.example.wimmy.db.PhotoViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_cal.*
import kotlinx.android.synthetic.main.main_activity.view.*
import java.util.*
import kotlin.collections.ArrayList

class DateFragment(v: AppBarLayout) : Fragment() {
    private lateinit var gridView : GridView
    private lateinit var vm : PhotoViewModel
    private var size : Pair<Int, Int>? = null
    private var count = 0
    val ab = v
    private var thisview: View? = null
    private var calendar_allheader: View? = null

    companion object {
        var calDate: Calendar = Calendar.getInstance()
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle? ): View? {
        ab.main_toolbar.visibility = View.GONE
        ab.setExpanded(true,false)

        thisview = inflater.inflate(R.layout.fragment_cal, container, false)

        calendar_allheader = thisview?.findViewById(R.id.calendar_allheader) as View
        // Inflate the layout for this fragment
        setView(thisview)
        setHeader(thisview)
        setGridLayout(thisview)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        updateCalendar(thisview, calDate.clone() as Calendar)


        // 상단 스와이프 제스처
        val gestureListener = SwipeGesture(calendar_allheader!!)
        val gesturedetector = GestureDetector(calendar_allheader!!.context, gestureListener)
        calendar_allheader!!.setOnTouchListener { v, event ->
            return@setOnTouchListener gesturedetector.onTouchEvent(event)
        }
        return thisview
    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                204 -> {
                    if(data!!.getIntExtra("delete_check", 0) == 1) {
                        calendar_allheader = thisview?.findViewById(R.id.calendar_allheader) as View
                        setView(thisview)
                        setHeader(thisview)
                        setGridLayout(thisview)
                    }
                }
            }
        }
    }*/

    fun setView(view : View?) {
        gridView = view!!.findViewById(R.id.cal_grid)
    }

    fun setHeader(view : View?) {
        val month_left_button = view!!.findViewById<AppCompatImageButton>(R.id.cal_month_left)
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
                val pd: YearMonthPickerDialog<View> = YearMonthPickerDialog(view, "calendar")
                //pd.setListener()
                pd.show(childFragmentManager, "YearMonthPickerTest")
            }
        })
        setHeaderDate(month_text)
    }

    private fun updateCalendar(view: View?, inputCalendar : Calendar) {
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
                startActivityForResult(intent, 204)
            }
        }
        else {
            val gridAdapter = gridView.adapter as DateAdapter
            gridAdapter.Update(cells, month)
        }

        //이전 달과 주 갯수가 같으면 실행할 필요없음
        if(this.count != count) {
            setColumnSize(view!!, count)
            this.count = count
        }
    }
    private fun setGridLayout(view : View?) {
        val gridViewWrapper = view?.findViewById<LinearLayout>(R.id.cal_grid_wrapper)
        val header = view?.findViewById<LinearLayout>(R.id.calendar_allheader)
        val statusBar = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = resources.getDimensionPixelSize(statusBar)

        val displayMetrics = DisplayMetrics()
        super.getActivity()!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val bnv = super.getActivity()!!.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        //padding
        val padding = header!!.paddingTop + header.paddingBottom + gridViewWrapper!!.paddingTop + gridViewWrapper.paddingBottom
        gridViewWrapper.layoutParams!!.height = displayMetrics.heightPixels - (header.layoutParams.height + bnv.height + statusBarHeight + padding)
    }

    private fun setColumnSize(view : View, count : Int) {
        val displayMetrics = DisplayMetrics()
        super.getActivity()!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val gridViewWrapper = view.findViewById<LinearLayout>(R.id.cal_grid_wrapper)
        val density = context!!.resources.displayMetrics.density
        val width = (displayMetrics.widthPixels - gridViewWrapper.paddingLeft - gridViewWrapper.paddingRight - (10*density).toInt()) / 7 - 1*density.toInt()
        val height = (gridViewWrapper.layoutParams.height - 1*density.toInt()) / count - 1*density.toInt()

        val gridAdapter = gridView.adapter as DateAdapter
        size = Pair(width, height)
        gridAdapter.setDateSize(size as Pair<Int, Int>)
    }

    fun setHeaderDate(month_text: TextView) {
        val year = calDate.get(Calendar.YEAR).toString()
        val month = (calDate.get(Calendar.MONTH) + 1).toString()
        month_text.text = "$year 년 $month 월"
    }
}




