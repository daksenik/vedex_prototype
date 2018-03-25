package com.vedex.android.mainapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.Group;
import com.vedex.android.mainapp.dataprocessing.LessonResult;
import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupSubjectsListFragment extends ConnectionFragment{
    //INCORRECT!!!
    private String getResultsURL = "http://194.87.103.130:8081/getResults";
    private RecyclerView mSubjectRecyclerView;
    private SubjectAdapter mAdapter;
    private Group mGroup;
    private ArrayList<LessonResult> mResults;
    private ArrayList<Integer> ids;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_subjects_list, container, false);
        mSubjectRecyclerView = (RecyclerView) view.findViewById(R.id.group_subjects_recycler_view);
        mSubjectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroup = (Group) getArguments().getSerializable("group");
        updateUI();
        return view;
    }

    private void updateUI() {
        User user = User.getInstance();
        JSONObject sendData = new JSONObject();
        try {
            sendData.put("subject", "");
            sendData.put("group_id", mGroup.getGroupId());
            APIConnection.getInstance().makeRequest(getResultsURL, sendData, GroupSubjectsListFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getString("errorcode").equals("0")) {
                ids = new ArrayList<>();
                mResults = new ArrayList<>();
                JSONArray array = obj.getJSONArray("results");
                ArrayList<String> subjects = new ArrayList<>();
                for (int i = 0; i < array.length(); i++)
                {
                    JSONObject result = array.getJSONObject(i);
                    ids.add(result.getInt("user_id"));
                    JSONObject lesson = result.getJSONObject("lesson");

                    String subject = lesson.getString("subject");
                    if (!subjects.contains(subject)) {
                        subjects.add(subject);
                    }
                    int id = lesson.getInt("lesson_id");
                    String name = lesson.getString("name");
                    String desc = lesson.getString("description");
                    String photo = lesson.getString("photo");
                    int form = lesson.getInt("form");
                    int quesNumber = lesson.getInt("questions_number");
                    double successRate = lesson.getDouble("success_rate");
                    int passTime = result.getInt("passage_time");
                    int rightPerc = result.getInt("right_percent");
                    Date date = new Date();
                    String output = DateFormat.format("dd.MM.yyyy HH:mm:ss", date).toString();
                    mResults.add(new LessonResult(subject, id, name, desc, photo, form, quesNumber,
                            successRate, passTime, rightPerc, output));
                }

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
            ArrayList<LessonResult> results = new ArrayList<>();
            ArrayList<Integer> userIds = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                if (mResults.get(i).getSubjectName().equals(mSubjectTextView.getText().toString())) {
                    results.add(mResults.get(i));
                    userIds.add(ids.get(i));
                }
            }
            b.putSerializable("results_list", results);
            b.putSerializable("ids", userIds);
            FragmentManager fm = getFragmentManager();
            Fragment fragment = new GroupResultsListFragment();
            fragment.setArguments(b);
            fm.beginTransaction().replace(R.id.fragment_container, fragment).
                    addToBackStack("group_results").commit();
        }
    }

    private class SubjectAdapter extends RecyclerView.Adapter<SubjectHolder> {
        private ArrayList<String> mSubjects;

        public SubjectAdapter(ArrayList<String> subjects) { mSubjects = subjects; }
        @Override
        public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_group_subject, parent, false);
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
