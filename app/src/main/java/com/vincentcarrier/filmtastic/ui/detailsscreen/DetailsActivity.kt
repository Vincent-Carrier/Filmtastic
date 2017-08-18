package com.vincentcarrier.filmtastic.ui.detailsscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.ui.moviegridscreen.loadImageInto
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

	// TODO: Display reviews and comments
	// TODO: Auto-resize title

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)

		val movie = intent.getParcelableExtra<Movie>("movie")
		loadImageInto(movie, detailsPoster)
		detailsTitle.text = movie.title
		year.text = movie.release_date.substring(0, 4)
		voteAverage.text = "${movie.vote_average}/10"
		synopsis.text = movie.overview
	}
}

