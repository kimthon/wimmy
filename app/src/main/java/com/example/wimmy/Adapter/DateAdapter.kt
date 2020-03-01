package com.example.wimmy.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.SystemClock
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.example.wimmy.DateFragment
import com.example.wimmy.Main_PhotoView
import com.example.wimmy.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DateAdapter(context : FragmentActivity, size : Pair<Int, Int>?, days : ArrayList<Pair<Date, String?>>, inputMonth : Int) :
        ArrayAdapter<Pair<Date, String?>>(context,
            R.layout.fragment_cal, days) {
    private val inflater : LayoutInflater = LayoutInflater.from(context)
    private var inputMonth : Int = inputMonth
    private var size : Pair<Int, Int>? = size
    private var requestFlag = false
    private var mLastClickTime: Long = 0

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
        val calendar_data = view.findViewById(R.id.calendar_data) as View


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
        if(year == calendarToday.get(Calendar.YEAR) &&
            month == calendarToday.get(Calendar.MONTH) &&
            day == calendarToday.get(Calendar.DAY_OF_MONTH)) {
            textView.setTypeface(null, Typeface.BOLD)
        } else {
            textView.setTypeface(null, Typeface.NORMAL)
        }

        textView.text = calendar.get(Calendar.DATE).toString()
        tagView.text = tag
        if(textView.currentTextColor != Color.GRAY) {
            calendar_data.setOnClickListener {
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    val intent = Intent(view.context, Main_PhotoView::class.java)
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra("date_name", date)
                    //val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E) / HH:mm:ss")
                    //val dateinfo: String = "" + date
                    Log.d("헬로", "${date}")
                    //val date_string = (formatter).format(photoList[position].date_info)
                    view.context.startActivity(intent)
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        }

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