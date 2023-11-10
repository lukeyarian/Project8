package com.example.project8

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var movieTitleEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var movieTitleTextView: TextView
    private lateinit var moviePosterImageView: ImageView
    private lateinit var movieYearTextView: TextView
    private lateinit var movieRatingTextView: TextView
    private lateinit var movieRuntimeTextView: TextView
    private lateinit var movieGenreTextView: TextView
    private lateinit var movieImdbRatingTextView: TextView
    private lateinit var imdbLinkButton: Button
    private lateinit var shareButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        movieTitleEditText = findViewById(R.id.movieTitleEditText)
        searchButton = findViewById(R.id.searchButton)
        movieTitleTextView = findViewById(R.id.movieTitleTextView)
        moviePosterImageView = findViewById(R.id.moviePosterImageView)
        movieYearTextView = findViewById(R.id.movieYearTextView)
        movieRatingTextView = findViewById(R.id.movieRatingTextView)
        movieRuntimeTextView = findViewById(R.id.movieRuntimeTextView)
        movieGenreTextView = findViewById(R.id.movieGenreTextView)
        movieImdbRatingTextView = findViewById(R.id.movieImdbRatingTextView)
        imdbLinkButton = findViewById(R.id.imdbLinkButton)
        shareButton = findViewById(R.id.shareButton)

        searchButton.setOnClickListener {
            val title = movieTitleEditText.text.toString()
            if (title.isNotEmpty()) {
                searchMovie(title)
            }
        }
    }

    private fun searchMovie(title: String) {
        val retrofit = Retrofit.Builder().baseUrl("http://www.omdbapi.com/").addConverterFactory(GsonConverterFactory.create()).build()
        val apiService = retrofit.create(OmdbApi::class.java)

        apiService.searchMovie(title).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    Log.d("MainActivity", "Movie data loaded.")
                    response.body()?.let { movie ->
                        runOnUiThread {
                            updateUI(movie)
                        }
                    }
                } else {
                    Log.e("MainActivity", "Error response: " + response.errorBody()?.string())
                }
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("MainActivity", "API call failed: " + t.message)
            }
        })
    }

    private fun updateUI(movie: MovieResponse) {
        movieTitleTextView.text = movie.title
        movieYearTextView.text = movie.year
        movieRatingTextView.text = movie.rated
        movieRuntimeTextView.text = movie.runtime
        movieGenreTextView.text = movie.genre
        movieImdbRatingTextView.text = movie.imdbRating

        Glide.with(this)
            .load(movie.poster)
            .into(moviePosterImageView)

        imdbLinkButton.setOnClickListener {
            val imdbIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/${movie.imdbID}"))
            startActivity(imdbIntent)
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this movie: ${movie.title} on IMDB: https://www.imdb.com/title/${movie.imdbID}")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_feedback -> {
                sendFeedbackEmail()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendFeedbackEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("lukeyarian0617@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        }
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        } else {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }




}