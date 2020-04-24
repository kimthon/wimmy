package com.jtsoft.wimmy.Adapter

import android.graphics.Color
import android.graphics.Typeface
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.jtsoft.wimmy.DBThread
import com.jtsoft.wimmy.MainHandler
import com.jtsoft.wimmy.R
import com.jtsoft.wimmy.db.PhotoViewModel
import java.util.*

class DateAdapter(context : FragmentActivity, size : Pair<Int, Int>?, days : ArrayList<Date>, inputMonth : Int , val itemClick: (Date) -> Unit) :
    ArrayAdapter<Date>(context, R.layout.fragment_cal, days) {
    private val vm = ViewModelProviders.of(context).get(PhotoViewModel::class.java)
    private val inflater : LayoutInflater = LayoutInflater.from(context)
    private var inputMonth : Int = inputMonth
    private var size : Pair<Int, Int>? = size
    private var mLastClickTime: Long = 0
    private var inputCheck : Int = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val calendar = Calendar.getInstance()
        val date = getItem(position)!!

        calendar.time = date
        //val day = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH)
        //val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.DAY_OF_WEEK)

        if (view == null) view = inflater.inflate(R.layout.calendar_day_layout, parent, false)
        val textView = view!!.findViewById<TextView>(R.id.calendar_day)
        val tagView = view.findViewById<TextView>(R.id.calendar_day_tag)

        view.setOnClickListener {
            if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                itemClick(date)
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }

        view.layoutParams.width = size!!.first
        if(view.layoutParams.height != size!!.second) {
            view.layoutParams.height = size!!.second
            view.requestLayout()
        }

        setExtraDay(textView, month, week)

        textView.text = calendar.get(Calendar.DATE).toString()
        tagView.text = ""
        val ckNum = inputCheck
        DBThread.execute {
            val textList = vm.getCalendarTags(this.context, calendar)
            if(textList.isNullOrEmpty()) return@execute

            else {
                var text = ""
                for (i in textList) {
                    if (ckNum != inputCheck) {
                        break
                    }
                    text += i + '\n'
                }
                if (ckNum == inputCheck)
                    MainHandler.post{ tagView.text = text }
            }
        }

        return view
    }

    fun setDateSize(size : Pair<Int, Int>) {
        this.size = size
        notifyDataSetChanged()
    }

    fun Update(cells : ArrayList<Date>, month : Int) {
        inputCheck++
        clear()
        MainHandler.removeMessages(0)
        addAll(cells)
        this.inputMonth = month
        notifyDataSetChanged()
    }

    private fun setToday(textView: TextView, year: Int, month: Int, day: Int) {
        val calendarToday = Calendar.getInstance()
        if(year == calendarToday.get(Calendar.YEAR) &&
            month == calendarToday.get(Calendar.MONTH) &&
            day == calendarToday.get(Calendar.DAY_OF_MONTH)) {
            textView.setTypeface(null, Typeface.BOLD)
        } else {
            textView.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun setExtraDay(textView: TextView, month : Int, week : Int) {
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
    }
}