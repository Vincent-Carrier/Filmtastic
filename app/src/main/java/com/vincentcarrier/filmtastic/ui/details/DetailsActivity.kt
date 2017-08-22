package com.vincentcarrier.filmtastic.ui.details

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.ui.moviegrid.loadImageInto
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.trailer_list_item.view.*

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
		trailerList.adapter = TrailerAdapter()

		viewModel.fetchMovieTrailers().subscribeBy(
				onNext = {
					viewModel.trailers = it.results
					trailerList.adapter.notifyDataSetChanged()
				}
		)
	}

	inner class TrailerAdapter() : RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {
		inner class TrailerViewHolder(itemView: View?) : ViewHolder(itemView)

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
			val view = LayoutInflater.from(parent.context)
					.inflate(R.layout.trailer_list_item, parent, false)
			return TrailerViewHolder(view)
		}

		override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
			val trailer = viewModel.trailers!![position]
			val baseUrl = if (trailer.site == "YouTube") "https://www.youtube.com/watch?v="
			else "https://vimeo.com/230446036"
			holder.itemView.trailerName.text = trailer.name
			holder.itemView.setOnClickListener {
				startActivity(Intent(ACTION_VIEW, Uri.parse(baseUrl + trailer.key)))
			}
		}

		override fun getItemCount(): Int = viewModel.trailers?.size ?: 0
	}
}

