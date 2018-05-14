package com.example.android.starbucksfinder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
/**
 * Created by leech on 5/6/2018.
 */
/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * [android.support.v4.app.Fragment] which can display a view.
 *
 *
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().run {
                replace(R.id.sample_content_fragment, ListFragment())
                commit()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val TAG = "MainActivity"
        private const val REQUEST_CHECK_SETTINGS = 12
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null)
            android.util.Log.e("MainActivity", "press the back button on the PlacePicker will cause null Intent returned, fixed by using Intent?")
        if (requestCode == MainActivity.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                android.util.Log.i(TAG, "onActivityResult Main REQUEST_CHECK_SETTINGS")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments = supportFragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

    }
}
