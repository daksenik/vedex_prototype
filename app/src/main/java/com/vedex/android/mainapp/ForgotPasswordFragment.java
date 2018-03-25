package com.vedex.android.mainapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ForgotPasswordFragment extends ConnectionFragment {

    private EditText mEmail;
    private Button mSendPassword;
    private TextView mRestorePasswordInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_forgot_password, container, false);
        mEmail = (EditText) view.findViewById(R.id.etEmailForPassword);
        mSendPassword = (Button) view.findViewById(R.id.bSendPassword);
        mRestorePasswordInfo = (TextView) view.findViewById(R.id.tvRestorePassword);
        mSendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mEmail.setVisibility(View.INVISIBLE);
                mSendPassword.setVisibility(View.INVISIBLE);
                mRestorePasswordInfo.setText(R.string.RestorePasswordInfo);
                mRestorePasswordInfo.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }


    @Override
    public void onPostConnection(String response) {
        //TODO
    }
}
