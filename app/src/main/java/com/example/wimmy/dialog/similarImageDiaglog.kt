package com.example.wimmy.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wimmy.*
import com.example.wimmy.Activity.Main_Map.Companion.latLngList
import com.example.wimmy.Activity.Main_Map.Companion.removelist
import com.example.wimmy.Activity.Main_PhotoView.Companion.list
import com.example.wimmy.Adapter.RecyclerAdapterDialog
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData
import kotlinx.android.synthetic.main.similar_image_layout.view.*
import kotlinx.android.synthetic.main.similar_image_select.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class similarImageDialog(v: View, vm: PhotoViewModel, location: String, date: String): DialogFragment() {

    private lateinit var recyclerAdapter : RecyclerAdapterDialog
    private lateinit var recyclerView : RecyclerView
    private val v = v
    private val vm = vm
    private val location = location
    private val date = date
    private val calendar = Calendar.getInstance()
    private var checkboxSet: HashSet<Long> = hashSetOf()
    private var removenum: Int = 0

    @RequiresApi(Build.VERSION_CODES.N)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        stringToCalendar()
        recyclerView = v.findViewById<RecyclerView>(R.id.similar_RecycleView)
        setView(ArrayList())

        val liveData = vm.getOpenLocationDirIdList(location)
        liveData.observe(this, androidx.lifecycle.Observer { idList ->
            DBThread.execute {
                val list = vm.getThumbnailListByIdList(context!!, idList, calendar)
                checkboxSet = recyclerAdapter.setThumbnailList(list)
            }
        })

        val maindlgBuilder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(    // 메인 다이얼로그
            context!!, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        maindlgBuilder.setView(v)

        val dlg = maindlgBuilder.create()
        saveSimilarPhoto(dlg)
        return dlg
    }

    private fun stringToCalendar() {
        val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E) / HH:mm:ss", Locale.getDefault())
        val tempDate = formatter.parse(date)
        calendar.setTime(tempDate)
    }


    private fun saveSimilarPhoto(dlg: androidx.appcompat.app.AlertDialog) {
        v.similar_cancel.setOnClickListener {
            dlg.cancel()
        }
        v.similar_ok.setOnClickListener {
            if(checkboxSet.size == 0) {
                Toast.makeText(context!!, "체크된 사진이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val warningBuilder: androidx.appcompat.app.AlertDialog.Builder =
                    androidx.appcompat.app.AlertDialog.Builder(
                        context!!,    // 경고 다이얼로그
                        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                    )
                warningBuilder.setTitle("알림") //제목
                warningBuilder.setMessage("체크된 사진들을 삭제합니다.\n정말 삭제하시겠습니까?\n\n (체크된 사진: ${checkboxSet.size} 개)") // 메시지
                warningBuilder.setCancelable(false)
                warningBuilder.setPositiveButton(
                    "확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        for (ckbox in checkboxSet) {
                            DBThread.execute { vm.Delete(context!!, ckbox) }
                            val index = list.indexOfFirst { it.photo_id == ckbox }
                            if (index >= 0) {
                                if(latLngList.isNotEmpty() && latLngList[index].id == list[index].photo_id) {
                                    removelist.add(latLngList[index])
                                    latLngList.removeAt(index)
                                }
                                list.removeAt(index)
                            }
                            removenum++
                        }
                        dialog.cancel()
                        dlg.cancel()
                        val pager: Activity = context!! as Activity        // 액티비티 종료

                        pager.finish()
                        Toast.makeText(
                            context!!,
                            "${removenum} 개의 사진이 삭제 완료 되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                warningBuilder.setNegativeButton(
                    "취소",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                    })
                val dlgWarning = warningBuilder.create()
                dlgWarning.show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(2, 2)
    }

    private fun setView(list: ArrayList<thumbnailData>) {
        recyclerAdapter =
            RecyclerAdapterDialog(activity, list) { thumbnailData ->
                val similarImageSelectView: View = layoutInflater.inflate(R.layout.similar_image_select, null)
                ImageLoder.execute(ImageLoad(context!!, similarImageSelectView.select_photo, thumbnailData.photo_id, 1))
                val dlgBuilder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(    // 확인 다이얼로그
                    context!!,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)

                dlgBuilder.setView(similarImageSelectView)
                val dlgselect = dlgBuilder.create()

                dlgselect.show()
                similarImageSelectView.select_cancel.setOnClickListener{
                    dlgselect.cancel()
                }

            }
        recyclerView.adapter = recyclerAdapter
        val lm = GridLayoutManager(context, 2)
        recyclerView.layoutManager = lm
    }

    private fun setPhotoSize(row : Int, padding : Int) {
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener( object : ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                val width = recyclerView.width
                val size = width / row - 2 * padding
                recyclerAdapter.setPhotoSize(size, padding)
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

}