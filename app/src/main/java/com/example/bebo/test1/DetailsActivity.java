package com.example.bebo.test1;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DetailsActivityFragment detailsFragment=new DetailsActivityFragment();
        Bundle args = new Bundle();
        args.putLong("ID", getIntent().getExtras().getLong("ID"));
        detailsFragment.setArguments(args);
        transaction.add(R.id.container, detailsFragment);
        transaction.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
