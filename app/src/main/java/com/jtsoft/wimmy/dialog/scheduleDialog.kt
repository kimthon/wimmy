package com.jtsoft.wimmy.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Delete
import com.jtsoft.wimmy.*
import com.jtsoft.wimmy.Activity.Main_Map.Companion.latLngList
import com.jtsoft.wimmy.Activity.Main_Map.Companion.removelist
import com.jtsoft.wimmy.Activity.Main_PhotoView.Companion.list
import com.jtsoft.wimmy.Adapter.RecyclerAdapterDialog
import com.jtsoft.wimmy.Adapter.RecyclerAdapterPhoto
import com.jtsoft.wimmy.db.CalendarData
import com.jtsoft.wimmy.db.PhotoViewModel
import com.jtsoft.wimmy.db.thumbnailData
import kotlinx.android.synthetic.main.schedule_insert.view.*
import kotlinx.android.synthetic.main.similar_image_layout.view.*
import kotlinx.android.synthetic.main.similar_image_select.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class scheduleDialog(v: View, vm: PhotoViewModel, cal: Calendar): DialogFragment() {

    private lateinit var recyclerAdapter : RecyclerAdapterPhoto
    private lateinit var recyclerView : RecyclerView
    private val v = v
    private val vm = vm
    private var imgList = arrayListOf<thumbnailData>()
    private val calendar = cal
    private var interfaceDlg: dialogListener? = null
    private var row: Int = 3

    @RequiresApi(Build.VERSION_CODES.N)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //stringToCalendar()
        recyclerView = v.findViewById<RecyclerView>(R.id.schedule_RecycleView)
        setView(ArrayList())
        val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val getdate = formatter.format(calendar.time) + " 일정"
        var calData: CalendarData? = null


        v.schedule_title.text = getdate


        val maindlgBuilder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(    // 메인 다이얼로그
            context!!)
        maindlgBuilder.setView(v)
        val dlg = maindlgBuilder.create()

        DBThread.execute {
            getOpenDirByCursor(vm, vm.getOpenDateDirCursor(context!!, calendar))
            calData = vm.getCalendarData(calendar)
            if (calData?.title != null) v.scheduleTitle_text.setText(calData?.title)
            if (calData?.memo != null) v.scheduleMemo_text.setText(calData?.memo)
            MainHandler.post { delete_schedule(dlg, calData) }
        }

        v.schedule_ok.setOnClickListener {
            val title = v.scheduleTitle_text.text.toString()
            val memo = v.scheduleMemo_text.text.toString()
            if(title.isEmpty())
                Toast.makeText(context!!, "주제를 입력해주세요.", Toast.LENGTH_SHORT).show()
            else {
                DBThread.execute{vm.Insert(Date(calendar.time.time), title, memo) }
                Toast.makeText(context!!, "일정이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                dlg.cancel()
                interfaceDlg!!.refresh()
            }
        }
        v.schedule_cancel.setOnClickListener {
            dlg.cancel()
        }

        return dlg
    }

    fun delete_schedule(maindlg: AlertDialog, data: CalendarData?) {
        v.schedule_delete.setOnClickListener {
            if(data?.title == null) {
                Toast.makeText(context!!, "삭제할 일정이 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                val dlgbuilder: AlertDialog.Builder = AlertDialog.Builder(context!!)
                dlgbuilder.setTitle("알림")
                dlgbuilder.setMessage("일정을 정말 삭제하시겠습니까? ")
                dlgbuilder.setCancelable(false)
                dlgbuilder.setIcon(R.drawable.ic_schedule_delete)
                dlgbuilder.setPositiveButton("확인") { _, _ ->
                    DeleteThread.execute {
                        vm.Delete(calendar)
                    }
                    Toast.makeText(context!!, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    maindlg.cancel()
                    interfaceDlg!!.refresh()
                }
                dlgbuilder.setNegativeButton("취소") { _, _ -> }
                dlgbuilder.create().show()
            }
        }
    }
    private fun getOpenDirByCursor(vm : PhotoViewModel, cursor : Cursor?) {
        if (vm.CursorIsValid(cursor)) {
            do {
                val data = vm.getThumbnailDataByCursor(cursor!!)
                recyclerAdapter.addThumbnailList(data)
                imgList.add(data)
            } while (cursor!!.moveToNext())
            cursor.close()
            MainHandler.post {
                if(imgList.size <= 2) row = 1
                else if(imgList.size <= 8) row = 2
                else if(imgList.size <= 20) row = 3
                else if(imgList.size <= 30) row = 4
                else row = 5
                setView(imgList)
                setPhotoSize(row, 2)
            }
        }
        MainHandler.post {
            if (imgList.size == 0) v.schedule_RecycleView.layoutParams.height = 0
        }
    }


    override fun onResume() {
        super.onResume()
        setPhotoSize(row, 2)
    }

    private fun setView(list: ArrayList<thumbnailData>) {
        recyclerAdapter =
            RecyclerAdapterPhoto(activity, list) { thumbnailData, num ->
                val ImageSelectView: View = layoutInflater.inflate(R.layout.similar_image_select, null)
                ImageLoder.execute(ImageLoad(context!!, ImageSelectView.select_photo, thumbnailData.photo_id, 0))
                val dlgBuilder: AlertDialog.Builder = AlertDialog.Builder(    // 확인 다이얼로그
                    context!!)

                dlgBuilder.setView(ImageSelectView)
                val dlgselect = dlgBuilder.create()

                dlgselect.show()
                ImageSelectView.select_cancel.setOnClickListener{
                    dlgselect.cancel()
                }

            }
        recyclerView.adapter = recyclerAdapter
        val lm = GridLayoutManager(context, row)
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
    public fun setDialogListener(listener: dialogListener) {
        this.interfaceDlg = listener
    }

    interface dialogListener {
        fun refresh()
    }
}