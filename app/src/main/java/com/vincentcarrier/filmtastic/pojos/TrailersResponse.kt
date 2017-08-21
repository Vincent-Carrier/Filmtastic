package com.vincentcarrier.filmtastic.pojos


data class TrailersResponse(val results: List<Trailer>)

data class Trailer(val name: String, val key: String, val site: String)