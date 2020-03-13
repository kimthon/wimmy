package com.example.wimmy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.Adapter.PagerRecyclerAdapter
import com.example.wimmy.Main_PhotoView.Companion.photoList
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.TagData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.photoview_frame.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

class PhotoViewPager : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener  {
    private var recyclerAdapter : PagerRecyclerAdapter?= null
    //private var subimg: ImageView? = null
    internal lateinit var viewPager: ViewPager
    private lateinit var vm : PhotoViewModel
    private var photoList = ArrayList<PhotoData>()
    private var index: Int = 0
    private lateinit var tag_name : AppCompatTextView
    private var thumbnail: Long? = null
    private var check: Int = 0
    private var delete_check: Int = 0



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.photoview_frame)
        val view: View = findViewById(R.id.imgViewPager)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        getExtra()
        val text_name = findViewById<AppCompatTextView>(R.id.imgView_text)
        val date_name = findViewById<AppCompatTextView>(R.id.imgView_date)
        val location_name = findViewById<AppCompatTextView>(R.id.imgView_location)
        tag_name = findViewById<AppCompatTextView>(R.id.imgView_tag)
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
        val id = photoList[position].photo_id
        val date_info = photoList[position].date_info
        date.text = if(date_info == null) {
            ""
        } else {
            (formatter).format(photoList[position].date_info)
        }

        vm.setLocation(location, id)
        vm.setTags(tag, id)
        vm.checkFavorite(favorite, id)

        favorite.setOnClickListener {
            vm.changeFavorite(favorite, id)
        }
    }

    fun getExtra(){
        if (intent.hasExtra("photo_num")) {
            //subimg = findViewById(R.id.sub_img) as ImageView // 뷰페이저로 넘어올 때, 애니메이션을 위한 눈속임
            //subimg!!.setImageBitmap(MediaStore_Dao.LoadThumbnail(this, thumbnail!!))

            index = intent.getIntExtra("photo_num", 0)
            photoList = intent.getSerializableExtra("photo_list") as ArrayList<PhotoData>

            // 번역 API, 이미지 분석 API Test
            val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.KO)
                .build()
            val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

            var bitmap = BitmapFactory.decodeFile(photoList[index].file_path +'/'+ photoList[index].name)

            bitmap =  MediaStore_Dao.modifyOrientaionById(this, photoList[index].photo_id, bitmap)
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler()
            labeler.processImage(image)
                .addOnSuccessListener { labels ->
                    translator.downloadModelIfNeeded()
                        .addOnSuccessListener {
                            for (label in labels) {
                                translator.translate(label.text)
                                    .addOnSuccessListener { translatedText ->
                                        if(label.confidence >= 0.7) {
                                            println("번호[" + index + "] " + "태그: " + translatedText)
                                            println("번호[" + index + "] " + "신뢰도: " + "${label.confidence}")
                                        }
                                    }
                                    .addOnFailureListener { exception ->

                                    }

                            }
                        }
                        .addOnFailureListener { exception ->
                        }
                }
                .addOnFailureListener { e ->

                }
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
                val et = EditText(this@PhotoViewPager)
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this@PhotoViewPager,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                dlg.setTitle("특징 삽입")
                dlg.setView(et)
                dlg.setMessage("삽입할 사진의 특징을 입력해주세요 ")
                dlg.setIcon(R.drawable.ic_tag)
                dlg.setPositiveButton("확인") { _, _ ->
                    Toast.makeText(this, "입력 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                    vm.Insert(TagData(photoList[index].photo_id, et.text.toString(), "manual"))
                    vm.setTags(tag_name, photoList[index].photo_id)
                }
                dlg.setNegativeButton("취소") { _, _ -> }
                dlg.show()
            }
            R.id.menu_share -> {
                val intent = Intent(Intent.ACTION_SEND)
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
        dlg.setPositiveButton("확인") { _, _ ->
            vm.Delete(photoList[index].photo_id)
            photoList.removeAt(index)
            Toast.makeText(this, "삭제 완료 되었습니다.", Toast.LENGTH_SHORT).show()
            if(index == 0 && photoList.size == 0) {
                finishActivity()
            } else {
                if (index >= photoList.size) {
                    index -= 1
                }
                setView(view, toolbar, bottombar)
                toolbar_text(index, imgView_text, imgView_date, imgView_location, imgView_tag, favorite)
            }
            delete_check = 1
        }
        dlg.setNegativeButton("취소") { _, _ -> }
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


