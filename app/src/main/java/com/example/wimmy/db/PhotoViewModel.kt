package com.example.wimmy.db

import android.app.Application
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import java.util.*

class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo : PhotoRepository = PhotoRepository(application)

    fun Insert(tag : TagData) {
       repo.insert(tag)
    }

    //기본 검색 썸네일

    fun setCalendarTag(textView: TextView, inputCalendar: Calendar) {
        repo.setCalendarTag(textView, inputCalendar)
    }
    fun getTagDir() : List<thumbnailData> {
        return repo.getTagDir()
    }

    fun getTagDirIdList(tag : String) : List<Long> {
        return repo.getTagDirIdList(tag)
    }

    fun getTag(id : Long) : List<String> {
        return repo.getTag(id)
    }
}