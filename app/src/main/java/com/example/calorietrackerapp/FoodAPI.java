package com.example.calorietrackerapp;

import android.provider.Settings;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FoodAPI {

    final private static String APP_KEY = "26e18b17ece34effa22ed2d2dddb5bf0";

    final private static String APP_SECRET = "0fcd7145c70b4fc2896f154918fd197f";

    final private static String APP_URL = "http://platform.fatsecret.com/rest/server.api";

    final private static String APP_SIGNATURE_METHOD = "HmacSHA1";

    final private static String HTTP_METHOD = "GET";

    public FoodAPI() {
    }


    public static String nonce() {
        Random r = new Random();
        StringBuffer n = new StringBuffer();
        for (int i = 0; i < r.nextInt(8) + 2; i++) {
            n.append(r.nextInt(26) + 'a');
        }
        return n.toString();
    }


    public static String[] generateOauthParams() {
        String[] a = {
                "oauth_consumer_key=" + APP_KEY,
                "oauth_signature_method=HMAC-SHA1",
                "oauth_timestamp=" + new Long(System.currentTimeMillis() / 1000).toString(),
                "oauth_nonce=" + nonce(),
                "oauth_version=1.0",
                "format=json"
        };
        return a;
    }


    public static String join(String[] params, String separator) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                b.append(separator);
            }
            b.append(params[i]);
        }
        return b.toString();
    }

    public static String paramify(String[] params) {
        String[] p = Arrays.copyOf(params, params.length);
        Arrays.sort(p);
        return join(p, "&");
    }

    public static String encode(String url) {
        if (url == null)
            return "";

        try {
            return URLEncoder.encode(url, "utf-8")
                    .replace("+", "%20")
                    .replace("!", "%21")
                    .replace("*", "%2A")
                    .replace("\\", "%27")
                    .replace("(", "%28")
                    .replace(")", "%29");
        } catch (UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }


    public static String sign(String method, String uri, String[] params) throws UnsupportedEncodingException {

        String encodedURI = encode(uri);
        String encodedParams = encode(paramify(params));

        String[] p = {method, encodedURI, encodedParams};

        String text = join(p, "&");
        String key = APP_SECRET + "&";
        SecretKey sk = new SecretKeySpec(key.getBytes(), APP_SIGNATURE_METHOD);
        String sign = "";
        try {
            Mac m = Mac.getInstance(APP_SIGNATURE_METHOD);
            m.init(sk);
            sign = encode(new String(Base64.encode(m.doFinal(text.getBytes()), Base64.DEFAULT)).trim());
        } catch (java.security.NoSuchAlgorithmException e) {

        } catch (java.security.InvalidKeyException e) {

        }
        return sign;
    }

    public static FoodTable getFoodItems(String query) throws UnsupportedEncodingException, JSONException {
        String textResult = "";
        HttpURLConnection connection = null;

        List<String> params = new ArrayList<String>(Arrays.asList(generateOauthParams()));
        String[] template = new String[1];
        params.add("method=foods.search");
        params.add("max_results=50");
        params.add("search_expression=" + encode(query));
        params.add("oauth_signature=" + sign(HTTP_METHOD, APP_URL, params.toArray(template)));
        try {
            URL url = new URL(APP_URL + "?" + paramify(params.toArray(template)));
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        JSONObject jsonObject = new JSONObject(textResult);
        JSONObject jsonObject1 = jsonObject.getJSONObject("foods");
        JSONArray jsonArray = jsonObject1.getJSONArray("food");
        String id = jsonArray.getJSONObject(0).getString("food_id");
        textResult = getFoodItem(Long.valueOf(id));

        jsonObject = new JSONObject(textResult);
        jsonObject1 = jsonObject.getJSONObject("food");
        jsonObject = jsonObject1.getJSONObject("servings");
        FoodTable food = new FoodTable();
        String details[];
        try {
            jsonArray = jsonObject.getJSONArray("serving");
            jsonObject1 = jsonArray.getJSONObject(0);
            food.setFoodservingamount(new Double(jsonObject1.getString("number_of_units")).intValue());
            food.setFoodcalamount(new Double(jsonObject1.getString("calories")).intValue());
            details = jsonObject1.getString("serving_description").split(" ",2);
            food.setFoodservingunit(details[1]);
            food.setFat(new Double(jsonObject1.getString("fat")).intValue());
        } catch (Exception e) {
        }
        try {
            jsonObject1 = jsonObject.getJSONObject("serving");
            food.setFoodservingamount(new Double(jsonObject1.getString("number_of_units")).intValue());
            food.setFoodcalamount(new Double(jsonObject1.getString("calories")).intValue());
            details = jsonObject1.getString("serving_description").split(" ",2);
            food.setFoodservingunit(details[1]);
            food.setFat(new Double(jsonObject1.getString("fat")).intValue());
        } catch (Exception e) {
        }

        return food;
    }

    public static String getFoodItem(long id) throws UnsupportedEncodingException, JSONException {
        String textResult = "";
        HttpURLConnection connection = null;

        List<String> params = new ArrayList<String>(Arrays.asList(generateOauthParams()));
        String[] template = new String[1];
        params.add("method=food.get");
        params.add("food_id=" + encode("" + id));
        params.add("oauth_signature=" + sign(HTTP_METHOD, APP_URL, params.toArray(template)));

        try {
            URL url = new URL(APP_URL + "?" + paramify(params.toArray(template)));
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return textResult;
    }
}
