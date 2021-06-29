package com.example.unittestingkotlin.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Insert LiveData to be observed, observes the first change and returns the value added if any, else null.
 */
class LiveDataTestUtil<T> {
    fun getValue(liveData: LiveData<T>): T? {
        val data: MutableList<T> = ArrayList()

        // latch for blocking thread until data is set
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                data.add(t)
                latch.countDown() // release the latch
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        try {
            latch.await(2, TimeUnit.SECONDS) // wait for onChanged to fire and set data
        } catch (e: InterruptedException) {
            throw InterruptedException()
        }
        return if (data.size > 0) {
            data[0]
        } else null
    }
}