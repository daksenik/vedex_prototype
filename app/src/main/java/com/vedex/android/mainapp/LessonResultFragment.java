package com.vedex.android.mainapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vedex.android.mainapp.R;
import com.vedex.android.mainapp.dataprocessing.LessonResult;
import com.vedex.android.mainapp.network.DownloadImageTask;

/**
 * Created by user on 21.09.2017.
 */

public class LessonResultFragment extends Fragment {
    public final static String RESULT_KEY = "Result";

    private TextView mLessonName;
    private TextView mSubjectName;
    private TextView mResultTv;
    private TextView mTimestamp;
    private ImageView mImage;
    private TextView mLessonDesc;
    private TextView mAverage;
    private TextView mQuestionsNumber;

    private LessonResult mResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResult = (LessonResult) getArguments().getSerializable("Result");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lesson_result, container, false);

        mLessonName = (TextView) rootView.findViewById(R.id.result_page_lesson_name);
        mSubjectName = (TextView) rootView.findViewById(R.id.result_page_subject);
        mResultTv = (TextView) rootView.findViewById(R.id.result_page_result);
        mTimestamp = (TextView) rootView.findViewById(R.id.result_page_date);
        mImage = (ImageView) rootView.findViewById(R.id.result_page_lesson_picture);
        mLessonDesc = (TextView) rootView.findViewById(R.id.result_page_lesson_desc);
        mAverage = (TextView) rootView.findViewById(R.id.result_page_lesson_average);
        mQuestionsNumber = (TextView) rootView.findViewById(R.id.result_page_lesson_questions_num);

        fillViews();

        return rootView;
    }

    private void fillViews() {
        mLessonName.setText(mResult.getLessonName());
        mSubjectName.setText(mResult.getSubjectName() + " (" + mResult.getForm() + ")");
        mResultTv.setText(getResources().getString(R.string.right_answers) + " " +
                Integer.toString(mResult.getRightPercentage()) + "%" + " (" +
                mResult.getPassageTime() + " " + getResources().getString(R.string.seconds) + ")");
        mTimestamp.setText(getResources().getString(R.string.result_timestmap) +
                " " + mResult.getRecordDate());
        mLessonDesc.setText(mResult.getLessonDesc());
        mAverage.setText(getResources().getString(R.string.success_rate) + ": " + Double.toString(mResult.getSuccessRate()) + "%");
        mQuestionsNumber.setText(getResources().getString(R.string.questions_number) + ": " + Integer.toString(mResult.getNumberOfQuestions()));

        new DownloadImageTask(mImage).execute(mResult.getLessonPhotoURL());
    }
}
