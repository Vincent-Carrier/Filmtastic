package com.vincentcarrier.filmtastic.pojos

import android.os.Parcel
import android.support.annotation.Keep
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@Keep
class TopMoviesResponse(val results: List<Movie>)

@Keep
@PaperParcel
data class Movie(val id: Int,
                 val title: String,
                 @SerializedName("poster_path") val posterPath: String?,
                 @SerializedName("release_date") val releaseDate: String?,
                 @SerializedName("vote_average") val voteAverage: Double?,
                 val overview: String?) : PaperParcelable {
	companion object {
		@JvmField
		val CREATOR = PaperParcelMovie.CREATOR

	}

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) {
		PaperParcelMovie.writeToParcel(this, dest, flags)
	}
}

@Keep
class TrailersResponse(val results: List<Trailer>?)

@Keep
class Trailer(val name: String,
              val key: String)

@Keep
class MovieRequest(@SerializedName("media_type") val mediaType: String = "movie",
                   @SerializedName("media_id") val mediaId: Int,
                   val watchlist: Boolean)

@Keep
class RequestTokenResponse(@SerializedName("request_token") val requestToken: String)

@Keep
class SessionIdResponse(@SerializedName("session_id") val sessionId: String?)

@Keep
class AccountDetailsResponse(val id: Int)