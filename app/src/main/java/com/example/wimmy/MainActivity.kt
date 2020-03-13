package com.example.wimmy

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.main_activity.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val REQUEST_TAKE_PHOTO = 200
    lateinit var mCurrentPhotoPath: String
    private var FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0
    private lateinit var observer: ChangeObserver
    var init_check: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bnv.setOnNavigationItemSelectedListener(this)

        SetHeader()
        init()
        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        //vm.Drop()
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
        checkPermission()
    }


    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
            val tempTime = System.currentTimeMillis()
            val intervalTime = tempTime - backPressedTime
            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed()
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
        if(init_check == 0) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragmentA = TagFragment(appbar)
            transaction.replace(R.id.frame_layout, fragmentA, "tag")
            transaction.commit()
            init_check = 1
        }
        return true
    }

    private fun getDate(year : Int, month : Int, day : Int) : Date {
        val date = Calendar.getInstance()
        date.set(year, month - 1, day)
        return date.time
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
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                    .setNeutralButton("설정", object: DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface:DialogInterface, i:Int) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:" + packageName)
                            startActivity(intent)
                        }
                    })
                    .setPositiveButton("확인", object:DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface:DialogInterface, i:Int) {
                            finish()
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show()
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSION_STORAGE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode:Int, @NonNull permissions:Array<String>, @NonNull grantResults:IntArray) {
        when (requestCode) {
            MY_PERMISSION_STORAGE -> for (i in grantResults.indices)
            {
                // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                if (grantResults[i] < 0)
                {
                    Toast.makeText(this@MainActivity, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }// 허용했다면 이 부분에서..
    }
    companion object {
        private val MY_PERMISSION_STORAGE = 1111
    }

}



