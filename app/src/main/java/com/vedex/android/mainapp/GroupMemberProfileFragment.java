package com.vedex.android.mainapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.Group;
import com.vedex.android.mainapp.dataprocessing.GroupMember;
import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.dataprocessing.datahelpcomponents.Encoder;
import com.vedex.android.mainapp.network.APIConnection;
import com.vedex.android.mainapp.network.DownloadImageTask;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONObject;


public class GroupMemberProfileFragment extends ConnectionFragment {

    public static final String ACCESS_LEVEL_KEY = "access_level";
    public static final String MEMBER_KEY = "member";
    public static final String GROUP_KEY = "group";
    public static final String [] ROLES = {"Участник", "Модератор", "Владелец"};

    private final String editMemberRoleURL = "http://194.87.103.130:8081/editMemberRole";

    GroupMember mGroupMember;
    Group mGroup;
    int myAccessLevel;

    ImageView mPhoto;
    TextView mName, mLogin, mEmail, mGroupRole;
    Button mRemoveUser;
    Button mSaveRole;
    BetterSpinner mBetterSpinner;

    LinearLayout editRole;

    boolean userRemoved = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_group_member_profile, container, false);

        mPhoto = (ImageView) rootView.findViewById(R.id.profile_picture);
        mName = (TextView) rootView.findViewById(R.id.profile_user_name_surname);
        mLogin = (TextView) rootView.findViewById(R.id.profile_user_login);
        mEmail = (TextView) rootView.findViewById(R.id.profile_user_email);
        mGroupRole = (TextView) rootView.findViewById(R.id.profile_group_role);
        mRemoveUser = (Button) rootView.findViewById(R.id.kick_user);
        editRole = (LinearLayout) rootView.findViewById(R.id.role_changing_layout);

        mGroupMember = (GroupMember) getArguments().get(MEMBER_KEY);
        myAccessLevel = getArguments().getInt(ACCESS_LEVEL_KEY);
        mGroup = (Group) getArguments().getSerializable(GROUP_KEY);

        mSaveRole = (Button) rootView.findViewById(R.id.save_role);
        mBetterSpinner = (BetterSpinner) rootView.findViewById(R.id.group_roles_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, ROLES);
        mBetterSpinner.setAdapter(arrayAdapter);

        if (myAccessLevel < 2 || (!mGroupMember.getRole().equals("Участник") && myAccessLevel != 3)
                || mGroupMember.getUserId() == User.getInstance().getId())
            mRemoveUser.setVisibility(View.INVISIBLE);
        if (myAccessLevel < 3 || mGroupMember.getRole().equals("Владелец")) {
            editRole.setVisibility(View.INVISIBLE);

        } else {
            int position = arrayAdapter.getPosition(mGroupMember.getRole());
            mBetterSpinner.setText(mGroupMember.getRole());
        }

        new DownloadImageTask(mPhoto).execute(mGroupMember.getPhoto());
        mName.setText(mGroupMember.getName() + " " + mGroupMember.getSurname());
        mLogin.setText(mGroupMember.getLogin());
        if (mGroupMember.getEmail() == null || mGroupMember.getEmail().length() == 0) {
            mEmail.setText(R.string.not_specified_email_alert);
            mEmail.setTextSize(10);
        } else mEmail.setText(mGroupMember.getEmail());
        mGroupRole.setText(mGroupMember.getRole());

        mRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject sendData = new JSONObject();
                userRemoved = true;
                try {
                    sendData.put("group_id", mGroup.getGroupId());
                    sendData.put("user_id", mGroupMember.getUserId());
                    sendData.put("new_role", "");

                    APIConnection.getInstance().makeRequest(editMemberRoleURL, sendData,
                            GroupMemberProfileFragment.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mSaveRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRemoved = false;
                JSONObject sendData = new JSONObject();
                try {
                    sendData.put("group_id", mGroup.getGroupId());
                    sendData.put("user_id", mGroupMember.getUserId());
                    sendData.put("new_role", Encoder.encode(mBetterSpinner.getText().toString()));
                    APIConnection.getInstance().makeRequest(editMemberRoleURL, sendData,
                            GroupMemberProfileFragment.this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onPostConnection(String response) {
        Log.d("TAG", response);
        JSONObject obj = null;
        try {
            obj = new JSONObject(response);
            if (obj.getString("errorcode").equals("0")) {
                if (userRemoved) {
                    mGroup.decreaseMembers();
                    Toast.makeText(getActivity(), R.string.user_removed, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else
                {
                    mGroupRole.setText(mBetterSpinner.getText().toString());
                }

            } else {
                Log.d("errorcode", obj.getString("errorcode"));
                Toast.makeText(getActivity(), R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
