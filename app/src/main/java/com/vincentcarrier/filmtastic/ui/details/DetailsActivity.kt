package com.vincentcarrier.filmtastic.ui.details

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.vincentcarrier.filmtastic.Filmtastic
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.MovieRequest
import com.vincentcarrier.filmtastic.ui.loadPoster
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.trailer_list_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.browse
import org.jetbrains.anko.toast


// TODO: Ask the server if the movie is in the watchlist instead of relying on cache
class DetailsActivity : LifecycleActivity(), AnkoLogger {

	lateinit private var vm: DetailsViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)
		vm = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
		vm.movie = intent.getParcelableExtra("movie")

		setUpDetailsView(vm.movie)

		vm.fetchMovieTrailers()
				.bindToLifecycle(this)
				.subscribeBy(
						onSuccess = {
							vm.trailers = it
							trailerList.adapter.notifyDataSetChanged()
						},
						onError = {
							vm.trailers = emptyList()
							toast(it.localizedMessage)
						}
				)
	}

	@SuppressLint("SetTextI18n")
	private fun setUpDetailsView(movie: Movie) {
		with(movie) {
			detailsPoster.loadPoster(this)
			detailsTitle.text = title
			year.text = releaseDate?.substring(0, 4)
			score.text = "$voteAverage/10"
			synopsis.text = overview
		}
		addToWatchListButton.visibility = if (isLoggedIn()) VISIBLE else GONE
		addToWatchListButton.setOnClickListener {
			if (vm.accountId == null) vm.fetchAccountId()
					.bindToLifecycle(this)
					.subscribeBy(
							onSuccess = {
								vm.accountId = it
								addMovieToWatchlist()
							},
							onError = { toast(it.localizedMessage) }
					)
			else addMovieToWatchlist()
		}
		trailerList.adapter = TrailerAdapter()
	}

	private fun addMovieToWatchlist() {
		vm.addMovieToWatchList(MovieRequest(mediaId = vm.movie.id, watchlist = true))
				.bindToLifecycle<DetailsActivity>(this) // Not sure if this is right
				.subscribeBy(
						onComplete = {
							addToWatchListButton.text = getString(string.added)
							addToWatchListButton.alpha = 0.5f
						},
						onError = { toast(it.localizedMessage) }
				)
	}

	private fun isLoggedIn() = (application as Filmtastic).isLoggedIn()

	inner class TrailerAdapter : RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {
		inner class TrailerViewHolder(itemView: View?) : ViewHolder(itemView)

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
			val view = LayoutInflater.from(parent.context)
					.inflate(R.layout.trailer_list_item, parent, false)
			return TrailerViewHolder(view)
		}

		override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
			val trailer = vm.trailers[position]
			holder.itemView.trailerName.text = trailer.name
			val baseUrl =
					if (trailer.site == "YouTube") "https://www.youtube.com/watch?v="
					else "https://vimeo.com/"
			holder.itemView.setOnClickListener { browse(baseUrl + trailer.key) }
		}

		override fun getItemCount(): Int = vm.trailers.size
	}
}

