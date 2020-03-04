package com.example.wimmy.db

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import android.widget.TextView
import java.util.*

class PhotoRepository(application: Application) {
   val photoDao : PhotoData_Dao

   companion object {
      private class insertTagAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<TagData, Void, Void>() {
         override fun doInBackground(vararg params: TagData?): Void? {
            asyncTask.insert(params[0]!!)
            return null
         }
      }

      private class setCalendarTagAsyncTask(asyncTask: PhotoData_Dao, textView: TextView, inputCalendar: Calendar) : AsyncTask<Context, Void, String>() {
         private val asyncTask = asyncTask
         private val textView = textView
         private val inputCalendar = inputCalendar

         override fun doInBackground(vararg params: Context?): String? {
            val list = MediaStore_Dao.getDateIdInfo(params[0]!!, inputCalendar)
            return asyncTask.getDateInfo(list)
         }

         override fun onPostExecute(result: String?) {
            textView.text = result
         }
      }

      private class getTagDirAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<Void, Void, List<thumbnailData>>() {
         override fun doInBackground(vararg params: Void?): List<thumbnailData>? {
            return asyncTask.getTagDir()
         }
      }

      private class getTagDirIdListAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<String, Void, List<Long>>() {
         override fun doInBackground(vararg params: String?): List<Long>? {
            return asyncTask.getTagDir(params[0]!!)
         }
      }

      private class getTagByIdAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<Long, Void, List<String>>() {
         override fun doInBackground(vararg params: Long?): List<String>? {
            return asyncTask.getTag(params[0]!!)
         }
      }
   }

   init {
      val db = PhotoDB.getInstance(application)!!
      photoDao = db.PhotoData_Dao()
   }
   fun insert(tag : TagData) {
      insertTagAsyncTask(photoDao).execute(tag)
   }

   fun setCalendarTag(textView: TextView, inputCalendar: Calendar) {
       setCalendarTagAsyncTask(photoDao, textView, inputCalendar).execute(textView.context)
   }
   fun getTagDir() : List<thumbnailData> {
      return getTagDirAsyncTask(photoDao).execute().get()
   }

   fun getTagDirIdList(tag : String) : List<Long> {
      return getTagDirIdListAsyncTask(photoDao).execute(tag).get()
   }

   fun getTag(id : Long) : List<String> {
      return getTagByIdAsyncTask(photoDao).execute(id).get()
   }
}
