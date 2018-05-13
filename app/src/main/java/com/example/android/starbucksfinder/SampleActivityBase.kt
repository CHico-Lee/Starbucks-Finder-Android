package com.example.android.starbucksfinder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
/**
 * Created by leech on 5/6/2018.
 */
/**
 * Base launcher activity, to handle most of the common plumbing for samples.
 */
open class SampleActivityBase : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    /** Set up targets to receive log data  */
    /*
    open fun initializeLogging() {
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        // Wraps Android's native log framework
        val logWrapper = LogWrapper()
        Log.logNode = logWrapper

        Log.i(TAG, "Ready")
    }
    */

    companion object {
        val TAG = "SampleActivityBase"
    }
}
