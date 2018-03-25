package com.vedex.android.mainapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vedex.android.mainapp.androidhelpcomponents.InvitationsListAdapter;
import com.vedex.android.mainapp.dataprocessing.Invitation;

import java.util.ArrayList;

/**
 * Created by user on 24.09.2017.
 */

public class InvitationsFragment extends Fragment {
    public static final String ARGS_KEY = "invitations";

    RecyclerView invitationsList;
    InvitationsListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_invitations_list, container, false);

        invitationsList = (RecyclerView) rootView.findViewById(R.id.my_groups_invitations_rv);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        invitationsList.setLayoutManager(lm);
        mAdapter = new InvitationsListAdapter((ArrayList<Invitation>)getArguments().getSerializable(ARGS_KEY), (AppCompatActivity) getActivity());
        invitationsList.setAdapter(mAdapter);

        return rootView;
    }
}