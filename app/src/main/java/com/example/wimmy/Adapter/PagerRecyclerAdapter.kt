package com.example.wimmy.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.PhotoViewPager
import com.example.wimmy.R
import com.example.wimmy.db.thumbnailData
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Thread.sleep

public class PagerRecyclerAdapter(private val context: Context, var list: List<thumbnailData>, var tb: View, var bt: View) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private var check: Boolean = false

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater!!.inflate(R.layout.photoview_pager, null)
        val v2 = layoutInflater!!.inflate(R.layout.photoview_frame, null)
        val image = v.findViewById<View>(R.id.imgView) as ImageView
       // val text = v.findViewById<TextView>(R.id.imgView_text)       //image.setImageResource(list[position])
        /*val text2 = v.findViewById<View>(R.id.imgView_where) as TextView
        val text3 = v.findViewById<View>(R.id.imgView_tag) as TextView
        val text4 = v.findViewById<View>(R.id.imgView_date) as TextView*/
        //val tb = v2.findViewById<Toolbar>(R.id.mainphoto_toolbar)

       // val bt = v.findViewById<View>(R.id.bottom_photo_menu)
       // text.setText(list[position].data)
        val vp = container as ViewPager
        vp.addView(v, 0)
        image.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(check == false) {
                   // Log.d("이건?", tb.toString())
                    tb.visibility = View.GONE
                    bt.visibility = View.GONE
                    check = true
                }
                else {
                    tb.visibility = View.VISIBLE
                    bt.visibility = View.VISIBLE
                    check = false
                }
            }
        })


        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val v = `object` as View
        vp.removeView(v)
    }

    fun setThumbnailList(list : List<thumbnailData>) {
        this.list = list
        notifyDataSetChanged()
    }


}

