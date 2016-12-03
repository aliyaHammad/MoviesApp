package com.example.bebo.test1;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A placeholder fragment containing a simple view.
 */

public class DetailsActivityFragment extends Fragment {

    public DetailsActivityFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        final Realm realm = Realm.getInstance(getContext());
        long id = getArguments().getLong("ID");
        final Map<String, String> reviews = new HashMap<String, String>();
        final MovieDetails movieDetailsResults = realm.where(MovieDetails.class).equalTo("id", id).findFirst();
        final Trailer trailer = realm.where(Trailer.class).equalTo("mId", id).findFirst();
        RealmResults<Reviews> reviewsResults = realm.where(Reviews.class).equalTo("mId", id).findAll();

        for (int i = 0; i < reviewsResults.size(); i++) {
            Reviews review = reviewsResults.get(i);
            reviews.put(review.getAuthor(), review.getContent());
        }

        ListView reviewsList = (ListView) v.findViewById(R.id.listView);
        ReviewsAdapter adapter = new ReviewsAdapter(reviews);
        reviewsList.setAdapter(adapter);
        TextView movieTitle = (TextView) v.findViewById(R.id.movieTitle);
        TextView overview = (TextView) v.findViewById(R.id.overview);
        TextView voteAvg = (TextView) v.findViewById(R.id.voteAvg);
        ImageView poster = (ImageView) v.findViewById(R.id.imageView);
        TextView releaseDate = (TextView) v.findViewById(R.id.releaseDate);
        Button trailerButton = (Button) v.findViewById(R.id.trailerButton);
        final Button favouriteButton = (Button) v.findViewById(R.id.favouriteButton);
        movieTitle.setText(movieDetailsResults.getTitle());
        overview.setText(movieDetailsResults.getOverview());
        releaseDate.setText(movieDetailsResults.getReleaseDate());
        voteAvg.setText("Votes=" + movieDetailsResults.getVoteAverage());

        trailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "trailer not found", Toast.LENGTH_SHORT).show();
                }

            }
        });

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 if (movieDetailsResults.isFavourite()) {
                    realm.beginTransaction();
                    movieDetailsResults.setFavourite(false);
                    realm.copyToRealmOrUpdate(movieDetailsResults);
                    realm.commitTransaction();
                    favouriteButton.setBackgroundColor(0xFF2B1616);

                }

                else {
                    realm.beginTransaction();
                    movieDetailsResults.setFavourite(true);
                    realm.copyToRealmOrUpdate(movieDetailsResults);
                    realm.commitTransaction();
                    favouriteButton.setBackgroundColor(Color.YELLOW);
                }

                Log.d("Check", String.valueOf(movieDetailsResults.isFavourite()));

            }
        });
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/" + "w500" + String.valueOf(movieDetailsResults.getPosterPath())).into(poster);
        return v;
    }
}
