package com.example.wimmy

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class CalendarAdapter(context : FragmentActivity, days : ArrayList<Date>, eventDays : HashSet<Date>?, inputMonth : Int) :
        ArrayAdapter<Date>(context, R.layout.fragment_cal, days) {
    private val inflater : LayoutInflater = LayoutInflater.from(context)
    private val inputMonth = inputMonth

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val calendar = Calendar.getInstance()
        val date = getItem(position)

        calendar.time = date
        val day = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.DAY_OF_WEEK)

        val today = Date()
        val calendarToday = Calendar.getInstance()
        calendarToday.time = today

        if (view == null) {
            view = inflater.inflate(R.layout.calendar_day_layout, parent, false)
        }

        (view as TextView).setTypeface(null, Typeface.NORMAL)
        var textView = view.findViewById<TextView>(R.id.calendar_day)

        //저번 달 날짜
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
        //오늘의 날짜
        else if(year != calendarToday.get(Calendar.YEAR) &&
            day == calendarToday.get(Calendar.DATE)) {

        }
        else {
            textView.setTextColor(Color.BLACK)
        }

        textView.text = calendar.get(Calendar.DATE).toString()

        return view
    }
}