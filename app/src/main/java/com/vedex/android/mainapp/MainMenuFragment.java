package com.vedex.android.mainapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 11.09.2017.
 */

public class MainMenuFragment extends ConnectionFragment {

    private String logOutURL = "http://194.87.103.130:8081/logOut";

    private Button mProfile;
    private Button mStartLearning;
    private Button mMyResults;
    private Button mGroups;
    private Button mAbout;
    private Button mExit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        mProfile = (Button) rootView.findViewById(R.id.my_profile);
        mStartLearning = (Button) rootView.findViewById(R.id.start_learning);
        mMyResults = (Button) rootView.findViewById(R.id.my_results);
        mGroups = (Button) rootView.findViewById(R.id.groups);
        mAbout = (Button) rootView.findViewById(R.id.about);
        mExit = (Button) rootView.findViewById(R.id.exit);

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new ProfileFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack("profile").commit();
            }
        });
        mStartLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new SubjectsListFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack("subjects").commit();
            }
        });
        mMyResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new MyResultsFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack("my_results").commit();
            }
        });
        mGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new GroupsListFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack("groups_frag").commit();
            }
        });
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getFragmentManager();
                Fragment fragment = new AboutFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack("about_program").commit();
            }
        });
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject sendData = new JSONObject();
                try {
                    APIConnection.getInstance().makeRequest(logOutURL, sendData, MainMenuFragment.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onPostConnection(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            Log.d("TAG", response);
            if (obj.getString("errorcode").equals("0")) {
                getActivity().finish();
                System.exit(0);
            } else {
                Toast.makeText(getContext(), R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
