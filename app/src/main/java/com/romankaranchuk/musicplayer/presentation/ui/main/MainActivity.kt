package com.romankaranchuk.musicplayer.presentation.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.data.Song
import timber.log.Timber
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

//    private var mIsServiceBound: Boolean = false
//    private lateinit var mSearchBinder: SearchService.SearchBinder

//    private val mSearchConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
//            Timber.d("MainActivity onServiceConnected")
//            mSearchBinder = (binder as SearchService.SearchBinder)
//            mIsServiceBound = true
////            mSearchService.startMusicSearch()
//        }
//
//        override fun onServiceDisconnected(name: ComponentName) {
//            Timber.d("MainActivity onServiceDisconnected")
//            mIsServiceBound = false
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val playerFragment = supportFragmentManager.findFragmentByTag(PlayerFragment.PLAYER_FRAGMENT_TAG)
//        val tracklistFragment = supportFragmentManager.findFragmentByTag(TRACK_LIST_TAG)
        val transaction = supportFragmentManager.beginTransaction()
        when {
//            playerFragment != null -> transaction.replace(R.id.fragment_container_main_activity, playerFragment, PlayerFragment.PLAYER_FRAGMENT_TAG)
//            tracklistFragment != null -> transaction.replace(R.id.fragment_container_main_activity, tracklistFragment, TRACK_LIST_TAG)
            else -> transaction.replace(R.id.ma_container, MainFragment.newInstance(), MainFragment.TAG)
        }
        transaction.commit()

        Timber.d("onCreate")
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//
//        if (hasFocus) {
//            hideSystemUI(window)
//        }
//    }

//    private fun searchMusic() {
//        if (mIsServiceBound) {
//            mSearchBinder.onBind()
//        } else {
//            val intent = Intent(this, SearchService::class.java)
//            bindService(intent, mSearchConnection, Context.BIND_AUTO_CREATE)
//        }
//    }

    override fun onStop() {
        super.onStop()
//        if (mIsServiceBound) {
//            unbindService(mSearchConnection)
//            mIsServiceBound = false
//        }
        Timber.d("onStop")
    }

    private fun requestPermAndSearchMusic() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_READ_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                searchMusic()
            } else {
                Toast.makeText(this, R.string.read_storage_not_granted, Toast.LENGTH_LONG).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_audio_files_from_sd -> {
                requestPermAndSearchMusic()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        @JvmStatic val listRecentlySongs = LinkedList<Song>()
        @JvmField val path: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)

        private const val REQUEST_READ_STORAGE = 100
        private const val TAG = "MainActivity"
    }
}