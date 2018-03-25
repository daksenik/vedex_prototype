package com.vedex.android.mainapp.androidhelpcomponents;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vedex.android.mainapp.R;
import com.vedex.android.mainapp.dataprocessing.Invitation;
import com.vedex.android.mainapp.network.SingleInvitationFragment;

import java.util.ArrayList;

/**
 * Created by user on 24.09.2017.
 */

public class InvitationsListAdapter extends RecyclerView.Adapter<InvitationsListAdapter.ViewHolder> {

    ArrayList<Invitation> invitationsDataSet;

    AppCompatActivity mParentActivity;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView groupName;
        TextView senderName;
        TextView membersCount;
        TextView invitationDate;

        Invitation mInvitation;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SingleInvitationFragment.ARG_KEY, mInvitation);

                    FragmentManager fm = mParentActivity.getSupportFragmentManager();

                    Fragment fragment = new SingleInvitationFragment();
                    fragment.setArguments(bundle);
                    fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("invitationItem").commit();
                }
            });

            groupName = (TextView) itemView.findViewById(R.id.invitation_group_name);
            senderName = (TextView) itemView.findViewById(R.id.invitation_sender);
            membersCount = (TextView) itemView.findViewById(R.id.invitation_group_count);
            invitationDate = (TextView) itemView.findViewById(R.id.invitation_date);
        }

        public void bindInvitation(Invitation invitation) {
            mInvitation = invitation;

            groupName.setText(mInvitation.getGroup().getName());
            senderName.setText(mInvitation.getSenderName());
            membersCount.setText(Integer.toString(mInvitation.getGroup().getNumberOfMembers()));
            invitationDate.setText(mInvitation.getSendingDate());
        }
    }

    public InvitationsListAdapter(ArrayList<Invitation> data, AppCompatActivity parentActivity) {
        invitationsDataSet = data;
        mParentActivity = parentActivity;
    }

    @Override
    public InvitationsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mParentActivity);
        View view = inflater.inflate(R.layout.list_item_invitation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InvitationsListAdapter.ViewHolder holder, int position) {
        Invitation invitation = invitationsDataSet.get(position);
        holder.bindInvitation(invitation);
    }

    @Override
    public int getItemCount() {
        return invitationsDataSet.size();
    }
}
