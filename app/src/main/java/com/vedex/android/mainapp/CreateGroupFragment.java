package com.vedex.android.mainapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.datahelpcomponents.Encoder;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 24.09.2017.
 */

public class CreateGroupFragment extends ConnectionFragment {

    private static final String CREATE_GROUP_URL = "http://194.87.103.130:8081/createGroup";

    EditText mGroupName;
    EditText mGroupDesc;

    Button mCreate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_group, container, false);

        mGroupName = (EditText) rootView.findViewById(R.id.create_group_name);
        mGroupDesc = (EditText) rootView.findViewById(R.id.create_group_desc);
        mCreate = (Button) rootView.findViewById(R.id.apply_create_group);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup(mGroupName.getText().toString(), mGroupDesc.getText().toString());
            }
        });

        return rootView;
    }

    private void createGroup(String name, String desc) {
        if(name == null || name.length() == 0 || desc == null || desc.length() == 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.empty_fields_error), Toast.LENGTH_SHORT);
        } else {
            try {
                JSONObject sendData = new JSONObject();
                sendData.put("name", Encoder.encode(name).replace('+', ' '));
                sendData.put("description", Encoder.encode(desc).replace('+', ' '));

                APIConnection.getInstance().makeRequest(CREATE_GROUP_URL, sendData, CreateGroupFragment.this);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPostConnection(String response) {
        Log.d("TAG", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int errorcode = jsonObject.getInt("errorcode");
            if(errorcode == 0) {
                mGroupDesc.setEnabled(false);
                mGroupName.setEnabled(false);
                mCreate.setEnabled(false);
                Toast.makeText(getActivity(), getResources().getString(R.string.group_successfully_created), Toast.LENGTH_SHORT).show();
            } else {
                String errorString = getResources().getString(R.string.api_error);
                if(errorcode == 6) {
                    errorString = getResources().getString(R.string.group_already_exists);
                }
                Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
