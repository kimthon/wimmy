package com.example.wimmy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.Adapter.PagerRecyclerAdapter
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData
import java.lang.Thread.sleep


class PhotoViewPager : AppCompatActivity() {
    private var recyclerAdapter : PagerRecyclerAdapter?= null
    private var layoutInflater2: LayoutInflater? = null
    private var subimg: ImageView? = null
    internal lateinit var viewPager: ViewPager
    private var thumbnailList = listOf<thumbnailData>()
    private var photoList = ArrayList<thumbnailData>()
    private var index: Int = 1
    private var ck: Boolean = false
    private var check: Boolean = false
    private var check1: Boolean = false
    private var check_index: Int = 0


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
                thumbnailList, toolbar, bottombar
            )

        //Log.d("asd",recyclerAdapter?.getThumbnailList())
        viewPager?.adapter = recyclerAdapter

    }

    override fun onBackPressed() {
        val intent = Intent()
        //subimg = findViewById(R.id.sub_img) as ImageView
        //subimg!!.setImageResource(R.drawable.loding_image) // 돌아갈 때, 애니메이션을 위한 눈속임
        intent.putExtra("index", check_index)
        setResult(Activity.RESULT_OK, intent)
        supportFinishAfterTransition()
    }

    fun toolbar_text(position: Int, name: AppCompatTextView){
        name.setText(photoList[position].data)
    }

    fun getExtra(){
        if (intent.hasExtra("photo_num") && intent.hasExtra("photo_list")) {
            index = intent.getIntExtra("photo_num", 0)
            photoList = intent.getSerializableExtra("photo_list") as ArrayList<thumbnailData>
        }
        else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        vm.getNameDir().observe(this,
            Observer<List<thumbnailData>> { t -> recyclerAdapter?.setThumbnailList(t)
            })


        val view: View = findViewById(R.id.imgViewPager)
        val text_name = findViewById<AppCompatTextView>(R.id.imgView_text)
        val tb = findViewById<View>(R.id.mainphoto_toolbar)
        val bt = findViewById<View>(R.id.bottom_photo_menu)

        /*image.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(check1 == false) {
                    Log.v("Dfss","fsdf")
                    bt.visibility = View.GONE
                    check1 = true
                }
                else {
                    bt.visibility = View.VISIBLE
                    check1 = false
                }
            }
        })*/
        setView(view, tb, bt)
        toolbar_text(index, text_name)




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
                text_name.setText(photoList[position].data)
            }
        })

    }


}