package com.example.wimmy.db

import android.app.Application
import android.content.Context
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.wimmy.DirectoryThread
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo : PhotoRepository = PhotoRepository(application)

    fun Insert(tag : TagData) {
        repo.insert(tag)
    }

    fun Delete(context: Context, id : Long) {
        repo.deleteById(context, id)
    }

    fun DeleteTag(id: Long) {
        repo.deleteTag(id)
    }
    // 폴더 보기
    fun getCalendarTags(context: Context, inputCalendar: Calendar) : List<String> {
        return repo.getCalendarTag(context, inputCalendar)
    }

    fun getLocationDir() : LiveData<List<thumbnailData>> {
        DirectoryThread.queue.clear()
        return repo.getLocationDir()
    }

    fun getNameDir(context: Context) : ArrayList<thumbnailData> {
        DirectoryThread.queue.clear()
        return repo.getNameDir(context)
    }

    fun getTagDir() : LiveData<List<thumbnailData>> {
        DirectoryThread.queue.clear()
        return repo.getTagDir()
    }

    // 검색
    fun getNameDirSearch(context: Context, name: String) : ArrayList<thumbnailData>{
        DirectoryThread.queue.clear()
        return repo.getNameDirSearch(context, name)
    }

    fun getLocationDirSearch(location: String) : ArrayList<thumbnailData>{
        DirectoryThread.queue.clear()
        return repo.getLocationDirSearch(location)
    }

    fun getDateDirSearch(context: Context, cal: Calendar) : ArrayList<thumbnailData>{
        DirectoryThread.queue.clear()
        return repo.getDateDirSearch(context, cal)
    }

    fun getTagDirSearch(tag: String) : ArrayList<thumbnailData>{
        DirectoryThread.queue.clear()
        return repo.getTagDirSearch(tag)
    }

    // 폴더 내용 보기
    fun getOpenDateDirCursor(context: Context, cal : Calendar) : Cursor? {
        return repo.getOpenDateDirCursor(context, cal)
    }

    fun getOpenLocationDirIdList(loc: String) : LiveData<List<Long>> {
        return repo.getOpenLocationDirIdList(loc)
    }

    fun getOpenNameDirCursor(context: Context, path : String) : Cursor? {
        return repo.getOpenNameDirCursor(context, path)
    }

    fun getOpenFileDirCursor(context: Context, name : String) : Cursor? {
        return repo.getOpenFileDirCursor(context , name)
    }

    fun getOpenTagDirIdList(tag : String) : LiveData<List<Long>> {
        return repo.getOpenTagDirIdList(tag)
    }

    fun getOpenFavoriteDirIdList() : LiveData<List<Long>> {
        return repo.getOpenFavoriteDirIdList()
    }

    // 기타 기능
    fun getFullName(context: Context, id: Long) : String {
        return repo.getName(context , id)
    }

    fun getName(context: Context, id: Long) : String {
        var name = repo.getName(context, id)
        if(name.length >= 40) {
            name = name.substring(0, 39)
            name += ".."
        }
        return name
    }

    fun getLongDate(context: Context, id: Long) : Long? {
        return repo.getDate(context, id)
    }

    fun getStringDate(context: Context, id: Long) : String {
        val date = repo.getDate(context, id)
        return if(date == null) { "날짜 정보 없음" }
        else {
            val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E) / HH:mm:ss", Locale.getDefault())
            (formatter).format(date)
        }
    }

    fun getFullLocation(context: Context, id : Long) : String{
        return repo.getLocation(context, id)
    }

    fun getLocation(context: Context, id : Long) : String{
        var loc =  repo.getLocation(context, id)
        if(loc.length >=30) {
            loc = loc.substring(0, 29)
            loc += ".."
        }
        return loc
    }

    fun getTagList(id : Long) : List<String> {
        return repo.getTagList(id)
    }

    fun getTags(id : Long) : String {
        val tagList = repo.getTagList(id)
        var tags: String
        if(tagList.isNotEmpty()) {
            tags = tagList.joinToString(", ")
            if (tags.length >= 30) {
                tags = tags.substring(0, 29)
                tags += ".."
            }
        } else {
            tags = "태그 정보 없음"
        }
        return tags
    }

    fun getFavorite(id : Long) : Boolean {
        return repo.getFavorite(id)
    }

    fun getThumbnailDataByCursor(cursor : Cursor) : thumbnailData {
        return MediaStore_Dao.getThumbnailDataByCursor(cursor)
    }

    fun getThumbnailListByIdList(context: Context, idList : List<Long>) : ArrayList<thumbnailData> {
        return MediaStore_Dao.getDirByIdList(context, idList)
    }

    fun getThumbnailListByIdList(context: Context, idList : List<Long>, cal: Calendar) : ArrayList<thumbnailData> {
        return MediaStore_Dao.getSimilarByIdList(context, idList, cal)
    }

    fun getLatLngById(context: Context, id: Long) : LatLng?{
        return MediaStore_Dao.getLatLngById(context, id)
    }

    fun CursorIsValid(cursor: Cursor?) : Boolean {
        return MediaStore_Dao.cursorIsValid(cursor)
    }

    fun CheckIdCursorValid(context: Context, cursor: Cursor) : Boolean {
        val id = cursor.getLong(cursor.getColumnIndex("photo_id"))
        if (!MediaStore_Dao.IsItValidId(context, id)) {
            repo.deleteById(context, id)
        }
        return MediaStore_Dao.IsItValidId(context, id)
    }

    fun changeFavorite(id: Long) : Boolean {
        return repo.changeFavorite(id)
    }

    fun getNewlySortedCursor(context: Context, lastAddedDate : Long) : Cursor? {
        return repo.getNewlySortedCursor(context, lastAddedDate)
    }

    fun getIdCursor() : Cursor? {
       return repo.getIdCursor()
    }

    fun Drop(context: Context) {
        repo.Drop(context)
    }
}