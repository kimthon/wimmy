package com.example.wimmy.db

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.Activity.Main_Map
import com.example.wimmy.dialog.tagInsertDialog
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.util.*

class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo : PhotoRepository = PhotoRepository(application)

    fun Insert(tag : TagData) {
        repo.insert(tag)
    }

    fun Insert(extraPhotoData: ExtraPhotoData) {
        repo.insert(extraPhotoData)
    }

    fun Delete(id : Long) {
        repo.deleteById(id)
    }

    fun DeleteTag(id : Long) {
        repo.deleteTag(id)
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

    // 검색
    fun setNameDirSearch(adapter: RecyclerAdapterForder, name: String) {
        repo.setNameDirSearch(adapter, name)
    }

    fun setLocationDirSearch(adapter: RecyclerAdapterForder, location: String) {
        repo.setLocationDirSearch(adapter, location)
    }

    fun setDateDirSearch(adapter: RecyclerAdapterForder, cal: Calendar) {
        repo.setDateDirSearch(adapter, cal)
    }

    fun setTagDirSearch(adapter: RecyclerAdapterForder, tag: String) {
        repo.setTagDirSearch(adapter, tag)
    }

    // 폴더 내용 보기
    fun setOpenDateDir(adapter: RecyclerAdapterPhoto, cal : Calendar) {
        repo.setOpenDateDir(adapter, cal)
    }
    fun setOpenLocationDir(adapter: RecyclerAdapterPhoto, loc : String) {
        repo.setOpenLocationDir(adapter, loc)
    }

    // test
    fun setOpenLocationDir(context: Context, loc : String, map: Main_Map) {
        repo.setOpenLocationDir(context, loc, map)
    }


    fun setOpenNameDir(adapter: RecyclerAdapterPhoto, path : String) {
        repo.setOpenNameDir(adapter, path)
    }

    fun setOpenFileDir(adapter: RecyclerAdapterPhoto, name : String) {
        repo.setOpenFileDir(adapter, name)
    }
    fun setOpenTagDir(adapter: RecyclerAdapterPhoto, tag : String) {
        repo.setOpenTagDir(adapter, tag)
    }

    fun setOpenFavoriteDir(adapter: RecyclerAdapterPhoto) {
        repo.setOpenFavoriteDir(adapter)
    }

    // 기타 기능
    fun setName(textView: TextView, id: Long) {
        repo.setName(textView, id)
    }

    fun setDate(textView: TextView, id: Long) {
        repo.setDate(textView, id)
    }

    fun setLocation(textView: TextView, id : Long) {
        repo.setLocation(textView, id)
    }

    fun setTags(textView: TextView, id : Long) {
        repo.setTags(textView, id)
    }

    fun setTags(marker: Marker, id : Long) {
        repo.setTags(marker, id)
    }

    fun getTags(tagInsertDialog: tagInsertDialog, view: View, id : Long) {
        repo.getTags(tagInsertDialog, view, id)
    }

    fun checkFavorite(imageView: ImageView, id: Long) {
        repo.checkFavorite(imageView, id)
    }

    fun changeFavorite(imageView: ImageView, id: Long){
        repo.changeFavorite(imageView, id)
    }

    fun checkChangedData(context: Context) {
        repo.checkChangedData(context)
    }

    fun Drop() {
        repo.Drop()
    }
}