package com.example.wimmy

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import com.example.wimmy.Adapter.RecyclerAdapterForder
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoViewModel

class DataBaseObserver(handler: Handler, val adapter : RecyclerAdapterForder) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        adapter.setThumbnailList(MediaStore_Dao.getNameDir(adapter.context!!.applicationContext))
    }
}

class ChangeObserver(handler: Handler, val vm : PhotoViewModel, val context: Context) : ContentObserver(handler){
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        vm.checkChangedData(context)
    }
}