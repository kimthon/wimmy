package com.example.wimmy.db

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.Adapter.RecyclerAdapterPhoto
import com.example.wimmy.Main_Map
import com.example.wimmy.Main_PhotoView.Companion.list
import com.example.wimmy.R
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.*

class PhotoRepository(application: Application) {
   private val photoDao : PhotoData_Dao
   private val DBThread = ThreadPoolExecutor(0, Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS, SynchronousQueue<Runnable>())
   private val DirectoryThread = ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
   private val changeCheckThread = ThreadPoolExecutor(1, 3, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
   private val handler = Handler(Looper.getMainLooper())
   private var lastAddedDate : Long = 0

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

   fun deleteById(context: Context, id: Long) {
      DBThread.execute {
         photoDao.deleteTagById(id)
         photoDao.deleteExtraById(id)

         val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
         context.contentResolver.delete(uri, null, null)
      }
   }

   // 폴더 생성
   fun setCalendarTag(textView: TextView, inputCalendar: Calendar) {
      if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
      DirectoryThread.execute {
         val list = MediaStore_Dao.getDateIdInfo(textView.context, inputCalendar)
         val textList = photoDao.getDateInfo(list)
         if(!textList.isNullOrEmpty()) {
            handler.post {
               var text = ""
               for (i in textList) {
                  text += i + '\n'
               }
               textView.text = text
            }
         }
      }
   }

   fun setLocationDir(adapter: RecyclerAdapterForder) {
      if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
      DirectoryThread.execute {
         val thumbnailList = photoDao.getLocationDir()
         handler.post { adapter.setThumbnailList(thumbnailList) }
      }
   }

   fun setNameDir(adapter: RecyclerAdapterForder) {
      if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
       DirectoryThread.execute {
          val thumbnailList = MediaStore_Dao.getNameDir(adapter.context!!.baseContext)
          handler.post { adapter.setThumbnailList(thumbnailList) }
       }
   }

   fun setTagDir(adapter: RecyclerAdapterForder) {
      if(DirectoryThread.isTerminating) DirectoryThread.shutdownNow()
      DirectoryThread.execute {
         val thumbnailList = photoDao.getTagDir()
         handler.post { adapter.setThumbnailList(thumbnailList) }
      }
   }

   //폴더 내용 생성
   fun setOpenDateDir(adapter: RecyclerAdapterPhoto, cal : Calendar) {
      DirectoryThread.execute {
         val cursor = MediaStore_Dao.getDateDir(adapter, cal)
         if(MediaStore_Dao.cursorIsValid(cursor)) {
            do {
               val data = MediaStore_Dao.getData(cursor!!)
               adapter.addThumbnailList(data)
               handler.post { adapter.notifyItemInserted(adapter.getSize()) }
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
               val id = idCursor.getLong(idCursor.getColumnIndex("photo_id"))
               val photoData = MediaStore_Dao.getDataById(adapter, id)
               if(photoData != null) {
                  adapter.addThumbnailList(photoData)
                  handler.post { adapter.notifyItemInserted(adapter.getSize()) }
               }
               else {
                  photoDao.deleteTagById(id)
                  photoDao.deleteExtraById(id)
               }
            } while (idCursor.moveToNext())
            idCursor.close()
         }
      }
   }

   fun setOpenLocationDir(context: Context, loc : String, map: Main_Map) {
      DirectoryThread.execute {
         val idCursor = photoDao.getLocationDir(loc)
         if(MediaStore_Dao.cursorIsValid(idCursor)) {
            list.clear()
            do {
               val id = idCursor.getLong(idCursor.getColumnIndex("photo_id"))
               list.add(thumbnailData(id, loc))
               MediaStore_Dao.setLatLngDataById(context, id, map)
            } while (idCursor.moveToNext())
            Handler(Looper.getMainLooper()).post {map.cameraInit()}
            idCursor.close()
         }
      }
   }

   fun setOpenNameDir(adapter: RecyclerAdapterPhoto, path : String) {
      DirectoryThread.execute {
         val cursor = MediaStore_Dao.getNameDir(adapter, path)
         if (MediaStore_Dao.cursorIsValid(cursor)) {
            do {
               val data = MediaStore_Dao.getData(cursor!!)
               adapter.addThumbnailList(data)
               handler.post { adapter.notifyItemInserted(adapter.getSize()) }
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
               val id = idCursor.getLong(idCursor.getColumnIndex("photo_id"))
               val photoData = MediaStore_Dao.getDataById(adapter, id)
               if (photoData != null) {
                  adapter.addThumbnailList(photoData)
                  handler.post { adapter.notifyItemInserted(adapter.getSize()) }
               } else {
                  photoDao.deleteTagById(id)
                  photoDao.deleteExtraById(id)
               }
            } while (idCursor.moveToNext())
            idCursor.close()
         }
      }
   }

   fun setOpenFavoriteDir(adapter: RecyclerAdapterPhoto) {
      DirectoryThread.execute {
         val idCursor = photoDao.getFavoriteDir()
         do {
            val id = idCursor.getLong(idCursor.getColumnIndex("photo_id"))
            val photoData = MediaStore_Dao.getDataById(adapter, id)
            if (photoData != null) {
               adapter.addThumbnailList(photoData)
               handler.post { adapter.notifyItemInserted(adapter.getSize()) }
            } else {
               photoDao.deleteTagById(id)
               photoDao.deleteExtraById(id)
            }
         } while (idCursor.moveToNext())
         idCursor.close()
      }
   }

   // 기타 기능
   fun setName(textView: TextView, id: Long) {
      DBThread.execute {
         val text = MediaStore_Dao.getNameById(textView.context, id)
         handler.post {
            if(text == null) textView.text = "정보 없음"
            else textView.text = text
         }
      }
   }

   fun setDate(textView: TextView, id: Long) {
      DBThread.execute {
         val time = MediaStore_Dao.getDateById(textView.context, id)
         if(time != null) {
            handler.post {
               val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E) / HH:mm:ss", Locale.getDefault())
               val date_string = (formatter).format(time)
               textView.text = date_string
            }
         }
      }
   }

   fun setLocation(textView: TextView, id : Long) {
      DBThread.execute {
         val text = photoDao.getLocationById(id)
         handler.post {
            textView.text = text
         }
      }
   }

   fun setTags(textView: TextView, id : Long) {
      DBThread.execute {
         val tags = photoDao.getTagsById(id)
         handler.post {
            textView.text = tags.joinToString(", ")
         }
      }
   }

   fun checkFavorite(imageView: ImageView, id: Long) {
      DBThread.execute {
         var favorite = photoDao.getFavoriteById(id)
         if(favorite == null) {
            insert(ExtraPhotoData(id, null, false))
            favorite = false
         }
         handler.post {
            if (favorite) imageView.setImageResource(R.drawable.ic_favorite_checked)
            else imageView.setImageResource(R.drawable.ic_favorite)
         }
      }
   }

   fun changeFavorite(imageView: ImageView, id : Long) {
      DBThread.execute {
         var favorite = photoDao.getFavoriteById(id)
         if(favorite == null) {
            insert(ExtraPhotoData(id, null, true))
            favorite = true
         }
         photoDao.update(id, !favorite)
         handler.post {
            //변경 되었으므로 반대로
            if (!favorite) imageView.setImageResource(R.drawable.ic_favorite_checked)
            else imageView.setImageResource(R.drawable.ic_favorite)
         }
      }
   }


   fun checkChangedData(context: Context) {
      //이미 변환 감지 실행중이면 다시 실행
      if (changeCheckThread.isTerminating) changeCheckThread.shutdownNow()
      //추가 작업
      changeCheckThread.execute {
         val pref = context.getSharedPreferences("pre", MODE_PRIVATE)
         val editor = pref.edit()
         lastAddedDate = pref.getLong("lastAddedDate", 0)
         val cursor = MediaStore_Dao.getNewlySortedCursor(context, lastAddedDate)
         if (MediaStore_Dao.cursorIsValid(cursor)) {
            do {
               val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
               // 인터넷이 끊길 시 스톱
               if (!NetworkIsValid(context)) break
               changeCheckThread.execute {
                  val loc = photoDao.getLocationById(id) ?: MediaStore_Dao.getLocation(context.applicationContext, id) ?: return@execute
                  val favorite = photoDao.getFavoriteById(id) ?: false
                  val extra = ExtraPhotoData(id, loc, favorite)
                  AddTagsByApi(context, id)
                  photoDao.insert(extra)
               }
               lastAddedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED))
               editor.putLong("lastAddedDate", lastAddedDate)
               editor.apply()
            } while (cursor!!.moveToNext())
            cursor.close()
         }
         //삭제 작업
         changeCheckThread.execute {
            val cursor = photoDao.getIdCursor()
            if (MediaStore_Dao.cursorIsValid(cursor)) {
               do {
                  val id = cursor.getLong(cursor.getColumnIndex("photo_id"))
                  if (!MediaStore_Dao.IsItValidId(context, id)) {
                     photoDao.deleteExtraById(id)
                     photoDao.deleteTagById(id)
                  }
               } while (cursor.moveToNext())
            }
         }
      }
   }

   fun Drop() {
      DBThread.execute {
         photoDao.dropExtraTable()
         photoDao.dropTagTable()
      }
   }

   private fun AddTagsByApi(context: Context, id: Long) {
      val options = FirebaseTranslatorOptions.Builder()
         .setSourceLanguage(FirebaseTranslateLanguage.EN)
         .setTargetLanguage(FirebaseTranslateLanguage.KO)
         .build()
      val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

      val bitmap = MediaStore_Dao.LoadThumbnailById(context, id)
      val image = FirebaseVisionImage.fromBitmap(bitmap)
      val labeler = FirebaseVision.getInstance().onDeviceImageLabeler
      labeler.processImage(image)
         .addOnSuccessListener { labels ->
            translator.downloadModelIfNeeded()
               .addOnSuccessListener {
                  for (label in labels) {
                     translator.translate(label.text)
                        .addOnSuccessListener { translatedText ->
                           if(label.confidence >= 0.85) {
                              DBThread.execute { photoDao.insert(TagData(id, translatedText, "auto"))}
                           }
                        }
                        .addOnFailureListener { e ->
                           e.stackTrace
                        }
                  }
               }
               .addOnFailureListener { e ->
                  e.stackTrace
               }
         }
         .addOnFailureListener { e ->
            e.stackTrace
         }
   }

   @Suppress("DEPRECATION")
   private fun NetworkIsValid(context: Context) : Boolean {
      var result = false
      val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         val networkCapabilities = cm.activeNetwork ?: return false
          val actNw = cm.getNetworkCapabilities(networkCapabilities) ?: return false
          result = when {
             actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
             actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
             actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
             else -> false
          }
       } else {
          cm.run {
             cm.activeNetworkInfo?.run {
                result = when(type) {
                   ConnectivityManager.TYPE_WIFI -> true
                   ConnectivityManager.TYPE_MOBILE -> true
                   ConnectivityManager.TYPE_ETHERNET -> true
                   else -> false
                }
             }
          }
       }
      return result
   }
}
