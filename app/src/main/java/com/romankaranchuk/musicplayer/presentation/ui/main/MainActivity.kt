package com.romankaranchuk.musicplayer.presentation.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.presentation.service.SearchService
import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerFragment
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import java.io.File
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private var mIsServiceBound: Boolean = false
    private lateinit var mSearchService: SearchService

    private val mSearchConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Log.d(TAG, "MainActivity onServiceConnected")
            mSearchService = (binder as SearchService.SearchBinder).service
            mIsServiceBound = true
            mSearchService.startMusicSearch()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "MainActivity onServiceDisconnected")
            mIsServiceBound = false
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return dispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val transaction = supportFragmentManager.beginTransaction()
        when {
            supportFragmentManager.findFragmentByTag(PlayerFragment.PLAYER_FRAGMENT_TAG) != null -> transaction.replace(R.id.fragment_container_main_activity,
                    supportFragmentManager.findFragmentByTag(PlayerFragment.PLAYER_FRAGMENT_TAG)!!, PlayerFragment.PLAYER_FRAGMENT_TAG)
            supportFragmentManager.findFragmentByTag(TRACK_LIST_TAG) != null -> transaction.replace(R.id.fragment_container_main_activity,
                    supportFragmentManager.findFragmentByTag(TRACK_LIST_TAG)!!, TRACK_LIST_TAG)
            else -> transaction.replace(R.id.fragment_container_main_activity, TrackListFragment(), TRACK_LIST_TAG)
        }
        transaction.commit()


        Log.d(TAG, "MainActivity onCreate")
    }

    private fun searchMusic() {
        if (mIsServiceBound) {
            mSearchService.startMusicSearch()
        } else {
            val intent = Intent(this, SearchService::class.java)
            bindService(intent, mSearchConnection, Context.BIND_AUTO_CREATE)
        }
    }


    override fun onStop() {
        super.onStop()
        if (mIsServiceBound) {
            unbindService(mSearchConnection)
            mIsServiceBound = false
        }
        Log.d(TAG, "MainActivity onStop")
    }


    private fun requestPermAndSearchMusic() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_READ_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                searchMusic()
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
        @JvmStatic var curSelectedSong: Song? = null
        @JvmField val path: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)

        private const val REQUEST_READ_STORAGE = 100
        private const val TAG = "MainActivity"
        private const val TRACK_LIST_TAG = "tracklistFragment"
    }
}