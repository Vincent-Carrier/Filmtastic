package com.vincentcarrier.filmtastic.data

import com.vincentcarrier.filmtastic.FilmtasticApp
import com.vincentcarrier.filmtastic.models.*
import com.vincentcarrier.filmtastic.models.SortMethod.popular
import com.vincentcarrier.filmtastic.models.SortMethod.top_rated
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import kotlin.properties.Delegates

interface Manager {}

class UserManager : Manager {
	private lateinit var api: TheMovieDbApi

	var requestToken: String? = null
	var sessionId: String? = null
	val accountId: Int? = null

	fun requestAccountDetails(sessionId: String): Single<AccountDetailsResponse> {
		return api.requestAccountDetails(sessionId)
	}

	fun requestRequestToken(): Single<RequestTokenResponse> {
		return api.requestRequestToken()
	}

	fun requestSessionId(requestToken: String): Single<SessionIdResponse> {
		return api.requestSessionId(requestToken)
	}
}

class MoviesManager : Manager {
	@Inject lateinit var api: TheMovieDbApi

	init {
		FilmtasticApp.netComponent.inject(this)
	}

	private val movies: MutableList<Movie> = mutableListOf()
	private var page = 1
	var sortMethod by Delegates.observable(popular) { _, _, _ ->
		page = 1; movies.clear()
	}
		private set

	fun fetchMovies(): Observable<List<Movie>> {
		return api.fetchMovies(sortMethod.name, page)
				.map(MoviesResponse::results)
				.doOnNext {
					movies.addAll(it)
					page += 1
				}
	}

	internal fun changeSortMethod() {
		sortMethod = when (sortMethod) {
			popular -> top_rated
			top_rated -> popular
		}
		page = 1
	}
}

class TrailersManager : Manager {
	private lateinit var api: TheMovieDbApi
	var trailers = emptyList<Trailer>()

	fun requestMovieTrailers(movieId: Int): Single<List<Trailer>> {
		return api.requestMovieTrailers(movieId)
				.map(TrailersResponse::results)
	}
}

class WatchlistManager : Manager {
	private lateinit var userManager: UserManager
	private lateinit var api: TheMovieDbApi
	fun postMovieToWatchList(movieId: Int): Completable {
		return api.postMovieToWatchList(MovieRequest(movieId), userManager.accountId!!, userManager.sessionId!!)
	}
}