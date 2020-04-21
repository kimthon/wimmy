package com.example.wimmy.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.*
import com.example.wimmy.Adapter.PagerRecyclerAdapter
import com.example.wimmy.Activity.Main_PhotoView.Companion.list
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.dialog.similarImageDialog
import com.example.wimmy.dialog.tagInsertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.photoview_frame.*
import java.io.ByteArrayOutputStream

class PhotoViewPager : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener  {
    private var recyclerAdapter : PagerRecyclerAdapter?= null
    private lateinit var viewPager: ViewPager
    private lateinit var vm : PhotoViewModel
    private lateinit var text_name : AppCompatTextView
    private lateinit var favorite: ImageView
    private lateinit var tag_name : AppCompatTextView
    private lateinit var date_name : AppCompatTextView
    private lateinit var location_name : AppCompatTextView
    private lateinit var Inflater: LayoutInflater

    private var index  = 0
    private var delete_check: Int = 0



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.photoview_frame)
        val view: View = findViewById(R.id.imgViewPager)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)


        getExtra()
        text_name = findViewById(R.id.imgView_text)
        date_name = findViewById(R.id.imgView_date)
        location_name = findViewById(R.id.imgView_location)
        tag_name = findViewById(R.id.imgView_tag)
        favorite = findViewById(R.id.favorite)

        bottom_photo_menu.setOnNavigationItemSelectedListener(this)
        try {
            setView(view, mainphoto_toolbar, bottom_photo_menu)
        } catch (e: Exception){
            Toast.makeText(this, "위치 데이터 초기 설정중입니다. 잠시만 기다려주세요", Toast.LENGTH_SHORT)
                .show()
        }

        toolbar_text(index)
        Inflater = LayoutInflater.from(this)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                mainphoto_toolbar!!.visibility = View.VISIBLE
                bottom_photo_menu.visibility = View.VISIBLE
            }

            override fun onPageSelected(position: Int) {
                index = position
                toolbar_text(position)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setView(view: View, toolbar: androidx.appcompat.widget.Toolbar, bottombar: View) {
        viewPager = view.findViewById(R.id.imgViewPager)
        recyclerAdapter = PagerRecyclerAdapter( this, list, toolbar, bottombar )

        viewPager.adapter = recyclerAdapter
        viewPager.adapter!!.notifyDataSetChanged()
        viewPager.setCurrentItem(index, false)
    }

    override fun onBackPressed() {
        finishActivity()
    }

    @SuppressLint("SimpleDateFormat")
    fun toolbar_text(position: Int){
        val id = list[position].photo_id

        DBThread.execute {
            val data = vm.getName(this.applicationContext, id)
            MainHandler.post { text_name.text = data }
        }

        DBThread.execute {
            val data = vm.getStringDate(applicationContext, id)
            MainHandler.post { date_name.text = data}
        }

        DBThread.execute {
            val data = vm.getLocation(applicationContext, id)
            MainHandler.post { location_name.text = data}
        }

        DBThread.execute {
            val data = vm.getTags(id)
            MainHandler.post { tag_name.text = data }
        }

        DBThread.execute {
            val data = vm.getFavorite(id)
            if (data) favorite.setImageResource(R.drawable.ic_favorite_checked)
            else favorite.setImageResource(R.drawable.ic_favorite)
        }

        favorite.setOnClickListener {
            DBThread.execute {
                val data = vm.changeFavorite(id)
                if (data) favorite.setImageResource(R.drawable.ic_favorite_checked)
                else favorite.setImageResource(R.drawable.ic_favorite)
            }
        }
    }


    fun getExtra(){
        if (intent.hasExtra("index")) {
            index = intent.getIntExtra("index", 0)
        }
        else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when(p0.itemId){
            R.id.menu_tag_insert -> {
                insertTag()
            }
            R.id.menu_share -> {
                share()
            }
            R.id.menu_similar -> {
                similarImage()
            }
            R.id.menu_delete -> {
                delete(imgViewPager, mainphoto_toolbar, bottom_photo_menu)
            }
        }

        return true
    }

    private fun similarImage() {
        val similarImageDialogView: View = layoutInflater.inflate(R.layout.similar_image_layout, null)
        val dlg = similarImageDialog(similarImageDialogView, vm, location_name.text.toString(), date_name.text.toString())
        dlg.isCancelable = false
        dlg.show(supportFragmentManager, "similarImageDialog")
    }

    private fun insertTag() {
        val popupInputDialogView: View = layoutInflater.inflate(R.layout.tag_diaglog, null)
        val dlg = tagInsertDialog(popupInputDialogView, vm, index, tag_name)
        dlg.isCancelable = false
        dlg.show(supportFragmentManager, "tagInsertDialog")
    }


    private fun share() {
        val intent = Intent(Intent.ACTION_SEND)
        var bitmap = getImage(this, list[index].photo_id)
        bitmap =  MediaStore_Dao.modifyOrientaionById(this, list[index].photo_id, bitmap)
        val uri: Uri? = getImageUri(this, bitmap)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        val chooser = Intent.createChooser(intent, "친구에게 공유하기")
        startActivity(chooser)
    }


    private fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            context.contentResolver,
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
        dlg.setCancelable(false)
        dlg.setIcon(R.drawable.ic_delete)
        dlg.setPositiveButton("확인") { _, _ ->
            val id = list[index].photo_id
            DBThread.execute { vm.Delete(this, id) }

            list.removeAt(index)
            Toast.makeText(this, "삭제 완료 되었습니다.", Toast.LENGTH_SHORT).show()
            if(list.size == 0) {
                finishActivity()
            } else {
                if (index >= list.size) {
                    index -= 1
                }
                setView(view, toolbar, bottombar)
                toolbar_text(index)
            }
            delete_check = 1
        }
        dlg.setNegativeButton("취소") { _, _ -> }
        dlg.show()
    }

    private fun finishActivity() {
        val intent = Intent()
        intent.putExtra("index", index)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}