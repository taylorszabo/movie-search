package com.example.movieapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {
    private String apiKey = "68e7cc33";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });


        ImageView moviePoster = findViewById(R.id.moviePoster);
        TextView movieTitle = findViewById(R.id.movieTitle);
        TextView movieYear = findViewById(R.id.movieYear);
        TextView movieDetails = findViewById(R.id.movieDetails);

        String imdbID = getIntent().getStringExtra("imdbID");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            JSONObject jsonResponse = getJsonObject(imdbID);
            movieTitle.setText(jsonResponse.getString("Title"));
            movieYear.setText(jsonResponse.getString("Year"));
            movieDetails.setText(jsonResponse.getString("Plot"));
            String posterUrl = jsonResponse.getString("Poster");
            Picasso.get().load(posterUrl).into(moviePoster);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private @NonNull JSONObject getJsonObject(String imdbID) throws IOException, JSONException {
        String apiUrl = "https://www.omdbapi.com/?apikey=" + apiKey + "&i=" + imdbID;
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
        return jsonResponse;
    }
}
