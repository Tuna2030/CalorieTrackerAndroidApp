package com.example.calorietrackerapp;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS;

public class ReportScreen extends Fragment implements View.OnClickListener {
    View vDisplayReport;
    TextView tvDate1;
    TextView tvDate2;
    private DatePickerDialog.OnDateSetListener mDateSetListener1, mDateSetListener2;
    Button btnpiechart;
    Button btnbarchart;
    ArrayList<ReportTable> reparray = new ArrayList<>();
    UserTable user = new UserTable();
    float[] yData = {1, 2, 3};
    String[] xData = {"Total Calories Consumed", "Total Calories Burned", "Remaining Calorie"};
    PieChart pieChart;
    BarChart barChart;
    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    Date date1, date2;
    String sdate1, sdate2;
    int count = 0;
    int daysInBetween;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayReport = inflater.inflate(R.layout.fragment_report_screen, container, false);
        tvDate1 = vDisplayReport.findViewById(R.id.textViewReportDate1);
        tvDate2 = vDisplayReport.findViewById(R.id.textViewReportDate2);
        btnbarchart = vDisplayReport.findViewById(R.id.buttonBarChart);
        btnpiechart = vDisplayReport.findViewById(R.id.buttonPieChart);
        pieChart = vDisplayReport.findViewById(R.id.piechart);
        barChart = (BarChart) vDisplayReport.findViewById(R.id.barchart);
        user = (UserTable) getActivity().getIntent().getSerializableExtra("UserTable");


        tvDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(vDisplayReport.getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener1, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;

                String smonth;
                String sday;
                if (month < 10) smonth = "0" + month;
                else smonth = month + "";

                if (dayOfMonth < 10) sday = "0" + dayOfMonth;
                else sday = dayOfMonth + "";

                sdate1 = sday + "/" + smonth + "/" + year;

                try {
                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sdate1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                tvDate1.setText(sdate1);

                reparray.add(new ReportTable());
                reparray.get(0).setDate(year + "-" + smonth + "-" + sday);

                count = 0;
                daysInBetween = 1;
                GetReportAsyncTask getrep = new GetReportAsyncTask();
                getrep.execute(reparray.get(0).getDate());
            }
        };

        tvDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(vDisplayReport.getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener2, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                reparray.clear();
                month += 1;

                String smonth;
                String sday;
                if (month < 10) smonth = "0" + month;
                else smonth = month + "";

                if (dayOfMonth < 10) sday = "0" + dayOfMonth;
                else sday = dayOfMonth + "";

                sdate2 = sday + "/" + smonth + "/" + year;

                try {
                    date2 = new SimpleDateFormat("dd/MM/yyyy").parse(sdate2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long timeDifference = date1.getTime() - date2.getTime();
                daysInBetween = (int) (-(timeDifference / (24 * 60 * 60 * 1000))) + 1;

                for (int i = 0; i < daysInBetween; i++) {
                    reparray.add(new ReportTable());
                }

                GregorianCalendar cal = new GregorianCalendar();

                for (int i = 0; i < daysInBetween; i++) {
                    cal.setTime(date1);
                    cal.add(Calendar.DATE, i);
                    reparray.get(i).setDate(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
                }

                tvDate2.setText(sdate2);

                count = 0;
                GetReportAsyncTask getrep = new GetReportAsyncTask();
                getrep.execute(reparray.get(0).getDate());
            }
        };


        btnpiechart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    yData[0] = reparray.get(0).getTotalcaloriesconsumed();
                    yData[1] = reparray.get(0).getTotalcaloriesburned();
                    yData[2] = (reparray.get(0).getDailycaloriegoal() - reparray.get(0).getTotalcaloriesconsumed());
                } catch (Exception e) {
                }
                setupPie();

                barChart.setVisibility(View.GONE);
                barChart.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                pieChart.setVisibility(View.VISIBLE);
                pieChart.setLayoutParams(new RelativeLayout.LayoutParams(400, 965));
            }
        });


        btnbarchart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupBar();
            }
        });

        return vDisplayReport;
    }

    @Override
    public void onClick(View v) {

    }

    public void setupBar() {
        pieChart.setVisibility(View.GONE);
        pieChart.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        barChart.setVisibility(View.VISIBLE);
        barChart.setLayoutParams(new RelativeLayout.LayoutParams(400, 965));

        ArrayList<BarEntry> barCaloriesBurned = new ArrayList<>();
        ArrayList<BarEntry> barCaloriesConsumed = new ArrayList<>();
        ArrayList<BarEntry> barRemainingCalorie = new ArrayList<>();
        ArrayList<String> sdate = new ArrayList<>();

        for (int i = 0; i < reparray.size(); i++) {
            barCaloriesBurned.add(new BarEntry(i, reparray.get(i).getTotalcaloriesburned()));
            barCaloriesConsumed.add(new BarEntry(i, reparray.get(i).getTotalcaloriesconsumed()));
            barRemainingCalorie.add(new BarEntry(i, (reparray.get(i).getDailycaloriegoal() - reparray.get(i).getTotalcaloriesconsumed())));
            System.out.println(reparray.get(i).getTotalcaloriesburned() + "" + reparray.get(i).getTotalcaloriesconsumed() + reparray.get(i).getDate());
            sdate.add(reparray.get(i).getDate());
        }

        BarDataSet barDataSet = new BarDataSet(barCaloriesBurned, "CaloriesBurned");
        BarDataSet barDataSet2 = new BarDataSet(barCaloriesConsumed, "CaloriesConsumed");
        BarDataSet barDataSet3 = new BarDataSet(barRemainingCalorie, "RemainingCalorie");

        barDataSet.setColor(Color.RED);
        barDataSet2.setColor(Color.BLUE);
        barDataSet3.setColor(Color.GREEN);

        BarData theData = new BarData(barDataSet, barDataSet2, barDataSet3);
        barChart.setData(theData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setCenterAxisLabels(true);

        barChart.getDescription().setText("");
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setFitBars(true);
        barChart.invalidate();
    }

    public void setupPie() {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < yData.length; i++) {
            pieEntries.add(new PieEntry(yData[i], xData[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Report Data");
        PieData data = new PieData(dataSet);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        dataSet.setColors(colors);

        dataSet.setSliceSpace(2);
        dataSet.setValueTextSize(12);

        pieChart.getDescription().setEnabled(false);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private class GetReportAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RESTClient.findAll("caloriepackage.reporttable/findByDate/" + params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (new JSONObject(jsonArray.getJSONObject(i).getString("userid")).getString("userid").equals("" + user.getUserid())) {
                        reparray.get(count).setTotalcaloriesburned(Integer.valueOf(jsonArray.getJSONObject(i).getString("totalcaloriesburned")));
                        reparray.get(count).setTotalstepstaken(Integer.valueOf(jsonArray.getJSONObject(i).getString("totalstepstaken")));
                        reparray.get(count).setDailycaloriegoal(Integer.valueOf(jsonArray.getJSONObject(i).getString("dailycaloriegoal")));
                        reparray.get(count).setTotalcaloriesconsumed(Integer.valueOf(jsonArray.getJSONObject(i).getString("totalcaloriesconsumed")));
                        reparray.get(count).setReportid(Integer.valueOf(jsonArray.getJSONObject(i).getString("reportid")));
                        reparray.get(count).setUserid(user.getUserid());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            count = count + 1;
            if (count == daysInBetween) ;
            else {
                GetReportAsyncTask getrep = new GetReportAsyncTask();
                getrep.execute(reparray.get(count).getDate());
            }

        }
    }
}