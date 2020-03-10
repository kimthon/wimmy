package com.example.wimmy.db

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.widget.TextView
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import java.util.*
import kotlin.collections.ArrayList

class PhotoRepository(application: Application) {
   private val photoDao : PhotoData_Dao

   companion object {
      private class insertTagAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<TagData, Void, Void>() {
         override fun doInBackground(vararg params: TagData?): Void? {
            asyncTask.insert(params[0]!!)
            return null
         }
      }

      private class insertExtraAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<ExtraPhotoData, Void, Void>() {
         override fun doInBackground(vararg params: ExtraPhotoData?): Void? {
            asyncTask.insert(params[0]!!)
            return null
         }
      }

      private class deleteTagByIdAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<Long, Void, Void>() {
         override fun doInBackground(vararg params: Long?): Void? {
            asyncTask.deleteTagById(params[0]!!)
            asyncTask.deleteExtraById(params[0]!!)
            return null
         }
      }

      // 폴더 생성
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

      private class setLocationDirAsyncTask(asyncTask: PhotoData_Dao, adapter : RecyclerAdapterForder) : AsyncTask<Void, Void, List<thumbnailData>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: Void?): List<thumbnailData>? {
            return asyncTask.getLocationDir()
         }

         override fun onPostExecute(result: List<thumbnailData>?) {
            adapter.setThumbnailList(result)
         }
      }

      private class setNameDirAsyncTask(asyncTask: MediaStore_Dao, adapter: RecyclerAdapterForder) : AsyncTask<Void, Void, List<thumbnailData>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: Void?): List<thumbnailData> {
            return asyncTask.getNameDir(adapter.context!!.applicationContext)
         }

         override fun onPostExecute(result: List<thumbnailData>?) {
            adapter.setThumbnailList(result)
         }
      }

      private class setTagDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterForder) : AsyncTask<Void, Void, List<thumbnailData>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter
         override fun doInBackground(vararg params: Void?): List<thumbnailData> {
            return asyncTask.getTagDir()
         }

         override fun onPostExecute(result: List<thumbnailData>) {
            adapter.setThumbnailList(result)
         }
      }

      // 폴더 내용 생성
      private class setOpenLocationDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<String, Void, List<Long>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: String?): List<Long>? {
            val idList = asyncTask.getLocationDir(params[0]!!)
            return idList
         }

         override fun onPostExecute(result: List<Long>?) {
            val list = MediaStore_Dao.getLocationDir(adapter, result)
            if(!result.isNullOrEmpty()) {
               for (id in list) {
                  setExtraData(asyncTask, adapter, id).execute()
               }
            }
         }
      }

      private class setOpenTagDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<String, Void, List<Long>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: String?): List<Long>? {
            var idList = asyncTask.getTagDir(params[0]!!)
            return idList
         }

         override fun onPostExecute(result: List<Long>?) {
            //adapter.setThumbnailList(result)
            val list =  MediaStore_Dao.getTagDir(adapter, result)
            if(!result.isNullOrEmpty()) {
               for (id in list) {
                  setExtraData(asyncTask, adapter, id).execute()
               }
            }
         }
      }

      private class setOpenFavoriteDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<Void, Void, List<Long>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: Void?): List<Long>? {
            val idList = asyncTask.getFavoriteDir()
            return idList
         }

         override fun onPostExecute(result: List<Long>?) {
            //임시
            val list = MediaStore_Dao.getLocationDir(adapter, result)
            if(!result.isNullOrEmpty()) {
               for (id in list) {
                  setExtraData(asyncTask, adapter, id).execute()
               }
            }
         }
      }

      private class setTagsAsyncTask(asyncTask: PhotoData_Dao, textView: TextView) : AsyncTask<Long, Void, List<String>>() {
         private val asyncTask  = asyncTask
         private val textView = textView

         override fun doInBackground(vararg params: Long?): List<String>? {
            return asyncTask.getTags(params[0]!!)
         }

         override fun onPostExecute(result: List<String>?) {
            if(result.isNullOrEmpty()) textView.text = ""
            else textView.text = result.joinToString ( ", " )
         }
      }

      private class setExtraData(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto, data: PhotoData) : AsyncTask<Void, Void, Void>() {
         private val asyncTask  = asyncTask
         private val adapter = adapter
         private val data = data

         override fun doInBackground(vararg params: Void?): Void? {
            var extra = asyncTask.getExtraPhotoData(data.photo_id)
            if(extra == null) {
               //인터넷 연결안될 시 패스
               if(!NetworkIsValid(adapter.context!!)) return null

               val loc = MediaStore_Dao.getLocation(adapter.context!!.applicationContext, data.photo_id)
               extra = ExtraPhotoData(data.photo_id, loc, false)
               asyncTask.insert(extra)
            }

            data.location_info = extra.location
            data.favorite  = extra.favorite

            return null
         }
      }

      private fun NetworkIsValid(context: Context) : Boolean {
         val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
         return cm.activeNetworkInfo != null
      }
   }

   init {
      val db = PhotoDB.getInstance(application)!!
      photoDao = db.PhotoData_Dao()
   }

   fun insert(tag : TagData) {
      insertTagAsyncTask(photoDao).execute(tag)
   }

   fun insert(extraPhotoData: ExtraPhotoData) {
      insertExtraAsyncTask(photoDao).execute(extraPhotoData)
   }

   fun deleteById(id: Long) {
      deleteTagByIdAsyncTask(photoDao).execute(id)
   }

   // 폴더 생성
   fun setLocationDir(adapter: RecyclerAdapterForder) {
      setLocationDirAsyncTask(photoDao, adapter).execute()
   }

   fun setNameDir(adapter: RecyclerAdapterForder) {
      setNameDirAsyncTask(MediaStore_Dao, adapter).execute()
   }

   fun setTagDir(adapter: RecyclerAdapterForder) {
      setTagDirAsyncTask(photoDao, adapter).execute()
   }

   fun setCalendarTag(textView: TextView, inputCalendar: Calendar) {
      setCalendarTagAsyncTask(photoDao, textView, inputCalendar).execute(textView.context)
   }

   //폴더 내용 생성
   fun setOpenDateDir(adapter: RecyclerAdapterPhoto, cal : Calendar) {
      val list = MediaStore_Dao.getDateDir(adapter, cal)
      if(!list.isNullOrEmpty()) {
         for (id in list) {
            setExtraData(photoDao, adapter, id).execute()
         }
      }
   }

   fun setOpenLocationDir(adapter: RecyclerAdapterPhoto, loc : String) {
       setOpenLocationDirAsyncTask(photoDao, adapter).execute(loc)
   }

   fun setOpenNameDir(adapter: RecyclerAdapterPhoto, path : String) {
      val list = MediaStore_Dao.getNameDir(adapter, path)
      if(!list.isNullOrEmpty()) {
         for (id in list) {
            setExtraData(photoDao, adapter, id).execute()
         }
      }
   }

   fun setOpenTagDir(adapter: RecyclerAdapterPhoto, tag : String) {
      setOpenTagDirAsyncTask(photoDao, adapter).execute(tag)
   }

   fun setOpenFavoriteDir(adapter: RecyclerAdapterPhoto) {
      setOpenFavoriteDirAsyncTask(photoDao, adapter).execute()
   }

   fun setTags(textView: TextView, id : Long) {
      setTagsAsyncTask(photoDao, textView).execute(id)
   }
}
