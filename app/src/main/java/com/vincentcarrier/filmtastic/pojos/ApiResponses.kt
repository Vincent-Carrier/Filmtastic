package com.vincentcarrier.filmtastic.pojos

import android.os.Parcel
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

class TopMoviesResponse(@SerializedName("results") val results: List<Movie>)

@PaperParcel
data class Movie(@SerializedName("id") val id: Int,
                 @SerializedName("title") val title: String,
                 @SerializedName("poster_path") val posterPath: String?,
                 @SerializedName("release_date") val releaseDate: String?,
                 @SerializedName("vote_average") val voteAverage: Double?,
                 @SerializedName("overview") val overview: String?) : PaperParcelable {
	companion object {
		@JvmField
		val CREATOR = PaperParcelMovie.CREATOR

	}

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) {
		PaperParcelMovie.writeToParcel(this, dest, flags)
	}
}

class TrailersResponse(@SerializedName("results") val results: List<Trailer>?)

class Trailer(@SerializedName("name") val name: String,
              @SerializedName("key") val key: String)

class MovieRequest(@SerializedName("media_id") val mediaId: Int,
                   @SerializedName("media_type") val mediaType: String = "movie",
                   @SerializedName("watchlist") val watchlist: Boolean = true)

class RequestTokenResponse(@SerializedName("request_token") val requestToken: String)

class SessionIdResponse(@SerializedName("session_id") val sessionId: String?)

class AccountDetailsResponse(@SerializedName("id") val id: Int)