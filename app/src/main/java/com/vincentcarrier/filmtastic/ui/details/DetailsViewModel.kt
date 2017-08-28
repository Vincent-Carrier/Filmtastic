package com.vincentcarrier.filmtastic.ui.details

import android.arch.lifecycle.ViewModel
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.Trailer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class DetailsViewModel : ViewModel() {

	@Inject lateinit var theMovieDbApi: TheMovieDbApi
	lateinit var movie: Movie
	var trailers: List<Trailer> = emptyList()

	init {
		App.netComponent.inject(this)
	}

	fun fetchMovieTrailers(): Single<List<Trailer>> {
		return theMovieDbApi.fetchMovieTrailers(movie.id)
				.observeOn(AndroidSchedulers.mainThread())
				.map { (results) -> results }
	}
}