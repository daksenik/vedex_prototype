package com.vedex.android.mainapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.dataprocessing.Validator;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONObject;

public class LoginFragment extends ConnectionFragment {
    private String url = "http://194.87.103.130:8081/doLogin";
    private EditText mUsername;
    private EditText mPassword;
    private Button mLogin;
    private TextView mToRegister;
    private TextView mForgotPassword;
    private String username;
    private String password;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        mUsername = (EditText) view.findViewById(R.id.etUsername);
        mPassword = (EditText) view.findViewById(R.id.etPassword);
        mLogin = (Button) view.findViewById(R.id.bLogin);
        mToRegister = (TextView) view.findViewById(R.id.tvToRegister);
        mForgotPassword = (TextView) view.findViewById(R.id.tvForgotPassword);

        mToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new RegisterFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack("login").commit();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();
                if (!Validator.ValidateUsername(username) &&
                        !Validator.ValidateEmail(username) ||
                        !Validator.ValidatePassword(password)) {
                    Activity activity = getActivity();
                    Toast.makeText(activity, R.string.FieldFillingError, Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject request = new JSONObject();
                    try {
                        if (Validator.ValidateUsername(username))
                            request.put("login", username); else
                                request.put("login", "");
                        if (Validator.ValidateEmail(username))
                            request.put("email", username); else
                            request.put("email", "");
                        request.put("password", password);
                        APIConnection connection = APIConnection.getInstance();
                        connection.makeRequest(url, request, LoginFragment.this);

                    } catch (Exception e) {
                        Log.d("TAG", "Error");
                        e.printStackTrace();
                    }
                }
            }
        });
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new ForgotPasswordFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).
                        addToBackStack(null).commit();
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
                User user = User.getInstance(obj.getString("login"));
                user.setId(obj.getInt("id"));
                user.setName(obj.getString("name"));
                user.setSurname(obj.getString("surname"));
                user.setEmail(obj.getString("email"));
                user.setPassword(password);
                user.setPhoto(obj.getString("photo"));
                FragmentActivity activity = getActivity();
                Intent intent = new Intent(activity, MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                activity.finish();
            } else {
                Activity activity = getActivity();
                Log.d("errorcode", obj.getString("errorcode"));
                Toast.makeText(activity, R.string.LoginError, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
