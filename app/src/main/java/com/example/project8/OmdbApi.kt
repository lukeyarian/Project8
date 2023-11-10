package com.example.project8
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {
    @GET("/")
    fun searchMovie(
        @Query("t") title: String,
        @Query("apikey") apiKey: String = "22891dd9"
    ): Call<MovieResponse>
}