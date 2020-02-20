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
    var bottomNavigationView: BottomNavigationView? = null
    private var thumbnailList = listOf<thumbnailData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        var view : View = inflater.inflate(R.layout.fragment_cal, container, false)
        setView(view)
        setPhotoSize(3, 10)
        // Inflate the layout for this fragment

        var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        return view
    }

    private fun setView(view : View) {
        val cal = view.findViewById<MaterialCalendarView>(R.id.calendarView)
        cal.isDynamicHeightEnabled = true
        cal.addDecorators(
            SundayDecorator(),
            SaturdayDecorator(),
            TagAddDecorator())

        cal.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)

    }

    private fun setPhotoSize(row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var size = width / row - 2*padding
    }
}

class SundayDecorator : DayViewDecorator {
    private var cal = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(cal)
        var week = cal.get(Calendar.DAY_OF_WEEK)
        return week == Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.RED))
    }
}

class SaturdayDecorator : DayViewDecorator {
    private var cal = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(cal)
        var week = cal.get(Calendar.DAY_OF_WEEK)
        return week == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.BLUE))
    }
}

class TagAddDecorator : DayViewDecorator {
    private var cal = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(cal)
        var week = cal.get(Calendar.DAY_OF_WEEK)
        return week == Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(TagAddSpan("test"))
    }
}

class TagAddSpan : LineBackgroundSpan {
    var text : String = ""

    constructor(text : String) {
        this.text = text
    }

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        canvas.drawText(this.text, ((left+right)/2 - 10).toFloat(), (bottom+30).toFloat(), paint);
    }
}

