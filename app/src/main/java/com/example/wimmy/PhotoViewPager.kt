package com.example.wimmy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.e
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.Adapter.PagerRecyclerAdapter
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.TagData
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.photoview_frame.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

class PhotoViewPager : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener  {
    private var recyclerAdapter : PagerRecyclerAdapter?= null
    //private var subimg: ImageView? = null
    internal lateinit var viewPager: ViewPager
    private var photoList = ArrayList<PhotoData>()
    private var tagList = ArrayList<TagData>()
    private var index: Int = 0
    private var thumbnail: Long? = null
    private var check: Int = 0
    private var delete_check: Int = 0



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        val uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        var newUiOptions = uiOptions;*/
        setContentView(R.layout.photoview_frame)
        val view: View = findViewById(R.id.imgViewPager)
        getExtra()
        val text_name = findViewById<AppCompatTextView>(R.id.imgView_text)
        val date_name = findViewById<AppCompatTextView>(R.id.imgView_date)
        val location_name = findViewById<AppCompatTextView>(R.id.imgView_location)
        val tag_name = findViewById<AppCompatTextView>(R.id.imgView_tag)
        val favorite = findViewById<ImageView>(R.id.favorite)

        bottom_photo_menu.setOnNavigationItemSelectedListener(this)
        setView(view, mainphoto_toolbar, bottom_photo_menu)
        toolbar_text(index, text_name, date_name, location_name, tag_name, favorite)


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                mainphoto_toolbar!!.visibility = View.VISIBLE
                bottom_photo_menu.visibility = View.VISIBLE
                //subimg!!.setImageResource(0)    // 애니메이션
            }

            override fun onPageSelected(position: Int) {
                index = position
                toolbar_text(position, text_name, date_name, location_name, tag_name, favorite)
            }
        })

        //TODO("DB코드 달아야함")
        favorite.setOnClickListener {
            if(check == 0) {
                favorite.setImageResource(R.drawable.ic_favorite_checked)
                check = 1
            }
            else {
                favorite.setImageResource(R.drawable.ic_favorite)
                check = 0
            }

        }

    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setView(view: View, toolbar: androidx.appcompat.widget.Toolbar, bottombar: View) {

        viewPager = view.findViewById<RecyclerView>(R.id.imgViewPager) as ViewPager

        recyclerAdapter =
            PagerRecyclerAdapter(
                this,
                photoList, toolbar, bottombar
            )

        viewPager.adapter = recyclerAdapter
        viewPager.setCurrentItem(index, false)

    }

    override fun onBackPressed() {
        finishActivity()
    }


    @SuppressLint("SimpleDateFormat")
    fun toolbar_text(position: Int, name: AppCompatTextView, date: AppCompatTextView, location: AppCompatTextView, tag: AppCompatTextView, favorite: ImageView){
        name.setText(photoList[position].name)

        val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E) / HH:mm:ss")
        val date_string = (formatter).format(photoList[position].date_info)
        //var date_string: String = Date.parse("${photoList[position].date_info, formatter}")
        date.setText(date_string)
        location.setText(photoList[position].location_info)

        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        vm.setTags(tag, photoList[position].photo_id)
        if(photoList[position].favorite == true) {
            favorite.setImageResource(R.drawable.ic_favorite_checked)
            check = 1
        }
        else {
            favorite.setImageResource(R.drawable.ic_favorite)
            check = 0
        }
    }

    fun getExtra(){
        if (intent.hasExtra("photo_num") && intent.hasExtra("photo_list")) {
            thumbnail = intent.getLongExtra("thumbnail", 0)
            //subimg = findViewById(R.id.sub_img) as ImageView // 뷰페이저로 넘어올 때, 애니메이션을 위한 눈속임
            //subimg!!.setImageBitmap(MediaStore_Dao.LoadThumbnail(this, thumbnail!!))

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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when(p0.itemId){
            R.id.menu_tag_insert -> {
                val et: EditText = EditText(this@PhotoViewPager);
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this@PhotoViewPager,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                dlg.setTitle("특징 삽입")
                dlg.setView(et)
                dlg.setMessage("삽입할 사진의 특징을 입력해주세요 ")
                dlg.setIcon(R.drawable.ic_tag)
                dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(this, "입력 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                })
                dlg.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->

                })
                dlg.show()
            }
            R.id.menu_share -> {
                val intent = Intent(android.content.Intent.ACTION_SEND)
                var bitmap = BitmapFactory.decodeFile(photoList[index].file_path +'/'+ photoList[index].name)
                bitmap =  MediaStore_Dao.modifyOrientaionById(this, photoList[index].photo_id, bitmap)
                val uri: Uri? = getImageUri(this, bitmap)
                intent.setType("image/*")
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                val chooser = Intent.createChooser(intent, "친구에게 공유하기")
                startActivity(chooser)
            }
            R.id.menu_delete -> {
                delete(imgViewPager, mainphoto_toolbar, bottom_photo_menu)
            }
        }

        return true
    }

    private fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            context.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun delete(view: View, toolbar: androidx.appcompat.widget.Toolbar, bottombar: View) {
        val dlg: AlertDialog.Builder = AlertDialog.Builder(this@PhotoViewPager,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        dlg.setTitle("사진 삭제")

        dlg.setMessage("정말 삭제하시겠습니까? ")
        dlg.setIcon(R.drawable.ic_delete)
        dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            photoList.removeAt(index)
            Toast.makeText(this, "삭제 완료 되었습니다.", Toast.LENGTH_SHORT).show()
            //TODO 삭제 쿼리 필요
            if(index == 0 && photoList.size == 0) {
                finishActivity()
            }
            else {
                if (index >= photoList.size) {
                    index = index - 1
                }
                setView(view, toolbar, bottombar)
                toolbar_text(index, imgView_text, imgView_date, imgView_location, imgView_tag, favorite)
            }
            delete_check = 1
        })
        dlg.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->

        })
        dlg.show()
    }

    private fun finishActivity() {
        val intent = Intent()
        intent.putExtra("index", index)
        if(delete_check == 1)
            intent.putParcelableArrayListExtra("delete_list", photoList)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}


    /*fun setTagList(list : List<TagData>) {
        tagList = list
    }*/


