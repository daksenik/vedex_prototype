package com.vedex.android.mainapp.androidhelpcomponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vedex.android.mainapp.GroupMemberProfileFragment;
import com.vedex.android.mainapp.GroupPageFragment;
import com.vedex.android.mainapp.GroupSubjectsListFragment;
import com.vedex.android.mainapp.R;
import com.vedex.android.mainapp.dataprocessing.Group;
import com.vedex.android.mainapp.dataprocessing.GroupMember;
import com.vedex.android.mainapp.network.DownloadImageTask;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by user on 04.10.2017.
 */

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {

    private ArrayList<GroupMember> srcDataSet;
    private ArrayList<GroupMember> dispDataSet;
    private AppCompatActivity mParentActivity;

    private Group mGroup;

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mPhoto;
        TextView mName;
        TextView mLogin;
        TextView mRole;
        GroupMember mUser;

        ViewHolder(View itemView) {
            super(itemView);

            mPhoto = (ImageView) itemView.findViewById(R.id.group_member_photo);
            mName = (TextView) itemView.findViewById(R.id.group_member_name);
            mLogin = (TextView) itemView.findViewById(R.id.group_member_login);
            mRole = (TextView) itemView.findViewById(R.id.group_member_role);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = mParentActivity.getSupportFragmentManager();
                    Fragment fragment = new GroupMemberProfileFragment();
                    Bundle b = new Bundle();
                    b.putSerializable(GroupMemberProfileFragment.MEMBER_KEY, mUser);
                    b.putInt(GroupMemberProfileFragment.ACCESS_LEVEL_KEY, mGroup.getAccessLevel());
                    b.putSerializable(GroupMemberProfileFragment.GROUP_KEY, mGroup);
                    fragment.setArguments(b);
                    fm.beginTransaction().replace(R.id.fragment_container, fragment).
                            addToBackStack("member_profile").commit();

                }
            });
        }

        void bindMember(GroupMember member) {
            mUser = member;
            mName.setText(mUser.getName() + " " + mUser.getSurname());
            mLogin.setText(mUser.getLogin());
            mRole.setText(mUser.getRole());
            if (mUser.getPhoto() != null && !mUser.getPhoto().equals("null"))
                new DownloadImageTask(mPhoto).execute(mUser.getPhoto());
            else mPhoto.setImageDrawable(mParentActivity
                    .getResources().getDrawable(R.drawable.ic_profile));
        }
    }

    public GroupMembersAdapter(ArrayList<GroupMember> membersArrayList, AppCompatActivity activity, Group mGroup) {
        srcDataSet = membersArrayList;
        dispDataSet = new ArrayList<>();
        for (GroupMember member : srcDataSet) dispDataSet.add(member);
        mParentActivity = activity;
        this.mGroup = mGroup;
    }

    @Override
    public GroupMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mParentActivity);
        View view = layoutInflater.inflate(R.layout.list_item_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupMembersAdapter.ViewHolder holder, int position) {
        GroupMember member = dispDataSet.get(position);
        holder.bindMember(member);
    }

    @Override
    public int getItemCount() {
        return dispDataSet.size();
    }

    public void setPrimaryFilter(String filterString) {

        dispDataSet.clear();
        for (GroupMember member : srcDataSet)
            if ((member.getName() + " " + member.getSurname()).indexOf(filterString) != -1)
                dispDataSet.add(member);

        notifyDataSetChanged();
    }
}
