package com.vincentcarrier.filmtastic.ui.moviegrid


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleActivity
import com.vincentcarrier.filmtastic.App
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.TheMovieDbApi
import com.vincentcarrier.filmtastic.pojos.*
import com.vincentcarrier.filmtastic.pojos.SortingMethod.popular
import com.vincentcarrier.filmtastic.pojos.SortingMethod.top_rated
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class MovieGridViewModel(app: Application) : AndroidViewModel(app) {

	@Inject lateinit var api: TheMovieDbApi

	init {
		App.netComponent.inject(this)
	}

	internal var hasRequestToken = false
	internal var movies: MutableList<Movie> = mutableListOf()
	internal var pageCount = 0
	internal var sortMethod = SortingMethod.popular
		set(value) {
			field = value
			movies.clear()
			pageCount = 0
		}

	internal fun fetchMovies(): Single<List<Movie>> {
		return api.fetchTopMoviesResponse(sortMethod.name, pageCount + 1)
				.observeOn(AndroidSchedulers.mainThread())
				.map(TopMoviesResponse::results)
	}

	internal fun fetchRequestToken(): Single<String> {
		return api.fetchRequestToken()
				.observeOn(AndroidSchedulers.mainThread())
				.map(RequestTokenResponse::requestToken)
	}

	internal fun fetchSessionId(): Single<String> {
		return api.fetchSessionId()
				.observeOn(AndroidSchedulers.mainThread())
				.map(SessionIdResponse::sessionId)
	}

	internal fun shouldFetchSessionId(): Boolean {
		return (hasRequestToken and (retrieveSessionId() == null))
	}

	internal fun storeSessionId(sessionId: String) {
		context().getSharedPreferences("session id", LifecycleActivity.MODE_PRIVATE)
				.edit().putString("session id", sessionId).apply()
	}

	internal fun retrieveSessionId(): String? {
		return context().getSharedPreferences("session id", LifecycleActivity.MODE_PRIVATE)
				.getString("session id", null)
	}

	fun changeSortMethod() {
		sortMethod = when (sortMethod) {
			popular -> top_rated
			top_rated -> popular
		}
	}

	internal fun getSortMethodMenuTitle(): String {
		return "${context().getString(string.sorted_by)} : ${context().getString(sortMethod.stringResource)}"
	}

	private fun context() = getApplication<App>().applicationContext
}
