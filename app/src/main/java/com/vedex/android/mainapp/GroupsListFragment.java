package com.vedex.android.mainapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.Invitation;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

/**
 * Created by user on 23.09.2017.
 */

public class GroupsListFragment extends ConnectionFragment {

    ArrayList<Invitation> invitations;

    private final String getInvitationsURL = "http://194.87.103.130:8081/getInvitations";

    TabLayout mTabLayout;
    Button mCreateGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        mTabLayout = (TabLayout) rootView.findViewById(R.id.my_groups_tab_layout);
        mCreateGroup = (Button) rootView.findViewById(R.id.groups_create_button);

        mCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new CreateGroupFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment)
                        .addToBackStack("create_group").commit();
            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().startsWith(getResources().getString(R.string.my_groups))) {
                    showGroups();
                } else {
                    Bundle data = new Bundle();
                    data.putSerializable(InvitationsFragment.ARGS_KEY, invitations);
                    FragmentManager fm = getFragmentManager();
                    Fragment fragment = new InvitationsFragment();
                    fragment.setArguments(data);

                    fm.beginTransaction().replace(R.id.groups_fragment_container, fragment)
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //TODO: update tab
            }
        });

        showGroups();

        loadInvitations();

        return rootView;
    }

    private void showGroups() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = new MyGroupsListFragment();

        fm.beginTransaction().replace(R.id.groups_fragment_container, fragment)
                .commit();
    }

    private void loadInvitations() {
        JSONObject sendData = new JSONObject();
        try {
            APIConnection.getInstance().makeRequest(getInvitationsURL, sendData, GroupsListFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String response) {
        Log.d("TAG", response);
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getInt("errorcode") == 0) {
                invitations = new ArrayList<>();

                JSONArray invitationsArray = jsonObject.getJSONArray("invitations");

                for (int i = 0; i < invitationsArray.length(); i++) {
                    JSONObject invitationObject = invitationsArray.getJSONObject(i);
                    invitations.add(Invitation.getFromJSON(invitationObject));
                }

                mTabLayout.getTabAt(1).setText(getResources().getString(R.string.my_invitations) +
                        " (" + Integer.toString(invitationsArray.length()) + ")");
            } else {
                Toast.makeText(getActivity(), R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
