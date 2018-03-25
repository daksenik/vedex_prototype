package com.vedex.android.mainapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.androidhelpcomponents.GroupListAdapter;
import com.vedex.android.mainapp.dataprocessing.Group;
import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.network.APIConnection;
import com.vedex.android.mainapp.network.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 16.09.2017.
 */

public class ProfileFragment extends ConnectionFragment {

    private String url = "http://194.87.103.130:8081/getGroupList";

    ImageView mPhoto;
    TextView mName, mLogin, mEmail;
    RecyclerView mGroupListRv;
    Button mEditProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mPhoto = (ImageView) rootView.findViewById(R.id.profile_picture);
        mName = (TextView) rootView.findViewById(R.id.profile_user_name_surname);
        mLogin = (TextView) rootView.findViewById(R.id.profile_user_login);
        mEmail = (TextView) rootView.findViewById(R.id.profile_user_email);
        mGroupListRv = (RecyclerView) rootView.findViewById(R.id.my_groups_rv);
        mEditProfile = (Button) rootView.findViewById(R.id.edit_profile_button);

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new EditProfileFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack("edit_profile").commit();
            }
        });

        User user = User.getInstance();

        new DownloadImageTask(mPhoto).execute(user.getPhoto());
        mName.setText(user.getName() + " " + user.getSurname());
        mLogin.setText(user.getUsername());
        if (user.getEmail() == null || user.getEmail().length() == 0) {
            mEmail.setTextColor(Color.RED);
            mEmail.setText(R.string.no_email_alert);
            mEmail.setTextSize(10);
        } else mEmail.setText(user.getEmail());

        loadGroups();

        return rootView;
    }

    private void loadGroups() {
        JSONObject request = new JSONObject();
        APIConnection connection = APIConnection.getInstance();
        try {
            connection.makeRequest(url, request, ProfileFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String response) {
        Log.d("TAG", response);
        try {
            JSONObject obj = new JSONObject(response);

            if (obj.getString("errorcode").equals("0")) {
                JSONArray groups = obj.getJSONArray("groups");

                ArrayList<Group> groupArrayList = new ArrayList<>();

                for (int i = 0; i < groups.length(); i++) {
                    JSONObject group = groups.getJSONObject(i);
                    groupArrayList.add(Group.getFromJSON(group));
                }

                RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
                mGroupListRv.setLayoutManager(lm);
                RecyclerView.Adapter mAdapter = new GroupListAdapter(groupArrayList, getActivity());
                mGroupListRv.setAdapter(mAdapter);
            } else {
                Toast.makeText(getActivity(), R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
