package com.vincentcarrier.filmtastic.pojos

import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

class TopMoviesResponse(val results: List<Movie>)

@PaperParcel
data class Movie(val id: Int,
                 val title: String,
                 @SerializedName("poster_path")
                 val posterPath: String?,
                 @SerializedName("release_date")
                 val releaseDate: String?,
                 @SerializedName("vote_average")
                 val voteAverage: Double?,
                 val overview: String?) : PaperParcelable {
	companion object {
		@JvmField
		val CREATOR = PaperParcelMovie.CREATOR
	}
}

class TrailersResponse(val results: List<Trailer>?)

class Trailer(val name: String,
              val key: String,
              val site: String)

class RequestTokenResponse(
		@SerializedName("request_token")
		val requestToken: String
)

class SessionIdResponse(
		@SerializedName("session_id")
		val sessionId: String
)
