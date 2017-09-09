package com.vincentcarrier.filmtastic.ui.moviegrid


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.vincentcarrier.filmtastic.Filmtastic
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.*
import com.vincentcarrier.filmtastic.pojos.SortMethod.popular
import com.vincentcarrier.filmtastic.pojos.SortMethod.top_rated
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class MovieGridViewModel(app: Application) : AndroidViewModel(app) {

	@Inject lateinit var api: TheMovieDbApi

	init {
		Filmtastic.netComponent.inject(this)
	}

	internal var movies: MutableList<Movie> = mutableListOf()
	internal var pageCount = 0
	internal var sortMethod = SortMethod.popular
		set(value) {
			field = value
			movies.clear()
			pageCount = 0
		}
	internal var requestToken: String? = null

	internal fun fetchMovies(): Single<List<Movie>> {
		return api.fetchTopMoviesResponse(sortMethod.name, pageCount + 1)
				.observeOn(AndroidSchedulers.mainThread())
				.map(TopMoviesResponse::results)
	}

	internal fun fetchRequestToken(): Single<String> {
		return api.fetchRequestToken()
				.observeOn(AndroidSchedulers.mainThread())
				.map(RequestTokenResponse::requestToken)
	}

	internal fun fetchSessionId(): Single<String>? {
		return requestToken?.let {
			api.fetchSessionId(it)
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
