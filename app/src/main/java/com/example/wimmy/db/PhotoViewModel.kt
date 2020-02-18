package com.example.wimmy.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo : PhotoRepository = PhotoRepository(application)

    fun Insert(photo : PhotoData) {
       repo.insert(photo)
    }

    fun Insert(tag : TagData) {
       repo.insert(tag)
    }

    //기본 검색 썸네일
    fun getNameDir() : LiveData<List<thumbnailData>> {
        return repo.getNameDir()
    }
    fun getLocationDir() : LiveData<List<thumbnailData>> {
        return repo.getLocationDir()
    }
    fun getDateDir() : LiveData<List<thumbnailData>> {
        return repo.getDateDir()
    }
    fun getTagDir() : LiveData<List<thumbnailData>> {
        return repo.getTagDir()
    }

    //폴더 선택 시
    fun getNameDir(name : String) : LiveData<List<PhotoData>> {
        return repo.getNameDir(name)
    }
    fun getLocationDir(loc : String) : LiveData<List<PhotoData>> {
        return repo.getLocationDir(loc)
    }
    fun getDateDir(date : Int) : LiveData<List<PhotoData>> {
        return repo.getDateDir(date)
    }
    fun getTagDir(tag : String) : LiveData<List<PhotoData>> {
        return repo.getTagDir(tag)
    }
}