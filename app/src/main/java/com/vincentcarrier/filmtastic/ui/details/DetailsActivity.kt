package com.vincentcarrier.filmtastic.ui.details

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.vincentcarrier.filmtastic.R
import com.vincentcarrier.filmtastic.pojos.Movie
import com.vincentcarrier.filmtastic.ui.loadPoster
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.trailer_list_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.browse
import org.jetbrains.anko.toast

class DetailsActivity : LifecycleActivity(), AnkoLogger {

	lateinit private var vm: DetailsViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)
		vm = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
		vm.movie = intent.getParcelableExtra<Movie>("movie")

		setUpDetailsView(vm.movie)

		vm.fetchMovieTrailers()
				.bindToLifecycle(this)
				.subscribeBy(
						onSuccess = {
							vm.trailers = it
							trailerList.adapter.notifyDataSetChanged()
						},
						onError = { toast(it.localizedMessage) }
				)
	}

	@SuppressLint("SetTextI18n")
	private fun setUpDetailsView(movie: Movie) {
		detailsPoster.loadPoster(movie)
		detailsTitle.text = movie.title
		year.text = movie.releaseDate?.substring(0, 4)
		voteAverage.text = "${movie.voteAverage}/10"
		synopsis.text = movie.overview
		trailerList.adapter = TrailerAdapter()
	}

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

