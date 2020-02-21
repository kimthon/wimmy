package com.example.wimmy.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.R
import com.example.wimmy.db.thumbnailData

public class PagerRecyclerAdapter(private val context: Context, var list: List<thumbnailData>) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
   /* private val img = arrayOf(
        R.drawable.ic_cal,
        R.drawable.ic_folder,
        R.drawable.ic_map
    )*/

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
        val text = v.findViewById<View>(R.id.imgView_text) as TextView        //image.setImageResource(list[position])
        //text.setText("dfs")
        val vp = container as ViewPager
        vp.addView(v, 0)
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

