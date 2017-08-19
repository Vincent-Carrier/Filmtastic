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
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.di.DaggerNetComponent
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.PosterWidth.XLARGE
import com.vincentcarrier.filmtastic.pojos.SortingMethod.popular
import com.vincentcarrier.filmtastic.pojos.SortingMethod.top_rated
import com.vincentcarrier.filmtastic.ui.details.DetailsActivity
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_movie_grid.*
import kotlinx.android.synthetic.main.movie_grid_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug


class MovieGridActivity : AppCompatActivity(), AnkoLogger {
	private lateinit var viewModel: MovieGridViewModel

	// TODO: Unsubscribe from RxJava subscriptions in OnPause, resubscribe on onResume
	// TODO: Save and restore scroll position
	// TODO: Implement infinite scrolling
	// TODO: Display error message

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_movie_grid)
		DaggerNetComponent.create().inject(this)
		viewModel = ViewModelProviders.of(this).get(MovieGridViewModel::class.java)
		initializeMovieGrid()
		fetchAndBindTopMovies()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		val sortingMethodMenu = menu.findItem(R.id.change_sorting_method)
		sortingMethodMenu.title = "${getString(R.string.sorted_by)} : ${viewModel.sortingMethod}"
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.change_sorting_method -> {
				when (viewModel.sortingMethod) {
					popular -> viewModel.sortingMethod = top_rated
					else -> viewModel.sortingMethod = popular
				}
				item.title = "${getString(R.string.sorted_by)} : ${viewModel.sortingMethod}"
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
			drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
			val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
			layoutManager = GridLayoutManager(this@MovieGridActivity, if (isPortrait) 2 else 4)
			adapter = MovieAdapter()
		}
	}

	private fun fetchAndBindTopMovies() {
		viewModel.fetchTopMoviesResponse()
				.subscribeBy(
						onNext = { (movieGrid.adapter as MovieAdapter).movies = it.results },
						onError = { debug { it } }
				)
	}

	inner class MovieAdapter() : RecyclerView.Adapter<MovieAdapter.PosterViewHolder>() {

		var movies: List<Movie> = emptyList()
			set(value) {
				field = value
				notifyDataSetChanged()
			}

		// Kotlin Android Extensions takes care of the binding
		inner class PosterViewHolder(itemView: View?) : ViewHolder(itemView)

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.PosterViewHolder {
			val view = LayoutInflater.from(parent.context)
					.inflate(R.layout.movie_grid_item, parent, false)
			return PosterViewHolder(view)
		}

		override fun onBindViewHolder(holder: MovieAdapter.PosterViewHolder, position: Int) {
			val movie = movies[position]
			loadImageInto(movie, holder.itemView.poster)
			holder.itemView.contentDescription = movie.title
			holder.itemView.poster.setOnClickListener {
				this@MovieGridActivity.startActivity(
						Intent(this@MovieGridActivity, DetailsActivity::class.java).putExtra("movie", movie))
			}
		}

		override fun getItemCount(): Int = movies.size
	}
}

fun loadImageInto(movie: Movie, imageView: ImageView) {
	Picasso.with(imageView.context)
			.load("https://image.tmdb.org/t/p/w${XLARGE.width + movie.poster_path}")
			.placeholder(R.drawable.poster_placeholder)
			.into(imageView)
}