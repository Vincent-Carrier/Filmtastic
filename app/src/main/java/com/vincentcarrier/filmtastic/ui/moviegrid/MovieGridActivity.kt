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
import com.livinglifetechway.k4kotlin.hideViews
import com.livinglifetechway.k4kotlin.showViews
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.ui.details.DetailsActivity
import com.vincentcarrier.filmtastic.ui.loadImageInto
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_movie_grid.*
import kotlinx.android.synthetic.main.movie_grid_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

/*
TODO: Fix menu overflow
TODO: Fix "mapper function returned a null value"
TODO: Deep-link the app, launch the sign-in page in an in-app browser
TODO: Allow the user to add a movie to his watchlist
TODO: Optimize gradle build
*/

class MovieGridActivity : LifecycleActivity(), AnkoLogger {

	private lateinit var vm: MovieGridViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_movie_grid)
		vm = ViewModelProviders.of(this).get(MovieGridViewModel::class.java)
		setActionBar(toolbar)
		initializeMovieGrid()
	}

	override fun onResume() {
		super.onResume()
		if (vm.movies.isEmpty()) fetchAndBindMovies()
		vm.fetchSessionId()?.subscribeBy(
				onSuccess = {
					vm.storeSessionId(it)
					invalidateOptionsMenu()
				},
				onError = { toast(it.localizedMessage) }
		)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		menu.findItem(R.id.change_sort_method).title = vm.getSortMethodMenuTitle()
		menu.findItem(R.id.sign_in).isVisible = (vm.retrieveSessionId() == null)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.change_sort_method -> {
				vm.changeSortMethod()
				item.title = vm.getSortMethodMenuTitle()
				fetchAndBindMovies()
			}
			R.id.sign_in -> vm.fetchRequestToken().subscribeBy(
					onSuccess = {
						val BASE_URL = "https://www.themoviedb.org/authenticate/"
						startActivity(Intent(ACTION_VIEW, Uri.parse(BASE_URL + it)))
						vm.hasRequestToken = true
					},
					onError = { toast(it.localizedMessage) }
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
							hideViews(movieGridLoadingSpinner, errorIcon, errorMessage)
						},
						onError = {
							if (vm.movies.isEmpty()) {
								showViews(errorIcon, errorMessage)
							} else toast(it.localizedMessage)
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