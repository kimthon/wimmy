package com.example.wimmy.Activity

import android.Manifest
import android.R
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel
import kotlin.system.exitProcess

class SplashActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            translation_api()
        }

        /*val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo == null) { }*/

    }

    fun translation_api() {
        val intent = Intent(this, MainActivity::class.java)
        val dlg: AlertDialog.Builder = AlertDialog.Builder(this@SplashActivity,  R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        val modelManager = FirebaseModelManager.getInstance()
        val Model = FirebaseTranslateRemoteModel.Builder(FirebaseTranslateLanguage.KO).build()
        val conditions = FirebaseModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelManager.getInstance().isModelDownloaded(Model)
            .addOnSuccessListener { isDownloaded ->
                if (isDownloaded) {
                    startActivity(intent)
                    finish()
                } else {
                    dlg.setTitle("환영합니다") //제목
                    dlg.setMessage("추가 파일 설치가 필요합니다. 와이파이를 연결해주세요. \n\n다운로드 하시겠습니까? (30mb)") // 메시지
                    dlg.setCancelable(false)
                    dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                        loading()
                        modelManager.download(Model, conditions).addOnSuccessListener { modelManager.getDownloadedModels(
                            FirebaseTranslateRemoteModel::class.java).addOnSuccessListener { models ->
                            Toast.makeText(this@SplashActivity, "설치가 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            finish()
                            loadingEnd()
                        }.addOnFailureListener {
                        }
                        }.addOnFailureListener {}
                    })
                    dlg.setNegativeButton("취소") { _, _ ->
                        exitProcess(0)
                    }
                    dlg.show()
                }
            }

    }

    fun loading() {
        //로딩
        Handler().postDelayed(
            {
                progressDialog = ProgressDialog(this@SplashActivity, R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setMessage("필요한 파일을 다운로드 중입니다.\n잠시만 기다려 주세요.")
                progressDialog!!.show()
            }, 0
        )
    }

    fun loadingEnd() {
        Handler().postDelayed(
            { progressDialog?.dismiss() }, 0
        )
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED)
        {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                android.app.AlertDialog.Builder(this,  R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                    .setTitle("알림")
                    .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                    .setNeutralButton("설정", object: DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface:DialogInterface, i:Int) {
                            Toast.makeText(this@SplashActivity, "저장소 권한을 활성화하고 앱을 다시 실행시켜 주세요.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:" + packageName)
                            startActivity(intent)
                            finish()
                        }
                    })
                    .setPositiveButton("확인", object:DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface:DialogInterface, i:Int) {
                            finish()
                            System.exit(0)
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show()
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSION_STORAGE
                )
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
                    Toast.makeText(this@SplashActivity, "앱을 사용하기 위해서는 해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show()
                    checkPermission()
                }
                else
                    translation_api()
            }
        }// 허용했다면 이 부분에서..
    }
    companion object {
        val MY_PERMISSION_STORAGE = 1111
    }
}