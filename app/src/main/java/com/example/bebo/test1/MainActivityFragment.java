package com.example.bebo.test1;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    ArrayList<Model> mDataset;
    public static RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int spanCount = 2;
    String baseUrl = "http://api.themoviedb.org/3/movie/";
    String apiKey;
    String url;
    SharedPreferences sharedPrefs;
    String filterOption = new String();
    private boolean category;
    ConnectivityManager connectivityManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mDataset = new ArrayList<>();

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        mLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        mAdapter = new MoviesAdapter(getContext(), mDataset);
        apiKey = getResources().getString(R.string.Api_Key);
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        filterOption = sharedPrefs.getString(
                getString(R.string.pref_filter_key), getString(R.string.pref_most_popular));
        Log.d("My Tag", filterOption);
        if (isInternetAvailable()) {
            loadMovies();
        } else {
            Toast.makeText(getActivity(), "No Network Available", Toast.LENGTH_LONG).show();
        }
        setMovieList();
    }

    public void setMovieList() {

        Realm realm = Realm.getInstance(getContext());
        RealmResults<MovieDetails> movies;
        mDataset.clear();
        if (filterOption.equals(getString(R.string.pref_top_rated))) {
            movies = realm.where(MovieDetails.class).equalTo("category", false).findAll();
        } else if (filterOption.equals(getString(R.string.pref_favourites))) {
            movies = realm.where(MovieDetails.class).equalTo("category", true).equalTo("favourite", true).findAll();
        } else {
            movies = realm.where(MovieDetails.class).equalTo("category", true).findAll();

        }

        for (int i = 0; i < movies.size(); i++) {
            MovieDetails movie = movies.get(i);
            Model model = null;
            try {
                model = new Model(new URL("http://image.tmdb.org/t/p/" + "w500" + movie.getPosterPath()), movie.getId());
            } catch (MalformedURLException e) {
                Log.d("myTag", "sorry sir :(");
            }
            mDataset.add(model);
        }

        mAdapter.notifyDataSetChanged();

    }

    public void secondRequest(final int mId) {

        JsonObjectRequest trailerRequest = new JsonObjectRequest
                (Request.Method.GET, baseUrl + mId + "/videos?api_key=" + apiKey, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            JSONArray array = response.getJSONArray("results");

                            Realm realm = Realm.getInstance(getContext());
                            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    return f.getDeclaringClass().equals(RealmObject.class);
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> clazz) {
                                    return false;
                                }
                            }).create();
                            for (int i = 0; i < array.length(); i++) {

                                realm.beginTransaction();
                                Trailer trailer = gson.fromJson(array.get(i).toString(), Trailer.class);
                                trailer.setmId(mId);
                                realm.copyToRealmOrUpdate(trailer);
                                realm.commitTransaction();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        JsonObjectRequest reviewsRequest = new JsonObjectRequest
                (Request.Method.GET, baseUrl + mId + "/reviews?api_key=" + apiKey, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            JSONArray array = response.getJSONArray("results");

                            Realm realm = Realm.getInstance(getContext());
                            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    return f.getDeclaringClass().equals(RealmObject.class);
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> clazz) {
                                    return false;
                                }
                            }).create();
                            for (int i = 0; i < array.length(); i++) {

                                realm.beginTransaction();
                                Reviews review = gson.fromJson(array.get(i).toString(), Reviews.class);
                                review.setmId(mId);
                                realm.copyToRealmOrUpdate(review);
                                realm.commitTransaction();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(getActivity()).add(trailerRequest);
        Volley.newRequestQueue(getActivity()).add(reviewsRequest);


    }

    public boolean isInternetAvailable() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void loadMovies() {

        if (filterOption.equals(getString(R.string.pref_top_rated))) {
            url = baseUrl + "top_rated?api_key=" + apiKey;
            category = false;
        } else {

            url = baseUrl + "popular?api_key=" + apiKey;
            category = true;
        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            JSONArray array = response.getJSONArray("results");

                            Realm realm = Realm.getInstance(getContext());
                            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    return f.getDeclaringClass().equals(RealmObject.class);
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> clazz) {
                                    return false;
                                }
                            }).create();
                            for (int i = 0; i < array.length(); i++) {

                                realm.beginTransaction();
                                MovieDetails movie = gson.fromJson(array.get(i).toString(), MovieDetails.class);
                                movie.setCategory(category);
                                if(realm.where(MovieDetails.class).equalTo("id", movie.getId()).equalTo("favourite", true).findFirst() != null) {
                                    movie.setFavourite(true);
                                }
                                realm.copyToRealmOrUpdate(movie);
                                realm.commitTransaction();
                                secondRequest((int) movie.getId());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setMovieList();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(getActivity()).add(jsonRequest);

    }


}