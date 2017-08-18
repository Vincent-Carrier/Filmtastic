package com.vincentcarrier.filmtastic.pojos

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

data class TopMoviesResponse(val results: List<Movie>)

@PaperParcel
data class Movie(val id: Int,
                 val title: String,
                 val poster_path: String,
                 val release_date: String,
                 val vote_average: Double,
                 val overview: String) : PaperParcelable {
	companion object {
		@JvmField
		val CREATOR = PaperParcelMovie.CREATOR
	}
}