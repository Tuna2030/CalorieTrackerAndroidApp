package com.example.calorietrackerapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class WelcomeScreen extends AppCompatActivity {

    private String username;
    private String pass;
    private String result = null;
    private EditText etusername;
    private EditText etpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        ImageView im = (ImageView) findViewById(R.id.imageView);
        int imageresource = getResources().getIdentifier("@drawable/mynetdiary", null, this.getPackageName());
        im.setImageResource(imageresource);

        etusername = (EditText) findViewById(R.id.editTextusernamwel);
        etpassword = (EditText) findViewById(R.id.editTextpasswel);

        etpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etusername.getText().toString();
                UsersAsyncTask getAllUsers = new UsersAsyncTask();
                getAllUsers.execute();
            }
        });

        Button btn = (Button) findViewById(R.id.buttonlogin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pass = etpassword.getText().toString();
                UserTable user = new UserTable();
                CredentialTable cred = new CredentialTable();

                try {

                    JSONArray jsonArray = new JSONArray(result);

                    cred.setCredid(Integer.valueOf(jsonArray.getJSONObject(0).getString("credentialid")));
                    cred.setCredpasswordhash(jsonArray.getJSONObject(0).getString("passwordhash"));
                    cred.setSignupdate(jsonArray.getJSONObject(0).getString("signupdate"));
                    cred.setUsername(jsonArray.getJSONObject(0).getString("username"));

                    JSONObject jsonObjectuser = new JSONObject(jsonArray.getJSONObject(0).getString("userid"));

                    cred.setUserid(Integer.valueOf(jsonObjectuser.getString("userid")));

                    user.setAddress(jsonObjectuser.getString("address"));
                    user.setDateofbirth(jsonObjectuser.getString("dateofbirth"));
                    user.setEmail(jsonObjectuser.getString("email"));
                    user.setGender(jsonObjectuser.getString("gender"));
                    user.setHeight(Integer.valueOf(jsonObjectuser.getString("height")));
                    user.setHeight(Integer.valueOf(jsonObjectuser.getString("levelofactivity")));
                    user.setName(jsonObjectuser.getString("name"));
                    user.setUserpostcode(jsonObjectuser.getString("postcode"));
                    user.setStepspermile(Integer.valueOf(jsonObjectuser.getString("stepspermile")));
                    user.setSurname(jsonObjectuser.getString("surname"));
                    user.setUserid(cred.getUserid());
                    user.setWeight(Integer.valueOf(jsonObjectuser.getString("weight")));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!HashFunction.SHA1(pass).toUpperCase().equals(cred.getCredpasswordhash())) {
                    Toast.makeText(getApplicationContext(), "Wrong password, try again!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(WelcomeScreen.this, UserHomePage.class);
                    intent.putExtra("UserTable", user);
                    startActivity(intent);
                }

            }
        });

        Button btn1 = (Button) findViewById(R.id.buttonsignup);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(WelcomeScreen.this, Signup.class);
                startActivity(intent1);

            }
        });

    }

    private class UsersAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RESTClient.findAll("caloriepackage.credentialtable/findByUsername/" + username);
        }

        @Override
        protected void onPostExecute(String users) {
            result = users;
            if (result.length() <= 2)
                Toast.makeText(getApplicationContext(), "Username not found!", Toast.LENGTH_LONG).show();
        }
    }
}
