package com.vincentcarrier.filmtastic.ui

import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.vincentcarrier.filmtastic.GlideApp
import com.vincentcarrier.filmtastic.R

private val posterWidths = setOf("92", "154", "185", "342", "500", "780", "original")

internal fun ImageView.loadPoster(posterPath: String?) {
	val baseUrl = "https://image.tmdb.org/t/p/w"
	GlideApp.with(this.context)
			.load(baseUrl + posterWidths.find { it == "780" } + posterPath)
			.placeholder(R.drawable.poster_placeholder)
			.transition(withCrossFade())
			.into(this)
}