package com.vincentcarrier.filmtastic.ui.details

import android.arch.lifecycle.ViewModel
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.TrailersResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class DetailsViewModel : ViewModel() {

	@Inject lateinit var theMovieDbApi: TheMovieDbApi
	lateinit var movie: Movie

	init {
		App.netComponent.inject(this)
	}

	fun fetchMovieTrailers(movieId: Int): Observable<TrailersResponse> {
		return theMovieDbApi.fetchMovieTrailers(movieId.toString())
				.observeOn(AndroidSchedulers.mainThread())
	}
}