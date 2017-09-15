package com.vincentcarrier.filmtastic.data

import com.vincentcarrier.filmtastic.FilmtasticApp
import com.vincentcarrier.filmtastic.models.Movie
import com.vincentcarrier.filmtastic.models.MoviesResponse
import com.vincentcarrier.filmtastic.models.RequestTokenResponse
import com.vincentcarrier.filmtastic.models.SessionIdResponse
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

abstract class Repository {
	@Inject lateinit var api: TheMovieDbService
}

class MovieRepository() : Repository() {
	init { FilmtasticApp.netComponent.inject(this) }
	val movies = emptyList<Movie>()

	fun fetchMovies(sortMethod: String, page: Int): Observable<MoviesResponse> {
		return api.fetchMovies(sortMethod, page)
	}

	fun requestRequestToken(): Single<RequestTokenResponse> {
		TODO()
	}

	fun requestSessionId(requestToken: String): Single<SessionIdResponse> {
		TODO()
	}
}