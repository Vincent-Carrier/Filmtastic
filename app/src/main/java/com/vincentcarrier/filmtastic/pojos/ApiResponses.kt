package com.vincentcarrier.filmtastic.pojos

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

class TopMoviesResponse(val results: List<Movie>)

@PaperParcel
data class Movie(val id: Int,
                 val title: String,
                 val poster_path: String?,
                 val release_date: String?,
                 val vote_average: Double?,
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
		val success: Boolean,
		val request_token: String
)

class SessionIdResponse(
		val success: Boolean,
		val session_id: String
)
