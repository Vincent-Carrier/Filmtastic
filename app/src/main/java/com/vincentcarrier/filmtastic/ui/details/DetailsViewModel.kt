package com.vincentcarrier.filmtastic.ui.details

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.vincentcarrier.filmtastic.FilmtasticApp
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import javax.inject.Inject


class DetailsViewModel(app: Application) : AndroidViewModel(app) {

	@Inject lateinit var api: TheMovieDbApi
	internal var accountId: Int? = null

	init {
		FilmtasticApp.netComponent.inject(this)
	}

	lateinit internal var movie: Movie
	internal var trailers: List<Trailer> = emptyList()

	internal fun fetchMovieTrailers(): Single<List<Trailer>> {
		return api.fetchMovieTrailers(movie.id)
				.observeOn(mainThread())
				.map(TrailersResponse::results)
	}

	internal fun addMovieToWatchList(movie: MovieRequest): Completable {
		return api.addMovieToWatchList(movie, accountId!!, sessionId()!!)
				.observeOn(mainThread())
	}

	internal fun fetchAccountId(): Single<Int> {
		return api.fetchAccountDetails(sessionId()!!)
				.observeOn(mainThread())
				.map(AccountDetailsResponse::id)
	}

	private fun sessionId() = getApplication<FilmtasticApp>().retrieveSessionId()
}