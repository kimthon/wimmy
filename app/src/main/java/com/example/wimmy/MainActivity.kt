package com.example.wimmy

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_map.*
import kotlinx.coroutines.selects.select
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var vm : PhotoViewModel
    private lateinit var observer: ChangeObserver
    private var init : Boolean = false
    lateinit var mCurrentPhotoPath: String
    private val REQUEST_TAKE_PHOTO = 200
    private var FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0

    companion object {
        var location_type: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bnv.setOnNavigationItemSelectedListener(this)

        SetHeader()
        init()

        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        /*
        vm.Drop()
        InitLastAddedDate()
         */

        vm.checkChangedData(this)

        observer = ChangeObserver( Handler(), vm, this )
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
            R.id.location_type -> {
                var selectitem = arrayOf<String>("맵으로 보기", "목록으로 보기")

                val dlg: AlertDialog.Builder = AlertDialog.Builder(this,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                dlg.setTitle("원하는 형태를 선택하세요.")
                dlg.setSingleChoiceItems(selectitem, location_type) { dialog, i ->
                    when(i) {
                        0 -> location_type = 0
                        1 -> location_type = 1
                    }
                }
                dlg.setIcon(R.drawable.ic_tag)
                dlg.setPositiveButton("확인") { _, _ ->
                    Toast.makeText(this, "완료 되었습니다.", Toast.LENGTH_SHORT).show()
                }
                dlg.setNegativeButton("취소") { _, _ -> }
                dlg.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            //R.id.favorate_menu =>
            //
        }
        return super .onOptionsItemSelected(item)
    }



    override fun onContextItemSelected(item: MenuItem): Boolean {
        val bottomNavigationView = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.menu_tag)
        return true
    }*/

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val toolbar: Toolbar = findViewById(R.id.main_toolbar)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        when(p0.itemId){
            R.id.menu_tag -> {
                val fragmentB = TagFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentB, "tag")
            }
            R.id.menu_cal -> {
                val fragmentC = DateFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentC, "cal")
            }
            R.id.menu_location -> {
                val fragmentD = LocationFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentD, "location")
            }
            R.id.menu_name ->{
                val fragmentA = NameFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentA, "name")
            }
        }
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        transaction.isAddToBackStackAllowed
        return true
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0) {
            var tempTime = System.currentTimeMillis();
            var intervalTime = tempTime - backPressedTime;
            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed();
            } else {
                backPressedTime = tempTime;
                Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return
            }
        }
        super.onBackPressed();
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        updateBottomMenu(bnv)
    }

    private fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("tag")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("cal")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("location")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("name")

        if(tag1 != null && tag1.isVisible) {navigation.menu.findItem(R.id.menu_tag).isChecked = true }
        if(tag2 != null && tag2.isVisible) {navigation.menu.findItem(R.id.menu_cal).isChecked = true }
        if(tag3 != null && tag3.isVisible) {navigation.menu.findItem(R.id.menu_location).isChecked = true }
        if(tag4 != null && tag4.isVisible) {navigation.menu.findItem(R.id.menu_name).isChecked = true }
    }

    fun init(): Boolean{
        if(!init) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragmentA = TagFragment(appbar)
            transaction.replace(R.id.frame_layout, fragmentA, "tag")
            transaction.commit()
            init = true
        }
        return true
    }

    private fun captureCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {

            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                Log.e("captureCamera Error", ex.toString())
                return
            }
            if (photoFile != null) { // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
                val providerURI = FileProvider.getUriForFile(this, packageName, photoFile)
                // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
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
                        galleryAddPic();

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

    private fun InitLastAddedDate() {
        val pref = getSharedPreferences("pref", MODE_PRIVATE)
        val editor = pref.edit()
        editor.remove("lastAddedDate")
        editor.apply()
    }
}



