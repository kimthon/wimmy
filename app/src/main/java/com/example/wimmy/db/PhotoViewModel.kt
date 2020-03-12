package com.example.wimmy.db

import android.app.Application
import android.content.Context
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import java.util.*

class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo : PhotoRepository = PhotoRepository(application)

    fun Insert(tag : TagData) {
        repo.insert(tag)
    }

    fun Insert(extraPhotoData: ExtraPhotoData) {
        repo.insert(extraPhotoData)
    }

    // 폴더 보기
    fun setCalendarTag(textView: TextView, inputCalendar: Calendar) {
        repo.setCalendarTag(textView, inputCalendar)
    }

    fun setLocationDir(adapter: RecyclerAdapterForder) {
        repo.setLocationDir(adapter)
    }

    fun setNameDir(adapter: RecyclerAdapterForder) {
        repo.setNameDir(adapter)
    }

    fun setTagDir(adapter: RecyclerAdapterForder) {
        repo.setTagDir(adapter)
    }

    // 폴더 내용 보기
    fun setOpenDateDir(adapter: RecyclerAdapterPhoto, cal : Calendar) {
        repo.setOpenDateDir(adapter, cal)
    }
    fun setOpenLocationDir(adapter: RecyclerAdapterPhoto, loc : String) {
        repo.setOpenLocationDir(adapter, loc)
    }

    fun setOpenNameDir(adapter: RecyclerAdapterPhoto, path : String) {
        repo.setOpenNameDir(adapter, path)
    }

    fun setOpenTagDir(adapter: RecyclerAdapterPhoto, tag : String) {
        repo.setOpenTagDir(adapter, tag)
    }

    fun setOpenFavoriteDir(adapter: RecyclerAdapterPhoto) {
        repo.setOpenFavoriteDir(adapter)
    }

    // 기타 기능
    fun setTags(textView: TextView, id : Long) {
        repo.setTags(textView, id)
    }

    fun CheckAddedData(context: Context) {
        repo.checkAddedData(context)
    }

    fun Drop() {
        repo.Drop()
    }
}