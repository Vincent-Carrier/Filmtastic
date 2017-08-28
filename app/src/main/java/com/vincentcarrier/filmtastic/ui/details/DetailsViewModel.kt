package com.vincentcarrier.filmtastic.ui.details

import android.arch.lifecycle.ViewModel
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.Trailer
import com.vincentcarrier.filmtastic.pojos.TrailersResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class DetailsViewModel : ViewModel() {

	@Inject lateinit var theMovieDbApi: TheMovieDbApi
	lateinit var movie: Movie
	var trailers: List<Trailer> = emptyList()

	init {
		App.netComponent.inject(this)
	}

	fun fetchMovieTrailers(): Observable<TrailersResponse> {
		return theMovieDbApi.fetchMovieTrailers(movie.id.toString())
				.observeOn(AndroidSchedulers.mainThread())
	}
}