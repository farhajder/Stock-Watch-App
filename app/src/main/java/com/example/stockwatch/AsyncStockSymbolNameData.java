package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AsyncStockSymbolNameData extends AsyncTask<String, Void, List<HashMap<String, String>>> {

    private String stockApiURL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private final String TAG = "AsyncSymbolLoader";
    private MainActivity myMainActivity;
    private String myStockSymbolText;

    public AsyncStockSymbolNameData(MainActivity mainActivity)
    {
        myMainActivity = mainActivity;
    }

    @Override
    protected List<HashMap<String, String>> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: ");
        List<HashMap<String, String>> stockSymbolList = null;
        myStockSymbolText = params[0];
        try {
            stockSymbolList = LoadStockSymbolData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(stockSymbolList == null) {
            return new ArrayList<>();
        }
        return stockSymbolList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
        myMainActivity.UpdateFromAsyncStockSymbolLoaderTask(hashMaps, myStockSymbolText);
    }

    private List<HashMap<String, String>> LoadStockSymbolData() throws IOException {
        Uri.Builder buildURL = Uri.parse(stockApiURL).buildUpon();
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "LoadStockSymbolData: URL = " + urlToUse);

        StringBuilder sB = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){ return null; }
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null){
                sB.append(line).append('\n');
            }
        }
        catch(Exception e) {
            e.getStackTrace();
            return null;
        }
        Log.d(TAG, "LoadStockSymbolData" + sB.toString());

        return parseJSON(sB.toString());
    }

    private List<HashMap<String, String>> parseJSON(String s) {
        List<HashMap<String, String>> stockSymbolDataList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(s);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String stockSymbol = jsonObject.getString("symbol").trim();
                String companyName = jsonObject.getString("name").trim();

                HashMap<String, String> stockSymbolData = new HashMap<>();
                stockSymbolData.put("symbol", stockSymbol);
                stockSymbolData.put("name", companyName);
                stockSymbolDataList.add(stockSymbolData);
            }
        }
        catch(Exception e) {
            e.getStackTrace();
            return null;
        }
        return stockSymbolDataList;
    }
}