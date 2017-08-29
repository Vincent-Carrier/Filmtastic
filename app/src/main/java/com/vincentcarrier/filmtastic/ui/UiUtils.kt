package com.vincentcarrier.filmtastic.ui

import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.PosterWidth
import com.vincentcarrier.filmtastic.pojos.PosterWidth.XLARGE

internal fun loadImageInto(movie: Movie, imageView: ImageView, posterWidth: PosterWidth = XLARGE) {
	Picasso.with(imageView.context)
			.load("https://image.tmdb.org/t/p/w${posterWidth.width + movie.poster_path}")
			.placeholder(R.drawable.poster_placeholder)
			.into(imageView)
}
