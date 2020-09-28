package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
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

public class AsyncStockFinancialData extends AsyncTask<String, Void, Stock> {

    private static final String TAG = "AsyncStockFinancialData";
    private final String stockApiURL = "https://cloud.iexapis.com/stable/stock";
    private final String ApiKey = "sk_7ffbdab9d24249e38ab220a67eaad414";
    private MainActivity myMainActivity;
    private String stockSymbolText;
    private String companyName;

    public AsyncStockFinancialData(MainActivity mainActivity) { myMainActivity = mainActivity; }

    @Override
    protected Stock doInBackground(String... params) {
        Log.d(TAG, "doInBackground: " + params[0] + " " + params[1] );
        Stock stock;
        stockSymbolText = params[0];
        companyName = params[1];
        stock = LoadStockFinancialData(companyName);
        Log.d(TAG, "doInBackground: return stock after LoadStockFinancialData call = " + stock);
        return stock;
    }

    @Override
    protected void onPostExecute(Stock stock) {
        myMainActivity.UpdateFromAsyncStockFinancialLoaderTask(stock, stockSymbolText);
    }

    private Stock LoadStockFinancialData(String companyName) {
        Uri.Builder buildURL = Uri.parse(stockApiURL).buildUpon();
        buildURL.appendPath(stockSymbolText);
        buildURL.appendPath("quote");
        buildURL.appendQueryParameter("token", ApiKey);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "LoadStockFinancialData: URL = "+ urlToUse);
        Stock stock;

        StringBuilder sB = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){ return null;}
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null){
                sB.append(line).append('\n');
            }
            Log.d(TAG, "LoadStockFinancialData: StringBuilder Data" + sB);
        }
        catch(Exception e) {
            e.getStackTrace();
            return null;
        }
        stock = parseJSON(sB.toString(), companyName);
        return stock;
    }

    private Stock parseJSON(String jsonString, String companyName) {
        Stock stock = new Stock();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String stockSymbol = jsonObject.getString("symbol").trim();
                String lastTradePrice = jsonObject.getString("latestPrice").trim();
                String priceChangeAmount = jsonObject.getString("change").trim();
                String priceChangePercentage = jsonObject.getString("changePercent").trim();

                stock.setStockSymbol(stockSymbol);
                stock.setCompanyName(companyName);
                stock.setPrice(lastTradePrice);
                stock.setPriceChange(priceChangeAmount);
                stock.setChangePercentage("(" + priceChangePercentage + "%)");
            }
            Log.d(TAG, "parseJSON: " + stock);
        }
        catch(Exception e) {
            e.getStackTrace();
            return null;
        }

        return stock;
    }
}