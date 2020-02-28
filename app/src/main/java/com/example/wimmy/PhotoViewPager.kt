package com.example.wimmy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.Adapter.PagerRecyclerAdapter
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData


class PhotoViewPager : AppCompatActivity() {
    private var recyclerAdapter : PagerRecyclerAdapter?= null
    internal lateinit var viewPager: ViewPager
    private var thumbnailList = listOf<thumbnailData>()
    private var index: Int = 1
    private var name: String? = null
    private var check: Boolean = false
    private var check_index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        vm.getNameDir().observe(this,
            Observer<List<thumbnailData>> { t -> recyclerAdapter?.setThumbnailList(t) })

        setContentView(R.layout.photoview_frame)
        val view: View = findViewById(R.id.imgViewPager)
        setView(view)
        if (intent.hasExtra("photo_num") && intent.hasExtra("photo_name")) {
            index = intent.getIntExtra("photo_num", 0)
            name = intent.getStringExtra("photo_name")
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    Log.v("여기:", "${position}")
                    if(check == false) {
                        viewPager.setCurrentItem(index, false)
                        check = true
                    }
                }
                override fun onPageSelected(position: Int) {
                    check_index = position
                }

            })

        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

        // Inflate the layout for this fragment

/*viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }
    override fun onPageSelected(position: Int) {

    }

})
*/

    }
    private fun setView(view: View) {
        viewPager = view.findViewById<RecyclerView>(R.id.imgViewPager) as ViewPager
        recyclerAdapter =
            PagerRecyclerAdapter(
                this,
                thumbnailList
            )
        viewPager?.adapter = recyclerAdapter
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("index", check_index)
        setResult(Activity.RESULT_OK, intent)
        Log.v("넌 뭐냐", "${check_index}")
        finish()
    }
}
