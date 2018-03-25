package com.vedex.android.mainapp;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.androidhelpcomponents.GroupMembersAdapter;
import com.vedex.android.mainapp.dataprocessing.Group;
import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.network.APIConnection;
import com.vedex.android.mainapp.network.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AddMemberListFragment extends ConnectionFragment{
    private String inviteUserURL = "http://194.87.103.130:8081/inviteUser";
    private String userListURL = "http://194.87.103.130:8081/userList";

    private Group mGroup;
    private RecyclerView mAddMemberRecyclerView;
    private HashSet<Integer> mGroupMembersId;
    private AddMemberAdapter mAdapter;
    private ArrayList<User> mUserList = new ArrayList<>();
    private EditText mSearchString;
    private int mTargetId = -1;
    //private ArrayList<User> mUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_member_list, container, false);
        mAddMemberRecyclerView = (RecyclerView) view.findViewById(R.id.add_member_rv);
        mSearchString = (EditText) view.findViewById(R.id.add_member_search_string);
        mAddMemberRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroup = (Group) getArguments().getSerializable(GroupMemberProfileFragment.GROUP_KEY);
        mGroupMembersId = (HashSet<Integer>) getArguments().getSerializable(GroupMembersFragment.MEMBERS_SET_KEY);

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
                ((AddMemberAdapter) mAdapter).setPrimaryFilter(newSearchString);
            }
        });

        updateUI();
        return view;
    }

    private void updateUI() {
        JSONObject sendData = new JSONObject();
        try {
            sendData.put("name", "");
            sendData.put("surname", "");
            sendData.put("login", "");
            APIConnection.getInstance().makeRequest(userListURL, sendData, AddMemberListFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getString("errorcode").equals("0")) {
                if (!obj.has("users")) {
                    if (mGroupMembersId.contains(mTargetId))
                        mGroupMembersId.remove(mTargetId);
                    for (int i = 0; i < mUserList.size(); i++) {
                        if (mUserList.get(i).getId() == mTargetId) {
                            mUserList.remove(i);
                            break;
                        }
                    }
                    mAdapter.modifyList(mUserList);
                    String curSearchString = mSearchString.getText().toString();
                    ((AddMemberAdapter) mAdapter).setPrimaryFilter(curSearchString);
                    Toast.makeText(getContext(), R.string.user_invited, Toast.LENGTH_SHORT).show();
                } else {
                    JSONArray users = obj.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        mUserList.add(User.parseFromJSON(user));
                    }
                    mAdapter = new AddMemberAdapter(mUserList);
                    mAddMemberRecyclerView.setAdapter(mAdapter);
                }

            } else {
                Toast.makeText(getContext(), R.string.api_error, Toast.LENGTH_SHORT).show();
                Log.e("add_member", obj.getString("errorcode"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AddMemberHolder extends RecyclerView.ViewHolder {
        private ImageButton mAddMemberButton;
        private TextView mNameSurname;
        private TextView mLogin;
        private ImageView mPhoto;
        private User mUser;
        public AddMemberHolder(View itemView) {
            super(itemView);
            mNameSurname = (TextView) itemView.findViewById(R.id.add_member_name);
            mLogin = (TextView) itemView.findViewById(R.id.add_member_login);
            mPhoto = (ImageView) itemView.findViewById(R.id.add_member_photo);
            mAddMemberButton = (ImageButton) itemView.findViewById(R.id.add_member_plus_button);
            mAddMemberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject sendData = new JSONObject();
                    try {
                        sendData.put("group_id", mGroup.getGroupId());
                        sendData.put("target_user_id", mUser.getId());
                        mTargetId = mUser.getId();
                        APIConnection.getInstance().makeRequest(inviteUserURL, sendData, AddMemberListFragment.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        }

        public void bindUser(User user) {
            mUser = user;
            mNameSurname.setText(user.getName() + " " + user.getSurname());
            mLogin.setText(user.getUsername());
            if (mUser.getPhoto() != null && !mUser.getPhoto().equals("null"))
                new DownloadImageTask(mPhoto).execute(mUser.getPhoto());
            else mPhoto.setImageDrawable(getActivity()
                    .getResources().getDrawable(R.drawable.ic_profile));
        }



    }

    private class AddMemberAdapter extends RecyclerView.Adapter<AddMemberHolder> {
        private ArrayList<User> mUsers = new ArrayList<>();
        private ArrayList<User> mCurUsers = new ArrayList<>();
        public AddMemberAdapter(ArrayList<User> users)
        {
            for (User user : users) {
                if (!mGroupMembersId.contains(user.getId())) {
                    mUsers.add(user);
                    mCurUsers.add(user);
                }
            }
        }

        public void modifyList(ArrayList<User> users)
        {
            mUsers.clear();
            mCurUsers.clear();
            for (User user : users) {
                if (!mGroupMembersId.contains(user.getId())) {
                    mUsers.add(user);
                    mCurUsers.add(user);
                }
            }
        }

        @Override
        public AddMemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_add_member, parent, false);
            return new AddMemberHolder(view);
        }

        @Override
        public void onBindViewHolder(AddMemberHolder holder, int pos) {
            holder.bindUser(mCurUsers.get(pos));
        }

        @Override
        public int getItemCount() {
            return mCurUsers.size();
        }

        public void setPrimaryFilter(String filterString) {

            mCurUsers.clear();
            for (User user : mUsers)
                if ((user.getName()  + " " + user.getSurname() + " " + user.getUsername()).indexOf(filterString)  != -1)
                    mCurUsers.add(user);

            notifyDataSetChanged();
        }
    }
}
