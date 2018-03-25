package com.vedex.android.mainapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.Validator;
import com.vedex.android.mainapp.dataprocessing.datahelpcomponents.Encoder;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONObject;

public class RegisterFragment extends ConnectionFragment {
    private String url = "http://194.87.103.130:8081/doRegister";
    private EditText mName;
    private EditText mSurname;
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mRegister;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_register, container, false);
        mName = (EditText) view.findViewById(R.id.etName);
        mSurname = (EditText) view.findViewById(R.id.etSurname);
        mUsername = (EditText) view.findViewById(R.id.etUsername);
        mEmail = (EditText) view.findViewById(R.id.etEmail);
        mPassword = (EditText) view.findViewById(R.id.etPassword);
        mConfirmPassword = (EditText) view.findViewById(R.id.etConfirmPassword);
        mRegister = (Button) view.findViewById(R.id.bRegister);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass1 = mPassword.getText().toString();
                String pass2 = mConfirmPassword.getText().toString();
                String name = mName.getText().toString();
                String surname = mSurname.getText().toString();
                String username = mUsername.getText().toString();
                String email = mEmail.getText().toString();
                if (!Validator.ValidateName(name) ||
                    !Validator.ValidateName(surname) ||
                    !Validator.ValidateUsername(username) ||
                    !Validator.ValidateEmail(email) ||
                    !Validator.ValidatePassword(pass1) ||
                    !pass1.equals(pass2))
                {
                    Activity activity = getActivity();
                    Toast.makeText(activity, R.string.FieldFillingError, Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject request = new JSONObject();
                    try {
                        request.put("name", Encoder.encode(name));
                        request.put("surname", Encoder.encode(surname));
                        request.put("login", username);
                        request.put("email", email);
                        request.put("password", pass1);
                        APIConnection connection = APIConnection.getInstance();
                        connection.makeRequest(url, request, RegisterFragment.this);

                    } catch (Exception e) {
                        Log.d("TAG", "Error");
                        e.printStackTrace();
                    }

                }
            }
        });

        return view;
    }

    public void onPostConnection(String response) {
        Log.d("TAG", response);
        JSONObject obj = null;
        try {
            obj = new JSONObject(response);
            if (obj.getString("errorcode").equals("0")) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack("login", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Fragment fragment = new LoginFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            } else {
                Activity activity = getActivity();
                Toast.makeText(activity, R.string.RegistrationError, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
