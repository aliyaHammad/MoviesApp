package com.example.bebo.test1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    final private ArrayList<Model> mDataset;
    private Context mContext;
    boolean isTablet;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public ImageView moviePoster;

        public ViewHolder(View v) {
            super(v);
            moviePoster = (ImageView) v.findViewById(R.id.image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoviesAdapter(Context context, ArrayList<Model> myDataset) {
        mDataset = myDataset;
        mContext = context;
        isTablet = mContext.getResources().getBoolean(R.bool.isTablet);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_model, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTablet) {

                    FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                    DetailsActivityFragment detailsFragment = new DetailsActivityFragment();
                    Bundle args = new Bundle();
                    args.putLong("ID",
                            mDataset.get(MainActivityFragment.mRecyclerView.getChildAdapterPosition(v)).getId());
                    detailsFragment.setArguments(args);
                    ft.replace(R.id.details, detailsFragment);
                    ft.commit();
                    Log.d("My Tag", "Done");

                } else {
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra("ID", mDataset.get(MainActivityFragment.mRecyclerView.getChildAdapterPosition(v)).getId());
                    mContext.startActivity(intent);
                }
            }
        });
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Model model = mDataset.get(position);
        Picasso.with(mContext).load((String.valueOf(model.getUrl()))).into(holder.moviePoster);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}