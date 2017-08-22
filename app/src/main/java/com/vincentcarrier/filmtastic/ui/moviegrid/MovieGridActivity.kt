package com.vincentcarrier.filmtastic.ui.moviegrid

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.*
import android.view.View.*
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.di.DaggerNetComponent
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.PosterWidth.XLARGE
import com.vincentcarrier.filmtastic.pojos.SortingMethod.popular
import com.vincentcarrier.filmtastic.pojos.SortingMethod.top_rated
import com.vincentcarrier.filmtastic.ui.details.DetailsActivity
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_movie_grid.*
import kotlinx.android.synthetic.main.movie_grid_item.view.*
import org.jetbrains.anko.AnkoLogger

// TODO: Implement infinite scrolling

class MovieGridActivity : AppCompatActivity(), AnkoLogger {

	private lateinit var viewModel: MovieGridViewModel
	private lateinit var topMoviesResponse: Disposable

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_movie_grid)
		DaggerNetComponent.create().inject(this)
		viewModel = ViewModelProviders.of(this).get(MovieGridViewModel::class.java)
		initializeMovieGrid()
		if (viewModel.movies == null) fetchAndBindTopMovies()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		val sortMethodMenu = menu.findItem(R.id.change_sorting_method)
		sortMethodMenu.title = "${getString(string.sorted_by)} : ${getString(viewModel.sortMethod.stringResource)}"
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.change_sorting_method -> {
				when (viewModel.sortMethod) {
					popular -> viewModel.sortMethod = top_rated
					else -> viewModel.sortMethod = popular
				}
				item.title = "${getString(string.sorted_by)} : ${getString(viewModel.sortMethod.stringResource)}"
				fetchAndBindTopMovies()
			}
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
			clearOnScrollListeners()
			addOnScrollListener(InfiniteScrollListener({ loadMoreMovies() }, layoutManager as GridLayoutManager))
			adapter = MovieAdapter()
		}
	}

	private fun fetchAndBindTopMovies() {
		topMoviesResponse = viewModel.fetchTopMoviesResponse()
				.subscribeBy(
						onNext = {
							viewModel.movies = it.results
							movieGrid.adapter.notifyDataSetChanged()
							movieGridLoadingSpinner.visibility = GONE
							movieGrid.visibility = VISIBLE
							errorIcon.visibility = GONE
							errorMessage.visibility = GONE
						},
						onError = {
							movieGrid.visibility = GONE
							errorIcon.visibility = VISIBLE
							errorMessage.visibility = VISIBLE
						}
				)
	}

	private fun loadMoreMovies() {
		viewModel.fetchTopMoviesResponse((viewModel.movies!!.size / 20) + 1)
				.subscribeBy(
						onNext = {
							viewModel.movies!!.plus(it.results)
							movieGrid.adapter.notifyDataSetChanged()
							movieGrid.visibility = VISIBLE
							errorIcon.visibility = GONE
							errorMessage.visibility = GONE
						},
						onError = {
							movieGrid.visibility = GONE
							errorIcon.visibility = VISIBLE
							errorMessage.visibility = VISIBLE
						}
				)
	}

	override fun onDestroy() {
		topMoviesResponse.dispose()
		super.onDestroy()
	}

	inner class MovieAdapter() : RecyclerView.Adapter<MovieAdapter.PosterViewHolder>() {

		// Kotlin Android Extensions takes care of the binding
		inner class PosterViewHolder(itemView: View?) : ViewHolder(itemView)

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.PosterViewHolder {
			val view = LayoutInflater.from(parent.context)
					.inflate(R.layout.movie_grid_item, parent, false)
			return PosterViewHolder(view)
		}

		override fun onBindViewHolder(holder: MovieAdapter.PosterViewHolder, position: Int) {
			val movie = viewModel.movies!!.get(position)
			loadImageInto(movie, holder.itemView.poster)
			holder.itemView.contentDescription = movie.title
			holder.itemView.setOnClickListener {
				startActivity(Intent(this@MovieGridActivity, DetailsActivity::class.java)
						.putExtra("movie", movie))
			}
		}

		override fun getItemCount(): Int = viewModel.movies?.size ?: 0
	}
}

fun loadImageInto(movie: Movie, imageView: ImageView) {
	Picasso.with(imageView.context)
			.load("https://image.tmdb.org/t/p/w${XLARGE.width + movie.poster_path}")
			.placeholder(R.drawable.poster_placeholder)
			.into(imageView)
}