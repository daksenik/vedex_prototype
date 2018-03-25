package com.vedex.android.mainapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.Group;
import com.vedex.android.mainapp.dataprocessing.Validator;
import com.vedex.android.mainapp.dataprocessing.datahelpcomponents.Encoder;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 26.09.2017.
 */

public class EditGroupFragment extends ConnectionFragment {

    public static final String ARG_KEY = "edit_group_group_argument";

    private static final String updateGroupURL = "http://194.87.103.130:8081/editGroup";

    EditText mGroupName;
    EditText mGroupDesc;
    Button mSave;

    Group mGroup;
    String lastSavedName;
    String lastSavedDesc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_edit_group, container, false);

        mGroup = (Group) getArguments().getSerializable(ARG_KEY);

        mGroupName = (EditText) rootView.findViewById(R.id.edit_group_groupname);
        mGroupDesc = (EditText) rootView.findViewById(R.id.edit_group_groupdesc);
        mSave = (Button) rootView.findViewById(R.id.edit_group_save);

        lastSavedName = mGroup.getName();
        lastSavedDesc = mGroup.getDescription();

        mGroupName.setText(mGroup.getName());
        mGroupDesc.setText(mGroup.getDescription());

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEditRequest();
            }
        });

        return rootView;
    }

    private void sendEditRequest() {
        if(!Validator.ValidateGroupName(mGroupName.getText().toString())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.FieldFillingError),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        lastSavedName = mGroupName.getText().toString();
        lastSavedDesc = mGroupDesc.getText().toString().trim();

        JSONObject sendData = new JSONObject();

        try {
            sendData.put("group_id", mGroup.getGroupId());
            sendData.put("new_name", Encoder.encode(mGroupName.getText().toString())
                    .replace('+', ' '));
            sendData.put("new_description", Encoder.encode(mGroupDesc.getText().toString()
                    .trim()).replace('+', ' '));

            mSave.setEnabled(false);
            APIConnection.getInstance().makeRequest(updateGroupURL, sendData,
                    EditGroupFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String response) {
        Log.d("TAG", response);
        try {
            JSONObject responseJSON = new JSONObject(response);
            if(responseJSON.getString("errorcode").equals("0")) {
                mGroup.setName(lastSavedName);
                mGroup.setDescription(lastSavedDesc);
                mSave.setEnabled(true);
                Toast.makeText(getActivity(), getResources().getString(R.string.group_updated_ok),
                        Toast.LENGTH_SHORT).show();
            } else {
                mSave.setEnabled(true);
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.group_updated_failed),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
