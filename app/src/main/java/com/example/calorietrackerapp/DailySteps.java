package com.example.calorietrackerapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Calendar;
import java.util.List;

public class DailySteps extends Fragment implements View.OnClickListener {
    View vDailySteps;
    DailyStepsClassDatabase db = null;
    TextView tv;
    EditText et;
    Button btnsave;
    Button btnupdate;
    Button btnsaveanddelete;
    String dailysteps;
    UserTable user;
    ReportTable rep = new ReportTable();
    String date;
    int totalsteps=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDailySteps = inflater.inflate(R.layout.fragment_daily_steps, container, false);

        db = Room.databaseBuilder(this.getActivity().getApplicationContext(), DailyStepsClassDatabase.class, "DailyStepsClassDatabase").fallbackToDestructiveMigration().build();

        user = (UserTable) getActivity().getIntent().getSerializableExtra("UserTable");

        tv = vDailySteps.findViewById(R.id.textViewdailysteps);
        et = vDailySteps.findViewById(R.id.editTextdailysteps);
        btnsave = vDailySteps.findViewById(R.id.buttondailystepssave);
        btnupdate = vDailySteps.findViewById(R.id.buttondailystepsupdate);
        btnsaveanddelete = vDailySteps.findViewById(R.id.buttonsaveanddelete);
        btnsave.setOnClickListener(this);

        ReadDatabase readDatabase = new ReadDatabase();
        readDatabase.execute();

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] details = et.getText().toString().split(" ");
                if (details.length == 2) {

                    if (!(isInteger(details[0], 10))) {
                        Toast.makeText(getActivity().getApplicationContext(), "Entered steps should be number!", Toast.LENGTH_LONG).show();
                    } else {
                        if (!(isInteger(details[1], 10))) {
                            Toast.makeText(getActivity().getApplicationContext(), "Entered steps should be number!", Toast.LENGTH_LONG).show();
                        } else {
                            UpdateDatabase updateDatabase = new UpdateDatabase();
                            updateDatabase.execute();
                            Toast.makeText(getActivity().getApplicationContext(), "Data is updated!", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "To update please enter id and new steps!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnsaveanddelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences spGetCalorie = getActivity().getSharedPreferences("Calorie", Context.MODE_PRIVATE);
                String calorietext = spGetCalorie.getString("calorie", 0 + "");
                int calorieconsumed = Integer.valueOf(calorietext);

                SharedPreferences spDailyGoal = getActivity().getSharedPreferences("MyDailyGoal", Context.MODE_PRIVATE);
                String goaltext = spDailyGoal.getString("goal", 0 + "");
                int goal = Integer.valueOf(goaltext);

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                String smonth;
                String sday;

                if (month < 10) smonth = "0" + (month + 1);
                else smonth = (month + 1) + "";

                if (day < 10) sday = "0" + day;
                else sday = day + "";

                rep.setDate(year + "-" + smonth + "-" + sday + "T00:00:00+10:00");
                rep.setTotalcaloriesconsumed(calorieconsumed);
                rep.setUserid(user.getUserid());
                rep.setDailycaloriegoal(goal);
                rep.setTotalstepstaken(totalsteps);

                ReportCountAsyncTask countAllReport = new ReportCountAsyncTask();
                countAllReport.execute();

                DeleteDatabase deleteDatabase = new DeleteDatabase();
                deleteDatabase.execute();
                Toast.makeText(getActivity().getApplicationContext(), "All data saved and deleted", Toast.LENGTH_LONG).show();
            }
        });

        return vDailySteps;
    }

    @Override
    public void onClick(View v) {
        dailysteps = et.getText().toString();
        if (!(isInteger(dailysteps, 10)))
            Toast.makeText(getActivity().getApplicationContext(), "Entered steps should be number!", Toast.LENGTH_LONG).show();
        else {
            InsertDatabase addDatabase = new InsertDatabase();
            addDatabase.execute();

            Toast.makeText(getActivity().getApplicationContext(), "Your steps are saved!", Toast.LENGTH_LONG).show();
        }
    }


    private class InsertDatabase extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String[] details = et.getText().toString().split(" ");
            if (details.length == 1) {
                Calendar cal = Calendar.getInstance();
                date = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
                DailyStepsClass dsc = new DailyStepsClass(user.getUserid(), Integer.valueOf(details[0]), date);
                long id = db.dailyStepsClassDAO().insert(dsc);
                return (id + " " + details[0]);
            } else return "";

        }

        @Override
        protected void onPostExecute(String details) {
            Toast.makeText(getActivity().getApplicationContext(), "Steps are saved!", Toast.LENGTH_LONG).show();
            ReadDatabase readDatabase = new ReadDatabase();
            readDatabase.execute();
        }
    }

    private class ReadDatabase extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            List<DailyStepsClass> users = db.dailyStepsClassDAO().getAll();
            if (!(users.isEmpty() || users == null)) {
                String allUsers = "";
                for (DailyStepsClass temp : users) {
                    if (temp.getUserid() == user.getUserid()) {
                        String userstr = "id: " + temp.getDscid() + " steps: " + temp.getSteps() + " time: " + temp.getDate() + "\n";
                        allUsers = allUsers + userstr;

                        totalsteps = 0;
                        for (int i = 0; i < users.size(); i++) {
                            totalsteps += users.get(i).getSteps();
                        }

                        SharedPreferences spTotalSteps = getActivity().getSharedPreferences("TotalSteps", Context.MODE_PRIVATE);
                        SharedPreferences.Editor eSteps = spTotalSteps.edit();
                        eSteps.putString("steps", totalsteps + "");
                        eSteps.apply();
                    }
                }
                return allUsers;
            } else return "";
        }

        @Override
        protected void onPostExecute(String details) {
            tv.setText(details);
        }
    }

    private class DeleteDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            db.dailyStepsClassDAO().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            ReadDatabase readDatabase = new ReadDatabase();
            readDatabase.execute();
        }
    }

    private class UpdateDatabase extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            DailyStepsClass dsc = null;
            String[] details = et.getText().toString().split(" ");

            Calendar cal1 = Calendar.getInstance();
            String date1 = cal1.get(Calendar.HOUR_OF_DAY) + ":" + cal1.get(Calendar.MINUTE) + ":" + cal1.get(Calendar.SECOND);
            int id = Integer.parseInt(details[0]);
            dsc = db.dailyStepsClassDAO().findByID(id);
            dsc.setSteps(Integer.parseInt(details[1]));
            dsc.setDate(date1);
            System.out.println(dsc.getUserid());

            if (dsc != null) {
                db.dailyStepsClassDAO().updateUsers(dsc);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String details) {
            ReadDatabase readDatabase = new ReadDatabase();
            readDatabase.execute();
        }

    }

    private class ReportCountAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RESTClient.countAll("caloriepackage.reporttable/count");
        }

        @Override
        protected void onPostExecute(String s) {
            rep.setReportid(Integer.valueOf(s) + 1);

            ReportCalorieBurnedPerStepAsyncTask cbps = new ReportCalorieBurnedPerStepAsyncTask();
            cbps.execute(user.getUserid().toString());
        }
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
            double temp = new Double(s) * rep.getTotalstepstaken();
            rep.setTotalcaloriesburned((int)temp);

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
            double temp = new Double(s);
            rep.setTotalcaloriesburned(rep.getTotalcaloriesburned() + (int)temp);

            PostReportAsyncTask createReport = new PostReportAsyncTask();
            createReport.execute();
        }
    }


    private class PostReportAsyncTask extends AsyncTask<ReportTable, UserTable, String> {
        @Override
        protected String doInBackground(ReportTable... params) {
            RESTClient.createReport(rep, user);
            return "Report is added";
        }

        @Override
        protected void onPostExecute(String response) {

            SharedPreferences spCalorie = getActivity().getSharedPreferences("Calorie", Context.MODE_PRIVATE);
            SharedPreferences.Editor eCalorie = spCalorie.edit();
            eCalorie.putString("calorie", 0 + "");
            eCalorie.apply();

            SharedPreferences spDailyGoal = getActivity().getSharedPreferences("MyDailyGoal", Context.MODE_PRIVATE);
            SharedPreferences.Editor eDailyGoal = spDailyGoal.edit();
            eDailyGoal.putString("goal", 0 +"");
            eDailyGoal.apply();

            SharedPreferences spFoodlist = getActivity().getSharedPreferences("Foodlist", Context.MODE_PRIVATE);
            SharedPreferences.Editor eFoodlist = spFoodlist.edit();
            eFoodlist.putString("foodlist", "");
            eFoodlist.apply();

            SharedPreferences spTotalSteps = getActivity().getSharedPreferences("TotalSteps", Context.MODE_PRIVATE);
            SharedPreferences.Editor eSteps = spTotalSteps.edit();
            eSteps.putString("steps", "");
            eSteps.apply();

        }
    }


    private boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

}

