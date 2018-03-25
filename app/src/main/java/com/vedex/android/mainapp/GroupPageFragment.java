package com.vedex.android.mainapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.Group;
import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.INVISIBLE;

/**
 * Created by user on 26.09.2017.
 */

public class GroupPageFragment extends ConnectionFragment {

    public static final String ARG_STR = "group_obj";
    private static final String LEAVE_GROUP_URL = "http://194.87.103.130:8081/editMemberRole";
    private static final String REMOVE_GROUP_URL = "http://194.87.103.130:8081/removeGroup";

    Group mGroup;

    TextView mGroupName;
    TextView mGroupDesc;
    TextView mGroupRole;
    TextView mGroupCreationDate;
    Button mGroupResults;
    Button mGroupMembers;
    Button mLeaveOrRemove;
    ImageButton mEditGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mGroup = (Group) getArguments().getSerializable(ARG_STR);

        View rootView = inflater.inflate(R.layout.fragment_single_group, container, false);

        mGroupName = (TextView) rootView.findViewById(R.id.group_page_groupname);
        mGroupDesc = (TextView) rootView.findViewById(R.id.group_page_groupdesc);
        mGroupRole = (TextView) rootView.findViewById(R.id.group_page_role);
        mGroupCreationDate = (TextView) rootView.findViewById(R.id.group_page_creationdate);
        mGroupMembers = (Button) rootView.findViewById(R.id.group_page_membersbutton);
        mGroupResults = (Button) rootView.findViewById(R.id.group_page_results);
        mLeaveOrRemove = (Button) rootView.findViewById(R.id.group_page_leave_or_remove);
        mEditGroup = (ImageButton) rootView.findViewById(R.id.group_page_editgroup);

        mGroupResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("group", mGroup);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = new GroupSubjectsListFragment();
                fragment.setArguments(b);
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack("group_results").commit();
            }
        });

        if (mGroup.getAccessLevel() < 2)
            mEditGroup.setVisibility(INVISIBLE);
        else {
            mEditGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putSerializable(EditGroupFragment.ARG_KEY, mGroup);
                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    Fragment fragment = new EditGroupFragment();
                    fragment.setArguments(args);
                    fm.beginTransaction().replace(R.id.fragment_container, fragment)
                            .addToBackStack("edit_group").commit();
                }
            });
        }

        mGroupCreationDate.setText(mGroup.getCreationDate());
        mGroupRole.setText(mGroup.getRoleName() + " (" +
                Integer.toString(mGroup.getAccessLevel()) + " " +
                getResources().getString(R.string.group_page_access_level) + ")");
        mGroupName.setText(mGroup.getName());
        mGroupDesc.setText(mGroup.getDescription()
                .substring(0, Math.min(800, mGroup.getDescription().length())) +
                (mGroup.getDescription().length() > 800 ? "..." : ""));
        mGroupMembers.setText(getResources().getString(R.string.group_members) +
                " (" + Integer.toString(mGroup.getNumberOfMembers()) + ")");
        mGroupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable(GroupMembersFragment.ARG_KEY, mGroup);
                FragmentManager fm = getActivity().getSupportFragmentManager();

                Fragment fragment = new GroupMembersFragment();
                fragment.setArguments(args);
                fm.beginTransaction().replace(R.id.fragment_container, fragment)
                        .addToBackStack("group_members").commit();
            }
        });
        if (mGroup.getAccessLevel() == 3) {
            mLeaveOrRemove.setText(getResources().getString(R.string.remove_group));
            mLeaveOrRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getResources().getString(R.string.remove_group))
                            .setMessage(getResources().getString(R.string.confirm_remove_group))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        JSONObject sendData = new JSONObject();
                                        sendData.put("group_id", mGroup.getGroupId());
                                        APIConnection.getInstance().makeRequest(
                                                REMOVE_GROUP_URL,
                                                sendData,
                                                GroupPageFragment.this);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
        } else {
            mLeaveOrRemove.setText(getResources().getString(R.string.leave_group));
            mLeaveOrRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getResources().getString(R.string.leave_group))
                            .setMessage(getResources().getString(R.string.confirm_leave_group))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        JSONObject sendData = new JSONObject();
                                        sendData.put("group_id", mGroup.getGroupId());
                                        sendData.put("user_id", User.getInstance().getId());
                                        sendData.put("new_role", "");
                                        APIConnection.getInstance().makeRequest(
                                                LEAVE_GROUP_URL,
                                                sendData,
                                                GroupPageFragment.this);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();

                }
            });
        }

        return rootView;
    }

    @Override
    public void onPostConnection(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getString("errorcode").equals("0")) {
                Toast.makeText(getContext(), R.string.groups_list_updated, Toast.LENGTH_SHORT)
                        .show();
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), R.string.api_error, Toast.LENGTH_SHORT).show();
                Log.e("subject", obj.getString("errorcode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
