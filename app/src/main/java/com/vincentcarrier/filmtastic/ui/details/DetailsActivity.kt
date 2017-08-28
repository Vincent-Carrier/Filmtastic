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
import com.vincentcarrier.filmtastic.ui.loadImageInto
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.trailer_list_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

class DetailsActivity : AppCompatActivity(), AnkoLogger {

	lateinit var vm: DetailsViewModel

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_details)
		vm = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
		vm.movie = intent.getParcelableExtra<Movie>("movie")

		loadImageInto(vm.movie, detailsPoster)
		detailsTitle.text = vm.movie.title
		year.text = vm.movie.release_date?.substring(0, 4)
		voteAverage.text = "${vm.movie.vote_average}/10"
		synopsis.text = vm.movie.overview
		trailerList.adapter = TrailerAdapter()

		vm.fetchMovieTrailers().subscribeBy(
				onSuccess = {
					vm.trailers = it.results
					trailerList.adapter.notifyDataSetChanged()
				},
				onError = {
					debug { it }
				}
		)
	}

	// TODO: Change the RecyclerView to something prettier
	inner class TrailerAdapter : RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {
		inner class TrailerViewHolder(itemView: View?) : ViewHolder(itemView)

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
			val view = LayoutInflater.from(parent.context)
					.inflate(R.layout.trailer_list_item, parent, false)
			return TrailerViewHolder(view)
		}

		override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
			val trailer = vm.trailers[position]
			// This API is stupid and should return the full URL, but no -_-
			val baseUrl = if (trailer.site == "YouTube") "https://www.youtube.com/watch?v="
			else "https://vimeo.com/"
			holder.itemView.trailerName.text = trailer.name
			holder.itemView.setOnClickListener {
				startActivity(Intent(ACTION_VIEW, Uri.parse(baseUrl + trailer.key)))
			}
		}

		override fun getItemCount(): Int = vm.trailers.size
	}
}

