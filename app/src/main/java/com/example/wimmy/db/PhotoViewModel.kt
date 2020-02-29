package com.example.wimmy.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import java.util.*

class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo : PhotoRepository = PhotoRepository(application)

    fun Insert(tag : TagData) {
       repo.insert(tag)
    }

    //기본 검색 썸네일
    fun getLocationDir() : LiveData<List<thumbnailData>> {
        return repo.getLocationDir()
    }
    fun getDateInfo(idList : List<Long>) : String? {
        return repo.getDateInfo(idList)
    }
    fun getTagDir() : List<thumbnailData> {
        return repo.getTagDir()
    }

    fun getLocationDir(loc : String) : LiveData<List<PhotoData>> {
        return repo.getLocationDir(loc)
    }
    fun getTagDirIdList(tag : String) : List<Long> {
        return repo.getTagDirIdList(tag)
    }

    fun getTag(id : Long) : List<String> {
        return repo.getTag(id)
    }
}