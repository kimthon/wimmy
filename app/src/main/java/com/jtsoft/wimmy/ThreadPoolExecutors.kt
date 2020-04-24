package com.jtsoft.wimmy

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

val DBThread = ThreadPoolExecutor(0, Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS, SynchronousQueue<Runnable>())
val DirectoryThread = ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
var ChangeCheckThread = ThreadPoolExecutor(1, 3, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
val MainHandler = Handler(Looper.getMainLooper())
