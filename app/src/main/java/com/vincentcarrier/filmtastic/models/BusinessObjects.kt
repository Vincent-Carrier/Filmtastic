package com.vincentcarrier.filmtastic.models

import android.os.Parcel
import android.support.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.vincentcarrier.filmtastic.R
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

enum class SortMethod(@StringRes val stringResource: Int, val httpName: String) {
	POPULAR(R.string.popular, "popular"), TOP_RATED(R.string.top_rated, "top_rated");
}

@PaperParcel
data class Movie(@SerializedName("id") val id: Int,
                 @SerializedName("title") val title: String,
                 @SerializedName("poster_path") val posterPath: String = "",
                 @SerializedName("release_date") val releaseDate: String = "",
                 @SerializedName("vote_average") val voteAverage: Double = 0.0,
                 @SerializedName("overview") val overview: String = "") : PaperParcelable {
	companion object {
		@JvmField
		val CREATOR = PaperParcelMovie.CREATOR
	}

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) {
		PaperParcelMovie.writeToParcel(this, dest, flags)
	}
}

class Trailer(@SerializedName("name") val name: String,
              @SerializedName("key") val key: String)