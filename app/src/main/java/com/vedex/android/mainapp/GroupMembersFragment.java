package com.vedex.android.mainapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.androidhelpcomponents.GroupMembersAdapter;
import com.vedex.android.mainapp.dataprocessing.Group;
import com.vedex.android.mainapp.dataprocessing.GroupMember;
import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by user on 03.10.2017.
 */

public class GroupMembersFragment extends ConnectionFragment {

    public static final String ACCESS_LEVEL_KEY = "access_level";
    public static final String MEMBER_KEY = "member";
    public static final String MEMBERS_SET_KEY = "members_set";
    public static final String GROUP_KEY = "group";
    public static final String ARG_KEY = "group_members_arg";
    private static final String url = "http://194.87.103.130:8081/getGroupMembers";

    RecyclerView.Adapter mAdapter;

    Group mGroup;
    HashSet<Integer> mGroupMembersId = new HashSet<>();

    TextView mMembersCount;
    EditText mSearchString;
    RecyclerView mMemberList;
    Button mAddMember;
    User curUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_group_members, container, false);

        Bundle args = getArguments();
        mGroup = (Group) args.getSerializable(ARG_KEY);

        mSearchString = (EditText) rootView.findViewById(R.id.group_members_search_string);
        mMembersCount = (TextView) rootView.findViewById(R.id.group_members_count);
        mMemberList = (RecyclerView) rootView.findViewById(R.id.group_members_rv);
        mAddMember = (Button) rootView.findViewById(R.id.add_member_button);

        if (mGroup.getAccessLevel() < 2) {
            mAddMember.setVisibility(View.INVISIBLE);
        }
        mMembersCount.setText(getResources().getString(R.string.group_count) + " " +
                Integer.toString(mGroup.getNumberOfMembers()));
        mSearchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newSearchString = s.toString();
                ((GroupMembersAdapter) mAdapter).setPrimaryFilter(newSearchString);
            }
        });

        mAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = new AddMemberListFragment();
                Bundle b = new Bundle();
                b.putInt(GroupMembersFragment.ACCESS_LEVEL_KEY, mGroup.getAccessLevel());
                b.putSerializable(GroupMembersFragment.GROUP_KEY, mGroup);
                b.putSerializable(GroupMembersFragment.MEMBERS_SET_KEY, mGroupMembersId);
                fragment.setArguments(b);
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack("add_member").commit();
            }
        });

        loadMembersList();

        return rootView;
    }


    private void loadMembersList() {
        try {
            JSONObject request = new JSONObject();
            APIConnection connection = APIConnection.getInstance();
            request.put("group_id", Integer.toString(mGroup.getGroupId()));
            connection.makeRequest(url, request, GroupMembersFragment.this);
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
                JSONArray members = obj.getJSONArray("members");

                ArrayList<GroupMember> membersArrayList = new ArrayList<>();

                for (int i = 0; i < members.length(); i++) {
                    JSONObject member = members.getJSONObject(i);
                    membersArrayList.add(GroupMember.parseFromJSON(member));
                    mGroupMembersId.add(membersArrayList.get(membersArrayList.size() - 1).getUserId());
                }

                RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
                mMemberList.setLayoutManager(lm);
                mAdapter = new GroupMembersAdapter(membersArrayList,
                        (AppCompatActivity) getActivity(), mGroup);
                mMemberList.setAdapter(mAdapter);
            } else {
                Toast.makeText(getActivity(), R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
