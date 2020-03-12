package com.example.wimmy.db

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.PhotoViewPager
import com.example.wimmy.R
import com.example.wimmy.db.MediaStore_Dao.noLocationData
import kotlinx.android.synthetic.main.photoview_frame.*
import java.io.File
import java.lang.Error
import java.util.*
import java.util.concurrent.*
import kotlin.collections.ArrayList

class PhotoRepository(application: Application) {
   private val photoDao : PhotoData_Dao

   companion object {
      private val handler = Handler(Looper.getMainLooper())

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

      private class deleteByIdAsyncTask constructor(private val asyncTask: PhotoData_Dao) : AsyncTask<Long, Void, Void>() {
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
      private class setOpenDateDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<Calendar, Void, Void>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter
         private var r : Runnable = Runnable{ adapter.notifyItemInserted(adapter.getSize()) }

         override fun doInBackground(vararg params: Calendar?): Void? {
            val cursor = MediaStore_Dao.getDateDir(adapter, params[0]!!)
            if(MediaStore_Dao.cursorIsValid(cursor)) {
               do {
                  val photoData = MediaStore_Dao.getPhotoData(cursor!!)
                  adapter.addThumbnailList(photoData)
                  handler.post(r)
               } while (cursor!!.moveToNext())
               cursor.close()
            }
            return null
         }
      }

      private class setOpenLocationDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<String, Void, Void>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter
         private var r : Runnable = Runnable{ adapter.notifyItemInserted(adapter.getSize()) }

         override fun doInBackground(vararg params: String?): Void? {
            val idCursor = asyncTask.getLocationDir(params[0]!!)

            if(MediaStore_Dao.cursorIsValid(idCursor)) {
               do {
                  val id = idCursor!!.getLong(idCursor.getColumnIndex("photo_id"))
                  val photoData = MediaStore_Dao.getDataById(adapter, id)
                  if(photoData != null) {
                     adapter.addThumbnailList(photoData)
                     handler.post(r)
                  }
                  else {
                     asyncTask.deleteTagById(id)
                     asyncTask.deleteExtraById(id)
                  }
               } while (idCursor!!.moveToNext())
               idCursor.close()
            }
            return null
         }
      }

      private class setOpenNameDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<String, Void, Void>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter
         private var r : Runnable = Runnable{ adapter.notifyItemInserted(adapter.getSize()) }

         override fun doInBackground(vararg params: String?): Void? {
            val cursor = MediaStore_Dao.getNameDir(adapter, params[0]!!)
            if(MediaStore_Dao.cursorIsValid(cursor)) {
               do {
                  val photoData = MediaStore_Dao.getPhotoData(cursor!!)
                  adapter.addThumbnailList(photoData)
                  handler.post(r)
               } while (cursor!!.moveToNext())
               cursor.close()
            }
            return null
         }
      }

      private class setOpenTagDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<String, Void, Void>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter
         private var r : Runnable = Runnable{ adapter.notifyItemInserted(adapter.getSize()) }

         override fun doInBackground(vararg params: String?): Void? {
            val idCursor = asyncTask.getTagDir(params[0]!!)
            if(MediaStore_Dao.cursorIsValid(idCursor)) {
               do {
                  val id = idCursor!!.getLong(idCursor.getColumnIndex("photo_id"))
                  val photoData = MediaStore_Dao.getDataById(adapter, id)
                  if (photoData != null) {
                     adapter.addThumbnailList(photoData)
                     handler.post(r)
                  } else {
                     asyncTask.deleteTagById(id)
                     asyncTask.deleteExtraById(id)
                  }
               } while (idCursor!!.moveToNext())
               idCursor.close()
            }
            return null
         }
      }

      private class setOpenFavoriteDirAsyncTask(asyncTask: PhotoData_Dao, adapter: RecyclerAdapterPhoto) : AsyncTask<Void, Void, Void>() {
         private val asyncTask = asyncTask
         private val  adapter = adapter
         private var r : Runnable = Runnable{ adapter.notifyItemInserted(adapter.getSize()) }

         override fun doInBackground(vararg params: Void?): Void? {
            val idCursor = asyncTask.getFavoriteDir()
            do {
               val id = idCursor!!.getLong(idCursor.getColumnIndex("photo_id"))
               val photoData = MediaStore_Dao.getDataById(adapter, id)
               if(photoData != null) {
                  adapter.addThumbnailList(photoData)
                  handler.post(r)
               }
               else {
                  asyncTask.deleteTagById(id)
                  asyncTask.deleteExtraById(id)
               }
            } while (idCursor!!.moveToNext())
            idCursor.close()
            return null
         }
      }

      // 기타 기능
      private class setLocationAsyncTask(asyncTask: PhotoData_Dao, textView: TextView) : AsyncTask<Long, Void, String>() {
         private val asyncTask  = asyncTask
         private val textView = textView

         override fun doInBackground(vararg params: Long?): String? {
            return asyncTask.getLocation(params[0]!!)
         }

         override fun onPostExecute(result: String?) {
            if(result.isNullOrEmpty()) textView.text = ""
            else textView.text = result
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

      private class checkFavoriteAsyncTask(asyncTask: PhotoData_Dao, imageView: ImageView, photoView: PhotoViewPager) : AsyncTask<Long, Void, Boolean>() {
         private val asyncTask  = asyncTask
         private val imageView = imageView
         private var photoView = photoView

         override fun doInBackground(vararg params: Long?): Boolean? {
            return asyncTask.getFavorite(params[0]!!)
         }

         override fun onPostExecute(result: Boolean) {
            if(result == true) imageView.setImageResource(R.drawable.ic_favorite_checked)
            else imageView.setImageResource(R.drawable.ic_favorite)
            photoView.setCheck(result)
         }
      }

      private class CheckAddedDataAsyncTask(asyncTask: PhotoData_Dao, context: Context) : AsyncTask<Void, Void, Void>() {
         private val asyncTask = asyncTask
         private val context = context

         override fun doInBackground(vararg params: Void): Void? {
            // 가장 최근 추가된 순서대로
            val cursor = MediaStore_Dao.getNewlySortedCurosr(context)
            if(MediaStore_Dao.cursorIsValid(cursor)) {
               do {
                  val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                  //이미 있는 것이나 인터넷이 끊길 시 패스
                  if(asyncTask.IsItInserted(id) != null || !NetworkIsValid(context)) continue
                  val loc = MediaStore_Dao.getLocation(context.applicationContext, id)
                  val extra = ExtraPhotoData(id, loc, false)
                  asyncTask.insert(extra)
               } while (cursor!!.moveToNext())
               cursor.close()
            }
            return null
         }
      }

      private class DropDataAsyncTask(asyncTask: PhotoData_Dao) : AsyncTask<Void, Void, Void>(){
         private val asyncTask = asyncTask
         override fun doInBackground(vararg params: Void?): Void? {
            asyncTask.dropTable()
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
      deleteByIdAsyncTask(photoDao).execute(id)
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
      setCalendarTagAsyncTask(photoDao, textView, inputCalendar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, textView.context)
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

   // 기타 기능
   fun setLocation(textView: TextView, id : Long) {
      setLocationAsyncTask(photoDao, textView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id)
   }

   fun setTags(textView: TextView, id : Long) {
      setTagsAsyncTask(photoDao, textView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id)
   }

   fun checkFavorite(imageView: ImageView, id: Long, photoView: PhotoViewPager) {
      checkFavoriteAsyncTask(photoDao, imageView, photoView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id)
   }

   fun checkAddedData(context: Context) {
       CheckAddedDataAsyncTask(photoDao, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
   }

   fun Drop() {
      DropDataAsyncTask(photoDao).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
   }
}
