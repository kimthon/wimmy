package com.example.wimmy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.Adapter.PagerRecyclerAdapter
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.TagData
import com.example.wimmy.db.thumbnailData
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class PhotoViewPager : AppCompatActivity() {
    private var recyclerAdapter : PagerRecyclerAdapter?= null
    private var subimg: ImageView? = null
    internal lateinit var viewPager: ViewPager
    private var photoList = ArrayList<PhotoData>()
    private var tagList = ArrayList<TagData>()
    private var index: Int = 1
    private var ck: Boolean = false
    private var check: Boolean = false
    private var check1: Boolean = false
    private var check_index: Int = 0
    private var list_temp = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        val uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        var newUiOptions = uiOptions;*/
        getExtra()
        setContentView(R.layout.photoview_frame)
        subimg = findViewById(R.id.sub_img) as ImageView // 뷰페이저로 넘어올 때, 애니메이션을 위한 눈속임
        subimg!!.setImageResource(R.drawable.loding_image)
    }
    private fun setView(view: View, toolbar: View, bottombar: View) {

        viewPager = view.findViewById<RecyclerView>(R.id.imgViewPager) as ViewPager
        recyclerAdapter =
            PagerRecyclerAdapter(
                this,
                photoList, toolbar, bottombar
            )

        //Log.d("asd",recyclerAdapter?.getThumbnailList())
        viewPager.adapter = recyclerAdapter

    }

    override fun onBackPressed() {
        val intent = Intent()
        //subimg = findViewById(R.id.sub_img) as ImageView
        //subimg!!.setImageResource(R.drawable.loding_image) // 돌아갈 때, 애니메이션을 위한 눈속임
        intent.putExtra("index", check_index)
        setResult(Activity.RESULT_OK, intent)
        supportFinishAfterTransition()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toolbar_text(position: Int, name: AppCompatTextView, date: AppCompatTextView, location: AppCompatTextView, tag: AppCompatTextView){
        name.setText(photoList[position].name)

        val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E) / HH:mm:ss")
        val date_string = (formatter).format(photoList[position].date_info)
        //var date_string: String = Date.parse("${photoList[position].date_info, formatter}")
        date.setText(date_string)
        location.setText(photoList[position].location_info)

        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        tag.setText(vm.getTag(photoList[position].photo_id).joinToString ( ", " ))

        list_temp.clear()
    }

    fun getExtra(){
        if (intent.hasExtra("photo_num") && intent.hasExtra("photo_list")) {
            index = intent.getIntExtra("photo_num", 0)
            photoList = intent.getSerializableExtra("photo_list") as ArrayList<PhotoData>
            tagList = intent.getSerializableExtra("tag_list") as ArrayList<TagData>
        }
        else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }
        //var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        //tagList.addAll(vm.getTag(photoList[index].photo_id))
        //Log.d("태그는:", "${vm.getTag(0)}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()

        val view: View = findViewById(R.id.imgViewPager)
        val text_name = findViewById<AppCompatTextView>(R.id.imgView_text)
        val date_name = findViewById<AppCompatTextView>(R.id.imgView_date)
        val location_name = findViewById<AppCompatTextView>(R.id.imgView_location)
        val tag_name = findViewById<AppCompatTextView>(R.id.imgView_tag)

        val tb = findViewById<View>(R.id.mainphoto_toolbar)
        val bt = findViewById<View>(R.id.bottom_photo_menu)

        setView(view, tb, bt)
        toolbar_text(index, text_name, date_name, location_name, tag_name)


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if(check == false ) {
                    viewPager.setCurrentItem(index, false)
                    check = true
                }
                subimg!!.setImageResource(0)    // 애니메이션
                tb.visibility = View.VISIBLE
                bt.visibility = View.VISIBLE
            }
            override fun onPageSelected(position: Int) {
                check_index = position
                toolbar_text(position, text_name, date_name, location_name, tag_name)
            }
        })

    }
    /*fun setTagList(list : List<TagData>) {
        tagList = list
    }*/


}