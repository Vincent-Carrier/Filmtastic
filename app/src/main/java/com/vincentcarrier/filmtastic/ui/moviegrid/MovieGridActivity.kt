package com.vincentcarrier.filmtastic.ui.moviegrid

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.*
import android.view.View.DRAWING_CACHE_QUALITY_HIGH
import com.vincentcarrier.filmtastic.Filmtastic
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.id.change_sort_method
import com.vincentcarrier.filmtastic.R.id.sign_in
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.ui.details.DetailsActivity
import com.vincentcarrier.filmtastic.ui.execute
import com.vincentcarrier.filmtastic.ui.loadPoster
import kotlinx.android.synthetic.main.activity_movie_grid.*
import kotlinx.android.synthetic.main.movie_grid_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor


class MovieGridActivity : LifecycleActivity(), AnkoLogger {

	private lateinit var vm: MovieGridViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_movie_grid)
		vm = ViewModelProviders.of(this).get(MovieGridViewModel::class.java)
		setActionBar(toolbar)
		setUpMovieGrid()
	}

	override fun onStart() {
		super.onStart()
		if (vm.movies.isEmpty()) fetchAndBindMovies()
		if (!app().isLoggedIn()) {
			// Warm up the in-app browser to reduce loading time
			CustomTabsClient.connectAndInitialize(this, "com.android.chrome")
			vm.fetchSessionId()?.execute(this) {
				app().storeSessionId(it)
				invalidateOptionsMenu()
			}
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		onPrepareOptionsMenu(menu)
		return true
	}

	override fun onPrepareOptionsMenu(menu: Menu): Boolean {
		menu.findItem(change_sort_method).title = getSortMethodMenuTitle()
		menu.findItem(sign_in).isVisible = !app().isLoggedIn()
		return super.onPrepareOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			change_sort_method -> {
				movieGrid.adapter.notifyItemRangeRemoved(0, vm.movies.size)
				vm.changeSortMethod()
				item.title = getSortMethodMenuTitle()
				fetchAndBindMovies()
			}
			sign_in -> {
				vm.fetchRequestToken().execute(this) {
					vm.requestToken = it
					val browser = CustomTabsIntent.Builder()
							.setToolbarColor(ContextCompat.getColor(this, R.color.chromeToolbar))
							.build()
					val BASE_URL = "https://www.themoviedb.org/authenticate/"
					browser.launchUrl(this, Uri.parse(BASE_URL + it))
				}
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun setUpMovieGrid() {
		val isPortrait = (resources.configuration.orientation == ORIENTATION_PORTRAIT)
		with(movieGrid) {
			adapter = MovieAdapter()
			layoutManager = GridLayoutManager(this@MovieGridActivity, if (isPortrait) 2 else 4)

			addOnScrollListener(
					InfiniteScrollListener({ fetchAndBindMovies() }, layoutManager as GridLayoutManager))

			setHasFixedSize(true)
			setItemViewCacheSize(20)
			isDrawingCacheEnabled = true
			drawingCacheQuality = DRAWING_CACHE_QUALITY_HIGH
		}
	}

	private fun fetchAndBindMovies() {
		vm.fetchMovies().execute(this) {
			val oldSize = vm.movies.size
			vm.movies.addAll(it)
			vm.pageCount += 1
			movieGrid.adapter.notifyItemRangeInserted(oldSize, it.size)
		}
	}

	private fun getSortMethodMenuTitle(): String {
		return "${getString(string.sorted_by)} : ${getString(vm.sortMethod.stringResource)}"
	}

	private fun app() = application as Filmtastic

	inner class MovieAdapter : RecyclerView.Adapter<MovieAdapter.PosterViewHolder>() {

		inner class PosterViewHolder(itemView: View?) : ViewHolder(itemView)

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
				MovieAdapter.PosterViewHolder {
			val view = LayoutInflater.from(parent.context)
					.inflate(R.layout.movie_grid_item, parent, false)
			return PosterViewHolder(view)
		}

		override fun onBindViewHolder(holder: MovieAdapter.PosterViewHolder, position: Int) {
			val movie = vm.movies[position]
			with(holder.itemView) {
				poster.loadPoster(movie)
				contentDescription = movie.title
				setOnClickListener {
					startActivity(intentFor<DetailsActivity>("movie" to movie))
				}
			}
		}

		override fun getItemCount(): Int = vm.movies.size
	}
}