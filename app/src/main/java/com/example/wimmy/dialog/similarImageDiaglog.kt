package com.example.wimmy.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.*
import com.example.wimmy.Activity.MainActivity
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData
import kotlinx.android.synthetic.main.similar_image_layout.view.*
import kotlinx.android.synthetic.main.similar_image_select.view.*

class similarImageDialog(context: Context, v: View, vm: PhotoViewModel, location: String, date: String): DialogFragment() {

    private val contextdlg = context
    private lateinit var recyclerAdapter : RecyclerAdapterPhoto
    private lateinit var recyclerView : RecyclerView
    private val v = v
    private val vm = vm
    private val location = location
    private val date = date
    private val savePhotoList = arrayListOf<Long>()
    private var similarList = arrayListOf<thumbnailData>()
    private var selectnum: Int = 0

    @RequiresApi(Build.VERSION_CODES.N)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        recyclerView = v.findViewById<RecyclerView>(R.id.similar_RecycleView)
        setView(v)
        setPhotoSize(2, 5)
        DBThread.execute {
            MainHandler.post {
                 getOpenDirByCursor(vm, vm.getOpenNameDirCursor(v.context, "Facebook"))
            }
        }

        val dlgBuilder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(    // 메인 다이얼로그
            contextdlg,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)

        dlgBuilder.setCancelable(false)
        dlgBuilder.setView(v)
        val dlg = dlgBuilder.create()
        saveSimilarPhoto(dlg)
        return dlg
    }

    private fun saveSimilarPhoto(dlg: androidx.appcompat.app.AlertDialog) {
        v.similar_cancel.setOnClickListener {
            dlg.cancel()
        }
        v.similar_ok.setOnClickListener {
            val warningBuilder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(context!!,    // 결고 다이얼로그
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            warningBuilder.setTitle("알림") //제목
            warningBuilder.setMessage("선택한 사진들을 제외하고는 모든 사진이 삭제됩니다.\n정말 저장하시겠습니까?\n\n (선택한사진: ${selectnum} 개)") // 메시지
            warningBuilder.setCancelable(false)
            warningBuilder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                //PhotoViewPager.this.finish()
                dialog.cancel()
                dlg.cancel()
                val pager: Activity = contextdlg as Activity        // 액티비티 종료
                pager.finish()
                Toast.makeText(contextdlg, "${selectnum} 개의 사진이 저장 완료 되었습니다. \n", Toast.LENGTH_SHORT).show()
            })
            warningBuilder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            val dlgWarning = warningBuilder.create()
            dlgWarning.show()
        }

    }
    private fun getOpenDirByCursor(vm : PhotoViewModel, cursor : Cursor?) {
        if (vm.CursorIsValid(cursor)) {
            do {
                val data = vm.getThumbnailDataByCursor(cursor!!)
                recyclerAdapter.addThumbnailList(data)
                MainHandler.post { recyclerAdapter.notifyItemInserted(recyclerAdapter.getSize()) }
            } while (cursor!!.moveToNext())
            cursor.close()
        }
    }
    override fun onResume() {
        super.onResume()
        setPhotoSize(2, 5)
    }

    private fun setView(view : View?) {
        recyclerAdapter =
            RecyclerAdapterPhoto(activity, ArrayList()) { thumbnailData, num ->
                val similarImageSelectView: View = layoutInflater.inflate(R.layout.similar_image_select, null)
                val dlgBuilder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(    // 확인 다이얼로그
                    contextdlg,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                //dlgBuilder.setCancelable(false)
                ImageLoder.execute(ImageLoad(similarImageSelectView.select_photo, thumbnailData.photo_id))

                dlgBuilder.setView(similarImageSelectView)
                dlgBuilder.setMessage("보존할 사진이 맞나요?")
                val dlgselect = dlgBuilder.create()
                dlgselect.show()
                similarImageSelectView.select_cancel.setOnClickListener{
                    dlgselect.cancel()
                }
                similarImageSelectView.select_ok.setOnClickListener{
                    savePhotoList.add(thumbnailData.photo_id)
                    dlgselect.cancel()
                    Toast.makeText(contextdlg, "입력 완료 되었습니다. \n저장을 누르시면 입력된 사진들만 저장됩니다.", Toast.LENGTH_SHORT).show()
                }

            }
        recyclerView.adapter = recyclerAdapter
        val lm = GridLayoutManager(context, 2)
        recyclerView.layoutManager = lm
    }

    private fun setPhotoSize(row : Int, padding : Int) {
        val display = activity!!.windowManager.defaultDisplay
        val deviceSize = Point()
        display.getSize(deviceSize)
        val width = deviceSize.x * 88/100
        val size = width!! / row - 2*padding
        recyclerAdapter.setPhotoSize(size, padding)
    }

}