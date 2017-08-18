package com.vincentcarrier.filmtastic.pojos

data class TopMoviesResponse(val results: List<Movie>)

data class Movie(val id: Int, val title: String, val poster_path: String)