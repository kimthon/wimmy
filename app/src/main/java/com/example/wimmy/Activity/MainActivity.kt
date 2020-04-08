package com.example.wimmy.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.*
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.TagData
import com.example.wimmy.fragment.LocationFragment
import com.example.wimmy.fragment.NameFragment
import com.example.wimmy.fragment.TagFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.main_activity.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var vm : PhotoViewModel
    private lateinit var observer: ChangeObserver
    private var init : Boolean = false
    lateinit var mCurrentPhotoPath: String
    private val REQUEST_TAKE_PHOTO = 200
    private var FINISH_INTERVAL_TIME: Long = 1500
    private var backPressedTime: Long = 0

    companion object {
        var location_type: Int = 0
        var folder_type: Int = 3
        var photo_type: Int = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bnv.setOnNavigationItemSelectedListener(this)

        SetHeader()
        init()

        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        DBThread.execute {
            vm.Drop(this)
            CheckChangeData()
        }

        observer = ChangeObserver( Handler(), this)
        this.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer)

        val go_search = findViewById<ImageView>(R.id.main_search_button)
        go_search.setOnClickListener {
            val intent = Intent(this, SearchView::class.java)
            startActivity(intent)
        }

        val go_camera = findViewById<ImageView>(R.id.main_camera_button)
        go_camera.setOnClickListener {
            captureCamera()
        }
    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.favorite -> {
                val intent = Intent(this, Main_PhotoView::class.java)
                intent.putExtra("favorite", "favorite")
                startActivityForResult(intent, 300)
            }
            R.id.location_type -> {
                val selectitem = arrayOf<String>("맵으로 보기", "목록으로 보기")
                var select = location_type
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                dlg.setTitle("위치별 사진 설정")
                dlg.setSingleChoiceItems(selectitem, location_type) { dialog, i ->
                    when(i) {
                        0 -> select = 0
                        1 -> select = 1
                    }
                }
                dlg.setIcon(R.drawable.ic_tag)
                dlg.setPositiveButton("확인") { _, _ ->
                    Toast.makeText(this, "완료 되었습니다.", Toast.LENGTH_SHORT).show()
                    if(location_type != select) {
                        location_type = select
                    }
                }
                dlg.setNegativeButton("취소") { _, _ -> }
                dlg.show()
            }
            R.id.folder_type -> {
                val selectitem = arrayOf<String>("2개씩 보기", "3개씩 보기", "4개씩 보기")
                var select = folder_type
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                dlg.setTitle("폴더 목록 설정")
                dlg.setSingleChoiceItems(selectitem, folder_type - 2) { dialog, i ->
                    when(i) {
                        0 -> select = 2
                        1 -> select = 3
                        2 -> select = 4
                    }
                }
                dlg.setIcon(R.drawable.ic_folder)
                dlg.setPositiveButton("확인") { _, _ ->
                    Toast.makeText(this, "완료 되었습니다.", Toast.LENGTH_SHORT).show()
                    if(folder_type != select) {
                        folder_type = select
                        for(fragment: Fragment in supportFragmentManager.fragments) {
                            if (fragment.isVisible) {
                                val tag = fragment.tag
                                lateinit var frag: Fragment
                                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                                supportFragmentManager.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                when(tag) {
                                    "name" -> {
                                        frag = NameFragment(appbar)
                                    }
                                    "tag" -> {
                                        frag = TagFragment(appbar)
                                    }
                                    "location" -> {
                                        frag = LocationFragment(appbar)
                                    }
                                }
                                transaction.replace(R.id.frame_layout, frag, tag)
                                transaction.addToBackStack(tag)
                                transaction.commit()
                                transaction.isAddToBackStackAllowed
                                break
                            }
                        }

                    }
                }
                dlg.setNegativeButton("취소") { _, _ -> }
                dlg.show()
            }
            R.id.photo_type -> {
                val selectitem = arrayOf<String>("2개씩 보기", "3개씩 보기", "4개씩 보기", "5개씩 보기", "6개씩 보기")
                var select = photo_type
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                dlg.setTitle("사진 목록 설정")
                dlg.setSingleChoiceItems(selectitem, photo_type - 2) { dialog, i ->
                    when(i) {
                        0 -> select = 2
                        1 -> select = 3
                        2 -> select = 4
                        3 -> select = 5
                        4 -> select = 6
                    }
                }
                dlg.setIcon(R.drawable.ic_image)
                dlg.setPositiveButton("확인") { _, _ ->
                    Toast.makeText(this, "완료 되었습니다.", Toast.LENGTH_SHORT).show()
                    if (photo_type != select) {
                        photo_type = select
                    }
                }
                dlg.setNegativeButton("취소") { _, _ -> }
                dlg.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val fm = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()

        when(p0.itemId){
            R.id.menu_name -> {
                fm.popBackStackImmediate("name", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentA = NameFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentA, "name")
                transaction.addToBackStack("name")
            }
            R.id.menu_tag -> {
                fm.popBackStackImmediate("tag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentB = TagFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentB, "tag")
                transaction.addToBackStack("tag")
            }
            R.id.menu_cal -> {
                fm.popBackStackImmediate("cal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentC = DateFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentC, "cal")
                transaction.addToBackStack("cal")
            }
            R.id.menu_location -> {
                fm.popBackStackImmediate("location", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentD = LocationFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentD, "location")
                transaction.addToBackStack("location")
            }
        }
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        transaction.isAddToBackStackAllowed
        return true
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0) {
            val tempTime = System.currentTimeMillis()
            val intervalTime = tempTime - backPressedTime
            if (!(0 > intervalTime || FINISH_INTERVAL_TIME < intervalTime)) {
                finishAffinity()
                System.runFinalization()
                System.exit(0)
            } else {
                backPressedTime = tempTime
                Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        super.onBackPressed()
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        updateBottomMenu(bnv)
    }

    private fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("name")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("tag")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("cal")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("location")

        if(tag1 != null && tag1.isVisible) {navigation.menu.findItem(R.id.menu_name).isChecked = true }
        if(tag2 != null && tag2.isVisible) {navigation.menu.findItem(R.id.menu_tag).isChecked = true }
        if(tag3 != null && tag3.isVisible) {navigation.menu.findItem(R.id.menu_cal).isChecked = true }
        if(tag4 != null && tag4.isVisible) {navigation.menu.findItem(R.id.menu_location).isChecked = true }

    }

    fun init(): Boolean{
        if(!init) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragmentA = NameFragment(appbar)
            transaction.replace(R.id.frame_layout, fragmentA, "name")
            transaction.commit()
            init = true
        }
        return true
    }

    private fun captureCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val photoFile = createImageFile()
                if (photoFile != null) { // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
                    val providerURI = FileProvider.getUriForFile(this, packageName, photoFile)
                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            } catch (ex: IOException) {
                Log.e("captureCamera Error", ex.toString())
                return
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> {
                Log.i("REQUEST_TAKE_PHOTO", "${Activity.RESULT_OK}" + " " + "${resultCode}")
                if (resultCode == RESULT_OK) {
                    try {
                        galleryAddPic()
                    } catch (e: Exception) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString())
                    }

                } else {
                    Toast.makeText(this@MainActivity, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File? { // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures",
            "Wimmy"
        )
        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString())
            storageDir.mkdirs()
        }
        val imageFile = File(storageDir, imageFileName)
        mCurrentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    private fun galleryAddPic() {
        Log.i("galleryAddPic", "Call")
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        val f = File(mCurrentPhotoPath)
        val contentUri: Uri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()
    }


    // 위험 권한, 권한 전용 팝업
    // 안드로이드 앱 개발시 TargetSDK가 마시멜로 버전(APK 23)이상인 경우, 디바이스의 특정 기능을 사용할 때 권한을 요구하는데
    // 그 권한 중에 위험 권한으로 분류된 권한은 개발자가 직접 사용자에게 권한 허용을 물을 수 있도록 작성해야한다.
    // 즉, 코드로 작성해야함.

    fun CheckChangeData() {
        ChangeCheckThread.shutdownNow()
        ChangeCheckThread = ThreadPoolExecutor(1, 3, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
        ChangeCheckThread.execute {
            CheckAddedPhoto()
            CheckDeletedPhoto()
        }
    }

    private fun CheckAddedPhoto() {
        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        var lastAddedDate = pref.getLong("lastAddedDate", 0)
        val cursor = vm.getNewlySortedCursor(this, lastAddedDate)

        if (MediaStore_Dao.cursorIsValid(cursor)) {
            do {
                val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                // 인터넷이 끊길 시 스톱
                if (!NetworkIsValid(this)) break
                ChangeCheckThread.execute {
                    vm.getFullLocation(this, id)
                    vm.getFavorite(id)
                    AddTagsByApi(this, id)
                }

                lastAddedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED))
                editor.putLong("lastAddedDate", lastAddedDate)
                editor.apply()
            } while (cursor!!.moveToNext())
            cursor.close()
        }
    }

    private fun CheckDeletedPhoto() {
        val idCursor = vm.getIdCursor()
        if (MediaStore_Dao.cursorIsValid(idCursor)) {
            do {
                vm.CheckIdCursorValid(this, idCursor!!)

            } while (idCursor!!.moveToNext())
            idCursor.close()
        }
    }

    @Suppress("DEPRECATION")
    private fun NetworkIsValid(context: Context) : Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = cm.activeNetwork ?: return false
            val actNw = cm.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            cm.run {
                cm.activeNetworkInfo?.run {
                    result = when(type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }

    private fun AddTagsByApi(context: Context, id: Long) {
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.KO)
            .build()
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

        val bitmap = MediaStore_Dao.LoadThumbnailById(context, id) ?: return
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val labeler = FirebaseVision.getInstance().onDeviceImageLabeler
        labeler.processImage(image)
            .addOnSuccessListener { labels ->
                translator.downloadModelIfNeeded()
                    .addOnSuccessListener {
                        for (label in labels) {
                            translator.translate(label.text)
                                .addOnSuccessListener { translatedText ->
                                    if(label.confidence >= 0.85) {
                                        DBThread.execute {
                                            vm.Insert(TagData(id, translatedText))
                                        }
                                    }
                                }
                                .addOnFailureListener { e -> e.stackTrace }
                        }
                    }
                    .addOnFailureListener { e -> e.stackTrace }
            }
            .addOnFailureListener { e -> e.stackTrace }
    }
}
