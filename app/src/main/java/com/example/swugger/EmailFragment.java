package com.example.swugger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class EmailFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private EmailFragment mTargetFragment;

    public static EmailFragment newInstance() {
        return new EmailFragment();
    }

    public EmailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_email, container, false);


        /* Get a reference to the RecyclerView view(?). */
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.email_recycler_view);

        // Specify and set an adapter
        // for now the adapter just takes an empty list (later we want the emails)
        List<Task> taskList = new ArrayList<>();
        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(), taskList); // was rootView
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Set LayoutManager
        mRecyclerViewLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);

        mTargetFragment = this;

        return rootView;
    }
}