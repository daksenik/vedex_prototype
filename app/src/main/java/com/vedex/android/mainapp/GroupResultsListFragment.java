package com.vedex.android.mainapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.LessonResult;
import com.vedex.android.mainapp.dataprocessing.User;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupResultsListFragment extends ConnectionFragment{
    private String usersProfileURL = "http://194.87.103.130:8081/usersProfile";
    private RecyclerView mGroupResultsRecyclerView;
    private LessonResultAdapter mAdapter;
    private ArrayList<LessonResult> mResults;
    private ArrayList<Integer> ids;
    private ArrayList<User> mUsers = new ArrayList<>();
    private EditText mSearchString;
    private HashMap<Integer, User> profiles = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_results_list, container, false);
        mGroupResultsRecyclerView = (RecyclerView) view.findViewById(R.id.group_results_recycler_view);
        mGroupResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mResults = (ArrayList<LessonResult>) getArguments().getSerializable("results_list");
        ids = (ArrayList<Integer>) getArguments().getSerializable("ids");
        mUsers = new ArrayList<>();
        updateUI();
        mSearchString = (EditText) view.findViewById(R.id.group_results_search_string);

        mSearchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newSearchString = editable.toString();
                ((LessonResultAdapter) mAdapter).setSecondaryFilter(newSearchString);
            }
        });
        return view;
    }

    private void updateUI() {
        JSONObject sendData = new JSONObject();
        JSONArray array = new JSONArray(ids);
        try {
            sendData.put("user_ids", array);
            APIConnection.getInstance().makeRequest(usersProfileURL, sendData, GroupResultsListFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            Log.d("TAG", response);
            if (obj.getString("errorcode").equals("0")) {
                JSONArray array = obj.getJSONArray("users");
                for (int i = 0; i < array.length(); i++) {

                    JSONObject user = (JSONObject)array.get(i);
                    int id = user.getInt("user_id");
                    String username = user.getString("login");
                    String name = user.getString("name");
                    String surname = user.getString("surname");
                    String email = user.getString("email");
                    String photo = user.getString("photo");
                    User curUser = new User(username, name, surname, email, photo);
                    curUser.setId(id);
                    profiles.put(id, curUser);

                }
                for (int i = 0; i < ids.size(); i++) {
                    mUsers.add(profiles.get(ids.get(i)));
                }
                mAdapter = new LessonResultAdapter();
                mGroupResultsRecyclerView.setAdapter(mAdapter);

            } else {
                Toast.makeText(getContext(), R.string.api_error, Toast.LENGTH_SHORT).show();
                Log.e("subject", obj.getString("errorcode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LessonResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mLessonNameTextView;
        private TextView mRightPercent;
        private TextView mUsername;

        private LessonResult result;
        private User user;

        public LessonResultHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mLessonNameTextView = (TextView) itemView.findViewById(R.id.list_item_result_lesson);
            mRightPercent = (TextView) itemView.findViewById(R.id.list_item_result_right_pcnt);
            mUsername = (TextView) itemView.findViewById(R.id.list_item_result_username);
        }

        public void bindLesson(LessonResult newResult, User newUser) {
            result = newResult;
            user = newUser;
            mLessonNameTextView.setText(result.getLessonName() + " (" + result.getSubjectName() + " "
                    + result.getForm() + ")");
            String rightPercent = getResources().getString(R.string.right_answers);
            int percent = result.getRightPercentage();
            mRightPercent.setText(rightPercent + ": " + String.valueOf(percent) + "% ");
            if (percent < 33)
                mRightPercent.setTextColor(Color.RED);
            else if (percent < 70)
                mRightPercent.setTextColor(Color.parseColor("#f2ca18"));
            else
                mRightPercent.setTextColor(Color.parseColor("#22890d"));
            mUsername.setText(user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")");
        }
        @Override
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            Fragment fragment = new UserResultFragment();
            Bundle b = new Bundle();
            b.putSerializable("Result", result);
            b.putSerializable("User", user);
            fragment.setArguments(b);
            fm.beginTransaction().replace(R.id.fragment_container, fragment).
                    addToBackStack("lessonpage").commit();
        }
    }

    private class LessonResultAdapter extends RecyclerView.Adapter<LessonResultHolder> {
        private ArrayList<LessonResult> results;
        private ArrayList<User> users;
        String secondaryFilter = "";

        public LessonResultAdapter()
        {
            results = new ArrayList<>();
            users = new ArrayList<>();
            for (int i = 0; i < mUsers.size(); i++) {
                results.add(mResults.get(i));
                users.add(mUsers.get(i));
            }
        }

        @Override
        public LessonResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_group_result, parent, false);
            return new LessonResultHolder(view);
        }
        @Override
        public void onBindViewHolder(LessonResultHolder holder, int pos) {
            holder.bindLesson(results.get(pos), users.get(pos));
        }
        @Override
        public int getItemCount() {
            return users.size();
        }

        public void setSecondaryFilter(String filter) {

            secondaryFilter = filter;

            results.clear();
            users.clear();
            for (int i = 0; i < mUsers.size(); i++) {
                boolean pass = true;

                if (mResults.get(i).getLessonName().indexOf(secondaryFilter) == -1)
                    pass = false;

                if (pass) {
                    results.add(mResults.get(i));
                    users.add(mUsers.get(i));
                }
            }

            notifyDataSetChanged();
        }

    }
}

