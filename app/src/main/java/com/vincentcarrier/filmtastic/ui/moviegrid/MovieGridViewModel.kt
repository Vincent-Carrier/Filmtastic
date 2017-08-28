package com.vincentcarrier.filmtastic.ui.moviegrid


import android.arch.lifecycle.ViewModel
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.SortingMethod
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class MovieGridViewModel : ViewModel() {

	@Inject lateinit var theMovieDbApi: TheMovieDbApi

	internal var pageCount = 0
	internal var movies: MutableList<Movie> = mutableListOf()
	internal var sortMethod = SortingMethod.popular
		set(value) {
			field = value
			movies.clear()
			pageCount = 0
		}

	init {
		App.netComponent.inject(this)
	}

	fun fetchTopMoviesResponse(): Single<List<Movie>> {
		return theMovieDbApi.fetchTopMoviesResponse(sortMethod.name, pageCount + 1)
				.observeOn(AndroidSchedulers.mainThread())
				.map { (results) -> results }
	}
}
