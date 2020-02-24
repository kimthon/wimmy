package com.example.wimmy.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.wimmy.R
import java.util.*
import kotlin.collections.ArrayList

class CalendarAdapter(context : FragmentActivity, size : Pair<Int, Int>?, days : ArrayList<Pair<Date, String?>>, inputMonth : Int) :
        ArrayAdapter<Pair<Date, String?>>(context,
            R.layout.fragment_cal, days) {
    private val inflater : LayoutInflater = LayoutInflater.from(context)
    private var inputMonth : Int = inputMonth
    private var size : Pair<Int, Int>? = size
    private var requestFlag = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val calendar = Calendar.getInstance()
        val date = getItem(position)!!.first
        val tag = getItem(position)!!.second

        calendar.time = date
        val day = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.DAY_OF_WEEK)

        val calendarToday = Calendar.getInstance()

        if (view == null) view = inflater.inflate(R.layout.calendar_day_layout, parent, false)
        val textView = view!!.findViewById<TextView>(R.id.calendar_day)
        val tagView = view.findViewById<TextView>(R.id.calendar_day_tag)

        if(size != null) {
            view.layoutParams.width = size!!.first
            view.layoutParams.height = size!!.second
            if(requestFlag) view.requestLayout()
        }

        //다른 달 날짜
        if (month != inputMonth) {
            textView.setTextColor(Color.GRAY)
        }
        // 토요일
        else if (week == Calendar.SATURDAY) {
            textView.setTextColor(Color.BLUE)
        }
        //일요일
        else if (week == Calendar.SUNDAY) {
            textView.setTextColor(Color.RED)
        }
        //나머지
        else {
            textView.setTextColor(Color.BLACK)
        }

        //오늘의 날짜 수정 필요함
        /*
        if(year == calendarToday.get(Calendar.YEAR) &&
            month == calendar.get(Calendar.MONTH) &&
            day == calendarToday.get(Calendar.DATE)) {

        }
        */
        textView.text = calendar.get(Calendar.DATE).toString()
        tagView.text = tag

        return view
    }

    fun setDateSize(size : Pair<Int, Int>) {
        this.size = size
        //사이즈 새로 고침이 필요
        requestFlag = true
        notifyDataSetChanged()
        requestFlag = false
    }

    fun Update(cells : ArrayList<Pair<Date, String?>>, month : Int) {
        clear()
        addAll(cells)
        this.inputMonth = month
        notifyDataSetChanged()
    }
}