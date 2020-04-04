package com.example.wimmy.db

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import java.util.*
import kotlin.collections.ArrayList

class PhotoRepository(application: Application) {
   private val photoDao : PhotoData_Dao
   init {
      val db = PhotoDB.getInstance(application)!!
      photoDao = db.PhotoData_Dao()
   }

   fun insert(tag : TagData) {
      photoDao.insert(tag)
   }

   fun insert(extraPhotoData: ExtraPhotoData) {
      photoDao.insert(extraPhotoData)
   }

   fun deleteById(context: Context, id: Long) {
      photoDao.deleteTagById(id)
      photoDao.deleteExtraById(id)

      val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
      context.contentResolver.delete(uri, null, null)
      context.contentResolver.notifyChange( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null)
   }

   fun deleteTag(id: Long) {
         photoDao.deleteTagById(id)
   }

   // 폴더 생성
   //TODO idlist가 길어지면 too many SQL variables 에러 발생
   fun getCalendarTag(context: Context, inputCalendar: Calendar) : List<String> {
      val list = MediaStore_Dao.getDateIdInfo(context, inputCalendar)
      return photoDao.getDateInfo(list)
   }

   fun getLocationDir() : LiveData<List<thumbnailData>> {
      return photoDao.getLocationDir()
   }

   fun getNameDir(context: Context) : ArrayList<thumbnailData>{
      return MediaStore_Dao.getNameDir(context)
   }

    fun getTagDir(): LiveData<List<thumbnailData>> {
       return photoDao.getTagDir()
    }

   // 검색
   fun getNameDirSearch(context: Context, name: String) : ArrayList<thumbnailData>{
      return MediaStore_Dao.getNameDirSearch(context, name)
   }

   fun getDateDirSearch(context: Context, cal: Calendar) : ArrayList<thumbnailData>{
      return MediaStore_Dao.getDateDirSearch(context, cal)
   }

   fun getTagDirSearch(tag: String) : ArrayList<thumbnailData>{
      val list = photoDao.getTagDirSearch(tag)
      return ArrayList(list)
   }

   fun getLocationDirSearch(name: String) : ArrayList<thumbnailData>{
         val list = photoDao.getLocationDirSearch(name)
         return ArrayList(list)
   }

   //폴더 내용 생성
   fun getOpenDateDirCursor(context: Context, cal : Calendar) : Cursor? {
       return MediaStore_Dao.getDateDir(context, cal)
   }

   fun getOpenLocationDirIdList(loc: String) : LiveData<List<Long>> {
      return photoDao.getLocationDir(loc)
   }

   fun getOpenNameDirCursor(context: Context, path : String) : Cursor?{
      return MediaStore_Dao.getNameDir(context, path)
   }

   fun getOpenTagDirIdList(tag : String) : LiveData<List<Long>> {
      return photoDao.getTagDir(tag)
   }

   fun getOpenFavoriteDirIdList(): LiveData<List<Long>> {
      return photoDao.getFavoriteDir()
   }

   fun getOpenFileDirCursor(context: Context, name : String) : Cursor?{
      return MediaStore_Dao.getFileDir(context, name)
   }

   // 기타 기능
   fun getName(context: Context, id: Long) : String {
      return MediaStore_Dao.getNameById(context, id) ?: "정보 없음"
   }

   fun getDate(context: Context, id: Long) : Long? {
      return MediaStore_Dao.getDateById(context, id)
   }

   fun getLocation(context: Context, id : Long) : String {
      var loc = photoDao.getLocationById(id)
      if(loc == null) {
         loc = MediaStore_Dao.getLocation(context, id) ?: return MediaStore_Dao.noLocationData
         val favorite = photoDao.getFavoriteById(id) ?: false
         insert(ExtraPhotoData(id, loc, favorite))
      }

      return loc
   }

   fun getTagList(id : Long) : List<String> {
      return photoDao.getTagsById(id)
   }

   fun getFavorite(id: Long) : Boolean {
      val favorite = photoDao.getFavoriteById(id)
      return if(favorite == null) {
         insert(ExtraPhotoData(id, null, false))
         false
      } else favorite
   }

   fun changeFavorite(id : Long) : Boolean {
      val favorite = photoDao.getFavoriteById(id)!!
      photoDao.update(id, !favorite)
      return !favorite
   }

   fun getNewlySortedCursor(context: Context, lastAddedDate : Long) : Cursor? {
      return MediaStore_Dao.getNewlySortedCursor(context, lastAddedDate)
   }

   fun getIdCursor() : Cursor? {
      return photoDao.getIdCursor()
   }

   fun Drop(context: Context) {
      photoDao.dropExtraTable()
      photoDao.dropTagTable()

      val pref = context.getSharedPreferences("pref", MODE_PRIVATE)
      val editor = pref.edit()
      editor.clear()
      editor.apply()
   }
}
