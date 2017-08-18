package com.vincentcarrier.filmtastic.detailsscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.PosterWidth.XLARGE
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)

		val movie = intent.getParcelableExtra<Movie>("movie")
		Picasso.with(this)
				.load("https://image.tmdb.org/t/p/w${XLARGE.width + movie.poster_path}")
				.placeholder(R.drawable.poster_placeholder)
				.into(detailsPoster)
		detailsTitle.text = movie.title
		year.text = movie.release_date.substring(0, 4)
		//		duration.setText();
		voteAverage.text = "${movie.vote_average}/10"
		synopsis.text = movie.overview
	}
}
