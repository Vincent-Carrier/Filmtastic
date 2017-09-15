package com.vincentcarrier.filmtastic.ui.details

import android.arch.lifecycle.ViewModel
import com.vincentcarrier.filmtastic.models.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread


class DetailsViewModel(private val trailersRepo: TrailersRepository,
                       private val userRepo) : ViewModel() {

	internal var accountId: Int? = null

	lateinit internal var movie: Movie
	internal var trailers: List<Trailer> = emptyList()

	internal fun requestMovieTrailers(): Single<List<Trailer>> {
		return trailerRepo.requestMovieTrailers(movie.id)
				.observeOn(mainThread())
				.map(TrailersResponse::results)
	}

	internal fun addMovieToWatchList(movie: MovieRequest): Completable {
		return trailerRepo.postMovieToWatchList(movie, accountId!!, sessionId()!!)
				.observeOn(mainThread())
	}

	internal fun requestAccountId(): Single<Int> {
		return trailerRepo.requestAccountDetails(sessionId()!!)
				.observeOn(mainThread())
				.map(AccountDetailsResponse::id)
	}

	private fun sessionId() {TODO()}
}