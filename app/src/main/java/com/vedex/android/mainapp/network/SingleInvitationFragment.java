package com.vedex.android.mainapp.network;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.ConnectionFragment;
import com.vedex.android.mainapp.R;
import com.vedex.android.mainapp.dataprocessing.Invitation;

import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by user on 24.09.2017.
 */

public class SingleInvitationFragment extends ConnectionFragment {

    private final static String invitationURL = "http://194.87.103.130:8081/acceptReject";

    public static final String ARG_KEY = "Invitation";

    Invitation mInvitation;

    TextView groupName;
    TextView senderName;
    TextView inviteDate;
    TextView inviteMessage;

    TextView groupDesc;
    TextView groupDate;
    TextView groupCount;

    Button accept;
    Button reject;

    Boolean invitationAccepted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invitation, container, false);

        mInvitation = (Invitation) getArguments().getSerializable(ARG_KEY);

        groupName = (TextView) rootView.findViewById(R.id.single_invitation_groupname);
        senderName = (TextView) rootView.findViewById(R.id.single_invitation_sendername);
        inviteDate = (TextView) rootView.findViewById(R.id.single_invitation_date);
        inviteMessage = (TextView) rootView.findViewById(R.id.single_invitation_message);
        groupDesc = (TextView) rootView.findViewById(R.id.single_invitation_groupdesc);
        groupDate = (TextView) rootView.findViewById(R.id.single_invitation_groupdate);
        groupCount = (TextView) rootView.findViewById(R.id.single_invitation_groupcount);
        accept = (Button) rootView.findViewById(R.id.accept_invitation);
        reject = (Button) rootView.findViewById(R.id.reject_invitation);

        groupName.setText(mInvitation.getGroup().getName());
        senderName.setText(mInvitation.getSenderName() + " (" + mInvitation.getSenderLogin() + ")");
        inviteDate.setText(mInvitation.getSendingDate());
        inviteMessage.setText(mInvitation.getMessage());
        groupDesc.setText(mInvitation.getGroup().getDescription());
        groupDate.setText(mInvitation.getGroup().getCreationDate());
        groupCount.setText(Integer.toString(mInvitation.getGroup().getNumberOfMembers()));

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationAccepted(true);
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationAccepted(false);
            }
        });

        return rootView;
    }

    private void sendInvitationAccepted(boolean isAccepted) {
        accept.setEnabled(false);
        reject.setEnabled(false);
        invitationAccepted = isAccepted;

        JSONObject sendData = new JSONObject();
        try {
            sendData.put("group_id", mInvitation.getGroup().getGroupId());
            sendData.put("is_accepted", isAccepted);

            APIConnection.getInstance().makeRequest(invitationURL, sendData, SingleInvitationFragment.this);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String s) {
        Log.d("TAG", s);
        try {
            JSONObject response = new JSONObject(s);
            if(response.getString("errorcode").equals("0")) {
                Toast.makeText(getActivity(),
                        invitationAccepted ?
                                getResources().getString(R.string.invitation_accepted) :
                                getResources().getString(R.string.invitation_rejected),
                        LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.api_error, Toast.LENGTH_SHORT).show();
                accept.setEnabled(true);
                reject.setEnabled(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
