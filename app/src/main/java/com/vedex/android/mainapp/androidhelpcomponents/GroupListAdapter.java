package com.vedex.android.mainapp.androidhelpcomponents;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vedex.android.mainapp.R;
import com.vedex.android.mainapp.dataprocessing.Group;

import java.util.ArrayList;

/**
 * Created by user on 17.09.2017.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    private ArrayList<Group> groupsDataSet;

    private Activity mParentActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView groupTitle;
        private TextView groupRole;
        private TextView groupDesc;

        Group mGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            groupTitle = (TextView) itemView.findViewById(R.id.list_item_group_title);
            groupRole = (TextView) itemView.findViewById(R.id.list_item_group_role);
            groupDesc = (TextView) itemView.findViewById(R.id.list_item_group_desc);
        }

        public void bindGroup(Group group) {
            mGroup = group;
            groupTitle.setText(mGroup.getName());
            groupRole.setText(mGroup.getRoleName());
            groupDesc.setText(mGroup.getDescription().substring(0, Math.min(200, mGroup.getDescription().length())) +
                    ((mGroup.getDescription().length() > 200) ? "..." : ""));
        }
    }

    public GroupListAdapter(ArrayList<Group> data, Activity parentActivity) {
        mParentActivity = parentActivity;
        groupsDataSet = data;
    }

    @Override
    public GroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
