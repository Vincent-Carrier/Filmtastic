package com.vincentcarrier.filmtastic.ui.moviegrid

import android.content.Intent
import android.support.constraint.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.models.Movie
import com.vincentcarrier.filmtastic.ui.details.DetailsActivity
import com.vincentcarrier.filmtastic.ui.loadPoster
import kotlinx.android.synthetic.main.movie_grid_item.view.*

@EpoxyModelClass(layout = R.layout.movie_grid_item)
abstract class MovieViewModel : EpoxyModel<ConstraintLayout>() {

	@EpoxyAttribute var posterPath = ""
	@EpoxyAttribute var title = ""
	@EpoxyAttribute var movie: Movie? = null

	override fun bind(view: ConstraintLayout) {
		with(view) {
			poster.loadPoster(posterPath)
			poster.contentDescription = title
			setOnClickListener {
				context.startActivity(
						Intent(this.context, DetailsActivity::class.java).putExtra("movie", movie)
				)
			}
		}
	}
}