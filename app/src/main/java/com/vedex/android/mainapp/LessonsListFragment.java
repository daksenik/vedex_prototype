package com.vedex.android.mainapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.dataprocessing.Lesson;
import com.vedex.android.mainapp.dataprocessing.datahelpcomponents.Encoder;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LessonsListFragment extends ConnectionFragment{
    private String getLessonsURL = "http://194.87.103.130:8081/getLessons";
    private RecyclerView mLessonRecyclerView;
    private LessonAdapter mAdapter;
    private String subject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lessons_list, container, false);
        mLessonRecyclerView = (RecyclerView) view.findViewById(R.id.lessons_recycler_view);
        mLessonRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        subject = getArguments().getString("SubjectName");
        updateUI();
        return view;
    }

    private void updateUI() {
        JSONObject sendData = new JSONObject();
        try {
            sendData.put("subject_name", Encoder.encode(subject));
            APIConnection.getInstance().makeRequest(getLessonsURL, sendData, LessonsListFragment.this);
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
                List<Lesson> lessons = new ArrayList<Lesson>();
                JSONArray array = obj.getJSONArray("lessons");
                Log.d("TAG", String.valueOf(array.length()));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject temp = array.getJSONObject(i);
                    lessons.add(Lesson.getFromJSON(temp, subject));
                }
                mAdapter = new LessonAdapter(lessons);
                mLessonRecyclerView.setAdapter(mAdapter);

            } else {
                Toast.makeText(getContext(), R.string.api_error, Toast.LENGTH_SHORT).show();
                Log.e("subject", obj.getString("errorcode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LessonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mLessonNameTextView;
        private TextView mSuccessRate;
        private TextView mQuestionNumber;
        private Lesson mLesson;

        public LessonHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mLessonNameTextView = (TextView) itemView.findViewById(R.id.lesson_name_text_view);
            mSuccessRate = (TextView) itemView.findViewById(R.id.success_rate_text_view);
            mQuestionNumber = (TextView) itemView.findViewById(R.id.questions_number_text_view);
        }

        public void bindLesson(Lesson lesson) {
            mLesson = lesson;
            mLessonNameTextView.setText(lesson.getName() + " (" + subject + " " + lesson.getForm() + ")");
            String successRate = getResources().getString(R.string.success_rate);
            String questionsNumber = getResources().getString(R.string.questions_number);
            mSuccessRate.setText(successRate + ": " + String.valueOf(lesson.getSuccessRate()) + "% ");
            mQuestionNumber.setText(questionsNumber + ": " + String.valueOf(lesson.getQuestionsNumber()));
        }
        @Override
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            Fragment fragment = new LessonPageFragment();
            Bundle b = new Bundle();
            if (mLessonNameTextView.getText().toString().equals("Движение частиц в электрическом поле (Физика 10)"))
                b.putString("lessonpackage", "com.VedeX.Electricity"); else
                    if (mLessonNameTextView.getText().toString().equals("Тепловое движение частиц (Физика 8)"))
                        b.putString("lessonpackage", "com.VedeX.WaterHeating"); else
                            if (mLessonNameTextView.getText().toString().equals("Правильные многогранники (Математика 11)"))
                                b.putString("lessonpackage", "com.VedeX.Geometry");
            b.putSerializable("lesson", mLesson);
            fragment.setArguments(b);
            fm.beginTransaction().replace(R.id.fragment_container, fragment).
                    addToBackStack("lessonpage").commit();
        }
    }

    private class LessonAdapter extends RecyclerView.Adapter<LessonHolder> {
        private List<Lesson> mLessons;
        public LessonAdapter(List<Lesson> lessons) { mLessons = lessons; }

        @Override
        public LessonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_lesson, parent, false);
            return new LessonHolder(view);
        }
        @Override
        public void onBindViewHolder(LessonHolder holder, int pos) {
            holder.bindLesson(mLessons.get(pos));
        }
        @Override
        public int getItemCount() {
            return mLessons.size();
        }
    }
}

