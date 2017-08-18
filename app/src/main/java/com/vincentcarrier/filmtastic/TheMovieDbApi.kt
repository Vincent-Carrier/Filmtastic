package com.vincentcarrier.filmtastic


import com.vincentcarrier.filmtastic.pojos.TopMoviesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface TheMovieDbApi {

	@GET("{sortingMethod}")
	fun fetchTopMoviesResponse(@Path("sortingMethod") sortingMethod: String): Observable<TopMoviesResponse>
}
