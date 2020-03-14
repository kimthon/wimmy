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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.Adapter.PagerRecyclerAdapter
import com.example.wimmy.Main_PhotoView.Companion.list
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.TagData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.photoview_frame.*
import kotlinx.android.synthetic.main.tag_diaglog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class PhotoViewPager(): AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener  {
    private var recyclerAdapter : PagerRecyclerAdapter?= null
    private lateinit var viewPager: ViewPager
    private lateinit var vm : PhotoViewModel
    private lateinit var tag_name : AppCompatTextView
    private var index  = 0
    private var delete_check: Int = 0
    private var Inflater: LayoutInflater? = null


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.photoview_frame)
        val view: View = findViewById(R.id.imgViewPager)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        index = intent.getIntExtra("index", 0)

        getExtra()
        val text_name = findViewById<AppCompatTextView>(R.id.imgView_text)
        val date_name = findViewById<AppCompatTextView>(R.id.imgView_date)
        val location_name = findViewById<AppCompatTextView>(R.id.imgView_location)
        tag_name = findViewById<AppCompatTextView>(R.id.imgView_tag)
        val favorite = findViewById<ImageView>(R.id.favorite)

        bottom_photo_menu.setOnNavigationItemSelectedListener(this)
        setView(view, mainphoto_toolbar, bottom_photo_menu)
        toolbar_text(index, text_name, date_name, location_name, tag_name, favorite)

        Inflater = LayoutInflater.from(this)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                mainphoto_toolbar!!.visibility = View.VISIBLE
                bottom_photo_menu.visibility = View.VISIBLE
                //subimg!!.setImageResource(0)    // 애니메이션
            }

            override fun onPageSelected(position: Int) {
                toolbar_text(position, text_name, date_name, location_name, tag_name, favorite)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setView(view: View, toolbar: androidx.appcompat.widget.Toolbar, bottombar: View) {
        viewPager = view.findViewById<RecyclerView>(R.id.imgViewPager) as ViewPager
        recyclerAdapter = PagerRecyclerAdapter( this, list, toolbar, bottombar )

        viewPager.adapter = recyclerAdapter
        viewPager.setCurrentItem(index, false)

    }

    override fun onBackPressed() {
        finishActivity()
    }


    @SuppressLint("SimpleDateFormat")
    fun toolbar_text(position: Int, name: AppCompatTextView, date: AppCompatTextView, location: AppCompatTextView, tag: AppCompatTextView, favorite: ImageView){
        val id = list[position].photo_id

        vm.setName(name, id)
        vm.setDate(date, id)
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

            // 번역 API, 이미지 분석 API Test
            val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.KO)
                .build()
            val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

            val path = MediaStore_Dao.getPathById(this, list[index].photo_id)
            var bitmap = BitmapFactory.decodeFile(path)
            bitmap =  MediaStore_Dao.modifyOrientaionById(this, list[index].photo_id, bitmap)

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

    fun tagsInit(view: View, tags: List<String>) {
        if(tags.size >= 1) { view.tag1_edit.setText( tags.elementAt(0)) }
        if(tags.size >= 2) {
            view.tag1_add.performClick()
            view.tag2_edit.setText( tags.elementAt(1))
        }
        if(tags.size >= 3) {
            view.tag2_add.performClick()
            view.tag3_edit.setText( tags.elementAt(2))
        }
        if(tags.size >= 4) {
            view.tag3_add.performClick()
            view.tag4_edit.setText( tags.elementAt(3))
        }
        if(tags.size == 5) {
            view.tag4_add.performClick()
            view.tag5_edit.setText( tags.elementAt(4))
        }




    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when(p0.itemId){
            R.id.menu_tag_insert -> {
                insert_tag()
            }
            R.id.menu_share -> {
                share()
            }
            R.id.menu_delete -> {
                delete(imgViewPager, mainphoto_toolbar, bottom_photo_menu)
            }
        }

        return true
    }

    private fun insert_tag() {
        var lst = listOf<String>()
        val popupInputDialogView: View =
            layoutInflater.inflate(R.layout.tag_diaglog, null)
        vm.getTags(this@PhotoViewPager, popupInputDialogView, list[index].photo_id)
        val dlgBuilder: AlertDialog.Builder = AlertDialog.Builder(
            this@PhotoViewPager,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        dlgBuilder.setTitle("태그 삽입");
        dlgBuilder.setMessage("삽입할 사진의 특징을 입력해주세요. \n태그를 수정하거나 삭제할 수도 있습니다.")
        dlgBuilder.setIcon(R.drawable.ic_tag);
        dlgBuilder.setCancelable(false);
        dlgBuilder.setView(popupInputDialogView)
        val dlg = dlgBuilder.create()
        insert_tag_click(popupInputDialogView, dlg)
        dlg.show()
    }

    private fun share() {
        val intent = Intent(Intent.ACTION_SEND)
        val path = MediaStore_Dao.getPathById(this, list[index].photo_id)
        var bitmap = BitmapFactory.decodeFile(path)
        bitmap =  MediaStore_Dao.modifyOrientaionById(this, list[index].photo_id, bitmap)
        val uri: Uri? = getImageUri(this, bitmap)
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        val chooser = Intent.createChooser(intent, "친구에게 공유하기")
        startActivity(chooser)
    }

    private fun insert_tag_click(view: View, dlg: AlertDialog) {
        tag_addRemove(view)
        insert_saveCancel(view, dlg)

    }

    private fun tag_addRemove(view: View) {
        view.tag1_add.setOnClickListener{
            view.tag1_add.visibility = View.INVISIBLE
            view.tag2.visibility = View.VISIBLE
        }
        view.tag2_add.setOnClickListener{
            view.tag2_add.visibility = View.INVISIBLE
            view.tag3.visibility = View.VISIBLE
            view.tag2_remove.visibility = View.INVISIBLE
        }
        view.tag3_add.setOnClickListener{
            view.tag3_add.visibility = View.INVISIBLE
            view.tag4.visibility = View.VISIBLE
            view.tag3_remove.visibility = View.INVISIBLE
        }
        view.tag4_add.setOnClickListener{
            view.tag4_add.visibility = View.INVISIBLE
            view.tag5.visibility = View.VISIBLE
            view.tag4_remove.visibility = View.INVISIBLE
        }

        view.tag5_remove.setOnClickListener{
            view.tag5.visibility = View.GONE
            view.tag4_add.visibility = View.VISIBLE
            view.tag4_remove.visibility = View.VISIBLE
            view.tag5_edit.setText("")
        }
        view.tag4_remove.setOnClickListener{
            view.tag4.visibility = View.GONE
            view.tag3_add.visibility = View.VISIBLE
            view.tag3_remove.visibility = View.VISIBLE
            view.tag4_edit.setText("")
        }
        view.tag3_remove.setOnClickListener{
            view.tag3.visibility = View.GONE
            view.tag2_add.visibility = View.VISIBLE
            view.tag2_remove.visibility = View.VISIBLE
            view.tag3_edit.setText("")
        }
        view.tag2_remove.setOnClickListener{
            view.tag2.visibility = View.GONE
            view.tag1_add.visibility = View.VISIBLE
            view.tag2_edit.setText("")
        }
    }

    private fun insert_saveCancel(view: View, dlg: AlertDialog) {
        view.tag_save.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.Default).async {
                    vm.DeleteTag(list[index].photo_id)
                }.await()
                if (view.tag1_edit.text.toString() != "") {
                    vm.Insert(TagData(list[index].photo_id, view.tag1_edit.text.toString(), "manual"))
                    vm.setTags(tag_name, list[index].photo_id)
                }
                if (view.tag2_edit.text.toString() != "") {
                    vm.Insert(TagData(list[index].photo_id, view.tag2_edit.text.toString(), "manual"))
                    vm.setTags(tag_name, list[index].photo_id)
                }
                if (view.tag3_edit.text.toString() != "") {
                    vm.Insert(TagData(list[index].photo_id, view.tag3_edit.text.toString(), "manual"))
                    vm.setTags(tag_name, list[index].photo_id)
                }
                if (view.tag4_edit.text.toString() != "") {
                    vm.Insert(TagData(list[index].photo_id, view.tag4_edit.text.toString(), "manual"))
                    vm.setTags(tag_name, list[index].photo_id)
                }
                if (view.tag5_edit.text.toString() != "") {
                    vm.Insert(TagData(list[index].photo_id, view.tag5_edit.text.toString(), "manual"))
                    vm.setTags(tag_name, list[index].photo_id)
                }
                Toast.makeText(this@PhotoViewPager, "입력 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                dlg.cancel()
            }
        }

        view.tag_cancel.setOnClickListener{ dlg.cancel() }
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
        dlg.setCancelable(false);
        dlg.setIcon(R.drawable.ic_delete)
        dlg.setPositiveButton("확인") { _, _ ->
            vm.Delete(list[index].photo_id)
            list.removeAt(index)
            Toast.makeText(this, "삭제 완료 되었습니다.", Toast.LENGTH_SHORT).show()
            if(index == 0 && list.size == 0) {
                finishActivity()
            } else {
                if (index >= list.size) {
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
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}


    /*fun setTagList(list : List<TagData>) {
        tagList = list
    }*/


