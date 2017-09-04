package com.vincentcarrier.filmtastic.ui.details

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.vincentcarrier.filmtastic.Filmtastic
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.Trailer
import com.vincentcarrier.filmtastic.pojos.TrailersResponse
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class DetailsViewModel(app: Application) : AndroidViewModel(app) {

	@Inject lateinit var api: TheMovieDbApi

	init {
		Filmtastic.netComponent.inject(this)
	}

	lateinit internal var movie: Movie
	internal var trailers: List<Trailer> = emptyList()

	internal fun fetchMovieTrailers(): Single<List<Trailer>> {
		return api.fetchMovieTrailers(movie.id)
				.observeOn(AndroidSchedulers.mainThread())
				.map(TrailersResponse::results)
	}

	internal fun addToWatchList(): Completable {
		return api.createWatchList()
	}
}