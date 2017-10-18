package com.vincentcarrier.filmtastic.ui.moviegrid

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.airbnb.epoxy.TypedEpoxyController
import com.vincentcarrier.filmtastic.data.TheMovieDbService
import com.vincentcarrier.filmtastic.models.Movie
import com.vincentcarrier.filmtastic.models.SortMethod.POPULAR
import com.vincentcarrier.filmtastic.models.SortMethod.TOP_RATED
import io.reactivex.Observable

class MovieGridViewModel(private val service: TheMovieDbService) : ViewModel() {

	internal fun fetchMovies(): Observable<List<Movie>> {
		return service.fetchMovies(sortMethod, page)
				.doOnNext {
					movies.addAll(it)
					page++
					controller.setData(movies)
				}
	}

	private val movies = mutableListOf<Movie>()

	private var page = 1
	private var sortMethod = POPULAR
		private set(value) {
			field = value
			page = 1
			movies.clear()
		}

	internal fun sortMethodName() = sortMethod.stringResource

	internal fun changeSortMethod() {
		sortMethod = when (sortMethod) {
			POPULAR -> TOP_RATED
			TOP_RATED -> POPULAR
		}
		controller.setData(emptyList())
	}

	// Epoxy Controller is essentially an activity-independent RecyclerView.Adapter
	internal fun adapter() = controller.adapter

	private val controller = MoviesController()

	private class MoviesController : TypedEpoxyController<List<Movie>>() {
		override fun buildModels(movies: List<Movie>) {
			movies.forEach { movie ->
				movieView {
					id(movie.id)
					posterPath(movie.posterPath)
					title(movie.title)
					movie(movie)
				}
			}
		}
	}
}

class MovieGridVmFactory(private val service: TheMovieDbService) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		@Suppress("UNCHECKED_CAST") return MovieGridViewModel(service) as T
	}
}