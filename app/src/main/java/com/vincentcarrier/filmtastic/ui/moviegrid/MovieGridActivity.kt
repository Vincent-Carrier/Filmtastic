package com.vincentcarrier.filmtastic.ui.moviegrid

import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.support.customtabs.CustomTabsClient
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View.DRAWING_CACHE_QUALITY_HIGH
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.id.change_sort_method
import com.vincentcarrier.filmtastic.R.id.sign_in
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.data.TheMovieDbService
import com.vincentcarrier.filmtastic.data.UserCredentials
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_movie_grid.*
import org.jetbrains.anko.AnkoLogger

class MovieGridActivity : AppCompatActivity(), AnkoLogger {

	private val vm: MovieGridViewModel by lazy {
		ViewModelProviders.of(this, MovieGridVmFactory(TheMovieDbService()))
				.get(MovieGridViewModel::class.java)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_movie_grid)
		setUpMovieGrid()
		fetchMovies()
	}

	private fun fetchMovies() {
		vm.fetchMovies()
				.bindToLifecycle(this)
				.subscribeBy()
	}

	override fun onStart() {
		super.onStart()
		if (!UserCredentials.isLoggedIn()) {
			// Warm up the in-app browser to reduce loading time
			CustomTabsClient.connectAndInitialize(this, "com.android.chrome")

			// If the user has just closed the login tab, try to get a session ID
			// TODO: Make this more explicit with a deep-link Intent
//			TheMovieDbService.requestSessionId()
//					.bindToLifecycle(this)
//					.subscribeBy()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		onPrepareOptionsMenu(menu)
		return true
	}

	override fun onPrepareOptionsMenu(menu: Menu): Boolean {
		menu.findItem(change_sort_method).title = "${getString(string.sorted_by)} : ${getString(vm.sortMethodName())}"
		menu.findItem(sign_in).isVisible = true
		return super.onPrepareOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			change_sort_method -> {
				changeSortMethod()
			}
			sign_in -> {
				signIn()
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun changeSortMethod() {
		vm.changeSortMethod()
		fetchMovies()
	}

	private fun signIn() {
//		TheMovieDbService.requestRequestToken()
//				.bindToLifecycle(this)
//				.subscribeBy(onSuccess = {
//					val browser = CustomTabsIntent.Builder()
//							.setToolbarColor(ContextCompat.getColor(this, R.color.chromeToolbar))
//							.build()
//					val loginUrl = "https://www.themoviedb.org/authenticate/"
//					browser.launchUrl(this, Uri.parse(loginUrl + it))
//				})
	}

	private fun setUpMovieGrid() {
		with(movieGrid) {
			val isPortrait = resources.configuration.orientation == ORIENTATION_PORTRAIT
			layoutManager = GridLayoutManager(context, if (isPortrait) 2 else 4)
			adapter = vm.adapter()

			addOnScrollListener(
					InfiniteScrollListener({ fetchMovies() }, layoutManager as GridLayoutManager))

			setHasFixedSize(true)
			setItemViewCacheSize(40)
			isDrawingCacheEnabled = true
			drawingCacheQuality = DRAWING_CACHE_QUALITY_HIGH
		}
	}
}