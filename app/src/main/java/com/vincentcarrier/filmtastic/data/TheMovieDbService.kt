package com.vincentcarrier.filmtastic.data

import com.vincentcarrier.filmtastic.models.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread

class TheMovieDbService {

	private fun api() = Retrofit.api

	fun requestRequestToken(): Single<String> {
		return api().requestRequestToken()
				.observeOn(mainThread())
				.map(RequestTokenResponse::requestToken)
				.doOnSuccess { UserCredentials.requestToken = it }
	}

	fun requestSessionId(): Single<String> {
		return api().requestSessionId(UserCredentials.requestToken)
				.observeOn(mainThread())
				.map(SessionIdResponse::sessionId)
				.doOnSuccess {
					UserCredentials.sessionId = it
					requestAccountDetails(it)
							.subscribe()
				}
	}

	private fun requestAccountDetails(sessionId: String): Single<Int> {
		return api().requestAccountDetails(sessionId)
				.observeOn(mainThread())
				.map(AccountDetailsResponse::id)
				.doOnSuccess { UserCredentials.accountId = it }
	}

	fun fetchMovies(sortMethod: SortMethod, page: Int): Observable<List<Movie>> {
		return api().fetchMovies(sortMethod.httpName, page)
				.observeOn(mainThread())
				.map(MoviesResponse::results)
	}

	fun requestMovieTrailers(movieId: Int): Single<List<Trailer>> {
		return api().requestMovieTrailers(movieId)
				.observeOn(mainThread())
				.map(TrailersResponse::results)
	}

	fun postMovieToWatchList(movieId: Int): Completable {
		return api().postMovieToWatchList(
				MovieRequest(movieId), UserCredentials.accountId, UserCredentials.sessionId)
				.observeOn(mainThread())
	}
}