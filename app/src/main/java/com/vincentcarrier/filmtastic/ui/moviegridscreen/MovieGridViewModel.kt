package com.vincentcarrier.filmtastic.ui.moviegridscreen


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.SortingMethod
import com.vincentcarrier.filmtastic.pojos.TopMoviesResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieGridViewModel(app: Application) : AndroidViewModel(app) {

	@Inject lateinit var theMovieDbApi: TheMovieDbApi

	init {
		App.netComponent.inject(this)
	}

	internal var sortingMethod = SortingMethod.popular

	fun fetchTopMoviesResponse(): Observable<TopMoviesResponse> {
		return theMovieDbApi.fetchTopMoviesResponse(sortingMethod.name)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.newThread())
	}
}
