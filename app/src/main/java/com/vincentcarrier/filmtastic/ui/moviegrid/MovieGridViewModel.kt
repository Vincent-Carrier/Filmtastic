package com.vincentcarrier.filmtastic.ui.moviegrid


import android.arch.lifecycle.ViewModel
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class MovieGridViewModel : ViewModel() {

	@Inject lateinit var api: TheMovieDbApi

	internal var hasRequestToken = false
	internal var sessionId: String? = null

	internal var movies: MutableList<Movie> = mutableListOf()
	internal var sortMethod = SortingMethod.popular
		set(value) {
			field = value
			movies.clear()
			pageCount = 0
		}
	internal var pageCount = 0

	init {
		App.netComponent.inject(this)
	}

	internal fun fetchMovies(): Single<List<Movie>> {
		return api.fetchTopMoviesResponse(sortMethod.name, pageCount + 1)
				.observeOn(AndroidSchedulers.mainThread())
				.map(TopMoviesResponse::results)
	}

	internal fun fetchRequestToken(): Single<String> {
		return api.fetchRequestToken()
				.observeOn(AndroidSchedulers.mainThread())
				.map(RequestTokenResponse::request_token)
	}

	internal fun fetchSessionId(): Single<String> {
		return api.fetchSessionId()
				.observeOn(AndroidSchedulers.mainThread())
				.map(SessionIdResponse::session_id)
	}
}
