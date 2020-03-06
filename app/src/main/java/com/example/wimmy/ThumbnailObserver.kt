package com.example.wimmy

import android.database.ContentObserver
import android.os.Handler
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.db.MediaStore_Dao

class DataBaseObserver(val handler: Handler, val adapter : RecyclerAdapterForder) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        adapter.setThumbnailList(MediaStore_Dao.getNameDir(adapter.context!!.applicationContext))
    }
}
