package com.example.calorietrackerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

public class CalorieTracker extends Fragment implements View.OnClickListener {
    View vDisplayCalorie;
    TextView tv;
    UserTable user;
    int burnedcalorie;
    int steps;
    int goal;
    String foodlist;
    int calorie;
    String info;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayCalorie = inflater.inflate(R.layout.fragment_calorie_tracker, container, false);
        tv = vDisplayCalorie.findViewById(R.id.textViewCalorietracker);
        user = (UserTable) getActivity().getIntent().getSerializableExtra("UserTable");

        SharedPreferences spDailyGoal = getActivity().getSharedPreferences("MyDailyGoal", Context.MODE_PRIVATE);
        String tempgoal = spDailyGoal.getString("goal", 0 + "");
        try {
            goal = Integer.valueOf(tempgoal);
        } catch (Exception e) {
            goal = 0;
        }

        SharedPreferences spTotalSteps = getActivity().getSharedPreferences("TotalSteps", Context.MODE_PRIVATE);
        String tempsteps = spTotalSteps.getString("steps", 0 + "");
        try {
            steps = Integer.valueOf(tempsteps);
        } catch (Exception e) {
            steps = 0;
        }

        SharedPreferences spCalorie = getActivity().getSharedPreferences("Calorie", Context.MODE_PRIVATE);
        String tempcalorie = spCalorie.getString("calorie", "");
        try {
            calorie = Integer.valueOf(tempcalorie);
        } catch (Exception e) {
            calorie = 0;
        }

        SharedPreferences spGatherfood = getActivity().getSharedPreferences("Foodlist", Context.MODE_PRIVATE);
        foodlist = spGatherfood.getString("foodlist", "");

        ReportCalorieBurnedPerStepAsyncTask cbps = new ReportCalorieBurnedPerStepAsyncTask();
        cbps.execute(user.getUserid() + "");

        return vDisplayCalorie;
    }

    @Override
    public void onClick(View v) {

    }

    private class ReportCalorieBurnedPerStepAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                return RESTClient.findCBPS(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            double temp = new Double(s) * steps;
            burnedcalorie = (int) temp;

            ReportCBATAsyncTask checkCBAT = new ReportCBATAsyncTask();
            checkCBAT.execute(user.getUserid().toString());
        }
    }

    private class ReportCBATAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                return RESTClient.findCBAR(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            int temp = new Double(s).intValue();
            burnedcalorie = burnedcalorie + temp;

            info = "Daily Calorie Goal: " + goal + "\n";
            info += "------------------------------------------\n";
            info += "Total Steps Taken: " + steps + "\n";
            info += "------------------------------------------\n";
            info += foodlist;
            info += "\n------------------------------------------\n";
            info += "Total calorie consumed ==> " + calorie + "\n";
            info += "------------------------------------------\n";
            info += "Total calorie burned ====> " + burnedcalorie +"\n";
            info += "\n";
            info += "\n";

            if (goal > calorie)
                info += "You can consume " + (goal - calorie) + " more calorie to achive your goal!";
            else if(goal == calorie) info += "You have exactly consumed calorie equal to your goal!";
            else info += "You are over your goal by " + (calorie - goal) + " calories!";

            tv.setText(info);
        }
    }

}
