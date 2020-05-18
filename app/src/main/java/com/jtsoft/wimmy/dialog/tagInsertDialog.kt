package com.jtsoft.wimmy.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.jtsoft.wimmy.Activity.Main_PhotoView
import com.jtsoft.wimmy.DBThread
import com.jtsoft.wimmy.MainHandler
import com.jtsoft.wimmy.R
import com.jtsoft.wimmy.db.PhotoViewModel
import com.jtsoft.wimmy.db.TagData
import kotlinx.android.synthetic.main.tag_diaglog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class tagInsertDialog(v: View, vm: PhotoViewModel, index: Int, tag_name : AppCompatTextView): DialogFragment() {

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
            context!!)
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
        val editlist = arrayListOf<TextView>(view.tag1_edit, view.tag2_edit, view.tag3_edit, view.tag4_edit, view.tag5_edit)
        tag_addRemove(view, editlist)
        insert_saveCancel(view, dlg, editlist)
    }

    private fun tag_addRemove(view: View, editlist: ArrayList<TextView>) {

        val addlist = arrayListOf<ImageView>(view.tag1_add, view.tag2_add, view.tag3_add, view.tag4_add)
        val taglist = arrayListOf<TableRow>(view.tag2, view.tag3, view.tag4, view.tag5)
        val removelist = arrayListOf<ImageView>(view.tag2_remove, view.tag3_remove, view.tag4_remove, view.tag5_remove)
        for(i in 0..3) {
            addlist[i].setOnClickListener {
                addlist[i].visibility = View.INVISIBLE
                taglist[i].visibility = View.VISIBLE
                if(i != 0)
                    removelist[i-1].visibility = View.INVISIBLE
            }
            removelist[i].setOnClickListener {
                addlist[i].visibility = View.VISIBLE
                taglist[i].visibility = View.GONE
                editlist[i+1].setText("")
                if(i != 0)
                    removelist[i-1].visibility = View.VISIBLE
            }
        }

    }

    private fun insert_saveCancel(view: View, dlg: androidx.appcompat.app.AlertDialog, editlist: ArrayList<TextView>) {
        view.tag_save.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    vm.DeleteTag(Main_PhotoView.list[index].photo_id)
                }
                var tags: String = ""
                for(i in 0..4) {
                    if (editlist[i].text.toString().trim() != "") {
                        DBThread.execute {
                            vm.Insert( TagData( Main_PhotoView.list[index].photo_id, editlist[i].text.toString() ) )
                        }
                        tags += editlist[i].text.toString() + ", "
                    }
                }
                if(tags.length == 0)
                    MainHandler.post { tag_name.text = "태그 정보 없음" }
                else {
                    tags = tags.substring(0, tags.length -2)
                    MainHandler.post { tag_name.text = tags }
                }

                Toast.makeText(context!!, "입력 완료 되었습니다.", Toast.LENGTH_SHORT).show()
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