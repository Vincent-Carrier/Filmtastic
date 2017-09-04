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
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.vincentcarrier.filmtastic.Filmtastic
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.id.change_sort_method
import com.vincentcarrier.filmtastic.R.id.sign_in
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.ui.details.DetailsActivity
import com.vincentcarrier.filmtastic.ui.loadPoster
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_movie_grid.*
import kotlinx.android.synthetic.main.movie_grid_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast


/*
TODO: Allow the user to add a movie to his watchlist
*/

class MovieGridActivity : LifecycleActivity(), AnkoLogger {

	private lateinit var vm: MovieGridViewModel
	private val app = application as Filmtastic

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
		if (app.isLoggedIn()) {
			CustomTabsClient.connectAndInitialize(this, "com.android.chrome")
			vm.fetchSessionId()?.subscribeBy(
				onSuccess = {
					(application as Filmtastic).storeSessionId(it)
					invalidateOptionsMenu()
				},
				onError = { toast(it.localizedMessage) }
			)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		onPrepareOptionsMenu(menu)
		return true
	}

	override fun onPrepareOptionsMenu(menu: Menu): Boolean {
		menu.findItem(change_sort_method).title = getSortMethodMenuTitle()
		menu.findItem(sign_in).isVisible = !app.isLoggedIn()
		return super.onPrepareOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			change_sort_method -> {
				vm.changeSortMethod()
				item.title = getSortMethodMenuTitle()
				fetchAndBindMovies()
			}
			sign_in -> {
				vm.fetchRequestToken().subscribeBy(
						onSuccess = {
							vm.requestToken = it
							val BASE_URL = "https://www.themoviedb.org/authenticate/"
							val browser = CustomTabsIntent.Builder()
									.setToolbarColor(ContextCompat.getColor(this, R.color.chromeToolbar))
									.build()
							browser.launchUrl(this, Uri.parse(BASE_URL + it))
						},
						onError = { toast(it.localizedMessage) }
				)
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun setUpMovieGrid() {
		with(movieGrid) {
			adapter = MovieAdapter()

			val isPortrait = (context.resources.configuration.orientation == ORIENTATION_PORTRAIT)
			layoutManager = GridLayoutManager(this@MovieGridActivity, if (isPortrait) 2 else 4)

			addOnScrollListener(InfiniteScrollListener({ fetchAndBindMovies() }, layoutManager as GridLayoutManager))

			setHasFixedSize(true)
			setItemViewCacheSize(20)
			isDrawingCacheEnabled = true
			drawingCacheQuality = DRAWING_CACHE_QUALITY_HIGH
		}
	}

	private fun fetchAndBindMovies() {
		vm.fetchMovies()
				.bindToLifecycle(this)
				.subscribeBy(
						onSuccess = {
							vm.movies.addAll(it)
							vm.pageCount += 1
							movieGrid.adapter.notifyDataSetChanged()
						},
						onError = { toast(it.localizedMessage) }
				)
	}

	private fun getSortMethodMenuTitle(): String {
		return "${this.getString(string.sorted_by)} : ${this.getString(vm.sortMethod.stringResource)}"
	}

	inner class MovieAdapter : RecyclerView.Adapter<MovieAdapter.PosterViewHolder>() {

		inner class PosterViewHolder(itemView: View?) : ViewHolder(itemView)

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.PosterViewHolder {
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