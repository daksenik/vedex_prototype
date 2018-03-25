package com.vedex.android.mainapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vedex.android.mainapp.androidhelpcomponents.MyGroupListAdapter;
import com.vedex.android.mainapp.dataprocessing.Group;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 25.09.2017.
 */

public class MyGroupsListFragment extends ConnectionFragment {

    private final static String url = "http://194.87.103.130:8081/getGroupList";

    RecyclerView groupsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_group_list, container, false);

        groupsList = (RecyclerView) rootView.findViewById(R.id.my_groups_list);
        loadGroups();

        return rootView;
    }


    private void loadGroups() {
        JSONObject request = new JSONObject();
        APIConnection connection = APIConnection.getInstance();
        try {
            connection.makeRequest(url, request, MyGroupsListFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String response) {
        Log.d("TAGG", response);
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
                groupsList.setLayoutManager(lm);
                RecyclerView.Adapter mAdapter = new MyGroupListAdapter(groupArrayList, (AppCompatActivity) getActivity());
                groupsList.setAdapter(mAdapter);
            } else {
                Toast.makeText(getActivity(), R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
