package com.vedex.android.mainapp.androidhelpcomponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vedex.android.mainapp.GroupPageFragment;
import com.vedex.android.mainapp.R;
import com.vedex.android.mainapp.dataprocessing.Group;

import java.util.ArrayList;

/**
 * Created by user on 25.09.2017.
 */

public class MyGroupListAdapter extends RecyclerView.Adapter<MyGroupListAdapter.ViewHolder> {

    private ArrayList<Group> groupsDataSet;

    private AppCompatActivity mParentActivity;

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView groupTitle;
        private TextView groupRole;
        private TextView groupDesc;

        Group mGroup;

        ViewHolder(View itemView) {
            super(itemView);
            groupTitle = (TextView) itemView.findViewById(R.id.list_item_group_title);
            groupRole = (TextView) itemView.findViewById(R.id.list_item_group_role);
            groupDesc = (TextView) itemView.findViewById(R.id.list_item_group_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putSerializable(GroupPageFragment.ARG_STR, mGroup);
                    FragmentManager fm = mParentActivity.getSupportFragmentManager();
                    Fragment fragment = new GroupPageFragment();
                    fragment.setArguments(args);
                    fm.beginTransaction().replace(R.id.fragment_container, fragment)
                            .addToBackStack("single_group_page").commit();
                }
            });
        }

        void bindGroup(Group group) {
            mGroup = group;

            groupTitle.setText(mGroup.getName());
            groupRole.setText(mGroup.getRoleName());
            groupDesc.setText(mGroup.getDescription()
                    .substring(0, Math.min(200, mGroup.getDescription().length())) +
                    ((mGroup.getDescription().length() > 200) ? "..." : ""));
        }
    }

    public MyGroupListAdapter(ArrayList<Group> data, AppCompatActivity parentActivity) {
        mParentActivity = parentActivity;
        groupsDataSet = data;
    }

    @Override
    public MyGroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mParentActivity);
        View view = layoutInflater.inflate(R.layout.list_item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = groupsDataSet.get(position);
        holder.bindGroup(group);
    }

    public int getItemCount() {
        return groupsDataSet.size();
    }
}
