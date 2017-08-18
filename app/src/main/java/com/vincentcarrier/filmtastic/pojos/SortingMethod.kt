package com.vincentcarrier.filmtastic.pojos


enum class SortingMethod {
	popular, top_rated;

	override fun toString(): String {
		return super.toString().replace('_', ' ')
	}
}
