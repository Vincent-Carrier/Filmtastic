package com.vincentcarrier.filmtastic.ui.details

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.data.TheMovieDbService
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.movie_details.*
import org.jetbrains.anko.AnkoLogger

class DetailsActivity : AppCompatActivity(), AnkoLogger {

	private val vm: DetailsViewModel by lazy {
		ViewModelProviders.of(this, DetailsVmFactory(TheMovieDbService()))
				.get(DetailsViewModel::class.java)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)
		vm.movie = intent.getParcelableExtra("movie")
		movieDetailsRecyclerView.adapter = vm.adapter()

		requestMovieTrailers()
	}

	private fun requestMovieTrailers() {
		vm.requestMovieTrailers()
				.bindToLifecycle(this)
				.subscribeBy()
	}

	private fun addMovieToWatchlist() {
		vm.addMovieToWatchList()
				.bindToLifecycle<AppCompatActivity>(this)
				.subscribeBy(
						onComplete = {
							addToWatchListButton.text = getString(string.added)
							addToWatchListButton.alpha = 0.5f
						})
	}
}