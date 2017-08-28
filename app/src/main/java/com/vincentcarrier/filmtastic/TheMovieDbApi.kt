package com.vincentcarrier.filmtastic


import com.vincentcarrier.filmtastic.pojos.TopMoviesResponse
import com.vincentcarrier.filmtastic.pojos.TrailersResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbApi {

	@GET("{sortMethod}")
	fun fetchTopMoviesResponse(@Path("sortMethod") sortingMethod: String,
	                           @Query("page") page: Int): Observable<TopMoviesResponse>

	@GET("{movieId}/videos")
	fun fetchMovieTrailers(@Path("movieId") movieId: String): Observable<TrailersResponse>
}
