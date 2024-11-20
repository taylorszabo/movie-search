package com.example.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieSearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private String apiKey = "68e7cc33";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);

        EditText searchField = findViewById(R.id.searchField);
        Button searchButton = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        searchButton.setOnClickListener(v -> {
            String query = searchField.getText().toString();
            if (!query.isEmpty()) {
                List<Movie> movies = searchMovies(query);
                if (movies != null) {
                    adapter = new MovieAdapter(movies, this::navigateToDetails);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter a movie name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Movie> searchMovies(String query) {
        try {
            JSONArray searchArray = getSearchArray(query);

            if (searchArray == null) return null;

            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < searchArray.length(); i++) {
                JSONObject movieObject = searchArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setTitle(movieObject.getString("Title"));
                movie.setYear(movieObject.getString("Year"));
                movie.setImdbID(movieObject.getString("imdbID"));
                movie.setPoster(movieObject.getString("Poster"));
                movies.add(movie);
            }
            return movies;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private @Nullable JSONArray getSearchArray(String query) throws IOException, JSONException {
        String apiUrl = "https://www.omdbapi.com/?apikey=" + apiKey + "&s=" + query;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray searchArray = jsonResponse.optJSONArray("Search");
        return searchArray;
    }

    private void navigateToDetails(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("imdbID", movie.getImdbID());
        startActivity(intent);
    }
}
