package com.vincentcarrier.filmtastic.ui.moviegrid


import android.arch.lifecycle.ViewModel
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.SortingMethod
import com.vincentcarrier.filmtastic.pojos.TopMoviesResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class MovieGridViewModel : ViewModel() {

	@Inject lateinit var theMovieDbApi: TheMovieDbApi
	internal var sortMethod = SortingMethod.popular
	internal var movies: List<Movie> = emptyList()

	init {
		App.netComponent.inject(this)
	}

	fun fetchTopMoviesResponse(page: Int = 1): Observable<TopMoviesResponse> {
		return theMovieDbApi.fetchTopMoviesResponse(sortMethod.name, "$page")
				.observeOn(AndroidSchedulers.mainThread())
	}
}
