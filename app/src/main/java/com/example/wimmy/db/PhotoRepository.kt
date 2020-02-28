package com.example.wimmy.db

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import java.util.*

class PhotoRepository(application: Application) {
   val photoDao : PhotoData_Dao

   companion object {
      private class insertPhotoAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<PhotoData, Void, Void>() {
         override fun doInBackground(vararg params: PhotoData?): Void? {
            asyncTask.insert(params[0]!!)
            return null
         }
      }

      private class insertTagAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<TagData, Void, Void>() {
         override fun doInBackground(vararg params: TagData?): Void? {
            asyncTask.insert(params[0]!!)
            return null
         }
      }

      private class getDateTagAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<Date, Void, String>() {
         override fun doInBackground(vararg params: Date?): String? {
            return asyncTask.getDateInfo(params[0]!!, params[1]!!)
         }
      }

      private class getSizeAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<Void, Void, Int>() {
         override fun doInBackground(vararg params: Void?): Int {
            return asyncTask.getSize()
         }
      }

      private class IsInsertedAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<Long, Void, Long>() {
         override fun doInBackground(vararg params: Long?): Long? {
            return asyncTask.IsInserted(params[0]!!)
         }
      }
   }

   init {
      val db = PhotoDB.getInstance(application)!!
      photoDao = db.PhotoData_Dao()
   }

   fun insert(photo : PhotoData) {
      insertPhotoAsyncTask(photoDao).execute(photo)
   }

   fun insert(tag : TagData) {
      insertTagAsyncTask(photoDao).execute(tag)
   }

   fun getNameDir() : LiveData<List<thumbnailData>> {
      return photoDao.getNameDir()
   }
   fun getLocationDir() : LiveData<List<thumbnailData>> {
      return photoDao.getLocationDir()
   }
   fun getDateInfo(from : Date, to : Date) : String? {
       return getDateTagAsyncTask(photoDao).execute(from, to).get()
   }
   fun getTagDir() : LiveData<List<thumbnailData>> {
      return photoDao.getTagDir()
   }

   fun getNameDir(name : String) : LiveData<List<PhotoData>> {
      return photoDao.getNameDir(name)
   }
   fun getLocationDir(loc : String) : LiveData<List<PhotoData>> {
      return photoDao.getLocationDir(loc)
   }
   fun getDateDir(date : Date) : LiveData<List<PhotoData>> {
      return photoDao.getDateDir(date)
   }
   fun getTagDir(tag : String) : LiveData<List<PhotoData>> {
      return photoDao.getTagDir(tag)
   }


   fun getNameTag(name : String) : LiveData<List<TagData>> {
      return photoDao.getNameTag(name)
   }
   fun getLocationTag(loc : String) : LiveData<List<TagData>> {
      return photoDao.getLocationTag(loc)
   }
   fun getDateTag(date : Int) : LiveData<List<TagData>> {
      return photoDao.getDateTag(date)
   }
   fun getTagTag(tag : String) : LiveData<List<TagData>> {
      return photoDao.getTagTag(tag)
   }

    fun IsInserted(id : Long) : Boolean {
        return (IsInsertedAsyncTask(photoDao).execute(id).get() != null)
    }
   fun getSize() : Int {
      return getSizeAsyncTask(photoDao).execute().get()
   }
}
