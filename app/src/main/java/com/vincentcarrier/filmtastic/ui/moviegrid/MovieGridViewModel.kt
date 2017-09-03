package com.vincentcarrier.filmtastic.ui.moviegrid


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleActivity.MODE_PRIVATE
import com.vincentcarrier.filmtastic.App
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

	internal var movies: MutableList<Movie> = mutableListOf()
	internal var pageCount = 0
	internal var sortMethod = SortingMethod.popular
		set(value) {
			field = value
			movies.clear()
			pageCount = 0
		}
	internal var requestToken: String? = null
	private val PREFS_NAME = "session_id"

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

	internal fun fetchSessionId(): Single<String>? {
		return requestToken?.let {
			api.fetchSessionId(it)
				.observeOn(AndroidSchedulers.mainThread())
				.map(SessionIdResponse::sessionId)
		}
	}

	internal fun isSignedIn(): Boolean = (retrieveSessionId() != null)

	internal fun storeSessionId(sessionId: String) {
		context().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
				.edit().putString(PREFS_NAME, sessionId).apply()
	}

	private fun retrieveSessionId(): String? {
		return context().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
				.getString(PREFS_NAME, null)
	}

	internal fun changeSortMethod() {
		sortMethod = when (sortMethod) {
			popular -> top_rated
			top_rated -> popular
		}
	}

	private fun context() = getApplication<App>().applicationContext
}
