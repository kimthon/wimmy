package com.example.wimmy.db

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.PhotoViewPager
import com.example.wimmy.R
import java.util.*
import java.util.concurrent.*

class PhotoRepository(application: Application) {
   private val photoDao : PhotoData_Dao
   private val DBThread = ThreadPoolExecutor(0, Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS, SynchronousQueue<Runnable>())
   private val DirectoryThread = ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
   private val changeCheckThread = ThreadPoolExecutor(1, 2, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
   private val handler = Handler(Looper.getMainLooper())

   init {
      val db = PhotoDB.getInstance(application)!!
      photoDao = db.PhotoData_Dao()
   }

   fun insert(tag : TagData) {
       DBThread.execute {
          photoDao.insert(tag)
       }
   }

   fun insert(extraPhotoData: ExtraPhotoData) {
       DBThread.execute {
          photoDao.insert(extraPhotoData)
       }
   }

   fun deleteById(id: Long) {
      DBThread.execute {
         photoDao.deleteTagById(id)
         photoDao.deleteExtraById(id)
      }
   }

   // 폴더 생성
   fun setCalendarTag(textView: TextView, inputCalendar: Calendar) {
      if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
      DirectoryThread.execute {
         val list = MediaStore_Dao.getDateIdInfo(textView.context, inputCalendar)
         val text = photoDao.getDateInfo(list)
         handler.post { textView?.text = text }
      }
   }

   fun setLocationDir(adapter: RecyclerAdapterForder) {
      if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
      DirectoryThread.execute {
         val thumbnailList = photoDao.getLocationDir()
         handler.post { adapter?.setThumbnailList(thumbnailList) }
      }
   }

   fun setNameDir(adapter: RecyclerAdapterForder) {
      if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
       DirectoryThread.execute {
          val thumbnailList = MediaStore_Dao.getNameDir(adapter.context!!.baseContext)
          handler.post { adapter?.setThumbnailList(thumbnailList) }
       }
   }

   fun setTagDir(adapter: RecyclerAdapterForder) {
      if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
      DirectoryThread.execute {
         val thumbnailList = photoDao.getTagDir()
         handler.post { adapter?.setThumbnailList(thumbnailList) }
      }
   }

   //폴더 내용 생성
   fun setOpenDateDir(adapter: RecyclerAdapterPhoto, cal : Calendar) {
       DirectoryThread.execute {
          val cursor = MediaStore_Dao.getDateDir(adapter, cal)
          if(MediaStore_Dao.cursorIsValid(cursor)) {
             do {
                val photoData = MediaStore_Dao.getPhotoData(cursor!!)
                adapter.addThumbnailList(photoData)
                handler.post { adapter?.notifyItemInserted(adapter.getSize()) }
             } while (cursor!!.moveToNext())
             cursor.close()
          }
       }
   }

   fun setOpenLocationDir(adapter: RecyclerAdapterPhoto, loc : String) {
      DirectoryThread.execute {
            val idCursor = photoDao.getLocationDir(loc)
            if(MediaStore_Dao.cursorIsValid(idCursor)) {
               do {
                  val id = idCursor!!.getLong(idCursor.getColumnIndex("photo_id"))
                  val photoData = MediaStore_Dao.getDataById(adapter, id)
                  if(photoData != null) {
                     adapter.addThumbnailList(photoData)
                     handler.post { adapter?.notifyItemInserted(adapter.getSize()) }
                  }
                  else {
                     photoDao.deleteTagById(id)
                     photoDao.deleteExtraById(id)
                  }
               } while (idCursor!!.moveToNext())
               idCursor.close()
            }
      }
   }

   fun setOpenNameDir(adapter: RecyclerAdapterPhoto, path : String) {
      DirectoryThread.execute {
         val cursor = MediaStore_Dao.getNameDir(adapter, path)
         if (MediaStore_Dao.cursorIsValid(cursor)) {
            do {
               val photoData = MediaStore_Dao.getPhotoData(cursor!!)
               adapter.addThumbnailList(photoData)
               handler.post { adapter?.notifyItemInserted(adapter.getSize()) }
            } while (cursor!!.moveToNext())
            cursor.close()
         }
      }
   }

   fun setOpenTagDir(adapter: RecyclerAdapterPhoto, tag : String) {
      DirectoryThread.execute {
         val idCursor = photoDao.getTagDir(tag)
         if (MediaStore_Dao.cursorIsValid(idCursor)) {
            do {
               val id = idCursor!!.getLong(idCursor.getColumnIndex("photo_id"))
               val photoData = MediaStore_Dao.getDataById(adapter, id)
               if (photoData != null) {
                  adapter.addThumbnailList(photoData)
                  handler.post { adapter?.notifyItemInserted(adapter.getSize()) }
               } else {
                  photoDao.deleteTagById(id)
                  photoDao.deleteExtraById(id)
               }
            } while (idCursor!!.moveToNext())
            idCursor.close()
         }
      }
   }

   fun setOpenFavoriteDir(adapter: RecyclerAdapterPhoto) {
      DirectoryThread.execute {
         val idCursor = photoDao.getFavoriteDir()
         do {
            val id = idCursor!!.getLong(idCursor.getColumnIndex("photo_id"))
            val photoData = MediaStore_Dao.getDataById(adapter, id)
            if (photoData != null) {
               adapter.addThumbnailList(photoData)
               handler.post { adapter?.notifyItemInserted(adapter.getSize()) }
            } else {
               photoDao.deleteTagById(id)
               photoDao.deleteExtraById(id)
            }
         } while (idCursor!!.moveToNext())
         idCursor.close()
      }
   }

   // 기타 기능
   fun setLocation(textView: TextView, id : Long) {
      DBThread.execute {
         val text = photoDao.getLocation(id)
         handler.post {
            textView.text = text
         }
      }
   }

   fun setTags(textView: TextView, id : Long) {
      DBThread.execute {
         val tags = photoDao.getTags(id)
         handler.post {
            textView.text = tags.joinToString(", ")
         }
      }
   }

   fun checkFavorite(imageView: ImageView, id: Long, photoView: PhotoViewPager) {
      DBThread.execute {
         val favorite = photoDao.getFavorite(id)
         handler.post {
            if (favorite) imageView.setImageResource(R.drawable.ic_favorite_checked)
            else imageView.setImageResource(R.drawable.ic_favorite)
            photoView.setCheck(favorite)
         }
      }
   }

   fun checkChangedData(context: Context) {
      //이미 변환 감지 실행중이면 다시 실행
      if (changeCheckThread.isTerminating) changeCheckThread.shutdownNow()
      //추가 작업
      changeCheckThread.execute {
         var cursor = MediaStore_Dao.getNewlySortedCurosr(context)
         if (MediaStore_Dao.cursorIsValid(cursor)) {
            do {
               val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
               //이미 있는 것이나 인터넷이 끊길 시 패스
               if (photoDao.IsItInserted(id) != null || !NetworkIsValid(context)) continue
               val loc = MediaStore_Dao.getLocation(context.applicationContext, id)
               val extra = ExtraPhotoData(id, loc, false)
               photoDao.insert(extra)
            } while (cursor!!.moveToNext())
            cursor.close()
         }
         //삭제 작업
         changeCheckThread.execute {
            val cursor = photoDao.getIdCursor()
            if (MediaStore_Dao.cursorIsValid(cursor)) {
               do {
                  val id = cursor!!.getLong(cursor.getColumnIndex("photo_id"))
                  if (!MediaStore_Dao.IsItValidId(context, id)) {
                     photoDao.deleteExtraById(id)
                     photoDao.deleteTagById(id)
                  }
               } while (cursor!!.moveToNext())
            }
         }
      }
   }

   fun Drop() {
      DBThread.execute { photoDao.dropTable() }
   }
   private fun NetworkIsValid(context: Context) : Boolean {
      val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      return cm.activeNetworkInfo != null
   }
}
