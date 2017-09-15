package com.vincentcarrier.filmtastic.ui.moviegrid


import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.vincentcarrier.filmtastic.data.MovieRepository
import com.vincentcarrier.filmtastic.models.*
import com.vincentcarrier.filmtastic.models.SortMethod.popular
import com.vincentcarrier.filmtastic.models.SortMethod.top_rated
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class MovieGridViewModel(val repository: MovieRepository) : ViewModel() {

	internal var movies: MutableList<Movie> = mutableListOf()
	internal var pageCount = 0
	internal var sortMethod = SortMethod.popular
		set(value) {
			field = value
			movies.clear()
			pageCount = 0
		}
	internal var requestToken: String? = null

	internal fun fetchMovies(): Observable<List<Movie>> {
		return repository.fetchMovies(sortMethod.name, pageCount + 1)
				.observeOn(AndroidSchedulers.mainThread())
				.map(MoviesResponse::results)
	}

	internal fun requestRequestToken(): Single<String> {
		return repository.requestRequestToken()
				.observeOn(AndroidSchedulers.mainThread())
				.map(RequestTokenResponse::requestToken)
	}

	internal fun requestSessionId(): Single<String>? {
		return requestToken?.let {
			repository.requestSessionId(it)
				.observeOn(AndroidSchedulers.mainThread())
				.map(SessionIdResponse::sessionId)
		}
	}

	internal fun changeSortMethod() {
		sortMethod = when (sortMethod) {
			popular -> top_rated
			top_rated -> popular
		}
	}
}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(MovieGridViewModel::class.java)) {
			return MovieGridViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}
