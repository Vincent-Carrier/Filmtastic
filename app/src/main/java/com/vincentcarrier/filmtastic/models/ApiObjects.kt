package com.vincentcarrier.filmtastic.models

import com.google.gson.annotations.SerializedName

class MoviesResponse(@SerializedName("results") val results: List<Movie>)

class TrailersResponse(@SerializedName("results") val results: List<Trailer>)

class MovieRequest(@SerializedName("media_id") val mediaId: Int,
                   @SerializedName("media_type") val mediaType: String = "movie",
                   @SerializedName("watchlist") val watchlist: Boolean = true)

class RequestTokenResponse(@SerializedName("request_token") val requestToken: String)

class SessionIdResponse(@SerializedName("session_id") val sessionId: String)

class AccountDetailsResponse(@SerializedName("id") val id: Int)