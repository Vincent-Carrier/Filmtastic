package com.vincentcarrier.filmtastic.ui.details

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.ui.loadPoster
import kotlinx.android.synthetic.main.movie_details.view.*
import kotlinx.android.synthetic.main.trailer_list_item.view.*
import org.jetbrains.anko.browse

@EpoxyModelClass(layout = R.layout.trailer_list_item)
abstract class TrailerViewModel : EpoxyModel<ConstraintLayout>() {

	@EpoxyAttribute var name = ""
	@EpoxyAttribute var key = ""

	override fun bind(view: ConstraintLayout) {
		with(view) {
			trailerName.text = name
			setOnClickListener { context.browse("https://www.youtube.com/watch?v=$key") }
		}
	}
}

@EpoxyModelClass(layout = R.layout.movie_details)
abstract class HeaderViewModel : EpoxyModel<ConstraintLayout>() {

	@EpoxyAttribute var title = ""
	@EpoxyAttribute var posterPath = ""
	@EpoxyAttribute var releaseDate = ""
	@EpoxyAttribute var voteAverage = ""
	@EpoxyAttribute var overview = ""

	@SuppressLint("SetTextI18n")
	override fun bind(view: ConstraintLayout) {
		with(view) {
			detailsPoster.loadPoster(posterPath)
			detailsTitle.text = title
			year.text = releaseDate
			score.text = voteAverage
			synopsis.text = overview
//			addToWatchListButton.visibility = if (isLoggedIn()) VISIBLE else GONE
//			addToWatchListButton.setOnClickListener { clickListener }
		}
	}
}