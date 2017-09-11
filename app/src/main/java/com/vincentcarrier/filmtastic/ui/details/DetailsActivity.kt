package com.vincentcarrier.filmtastic.ui.details

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.vincentcarrier.filmtastic.Filmtastic
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.pojos.MovieRequest
import com.vincentcarrier.filmtastic.ui.execute
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.movie_details.*
import org.jetbrains.anko.AnkoLogger

// TODO: Request the account ID in onStart()
// TODO: Ask the server if the movie is in the watchlist instead of relying on cache
class DetailsActivity : LifecycleActivity(), AnkoLogger {

	lateinit private var vm: DetailsViewModel

	private val clickListener = {
		if (vm.accountId == null) vm.fetchAccountId().execute(this@DetailsActivity) {
			vm.accountId = it
			addMovieToWatchlist()
		} else addMovieToWatchlist()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)
		vm = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
		vm.movie = intent.getParcelableExtra("movie")

		val controller = DetailsController(vm.movie)
		movieDetailsRecyclerView.adapter = controller.adapter

		vm.fetchMovieTrailers().execute(this) {
			vm.trailers = it
			controller.setData(vm.movie, vm.trailers)
		}
	}


	private fun addMovieToWatchlist() {
		vm.addMovieToWatchList(MovieRequest(vm.movie.id)).execute(this) {
			addToWatchListButton.text = getString(string.added)
			addToWatchListButton.alpha = 0.5f
		}
	}

	private fun isLoggedIn() = (application as Filmtastic).isLoggedIn()
}

