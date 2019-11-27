package com.example.calorietrackerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private RadioGroup rg;
    private Spinner sp;
    private String usercount;
    private RadioButton rb;
    private String gender;
    private String DOB;
    private UserTable user;
    private CredentialTable cred;
    private String signupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        UserCountAsyncTask getAllUsers = new UserCountAsyncTask();
        getAllUsers.execute();

        mDisplayDate = (TextView) findViewById(R.id.textViewDOB);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Signup.this, android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                String smonth;
                String sday;

                if (month < 10) smonth = "0" + (month + 1);
                else smonth = (month + 1) +"";

                if (day < 10) sday = "0" + day;
                else sday = day + "";

                signupdate = year + "-" + smonth + "-" + sday + "T00:00:00+10:00";

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                mDisplayDate.setText(date);

                String smonth;
                String sday;

                if (month < 10) smonth = "0" + month;
                else smonth = month +"";

                if (dayOfMonth < 10) sday = "0" + dayOfMonth;
                else sday = dayOfMonth + "";

                DOB = year + "-" + smonth + "-" + sday + "T00:00:00+10:00";
            }
        };

        sp = (Spinner) findViewById(R.id.spinnerLOA);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);

        rg = (RadioGroup) findViewById(R.id.radioGroup);

        Button btn = (Button) findViewById(R.id.buttonsignsubmit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int radioid = rg.getCheckedRadioButtonId();
                rb = (RadioButton) findViewById(radioid);

                EditText etfn = (EditText) findViewById(R.id.editTextfirstname);
                EditText etsn = (EditText) findViewById(R.id.editTextsurname);
                EditText etemail = (EditText) findViewById(R.id.editTextemail);
                EditText etheight = (EditText) findViewById(R.id.editTextheight);
                EditText etweight = (EditText) findViewById(R.id.editTextweight);
                EditText etaddress = (EditText) findViewById(R.id.editTextaddress);
                EditText etpostcode = (EditText) findViewById(R.id.editTextpostcode);
                EditText etSPM = (EditText) findViewById(R.id.editTextSPM);
                EditText etusername = (EditText) findViewById(R.id.editTextusername);
                EditText etpass = (EditText) findViewById(R.id.editTextpassword);
                //10
                TextView tvDOB = (TextView) findViewById(R.id.textViewDOB);

                if (tvDOB.getText().toString().equals("Date of Birth"))
                    Toast.makeText(getApplicationContext(), "Please enter a date of birth!", Toast.LENGTH_LONG).show();

                if (rb.getText().toString().equals("Male")) gender = "M";
                else gender = "F";

                if (checkString(etfn) && checkString(etsn) && checkString(etemail) && checkInt(etheight) && checkInt(etweight) && checkString(etaddress) && checkString(etpostcode)
                        && checkInt(etSPM) && checkString(etusername) && checkString(etpass) && isValidEmail(etemail.getText().toString()) && validate(etusername.getText().toString())
                        && is_Valid_Password(etpass.getText().toString()) && !tvDOB.getText().toString().equals("Date of Birth")) {

                    Integer userid = Integer.valueOf(usercount);
                    userid +=1;
                    String firstname = etfn.getText().toString();
                    String surname = etsn.getText().toString();
                    String email = etemail.getText().toString();
                    Integer height = Integer.valueOf(etheight.getText().toString());
                    Integer weight = Integer.valueOf(etweight.getText().toString());
                    String address = etaddress.getText().toString();
                    String postcode = etpostcode.getText().toString();
                    Integer LOA = Integer.valueOf(sp.getSelectedItem().toString());
                    Integer SPM = Integer.valueOf(etSPM.getText().toString());

                    user = new UserTable(userid, firstname, surname, email, DOB, height, weight, gender, address, postcode, LOA, SPM);
                    cred = new CredentialTable(userid, userid, etusername.getText().toString(), HashFunction.SHA1(etpass.getText().toString()), signupdate);

                    PostUserAsyncTask postuserAsyncTask = new PostUserAsyncTask();
                    postuserAsyncTask.execute(user);

                    Intent intent = new Intent(Signup.this, UserHomePage.class);
                    intent.putExtra("UserTable", user);

                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean checkString(EditText et) {
        if (et.getText().toString().equals("") || et.getText().toString().equals("First Name") || et.getText().toString().equals("Surname") || et.getText().toString().equals("E-mail") || et.getText().toString().equals("Height") || et.getText().toString().equals("Weight") || et.getText().toString().equals("Date of Birth") || et.getText().toString().equals("Address")
                || et.getText().toString().equals("PostCode") || et.getText().toString().equals("Steps Per Mile") || et.getText().toString().equals("Username") || et.getText().toString().equals("Password")) {
            Toast.makeText(getApplicationContext(), "All form must be filled correctly!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    public boolean checkInt(EditText et) {
        if (et.getText().toString().equals("") || et.getText().toString().equals("First Name") || et.getText().toString().equals("Surname") || et.getText().toString().equals("E-mail") || et.getText().toString().equals("Height") || et.getText().toString().equals("Weight") || et.getText().toString().equals("Date of Birth") || et.getText().toString().equals("Address")
                || et.getText().toString().equals("PostCode") || et.getText().toString().equals("Steps Per Mile") || et.getText().toString().equals("Username") || et.getText().toString().equals("Password")) {
            Toast.makeText(getApplicationContext(), "All form must be filled correctly!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (isInteger(et.getText().toString(), 10)) {
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "Weight, Height and Steps Per Mile should be number!", Toast.LENGTH_LONG).show();
                return false;
            }
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

    public boolean validate(final String username) {
        Pattern pattern = Pattern.compile("^[a-z0-9__-]{3,15}$");
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches())
            Toast.makeText(getApplicationContext(), "Username should be at least 3 character long and contain a-z0-9 characters!", Toast.LENGTH_LONG).show();
        return matcher.matches();
    }

    public boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if (!email.matches(regex))
            Toast.makeText(getApplicationContext(), "Wrong E-mail!", Toast.LENGTH_LONG).show();
        return email.matches(regex);
    }

    public boolean is_Valid_Password(String password) {

        if (password.length() < 8) {
            Toast.makeText(getApplicationContext(), "Password should be at least 8 character long and contain at least 2 numbers!", Toast.LENGTH_LONG).show();
            return false;
        }

        int charCount = 0;
        int numCount = 0;
        for (int i = 0; i < password.length(); i++) {

            char ch = password.charAt(i);

            if (is_Numeric(ch)) numCount++;
            else if (is_Letter(ch)) charCount++;
        }

        if (!(charCount >= 2 && numCount >= 2)) {
            Toast.makeText(getApplicationContext(), "Password should contain at least 2 numbers and letters!", Toast.LENGTH_LONG).show();
        }
        return (charCount >= 2 && numCount >= 2);
    }

    public boolean is_Letter(char ch) {
        ch = Character.toUpperCase(ch);
        return (ch >= 'A' && ch <= 'Z');
    }


    public boolean is_Numeric(char ch) {

        return (ch >= '0' && ch <= '9');
    }

    private class UserCountAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RESTClient.countAll("caloriepackage.usertable/count");
        }

        @Override
        protected void onPostExecute(String users) {
            usercount = users;
        }
    }

    private class PostUserAsyncTask extends AsyncTask<UserTable, Void, String> {
        @Override
        protected String doInBackground(UserTable... params) {
            RESTClient.createUser(user);
            return "User is added";
        }

        @Override
        protected void onPostExecute(String response) {
            PostCredAsyncTask postcredasyntask = new PostCredAsyncTask();
            postcredasyntask.execute(cred);
        }
    }

    private class PostCredAsyncTask extends AsyncTask<CredentialTable,UserTable, String> {
        @Override
        protected String doInBackground(CredentialTable... params) {
            RESTClient.createCred(params[0],user);
            return "Credentials are added";
        }

        @Override
        protected void onPostExecute(String response) {
            Toast.makeText(getApplicationContext(), response + " Successfully signed up!", Toast.LENGTH_LONG).show();
        }
    }


}