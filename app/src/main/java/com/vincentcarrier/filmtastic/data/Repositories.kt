package com.vincentcarrier.filmtastic.data

import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.models.*
import com.vincentcarrier.filmtastic.models.SortMethod.POPULAR
import com.vincentcarrier.filmtastic.models.SortMethod.TOP_RATED
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import kotlin.properties.Delegates

// Note: Kotlin singletons are thread-safe and lazy-loaded
// Ideally, these should be normal classes, but it simplified a lot of code regarding
// dependency injection and ViewModel factories.
// The state of dependency injection in Kotlin is pretty meh right now, and Dagger would
// make a lot of things more unclear.

private fun api() = Retrofit.api

object UserRepository {
	private var requestToken = ""
	internal var sessionId = ""
	internal var accountId = 0

	init {
		retrieveSessionId()
	}

	fun isLoggedIn() = (sessionId.isNotEmpty())

	fun requestRequestToken(): Single<String> {
		return api().requestRequestToken()
				.observeOn(mainThread())
				.map(RequestTokenResponse::requestToken)
				.doOnSuccess { requestToken = it }
	}

	fun requestSessionId(): Single<String> {
		return api().requestSessionId(requestToken)
				.observeOn(mainThread())
				.map(SessionIdResponse::sessionId)
				.doOnSuccess {
					storeSessionId(it)
					requestAccountDetails(sessionId)
							.subscribe()
				}
	}

	private fun requestAccountDetails(sessionId: String): Single<Int> {
		return api().requestAccountDetails(sessionId)
				.observeOn(mainThread())
				.map(AccountDetailsResponse::id)
				.doOnSuccess { accountId = it }
	}

	private fun storeSessionId(id: String) {
		sessionId = id
		App.sharedPrefs().edit().putString("sessionid", id).apply()
	}

	private fun retrieveSessionId() {
		sessionId = App.sharedPrefs().getString("sessionid", "")
	}
}

object MoviesRepository {
	private val movies: MutableList<Movie> = mutableListOf()
	private var page = 1
	var sortMethod by Delegates.observable(POPULAR) { _, _, _ ->
		page = 1
		movies.clear()
	}
		private set

	fun fetchMovies(): Observable<List<Movie>> {
		return api().fetchMovies(sortMethod.httpName, page)
				.observeOn(mainThread())
				.map(MoviesResponse::results)
				.doOnNext {
					movies.addAll(it)
					page += 1
				}
	}

	internal fun changeSortMethod() {
		sortMethod = when (sortMethod) {
			POPULAR -> TOP_RATED
			TOP_RATED -> POPULAR
		}
	}
}

object TrailersRepository {
	var trailers = emptyList<Trailer>()

	fun requestMovieTrailers(movieId: Int): Single<List<Trailer>> {
		return api().requestMovieTrailers(movieId)
				.observeOn(mainThread())
				.map(TrailersResponse::results)
	}
}

object WatchlistRepository {
	fun postMovieToWatchList(movieId: Int): Completable {
		return api().postMovieToWatchList(
				MovieRequest(movieId), UserRepository.accountId, UserRepository.sessionId)
				.observeOn(mainThread())
	}
}