package com.vincentcarrier.filmtastic.ui.moviegrid

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.airbnb.epoxy.TypedEpoxyController
import com.vincentcarrier.filmtastic.data.MoviesManager
import com.vincentcarrier.filmtastic.models.Movie
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread

class MovieGridViewModel(private val manager: MoviesManager) : ViewModel() {

	internal fun fetchMovies(): Observable<List<Movie>> {
		return manager.fetchMovies()
				.observeOn(mainThread())
				.doOnNext {
					controller.setData(controller.currentData?.plus(it) ?: it)
				}
	}

	internal fun sortMethod() = manager.sortMethod.stringResource

	internal fun changeSortMethod() = manager.changeSortMethod()

	private val controller = MoviesController()

	internal fun adapter() = controller.adapter

	class MoviesController() : TypedEpoxyController<List<Movie>>() {
		override fun buildModels(movies: List<Movie>) {
			for (movie in movies) MovieModel_(movie).id(movie.id).addTo(this)
		}
	}
}

@Suppress("UNCHECKED_CAST")
class MovieGridVmFactory(private val manager: MoviesManager) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return MovieGridViewModel(manager) as T
	}
}
