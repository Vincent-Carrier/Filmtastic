package com.vincentcarrier.filmtastic


import com.vincentcarrier.filmtastic.pojos.RequestTokenResponse
import com.vincentcarrier.filmtastic.pojos.SessionIdResponse
import com.vincentcarrier.filmtastic.pojos.TopMoviesResponse
import com.vincentcarrier.filmtastic.pojos.TrailersResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbApi {

	@GET("movie/{sortMethod}")
	fun fetchTopMoviesResponse(@Path("sortMethod") sortingMethod: String,
	                           @Query("page") page: Int): Single<TopMoviesResponse>

	@GET("movie/{movieId}/videos")
	fun fetchMovieTrailers(@Path("movieId") movieId: Int): Single<TrailersResponse>

	/* To log in to TheMovieDb, you must first fetch a request token, then go to
* /authenticate/{REQUEST_TOKEN} and authorize the app. Finally, one must call
*  /authentication/session/new?api_key={API_KEY}&request_token={REQUEST_TOKEN} and
*  store the session_id you get as a response. Use the Session ID as a parameter
*  to your API calls to take actions on the user's behalf, like adding a movie
*  to one of his lists */
	@GET("authentication/token/new")
	fun fetchRequestToken(): Single<RequestTokenResponse>

	@GET("authentication/session/new")
	fun fetchSessionId(@Query("request_token") requestToken: String): Single<SessionIdResponse>
}
