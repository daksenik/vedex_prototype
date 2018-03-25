package com.vedex.android.mainapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubjectsListFragment extends ConnectionFragment{
    private String getSubjectsURL = "http://194.87.103.130:8081/getAvailableSubjects";
    private RecyclerView mSubjectRecyclerView;
    private SubjectAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subjects_list, container, false);
        mSubjectRecyclerView = (RecyclerView) view.findViewById(R.id.subjects_recycler_view);
        mSubjectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI() {
        User user = User.getInstance();
        JSONObject sendData = new JSONObject();
        try {
            APIConnection.getInstance().makeRequest(getSubjectsURL, sendData, SubjectsListFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getString("errorcode").equals("0")) {
                List<String> subjects = new ArrayList<String>();
                JSONArray array = obj.getJSONArray("subjects");
                for (int i = 0; i < array.length(); i++) subjects.add(array.getString(i));
                mAdapter = new SubjectAdapter(subjects);
                mSubjectRecyclerView.setAdapter(mAdapter);

            } else {
                Toast.makeText(getContext(), R.string.api_error, Toast.LENGTH_SHORT).show();
                Log.e("subject", obj.getString("errorcode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class SubjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mSubjectTextView;
        public SubjectHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mSubjectTextView = (TextView) itemView.findViewById(R.id.subject_text_view);
        }

        public void bindSubject(String name) {
            mSubjectTextView.setText(name);
        }
        @Override
        public void onClick(View v) {
            Bundle b = new Bundle();
            b.putString("SubjectName", ((TextView)v.findViewById(R.id.subject_text_view)).getText().toString());
            FragmentManager fm = getFragmentManager();
            Fragment fragment = new LessonsListFragment();
            fragment.setArguments(b);
            fm.beginTransaction().replace(R.id.fragment_container, fragment).
                    addToBackStack("lessons").commit();
        }
    }

    private class SubjectAdapter extends RecyclerView.Adapter<SubjectHolder> {
        private List<String> mSubjects;
        public SubjectAdapter(List<String> subjects) {mSubjects = subjects; }

        @Override
        public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_subject, parent, false);
            return new SubjectHolder(view);
        }
        @Override
        public void onBindViewHolder(SubjectHolder holder, int pos) {
            holder.bindSubject(mSubjects.get(pos));
        }
        @Override
        public int getItemCount() {
            return mSubjects.size();
        }
    }
}
