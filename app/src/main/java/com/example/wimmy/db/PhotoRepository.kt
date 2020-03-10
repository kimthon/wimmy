package com.example.wimmy.db

import android.app.Application
import android.content.Context
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
      private class setOpenDateDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<Calendar, Void, ArrayList<PhotoData>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: Calendar?): ArrayList<PhotoData>? {
            val list = MediaStore_Dao.getDateDir(adapter.context!!, params[0]!!)
            for(id in list) {
               setExtraData(asyncTask, adapter, id).execute()
            }

            return list
         }

         override fun onPostExecute(result: ArrayList<PhotoData>?) {
            adapter.setThumbnailList(result)
         }
      }

      private class setOpenLocationDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<String, Void, ArrayList<PhotoData>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: String?): ArrayList<PhotoData>? {
            val idList = asyncTask.getLocationDir(params[0]!!)
            val list = MediaStore_Dao.getLocationDir(adapter.context!!, idList)
            for(id in list) {
               setExtraData(asyncTask, adapter, id).execute()
            }

            return list
         }

         override fun onPostExecute(result: ArrayList<PhotoData>?) {
            adapter.setThumbnailList(result)
         }
      }

      private class setOpenNameDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<String, Void, ArrayList<PhotoData>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: String?): ArrayList<PhotoData>? {
            val list = MediaStore_Dao.getNameDir(adapter.context!!, params[0]!!)
            for(id in list) {
               setExtraData(asyncTask, adapter, id).execute()
            }

            return list
         }

         override fun onPostExecute(result: ArrayList<PhotoData>?) {
            adapter.setThumbnailList(result)
         }
      }

      private class setOpenTagDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<String, Void, ArrayList<PhotoData>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: String?): ArrayList<PhotoData>? {
            var idList = asyncTask.getTagDir(params[0]!!)
            val list =  MediaStore_Dao.getTagDir(adapter.context!!, idList)
            for(id in list) {
               setExtraData(asyncTask, adapter, id).execute()
            }

            return list
         }

         override fun onPostExecute(result: ArrayList<PhotoData>?) {
            adapter.setThumbnailList(result)
         }
      }

      private class setOpenFavoriteDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<Void, Void, ArrayList<PhotoData>>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter

         override fun doInBackground(vararg params: Void?): ArrayList<PhotoData>? {
            val idList = asyncTask.getFavoriteDir()
            val list =  MediaStore_Dao.getLocationDir(adapter.context!!, idList)
            for(id in list) {
               setExtraData(asyncTask, adapter, id).execute()
            }

            return list
         }

         override fun onPostExecute(result: ArrayList<PhotoData>?) {
            adapter.setThumbnailList(result)
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
               val loc = MediaStore_Dao.getLocation(adapter.context!!.applicationContext, data.photo_id)
               extra = ExtraPhotoData(data.photo_id, loc, false)
               asyncTask.insert(extra)
            }

            data.location_info = extra.location
            data.favorite  = extra.favorite

            return null
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
      setOpenDateDirAsyncTask(photoDao, adapter).execute(cal)
   }

   fun setOpenLocationDir(adapter: RecyclerAdapterPhoto, loc : String) {
       setOpenLocationDirAsyncTask(photoDao, adapter).execute(loc)
   }

   fun setOpenNameDir(adapter: RecyclerAdapterPhoto, path : String) {
       setOpenNameDirAsyncTask(photoDao, adapter).execute(path)
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
