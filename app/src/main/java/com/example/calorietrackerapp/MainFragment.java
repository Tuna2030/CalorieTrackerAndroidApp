package com.example.calorietrackerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainFragment extends Fragment implements View.OnClickListener {
    View vMain;
    private ImageView im;
    private TextView tv1;
    private TextView tv;
    private Button bt;
    private EditText et;
    private UserTable user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMain = inflater.inflate(R.layout.fragment_main, container, false);
        im = (ImageView) vMain.findViewById(R.id.imageViewuserhp);
        tv1 = vMain.findViewById(R.id.textViewuserhpdate);
        tv = vMain.findViewById(R.id.textViewuserhpwel);
        et = vMain.findViewById(R.id.editTextCalGoalSet);
        bt = vMain.findViewById(R.id.buttonCalGoalSet);
        bt.setOnClickListener(this);

        int imagesrc = getResources().getIdentifier("@drawable/mynetdiary", null, this.getActivity().getPackageName());
        im.setImageResource(imagesrc);

        Calendar cal = Calendar.getInstance();
        tv1.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));

        user = (UserTable) getActivity().getIntent().getSerializableExtra("UserTable");
        tv.setText("Welcome " + user.getName());

        return vMain;
    }

    @Override
    public void onClick(View v) {
        String goal = et.getText().toString();
        if (!(isInteger(goal, 10)))
            Toast.makeText(this.getActivity().getApplicationContext(), "Calorie goal should be number!", Toast.LENGTH_LONG).show();
        else if (Integer.valueOf(goal) > 10000)
            Toast.makeText(this.getActivity().getApplicationContext(), "Calorie goal should be less than 10000!", Toast.LENGTH_LONG).show();
        else {

            SharedPreferences spDailyGoal = getActivity().getSharedPreferences("MyDailyGoal", Context.MODE_PRIVATE);
            SharedPreferences.Editor eDailyGoal = spDailyGoal.edit();
            eDailyGoal.putString("goal", goal);
            eDailyGoal.apply();

            Toast.makeText(this.getActivity().getApplicationContext(), "Your goal is set", Toast.LENGTH_LONG).show();
        }

    }

    public static boolean isInteger(String s, int radix) {
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
