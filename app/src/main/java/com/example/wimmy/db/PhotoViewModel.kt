package com.example.wimmy.db

import android.app.Application
import android.content.Context
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import com.example.wimmy.DBThread
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

    fun Insert(extraPhotoData: ExtraPhotoData) {
        repo.insert(extraPhotoData)
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

    fun getLocationDir() : ArrayList<thumbnailData>{
        if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
        return repo.getLocationDir()
    }

    fun getNameDir(context: Context) : ArrayList<thumbnailData>{
        if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
        return repo.getNameDir(context)
    }

    fun getTagDir() : ArrayList<thumbnailData>{
        if(DBThread.isTerminating) DBThread.shutdownNow()
        return repo.getTagDir()
    }

    // 검색
    fun getNameDirSearch(context: Context, name: String) : ArrayList<thumbnailData>{
        if(DBThread.isTerminating) DBThread.shutdownNow()
        return repo.getNameDirSearch(context, name)
    }

    fun getLocationDirSearch(location: String) : ArrayList<thumbnailData>{
        if(DBThread.isTerminating) DBThread.shutdownNow()
        return repo.getLocationDirSearch(location)
    }

    fun getDateDirSearch(context: Context, cal: Calendar) : ArrayList<thumbnailData>{
        if(DBThread.isTerminating) DBThread.shutdownNow()
        return repo.getDateDirSearch(context, cal)
    }

    fun getTagDirSearch(tag: String) : ArrayList<thumbnailData>{
        if(DBThread.isTerminating) DBThread.shutdownNow()
        return repo.getTagDirSearch(tag)
    }

    // 폴더 내용 보기
    fun getOpenDateDirCursor(context: Context, cal : Calendar) : Cursor? {
        return repo.getOpenDateDirCursor(context, cal)
    }

    fun getOpenLocationDirIdCursor(loc: String) : Cursor? {
        return repo.getOpenLocationDirIdCursor(loc)
    }

    fun getOpenNameDirCursor(context: Context, path : String) : Cursor? {
        return repo.getOpenNameDirCursor(context, path)
    }

    fun getOpenFileDirCursor(context: Context, name : String) : Cursor? {
        return repo.getOpenFileDirCursor(context , name)
    }

    fun getOpenTagDirIdCursor(tag : String) : Cursor? {
        return repo.getOpenTagDirIdCursor(tag)
    }

    fun getOpenFavoriteDirIdCursor() : Cursor? {
        return repo.getOpenFavoriteDirIdCursor()
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
        var tags: String = ""
        if(tagList.size != 0) {
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

    fun getThumbnailDataByIdCursor(context: Context, idCursor : Cursor) : thumbnailData? {
        val id = idCursor.getLong(idCursor.getColumnIndex("photo_id"))
        val name = MediaStore_Dao.getNameById(context, id)
        if(name == null) {
            repo.deleteById(context, id)
            return null
        }
        return thumbnailData(id, name)
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