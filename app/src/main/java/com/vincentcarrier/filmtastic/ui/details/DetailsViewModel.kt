package com.vincentcarrier.filmtastic.ui.details

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.vincentcarrier.filmtastic.Filmtastic
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.*
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


class DetailsViewModel(app: Application) : AndroidViewModel(app) {

	@Inject lateinit var api: TheMovieDbApi
	internal var accountId: Int? = null

	init {
		Filmtastic.netComponent.inject(this)
	}

	lateinit internal var movie: Movie
	internal var trailers: List<Trailer> = emptyList()

	internal fun fetchMovieTrailers(): Single<List<Trailer>> {
		return api.fetchMovieTrailers(movie.id)
				.map(TrailersResponse::results)
	}

	internal fun addMovieToWatchList(movie: MovieRequest): Completable {
		return api.addMovieToWatchList(movie, accountId!!, sessionId()!!)
	}

	internal fun fetchAccountId(): Single<Int> {
		return api.fetchAccountDetails(sessionId()!!)
				.map(AccountDetailsResponse::id)
	}

	private fun sessionId() = getApplication<Filmtastic>().retrieveSessionId()
}