package com.vincentcarrier.filmtastic.ui.details

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.Trailer
import com.vincentcarrier.filmtastic.ui.loadPoster
import kotlinx.android.synthetic.main.movie_details.view.*
import kotlinx.android.synthetic.main.trailer_list_item.view.*
import org.jetbrains.anko.browse

@EpoxyModelClass(layout = R.layout.trailer_list_item)
abstract class TrailerModel(trailer: Trailer) : EpoxyModel<ConstraintLayout>() {

	@EpoxyAttribute var name: String = trailer.name
	@EpoxyAttribute var key: String = trailer.key

	override fun bind(view: ConstraintLayout) {
		with(view) {
			trailerName.text = name
			setOnClickListener { context.browse("https://www.youtube.com/watch?v=$key") }
		}
	}
}

@EpoxyModelClass(layout = R.layout.movie_details)
abstract class DetailsModel(private val movie: Movie) : EpoxyModel<ConstraintLayout>() {
	@SuppressLint("SetTextI18n")
	override fun bind(view: ConstraintLayout) {
		with(view) {
			detailsPoster.loadPoster(movie)
			detailsTitle.text = movie.title
			year.text = movie.releaseDate?.substring(0, 4)
			score.text = "${movie.voteAverage}/10"
			synopsis.text = movie.overview
//			addToWatchListButton.visibility = if (isLoggedIn()) VISIBLE else GONE
//			addToWatchListButton.setOnClickListener { clickListener }
		}
	}
}