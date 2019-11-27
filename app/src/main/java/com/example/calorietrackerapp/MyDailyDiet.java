package com.example.calorietrackerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MyDailyDiet extends Fragment {
    View vDisplayFood;
    ListView categoryList;
    ListView foodlist;
    EditText et;
    ImageView im;
    TextView tvfoodinfo;
    TextView tvfoodsearchinfo;
    Button btnsave;
    Button btnnewfood;
    String[] details;
    ArrayList<FoodTable> foodlistarray = new ArrayList<FoodTable>();
    String categoryname;
    Integer totalconsumedcalorie = 0;
    FoodTable newfood = new FoodTable();
    int foodid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayFood = inflater.inflate(R.layout.fragment_my_daily_diet, container, false);
        btnsave = vDisplayFood.findViewById(R.id.buttonsavecalorie);
        btnnewfood = vDisplayFood.findViewById(R.id.buttonnewfood);
        im = vDisplayFood.findViewById(R.id.imageViewfoodimage);
        tvfoodinfo = vDisplayFood.findViewById(R.id.textViewFoodinfo);
        tvfoodsearchinfo = vDisplayFood.findViewById(R.id.textViewFoodsearch);
        et = vDisplayFood.findViewById(R.id.editTextSelectFood);
        categoryList = (ListView) vDisplayFood.findViewById(R.id.list_view_category);
        foodlist = (ListView) vDisplayFood.findViewById(R.id.list_view_food);

        SharedPreferences spGetCalorie = getActivity().getSharedPreferences("Calorie", Context.MODE_PRIVATE);
        String calorietext = spGetCalorie.getString("calorie", 0 + "");
        totalconsumedcalorie = Integer.valueOf(calorietext);

        final String[] menuitems = {"Drink", "Meal", "Meat", "Snack", "Bread", "Cake", "Fruit", "Veg", "Other"};
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuitems);
        categoryList.setAdapter(myListAdapter);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryname = menuitems[position].toLowerCase();
                GetFoodAsyncTask getAllFood = new GetFoodAsyncTask();
                getAllFood.execute();
            }
        });

        foodlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et.setText(foodlistarray.get(position).getFoodname().replace(" ", "") + " (piece)");
                tvfoodinfo.setText("calorieamount: " + foodlistarray.get(position).getFoodcalamount() + " servingunit: " + foodlistarray.get(position).getFoodservingunit() + " servingamount: " + foodlistarray.get(position).getFoodservingamount() + " fat: " + foodlistarray.get(position).getFat());
            }
        });


        btnnewfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details = et.getText().toString().toLowerCase().split(" ");
                if (details.length == 2) {
                    if (details[1].equals("drink") || details[1].equals("meal") || details[1].equals("meat") || details[1].equals("snack") || details[1].equals("bread") || details[1].equals("cake") || details[1].equals("fruit") || details[1].equals("veg") || details[1].equals("other")) {
                        SearchAsyncTask searchAsyncTask = new SearchAsyncTask();
                        searchAsyncTask.execute(details[0]);

                        FoodAPIAsyncTask getAllFoods = new FoodAPIAsyncTask();
                        getAllFoods.execute(details[0]);

                        FoodCountAsyncTask countAllFoods = new FoodCountAsyncTask();
                        countAllFoods.execute();

                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "Wrong category!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "Food name and category should be entered!", Toast.LENGTH_LONG).show();
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String det[] = et.getText().toString().split(" ");
                if (det.length == 2) {
                    if (isInteger(det[1], 10)) {
                        int i;
                        for (i = 0; i < foodlistarray.size(); i++) {
                            if (det[0].equals(foodlistarray.get(i).getFoodname().replace(" ", ""))) {
                                totalconsumedcalorie = totalconsumedcalorie + (Integer.valueOf(det[1]) * foodlistarray.get(i).getFoodcalamount());

                                SharedPreferences spGatherfood = getActivity().getSharedPreferences("Foodlist", Context.MODE_PRIVATE);
                                String foodlist = spGatherfood.getString("foodlist", "");

                                SharedPreferences spFoodlist = getActivity().getSharedPreferences("Foodlist", Context.MODE_PRIVATE);
                                SharedPreferences.Editor eFoodlist = spFoodlist.edit();
                                eFoodlist.putString("foodlist",foodlist + "\nFood name: " + foodlistarray.get(i).getFoodname() + " Food calorie: " + foodlistarray.get(i).getFoodcalamount() + " Consumed unit: " + det[1] + " Total calorie consumed: " + (Integer.valueOf(det[1])*foodlistarray.get(i).getFoodcalamount()) + "\n");
                                eFoodlist.apply();

                                break;
                            }
                        }

                        SharedPreferences spCalorie = getActivity().getSharedPreferences("Calorie", Context.MODE_PRIVATE);
                        SharedPreferences.Editor eCalorie = spCalorie.edit();
                        eCalorie.putString("calorie", totalconsumedcalorie + "");
                        eCalorie.apply();

                        if (i == foodlistarray.size())
                            Toast.makeText(getActivity().getApplicationContext(), "Wrong food name!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity().getApplicationContext(), "Your total " + totalconsumedcalorie + " calorie consumed is saved!", Toast.LENGTH_LONG).show();

                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "Piece should be number!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "Food name and consumed piece should be entered!", Toast.LENGTH_LONG).show();
            }
        });

        return vDisplayFood;
    }

    private void LoadImageFromURL(String url, ImageView im) {
        Picasso.with(this.getActivity().getApplicationContext()).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(im, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });

    }


    private class SearchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return SearchGoogleAPI.search(params[0], new String[]{"num"}, new String[]{"1"});
        }

        @Override
        protected void onPostExecute(String result) {
            tvfoodsearchinfo.setText(SearchGoogleAPI.getSnippet(result));
            LoadImageFromURL(SearchGoogleAPI.getImage(result), im);
        }
    }

    private class GetFoodAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RESTClient.findAll("caloriepackage.foodtable/findByCategory/" + categoryname);
        }

        @Override
        protected void onPostExecute(String users) {
            String result = users;

            try {
                JSONArray jsonArray = new JSONArray(result);
                foodlistarray.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    FoodTable tempfood = new FoodTable();
                    tempfood.setFat(Integer.valueOf(jsonArray.getJSONObject(i).getString("fat")));
                    tempfood.setFoodcalamount(Integer.valueOf(jsonArray.getJSONObject(i).getString("calorieamount")));
                    tempfood.setFoodcat(jsonArray.getJSONObject(i).getString("category"));
                    tempfood.setFoodname(jsonArray.getJSONObject(i).getString("name"));
                    tempfood.setFoodservingunit(jsonArray.getJSONObject(i).getString("servingunit"));
                    tempfood.setFoodid(Integer.valueOf(jsonArray.getJSONObject(i).getString("foodid")));
                    tempfood.setFoodservingamount(Integer.valueOf(jsonArray.getJSONObject(i).getString("servingamount")));
                    foodlistarray.add(tempfood);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] menuitems2 = new String[foodlistarray.size()];
            for (int j = 0; j < menuitems2.length; j++) {
                menuitems2[j] = foodlistarray.get(j).getFoodname().replace(" ", "");
            }
            ArrayAdapter<String> myListAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuitems2);
            foodlist.setAdapter(myListAdapter2);

        }
    }

    private class FoodAPIAsyncTask extends AsyncTask<String, Void, FoodTable> {
        @Override
        protected FoodTable doInBackground(String... params) {
            try {
                return FoodAPI.getFoodItems(params[0]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new FoodTable();
        }

        @Override
        protected void onPostExecute(FoodTable food) {
            newfood = food;
            newfood.setFoodname(details[0]);
            newfood.setFoodcat(details[1]);
            tvfoodinfo.setText("calorieamount: " + newfood.getFoodcalamount() + " servingunit: " + newfood.getFoodservingunit() + " servingamount: " + newfood.getFoodservingamount() + " fat: " + newfood.getFat());
        }
    }

    private class FoodCountAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RESTClient.countAll("caloriepackage.foodtable/count");
        }

        @Override
        protected void onPostExecute(String s) {
            foodid = Integer.valueOf(s);
            newfood.setFoodid(foodid + 1);
            FoodSaveDatabaseAsyncTask saveToDatabase = new FoodSaveDatabaseAsyncTask();
            saveToDatabase.execute();
        }
    }

    private class FoodSaveDatabaseAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            RESTClient.createFood(newfood);
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getActivity().getApplicationContext(), newfood.getFoodname() + " is added to database!", Toast.LENGTH_LONG).show();
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
