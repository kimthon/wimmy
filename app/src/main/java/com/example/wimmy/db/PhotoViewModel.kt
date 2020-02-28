package com.example.wimmy.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import java.util.*

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
    fun getDateInfo(cal : Calendar) : String? {
        return repo.getDateInfo(getDateStart(cal), getDateEnd(cal))
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
    fun getDateDir(date : Date) : LiveData<List<PhotoData>> {
        return repo.getDateDir(date)
    }
    fun getTagDir(tag : String) : LiveData<List<PhotoData>> {
        return repo.getTagDir(tag)
    }

    fun getNameTag(name : String) : LiveData<List<TagData>> {
        return repo.getNameTag(name)
    }
    fun getLocationTag(loc : String) : LiveData<List<TagData>> {
        return repo.getLocationTag(loc)
    }
    fun getDateTag(date : Int) : LiveData<List<TagData>> {
        return repo.getDateTag(date)
    }
    fun getTagTag(tag : String) : LiveData<List<TagData>> {
       return repo.getTagTag(tag)
    }

    fun IsInserted(id : Long) : Boolean {
        return repo.IsInserted(id)
    }
    fun getSize() : Int {
        return repo.getSize()
    }

    private fun getDateStart(cal : Calendar) : Date{
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    private fun getDateEnd(cal : Calendar) : Date{
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.time
    }
}