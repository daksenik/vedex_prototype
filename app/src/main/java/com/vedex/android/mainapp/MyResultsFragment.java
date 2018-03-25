package com.vedex.android.mainapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vedex.android.mainapp.androidhelpcomponents.ResultListAdapter;
import com.vedex.android.mainapp.dataprocessing.LessonResult;
import com.vedex.android.mainapp.network.APIConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyResultsFragment extends ConnectionFragment {

    private String url = "http://194.87.103.130:8081/getMyResults";

    RecyclerView mResultListRv;
    EditText searchString;

    RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_my_results, container, false);

        mResultListRv = (RecyclerView) rootView.findViewById(R.id.my_results_rv);
        searchString = (EditText) rootView.findViewById(R.id.my_results_search_string);

        searchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newSearchString = editable.toString();
                ((ResultListAdapter) mAdapter).setSecondaryFilter(newSearchString);
            }
        });

        loadResults();

        return rootView;
    }

    private void loadResults() {
        JSONObject request = new JSONObject();
        APIConnection connection = APIConnection.getInstance();
        try {
            connection.makeRequest(url, request, MyResultsFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostConnection(String response) {
        Log.d("TAG", response);

        try {
            JSONObject obj = new JSONObject(response);

            if (obj.getString("errorcode").equals("0")) {
                JSONArray results = obj.getJSONArray("results");

                ArrayList<LessonResult> resultArrayList = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    resultArrayList.add(LessonResult.getFromJSON(result));
                }

                RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
                mResultListRv.setLayoutManager(lm);
                mAdapter = new ResultListAdapter(resultArrayList, (AppCompatActivity) getActivity());
                mResultListRv.setAdapter(mAdapter);
            } else {
                Toast.makeText(getActivity(), R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
