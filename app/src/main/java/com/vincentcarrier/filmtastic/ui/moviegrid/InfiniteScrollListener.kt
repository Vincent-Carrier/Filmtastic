package com.vincentcarrier.filmtastic.ui.moviegrid

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView


class InfiniteScrollListener(
		private val func: () -> Unit,
		private val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

	private var previousTotal = 0
	private var loading = true
	private val VISIBLE_THRESHOLD = 4
	private var firstVisibleItem = 0
	private var visibleItemCount = 0
	private var totalItemCount = 0

	override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
		super.onScrolled(recyclerView, dx, dy)

		if (dy > 0) {
			visibleItemCount = recyclerView.childCount
			totalItemCount = layoutManager.itemCount
			firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false
					previousTotal = totalItemCount
				}
			}
			if (!loading && (totalItemCount - visibleItemCount)
					<= (firstVisibleItem + VISIBLE_THRESHOLD)) {
				func()
				loading = true
			}
		}
	}
}