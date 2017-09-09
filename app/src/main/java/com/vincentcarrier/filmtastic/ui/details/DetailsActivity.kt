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
import com.vincentcarrier.filmtastic.Filmtastic
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.R.string
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.pojos.MovieRequest
import com.vincentcarrier.filmtastic.ui.execute
import com.vincentcarrier.filmtastic.ui.loadPoster
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.trailer_list_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.browse


// TODO: Ask the server if the movie is in the watchlist instead of relying on cache
class DetailsActivity : LifecycleActivity(), AnkoLogger {

	lateinit private var vm: DetailsViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)
		vm = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
		vm.movie = intent.getParcelableExtra("movie")

		setUpDetailsView(vm.movie)

		vm.fetchMovieTrailers().execute(this) {
			vm.trailers = it
			trailerList.adapter.notifyDataSetChanged()
		}
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
			if (vm.accountId == null) vm.fetchAccountId().execute(this) {
				vm.accountId = it
				addMovieToWatchlist()
			} else addMovieToWatchlist()
		}
		trailerList.adapter = TrailerAdapter()
	}

	private fun addMovieToWatchlist() {
		vm.addMovieToWatchList(MovieRequest(vm.movie.id)).execute(this) {
			addToWatchListButton.text = getString(string.added)
			addToWatchListButton.alpha = 0.5f
		}
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
			val BASE_URL = "https://www.youtube.com/watch?v="
			with(holder.itemView) {
				trailerName.text = trailer.name
				setOnClickListener { browse(BASE_URL + trailer.key) }
			}
		}

		override fun getItemCount(): Int = vm.trailers.size
	}
}

