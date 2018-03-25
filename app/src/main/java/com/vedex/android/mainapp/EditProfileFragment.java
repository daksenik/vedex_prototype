package com.vedex.android.mainapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.dataprocessing.Validator;
import com.vedex.android.mainapp.dataprocessing.datahelpcomponents.Encoder;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 17.09.2017.
 */

public class EditProfileFragment extends ConnectionFragment {

    String updateProfileURL = "http://194.87.103.130:8081/updateAccountInformation";

    EditText mName;
    EditText mSurname;
    EditText mEmail;
    EditText mPassword;
    EditText mNewPassword;
    EditText mConfirmPass;

    Button mSaveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mName = (EditText) rootView.findViewById(R.id.edit_profile_name);
        mSurname = (EditText) rootView.findViewById(R.id.edit_profile_surname);
        mEmail = (EditText) rootView.findViewById(R.id.edit_profile_email);
        mPassword = (EditText) rootView.findViewById(R.id.edit_profile_old_pass);
        mNewPassword = (EditText) rootView.findViewById(R.id.edit_profile_new_password);
        mConfirmPass = (EditText) rootView.findViewById(R.id.edit_profile_new_pass_conf);
        mSaveButton = (Button) rootView.findViewById(R.id.edit_profile_save);

        initFields();

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = User.getInstance();
                String newName;
                String newSurname;
                String newEmail;
                String newPassword = user.getPassword();

                if(mNewPassword.getText().length() != 0) {
                    if(!mNewPassword.getText().toString().equals(mConfirmPass.getText().toString())) {
                        Toast.makeText(getContext(), R.string.wrong_confirmation, Toast.LENGTH_SHORT).show();
                        return;
                    } else if(!mPassword.getText().toString().equals(User.getInstance().getPassword())) {
                        Toast.makeText(getContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
                        return;
                    } else if(!Validator.ValidatePassword(mNewPassword.getText().toString())) {
                        Toast.makeText(getContext(), R.string.bad_password, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        newPassword = mNewPassword.getText().toString();
                    }
                }

                if(Validator.ValidateName(mName.getText().toString())) {
                    newName = mName.getText().toString();
                } else {
                    Toast.makeText(getContext(), R.string.bad_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Validator.ValidateName(mSurname.getText().toString())) {
                    newSurname = mSurname.getText().toString();
                } else {
                    Toast.makeText(getContext(), R.string.bad_surname, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mEmail.getText().toString().length() == 0 ||
                        Validator.ValidateEmail(mEmail.getText().toString())) {
                    newEmail = mEmail.getText().toString();
                } else {
                    Toast.makeText(getContext(), R.string.bad_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(newName.length() == 0 ||
                        newSurname.length() == 0 ||
                        newPassword == null || newPassword.length() == 0) {
                    Toast.makeText(getContext(), R.string.empty_fields_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject sendData = new JSONObject();
                try {
                    sendData.put("name", Encoder.encode(newName));
                    sendData.put("surname", Encoder.encode(newSurname));
                    sendData.put("email", newEmail);
                    sendData.put("new_password", newPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    user.setName(newName);
                    user.setSurname(newSurname);
                    user.setEmail(newEmail);
                    user.setPassword(newPassword);
                    APIConnection.getInstance().makeRequest(updateProfileURL, sendData, EditProfileFragment.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return rootView;
    }


    private void initFields() {
        User user = User.getInstance();

        mName.setText(user.getName());
        mSurname.setText(user.getSurname());
        mEmail.setText(user.getEmail());
    }


    @Override
    public void onPostConnection(String response) {
        Log.d("TAG", response);
        try {
            JSONObject obj = new JSONObject(response);

            if (obj.getString("errorcode").equals("0")) {
                Toast.makeText(getContext(), R.string.profile_updated_ok, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.profile_updated_failed, Toast.LENGTH_SHORT).show();
                Log.e("PROFUPD", obj.getString("errorcode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
