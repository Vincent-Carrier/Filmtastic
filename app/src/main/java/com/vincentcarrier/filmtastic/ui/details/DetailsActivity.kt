package com.vincentcarrier.filmtastic.ui.details

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.data.TrailersManager
import com.vincentcarrier.filmtastic.data.WatchlistManager
import com.vincentcarrier.filmtastic.models.Movie
import com.vincentcarrier.filmtastic.models.Trailer
import com.vincentcarrier.filmtastic.ui.loadPoster
import com.vincentcarrier.filmtastic.ui.subscribeWithLifecycle
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.movie_details.*
import kotlinx.android.synthetic.main.movie_details.view.*
import kotlinx.android.synthetic.main.trailer_list_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.browse

// TODO: Request the account ID in onStart()
// TODO: Ask the server if the movie is in the watchlist instead of relying on cache
class DetailsActivity : AppCompatActivity(), AnkoLogger {

	lateinit private var vm: DetailsViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)
		vm = ViewModelProviders.of(this, DetailsVmFactory(WatchlistManager(),
				TrailersManager())).get(DetailsViewModel::class.java)
		vm.movie = intent.getParcelableExtra("movie")
		movieDetailsRecyclerView.adapter = vm.adapter()

	}


	private fun addMovieToWatchlist() {
		vm.addMovieToWatchList().subscribeWithLifecycle(this) {
			addToWatchListButton.text = getString(string.added)
			addToWatchListButton.alpha = 0.5f
		}
	}
}

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
			detailsPoster.loadPoster(movie.posterPath)
			detailsTitle.text = movie.title
			year.text = movie.releaseDate?.substring(0, 4)
			score.text = "${movie.voteAverage}/10"
			synopsis.text = movie.overview
//			addToWatchListButton.visibility = if (isLoggedIn()) VISIBLE else GONE
//			addToWatchListButton.setOnClickListener { clickListener }
		}
	}
}