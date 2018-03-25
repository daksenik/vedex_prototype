package com.vedex.android.mainapp.androidhelpcomponents;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vedex.android.mainapp.R;
import com.vedex.android.mainapp.dataprocessing.LessonResult;
import com.vedex.android.mainapp.LessonResultFragment;

import java.util.ArrayList;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {
    private ArrayList<LessonResult> sourceDataSet;
    private ArrayList<LessonResult> primaryFilteredDataSet;
    private ArrayList<LessonResult> resultsDataSet;

    String secondaryFilter = "";
    String primarySubjectFilter = "";
    int primaryFormFilter = -1;

    private AppCompatActivity mParentActivity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView resultLessonName;
        private TextView resultRightPercentage;
        private TextView resultTimestamp;

        LessonResult mResult;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(LessonResultFragment.RESULT_KEY, mResult);

                    FragmentManager fm = mParentActivity.getSupportFragmentManager();

                    Fragment fragment = new LessonResultFragment();
                    fragment.setArguments(bundle);
                    fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("resultItem").commit();
                }
            });

            resultLessonName = (TextView) itemView.findViewById(R.id.list_item_result_lesson);
            resultRightPercentage = (TextView) itemView.findViewById(R.id.list_item_result_right_pcnt);
            resultTimestamp = (TextView) itemView.findViewById(R.id.list_item_result_timestamp);
        }

        public void bindResult(LessonResult result) {
            mResult = result;
            resultLessonName.setText(mResult.getLessonName() + " (" + mResult.getSubjectName() + " " + mResult.getForm() + ")");
            resultTimestamp.setText(mResult.getRecordDate());

            int rightPercentage = mResult.getRightPercentage();
            resultRightPercentage.setText(mParentActivity.getResources().getString(R.string.right_answers) + ": " + Integer.toString(rightPercentage) + "%");
            if (rightPercentage < 33)
                resultRightPercentage.setTextColor(Color.RED);
            else if (rightPercentage < 70)
                resultRightPercentage.setTextColor(Color.parseColor("#f2ca18"));
            else
                resultRightPercentage.setTextColor(Color.parseColor("#22890d"));
        }
    }

    public ResultListAdapter(ArrayList<LessonResult> data, AppCompatActivity parentActivity) {
        mParentActivity = parentActivity;
        sourceDataSet = data;
        primaryFilteredDataSet = new ArrayList<>();
        resultsDataSet = new ArrayList<>();
        for (int i = 0; i < sourceDataSet.size(); i++) {
            resultsDataSet.add(sourceDataSet.get(i));
            primaryFilteredDataSet.add(sourceDataSet.get(i));
        }
    }

    @Override
    public ResultListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mParentActivity);
        View view = layoutInflater.inflate(R.layout.list_item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LessonResult result = resultsDataSet.get(position);
        holder.bindResult(result);
    }

    public int getItemCount() {
        return resultsDataSet.size();
    }

    public void setPrimaryFilter(String subject, int form) {
        primarySubjectFilter = subject;
        primaryFormFilter = form;

        primaryFilteredDataSet.clear();

        for (int i = 0; i < sourceDataSet.size(); i++) {
            boolean pass = true;

            if (!sourceDataSet.get(i).getSubjectName().contains(primarySubjectFilter))
                pass = false;
            if (primaryFormFilter != -1 && sourceDataSet.get(i).getForm() != primaryFormFilter)
                pass = false;

            if (pass)
                primaryFilteredDataSet.add(sourceDataSet.get(i));
        }

        setSecondaryFilter(secondaryFilter);
    }

    public void setSecondaryFilter(String filter) {

        secondaryFilter = filter;

        resultsDataSet.clear();
        for (int i = 0; i < primaryFilteredDataSet.size(); i++) {
            boolean pass = true;

            if (!primaryFilteredDataSet.get(i).getLessonName().contains(secondaryFilter))
                pass = false;

            if (pass)
                resultsDataSet.add(primaryFilteredDataSet.get(i));
        }

        notifyDataSetChanged();
    }
}