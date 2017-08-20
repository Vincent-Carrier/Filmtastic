package com.vincentcarrier.filmtastic.ui.details

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.ui.moviegrid.loadImageInto
import kotlinx.android.synthetic.main.activity_details.*

// TODO: Display reviews and comments

class DetailsActivity : AppCompatActivity() {

	lateinit var viewModel: DetailsViewModel

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)
		viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
		viewModel.movie = intent.getParcelableExtra<Movie>("movie")

		loadImageInto(viewModel.movie, detailsPoster)
		detailsTitle.text = viewModel.movie.title
		year.text = viewModel.movie.release_date.substring(0, 4)
		voteAverage.text = "${viewModel.movie.vote_average}/10"
		synopsis.text = viewModel.movie.overview


	}
}

