package com.example.wimmy.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.example.wimmy.Activity.Main_PhotoView
import com.example.wimmy.DBThread
import com.example.wimmy.MainHandler
import com.example.wimmy.R
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.TagData
import kotlinx.android.synthetic.main.tag_diaglog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class tagInsertDialog(context: Context, v: View, vm: PhotoViewModel, index: Int, tag_name : AppCompatTextView): DialogFragment() {

    private val contextdlg = context
    private val v = v
    private val vm = vm
    private val index = index
    private val tag_name = tag_name
    @RequiresApi(Build.VERSION_CODES.N)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        DBThread.execute {
            val tags = vm.getTagList(Main_PhotoView.list[index].photo_id)
            MainHandler.post {
                tagsInit(v, tags)
            }
        }

        val dlgBuilder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(
            contextdlg,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        dlgBuilder.setTitle("태그 삽입")
        dlgBuilder.setMessage("삽입할 사진의 특징을 입력해주세요. \n태그를 수정하거나 삭제할 수도 있습니다.")
        dlgBuilder.setIcon(R.drawable.ic_tag)
        dlgBuilder.setCancelable(false)
        dlgBuilder.setView(v)
        val dlg = dlgBuilder.create()
        insert_tag_click(v, dlg)
        return dlg
    }


    private fun insert_tag_click(view: View, dlg: androidx.appcompat.app.AlertDialog) {
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

    private fun insert_saveCancel(view: View, dlg: androidx.appcompat.app.AlertDialog) {
        view.tag_save.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    vm.DeleteTag(Main_PhotoView.list[index].photo_id)
                }
                var inserted = false
                if (view.tag1_edit.text.toString().trim() != "") {
                    DBThread.execute {
                        vm.Insert( TagData( Main_PhotoView.list[index].photo_id, view.tag1_edit.text.toString() ) )
                    }
                    inserted = true
                }
                if (view.tag2_edit.text.toString().trim() != "") {
                    DBThread.execute {
                        vm.Insert( TagData( Main_PhotoView.list[index].photo_id, view.tag2_edit.text.toString() ) )
                    }
                    inserted = true
                }
                if (view.tag3_edit.text.toString().trim() != "") {
                    DBThread.execute {
                        vm.Insert( TagData( Main_PhotoView.list[index].photo_id, view.tag3_edit.text.toString() ) )
                    }
                    inserted = true
                }
                if (view.tag4_edit.text.toString().trim() != "") {
                    DBThread.execute {
                        vm.Insert( TagData( Main_PhotoView.list[index].photo_id, view.tag4_edit.text.toString() ) )
                    }
                    inserted = true
                }
                if (view.tag5_edit.text.toString().trim() != "") {
                    DBThread.execute {
                        vm.Insert( TagData( Main_PhotoView.list[index].photo_id, view.tag5_edit.text.toString() ) )
                    }
                    inserted = true
                }

                if(inserted) {
                    DBThread.execute {
                        val tags = vm.getTags(Main_PhotoView.list[index].photo_id)
                        MainHandler.post { tag_name.text = tags }
                    }
                }

                Toast.makeText(contextdlg, "입력 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                dlg.cancel()
            }
        }

        view.tag_cancel.setOnClickListener{ dlg.cancel() }
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
}