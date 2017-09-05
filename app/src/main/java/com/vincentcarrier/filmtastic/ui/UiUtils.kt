package com.vincentcarrier.filmtastic.ui

import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.PosterWidth.XLARGE

internal fun ImageView.loadPoster(movie: Movie) {
	Picasso.with(this.context)
			.load("https://image.tmdb.org/t/p/w${XLARGE.width + movie.posterPath}")
			.placeholder(R.drawable.poster_placeholder)
			.into(this)
}