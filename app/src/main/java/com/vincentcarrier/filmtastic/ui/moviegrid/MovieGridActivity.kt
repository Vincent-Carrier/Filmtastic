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
import android.view.View.DRAWING_CACHE_QUALITY_HIGH
import com.livinglifetechway.k4kotlin.hide
import com.livinglifetechway.k4kotlin.hideViews
import com.livinglifetechway.k4kotlin.show
import com.livinglifetechway.k4kotlin.showViews
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
import org.jetbrains.anko.debug

// TODO: Implement RxLifecycle

class MovieGridActivity : AppCompatActivity(), AnkoLogger {

	private lateinit var vm: MovieGridViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_movie_grid)
		initializeMovieGrid()
		vm = ViewModelProviders.of(this).get(MovieGridViewModel::class.java)
		if (vm.movies.isEmpty()) fetchAndBindMovies()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		val sortMethodMenu = menu.findItem(R.id.change_sorting_method)
		sortMethodMenu.title = "${getString(string.sorted_by)} : ${getString(vm.sortMethod.stringResource)}"
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
			addOnScrollListener(InfiniteScrollListener({ fetchAndBindMovies() }, layoutManager as GridLayoutManager))
			adapter = MovieAdapter()
		}
	}

	private fun fetchAndBindMovies() {
		vm.fetchTopMoviesResponse()
				.subscribeBy(
						onSuccess = {
							vm.movies.addAll(it.results)
							vm.pageCount += 1
							movieGrid.adapter.notifyDataSetChanged()
							movieGrid.show()
							hideViews(movieGridLoadingSpinner, errorIcon, errorMessage)
						},
						onError = {
							debug { it }
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