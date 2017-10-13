package com.vincentcarrier.filmtastic.data


import com.vincentcarrier.filmtastic.models.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

interface TheMovieDbApi {

	@GET("movie/{sort_method}")
	fun fetchMovies(@Path("sort_method") sortMethod: String,
	                @Query("page") page: Int): Observable<MoviesResponse>

	@GET("movie/{movie_id}/videos")
	fun requestMovieTrailers(@Path("movie_id") movieId: Int): Single<TrailersResponse>

	/* To log in to TheMovieDb, you must first request a request token, then navigate to
	* /authenticate/{REQUEST_TOKEN} and authorize the app. Finally, one must call
	*  /authentication/session/new?api_key={API_KEY}&request_token={REQUEST_TOKEN} and
	*  store the session_id you get as a response. Use the Session ID as a parameter
	*  to your API calls to take actions on the user's behalf, like adding a movie
	*  to one of his lists */
	@GET("authentication/token/new")
	fun requestRequestToken(): Single<RequestTokenResponse>

	@GET("authentication/session/new")
	fun requestSessionId(@Query("request_token") requestToken: String): Single<SessionIdResponse>

	@GET("account")
	fun requestAccountDetails(@Query("session_id") sessionId: String): Single<AccountDetailsResponse>

	@Headers("Content-Type: application/json;charset=utf-8")
	@POST("account/{account_id}/watchlist")
	fun postMovieToWatchList(@Body movie: MovieRequest,
	                         @Path("account_id") accountId: Int,
	                         @Query("session_id") sessionId: String): Completable
}
