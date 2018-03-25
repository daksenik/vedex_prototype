package com.vedex.android.mainapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vedex.android.mainapp.ConnectionFragment;
import com.vedex.android.mainapp.R;
import com.vedex.android.mainapp.dataprocessing.Lesson;
import com.vedex.android.mainapp.dataprocessing.LessonResult;
import com.vedex.android.mainapp.network.APIConnection;
import com.vedex.android.mainapp.network.DownloadImageTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.Timer;


public class LessonPageFragment extends ConnectionFragment {
    private String url = "http://194.87.103.130:8081/addResult";
    private Button mStartLesson;
    private TextView mLessonName;
    private ImageView mImage;
    private TextView mSubjectName;
    private TextView mDescription;
    private TextView mSuccessRate;
    private TextView mQuestionsNumber;
    private String mPackageName;
    private long startTime;
    private boolean isStarted = false;
    private Lesson mLesson;
    private LessonResult mLessonResult;
    private int mPassageTime;
    private int mRightPercent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_page, container, false);
        mPackageName = getArguments().getString("lessonpackage");
        mLesson = (Lesson) getArguments().getSerializable("lesson");
        mStartLesson = (Button) view.findViewById(R.id.start_lesson_button);
        mLessonName = (TextView) view.findViewById(R.id.lesson_page_lesson_name);
        mSubjectName = (TextView) view.findViewById(R.id.lesson_page_subject);
        mImage = (ImageView) view.findViewById(R.id.lesson_page_lesson_picture);
        mDescription = (TextView) view.findViewById(R.id.lesson_page_lesson_desc);
        mSuccessRate = (TextView) view.findViewById(R.id.lesson_page_lesson_average);
        mQuestionsNumber = (TextView) view.findViewById(R.id.lesson_page_lesson_questions_num);

        mLessonName.setText(mLesson.getName());
        mSubjectName.setText(mLesson.getSubject() + " " + mLesson.getForm());
        mDescription.setText(mLesson.getDescription());
        String successRate = getResources().getString(R.string.success_rate);
        String questionNumber = getResources().getString(R.string.questions_number);
        mSuccessRate.setText(successRate + ": " + mLesson.getSuccessRate() + "%");
        mQuestionsNumber.setText(questionNumber + ": " + mLesson.getQuestionsNumber());

        new DownloadImageTask(mImage).execute(mLesson.getPhoto());

        mStartLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = System.currentTimeMillis();
                Intent launchIntent = getActivity().getPackageManager().
                        getLaunchIntentForPackage(mPackageName);
                if (launchIntent != null) {
                    isStarted = true;
                    startActivity(launchIntent);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isStarted) {
            mPassageTime = (int)(System.currentTimeMillis() - startTime) / 1000;
            mRightPercent = 0;
            JSONObject request = new JSONObject();
            APIConnection connection = APIConnection.getInstance();
            try {
                request.put("lesson_id", mLesson.getLessonId());
                request.put("passage_time", mPassageTime);
                request.put("right_percent", mRightPercent);
                connection.makeRequest(url, request, LessonPageFragment.this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onPostConnection(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getString("errorcode").equals("0")) {
                Date date = new Date();
                String output = DateFormat.format("dd.MM.yyyy HH:mm:ss", date).toString();
                mLessonResult = new LessonResult(mLesson.getSubject(), mLesson.getLessonId(),
                        mLesson.getName(), mLesson.getDescription(), mLesson.getPhoto(), mLesson.getForm(),
                        mLesson.getQuestionsNumber(), mLesson.getSuccessRate(), mPassageTime,
                        mRightPercent, output);
                Bundle b = new Bundle();
                b.putSerializable("Result", mLessonResult);
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new LessonResultFragment();
                fragment.setArguments(b);
                fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("result").commit();
            } else {
                Toast.makeText(getContext(), R.string.api_error, Toast.LENGTH_SHORT).show();
                Log.e("result", obj.getString("errorcode"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }




}
