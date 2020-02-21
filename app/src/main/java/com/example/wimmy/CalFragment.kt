package com.example.wimmy


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.provider.CalendarContract
import android.text.style.*
import android.util.DisplayMetrics
import android.view.*
import android.widget.CalendarView
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.prolificinteractive.materialcalendarview.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class CalFragment : Fragment() {
    private var thumbnailList = listOf<thumbnailData>()
    private lateinit var header : LinearLayout
    private lateinit var gridView : GridView
    private var cal_date : Calendar = Calendar.getInstance()

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        var view : View = inflater.inflate(R.layout.fragment_cal, container, false)

        // Inflate the layout for this fragment
        setView(view)

        var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        return view
    }

    private fun setView(view : View) {
        header = view.findViewById(R.id.calendar_week)
        gridView = view.findViewById(R.id.calendar_grid)

        var month_left_button = view.findViewById<AppCompatButton>(R.id.cal_month_left)
        var month_right_button = view.findViewById<AppCompatButton>(R.id.cal_month_right)
        var month_text = view?.findViewById<TextView>(R.id.cal_month_text)

        month_left_button.setOnClickListener {
            cal_date.add(Calendar.MONTH, -1)
            updateCalendar(null, cal_date.clone() as Calendar)
            setHeaderDate(month_text,cal_date)
        }

        month_right_button.setOnClickListener {
            cal_date.add(Calendar.MONTH, 1)
            updateCalendar(null, cal_date.clone() as Calendar)
            setHeaderDate(month_text,cal_date)
        }

        updateCalendar(null, cal_date.clone() as Calendar)
        setHeaderDate(month_text,cal_date)
    }

    fun updateCalendar(events : HashSet<Date>?, inputCalendar : Calendar) {
        var cells = ArrayList<Date>()

        //해당 달의 1일으로 설정
        inputCalendar.set(Calendar.DAY_OF_MONTH, 1)
        var month = inputCalendar.get(Calendar.MONTH)

        //월의 시작 요일 계산
        val monthBeginningCell = inputCalendar.get(Calendar.DAY_OF_WEEK) -1
        inputCalendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        do {
            for (i in 1..7) {
                cells.add(inputCalendar.time)
                inputCalendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        } while(inputCalendar.get(Calendar.MONTH) == month);

        gridView.adapter = CalendarAdapter(activity!!, cells, events, month)
    }

    fun setHeaderDate(month_text: TextView, cal : Calendar) {
        var year = cal_date.get(Calendar.YEAR).toString()
        var month = (cal_date.get(Calendar.MONTH) + 1).toString()
        month_text.text = "$year 년 $month 월"
    }
}


