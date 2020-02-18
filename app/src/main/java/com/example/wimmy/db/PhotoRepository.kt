package com.example.wimmy.db

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

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
   }

   init {
      var db = PhotoDB.getInstance(application)!!
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
   fun getDateDir() : LiveData<List<thumbnailData>> {
      return photoDao.getDateDir()
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
   fun getDateDir(date : Int) : LiveData<List<PhotoData>> {
      return photoDao.getDateDir(date)
   }
   fun getTagDir(tag : String) : LiveData<List<PhotoData>> {
      return photoDao.getTagDir(tag)
   }
}
