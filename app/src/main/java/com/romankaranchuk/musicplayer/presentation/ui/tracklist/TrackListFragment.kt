package com.romankaranchuk.musicplayer.presentation.ui.tracklist

import android.annotation.TargetApi
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.di.util.Injectable
import com.romankaranchuk.musicplayer.presentation.navigation.Navigator
import com.romankaranchuk.musicplayer.presentation.ui.main.MainActivity
import timber.log.Timber
import javax.inject.Inject

class TrackListFragment : Fragment(), Injectable {

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: TrackListViewModel by viewModels { viewModelFactory }
    private lateinit var trackListAdapter: TrackListAdapter

    private var updateSongsBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.loadSongs(TrackListViewModel.BY_DURATION)
        }
    }

    private val mSongItemClickListener: (Song) -> Unit = { song ->
        viewModel.songClicked(song)
    }

    private val mSongItemLongClickListener: (Song) -> Unit = { song ->
        viewModel.songLongClicked(song)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        context!!.registerReceiver(updateSongsBroadcastReceiver, IntentFilter(UPDATE_SONG_BROADCAST))

        //        ArrayList<Integer> durations = JniUtils.printAllSongs(TrackListFragment.getSongs());
        //        ArrayList<Integer> durations = mRepository.printAllSongs(); //NDK
        //        JniUtils.checkJNI(durations); //NDK
        Timber.d("onCreate")
    }

    override fun onDestroy() {
        context!!.unregisterReceiver(updateSongsBroadcastReceiver)
        super.onDestroy()
        Timber.d("onDestroy")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        lifecycle.addObserver(viewModel)
        navigator.activity = requireActivity()
    }

    override fun onDetach() {
        super.onDetach()

        lifecycle.removeObserver(viewModel)
        navigator.activity = null
    }

    @TargetApi(19)
    fun restoreDefaultToolbar(activity: Activity) {
        val attrs = activity.window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION.inv()
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        activity.window.attributes = attrs
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView")
        return inflater.inflate(R.layout.fragment_tracklist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecycler()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TrackListViewModel.ViewState.ShowTracks -> {
                    trackListAdapter.updateSongs(state.tracks)
                }
            }
        }
    }

    private fun setupRecycler() {
        val tracklistRecycler = requireView().findViewById<RecyclerView>(R.id.tracklist_list)
        tracklistRecycler.layoutManager = LinearLayoutManager(context)
        trackListAdapter = TrackListAdapter(mSongItemClickListener, mSongItemLongClickListener)
        tracklistRecycler.adapter = trackListAdapter
    }

    private fun setupToolbar() {
        val toolbar = requireView().findViewById<Toolbar>(R.id.tracklist_toolbar)
        val ma = activity as MainActivity?
        ma!!.setSupportActionBar(toolbar)

        restoreDefaultToolbar(requireActivity())

        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.songs_sort_by_name -> {
                sortBy = "0"
                Toast.makeText(activity, "name", Toast.LENGTH_SHORT).show()
                viewModel.sortSongs(sortBy)
                return true
            }
            R.id.songs_sort_by_duration -> {
                sortBy = "1"
                Toast.makeText(activity, "duration", Toast.LENGTH_SHORT).show()
                viewModel.sortSongs(sortBy)
                return true
            }
            R.id.songs_sort_by_year -> {
                sortBy = "2"
                Toast.makeText(activity, "year", Toast.LENGTH_SHORT).show()
                viewModel.sortSongs(sortBy)
                return true
            }
            R.id.songs_sort_by_date_modified -> {
                sortBy = "3"
                Toast.makeText(activity, "date modified", Toast.LENGTH_SHORT).show()
                viewModel.sortSongs(sortBy)
                return true
            }
            R.id.songs_sort_by_format -> {
                sortBy = "4"
                Toast.makeText(activity, "format", Toast.LENGTH_SHORT).show()
                viewModel.sortSongs(sortBy)
                return true
            }
            R.id.songs_sort_by_language -> {
                sortBy = "5"
                Toast.makeText(activity, "language", Toast.LENGTH_SHORT).show()
                viewModel.sortSongs(sortBy)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.songs_sort_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {

        const val TAG = "TrackListFragment"

        const val UPDATE_SONG_BROADCAST = "UPDATE_SONG"

        private const val SELECTED_SONG = "selectedSong"
        const val LIST_SONGS = "$TAG.LIST_SONGS"

        var sortBy: String = "0"
            private set
    }
}