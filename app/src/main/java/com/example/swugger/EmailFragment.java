package com.example.swugger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.*;

public class EmailFragment extends Fragment {

    public static EmailFragment newInstance() {
        return new EmailFragment();
    }

    public EmailFragment() {
    }

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private EmailFragment mTargetFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_email, container, false);



        /* RecyclerView stuff */
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.tasks_recycler_view);

        // Specify and set an adapter
        // mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(), mTaskList); // was rootView
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Set LayoutManager
        mRecyclerViewLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);

        mTargetFragment = this;

        return rootView;
    }
}