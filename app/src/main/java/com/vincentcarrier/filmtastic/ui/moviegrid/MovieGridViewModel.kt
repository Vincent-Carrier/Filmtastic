package com.vincentcarrier.filmtastic.ui.moviegrid


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.SortingMethod
import com.vincentcarrier.filmtastic.pojos.TopMoviesResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class MovieGridViewModel(app: Application) : AndroidViewModel(app) {

	@Inject lateinit var theMovieDbApi: TheMovieDbApi

	init {
		App.netComponent.inject(this)
	}

	internal var sortingMethod = SortingMethod.popular
	internal var movies: List<Movie>? = null

	fun fetchTopMoviesResponse(page: Int = 1): Observable<TopMoviesResponse> {
		return theMovieDbApi.fetchTopMoviesResponse(sortingMethod.name, page.toString())
				.observeOn(AndroidSchedulers.mainThread())
	}
}
