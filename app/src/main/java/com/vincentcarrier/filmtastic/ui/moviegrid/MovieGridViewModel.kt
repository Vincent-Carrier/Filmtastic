package com.vincentcarrier.filmtastic.ui.moviegrid

import android.arch.lifecycle.ViewModel
import com.airbnb.epoxy.TypedEpoxyController
import com.vincentcarrier.filmtastic.data.MoviesRepository
import com.vincentcarrier.filmtastic.models.Movie
import io.reactivex.Observable

class MovieGridViewModel : ViewModel() {

	internal fun fetchMovies(): Observable<List<Movie>> {
		return MoviesRepository.fetchMovies()
				.doOnNext {
					controller.setData(controller.currentData?.plus(it) ?: it)
				}
	}

	internal fun sortMethod() = MoviesRepository.sortMethod.stringResource

	internal fun changeSortMethod() {
		MoviesRepository.changeSortMethod()
		controller.setData(emptyList())
	}

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