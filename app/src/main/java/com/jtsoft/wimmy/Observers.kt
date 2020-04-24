package com.jtsoft.wimmy

import android.database.ContentObserver
import android.os.Handler
import com.jtsoft.wimmy.Activity.MainActivity
import com.jtsoft.wimmy.Adapter.RecyclerAdapterForder
import com.jtsoft.wimmy.db.MediaStore_Dao

class DataBaseObserver(handler: Handler, val adapter : RecyclerAdapterForder) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        adapter.setThumbnailList(MediaStore_Dao.getNameDir(adapter.context!!.applicationContext))
    }
}

class ChangeObserver(handler: Handler, val activity : MainActivity) : ContentObserver(handler){
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        activity.CheckChangeData()
    }
}