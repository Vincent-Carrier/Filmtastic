package com.vincentcarrier.filmtastic.ui.moviegrid

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.*
import android.view.View.DRAWING_CACHE_QUALITY_HIGH
import com.livinglifetechway.k4kotlin.hide
import com.livinglifetechway.k4kotlin.hideViews
import com.livinglifetechway.k4kotlin.show
import com.livinglifetechway.k4kotlin.showViews
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.pojos.SortingMethod.popular
import com.vincentcarrier.filmtastic.pojos.SortingMethod.top_rated
import com.vincentcarrier.filmtastic.ui.details.DetailsActivity
import com.vincentcarrier.filmtastic.ui.loadImageInto
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_movie_grid.*
import kotlinx.android.synthetic.main.movie_grid_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info

/*
 TODO: Change JSON adapter to Gson
 TODO: Write some Robolectric tests
 TODO: Convert RecyclerView.Adapters to Epoxy
 TODO: Deep-link the app, launch the sign-in page in an in-app browser
 TODO: Allow the user to add a movie to his watchlist
 TODO: Optimize gradle build */

class MovieGridActivity : LifecycleActivity(), AnkoLogger {

	private lateinit var vm: MovieGridViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_movie_grid)
		setActionBar(toolbar)
		initializeMovieGrid()
		vm = ViewModelProviders.of(this).get(MovieGridViewModel::class.java)
	}

	override fun onStart() {
		super.onStart()
		if (vm.movies.isEmpty()) fetchAndBindMovies()
	}

	override fun onResume() {
		super.onResume()
		if (vm.hasRequestToken) vm.fetchSessionId().subscribeBy(
				onSuccess = { it ->
					vm.sessionId = it
					vm.hasRequestToken = false
					info { it }
					invalidateOptionsMenu()

					getSharedPreferences("session id", MODE_PRIVATE)
							.edit().putString("session id", it).apply()
				},
				onError = { error { it } }
		)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		val sortMethodMenu = menu.findItem(R.id.change_sorting_method)
		sortMethodMenu.title = "${getString(string.sorted_by)} : ${getString(vm.sortMethod.stringResource)}"

		val signInMenu = menu.findItem(R.id.sign_in)
		if (vm.sessionId != null) signInMenu.isVisible = false
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.change_sorting_method -> {
				when (vm.sortMethod) {
					popular -> vm.sortMethod = top_rated
					else -> vm.sortMethod = popular
				}
				item.title = "${getString(string.sorted_by)} : ${getString(vm.sortMethod.stringResource)}"
				fetchAndBindMovies()
			}
			R.id.sign_in -> vm.fetchRequestToken().subscribeBy(
					onSuccess = { it ->
						vm.hasRequestToken = true
						startActivity(Intent(ACTION_VIEW,
								Uri.parse("https://www.themoviedb.org/authenticate/" + it)))
					},
					onError = { it -> error { it } }
			)
		}
		return super.onOptionsItemSelected(item)
	}

	private fun initializeMovieGrid() {
		movieGrid.apply {
			setHasFixedSize(true)
			isDrawingCacheEnabled = true
			setItemViewCacheSize(20)
			drawingCacheQuality = DRAWING_CACHE_QUALITY_HIGH

			val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
			layoutManager = GridLayoutManager(this@MovieGridActivity, if (isPortrait) 2 else 4)
			adapter = MovieAdapter()

			addOnScrollListener(InfiniteScrollListener({ fetchAndBindMovies() }, layoutManager as GridLayoutManager))
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
							movieGrid.show()
							hideViews(movieGridLoadingSpinner, errorIcon, errorMessage)
						},
						onError = {
							error { it }
							movieGrid.hide()
							showViews(errorIcon, errorMessage)
						}
				)
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
			loadImageInto(movie, holder.itemView.poster)
			holder.itemView.contentDescription = movie.title
			holder.itemView.setOnClickListener {
				startActivity(Intent(this@MovieGridActivity, DetailsActivity::class.java)
						.putExtra("movie", movie))
			}
		}

		override fun getItemCount(): Int = vm.movies.size
	}
}