package com.example.wimmy.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.R
import com.example.wimmy.db.PhotoData

public class PagerRecyclerAdapter(private val context: Context, var list: ArrayList<PhotoData>, var tb: View, var bt: View) : PagerAdapter() {
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
        val image = v.findViewById<View>(R.id.imgView) as ImageView
        val vp = container as ViewPager
        vp.addView(v, 0)
        val bitmap = BitmapFactory.decodeFile(list[position].file_path +'/'+ list[position].name)
        image.setImageBitmap(bitmap)
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

    fun setThumbnailList(list : ArrayList<PhotoData>) {
        this.list = list
        notifyDataSetChanged()
    }
}

