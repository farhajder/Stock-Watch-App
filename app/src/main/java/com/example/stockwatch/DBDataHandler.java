package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBDataHandler extends SQLiteOpenHelper {

    private static final String TAG = "DBDataHandler";
    private static SQLiteDatabase myDataBase;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    private static DBDataHandler myDataBaseHandler;

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " Text not null )";

    public static DBDataHandler getDatabaseHandler(Context context) {
        if(myDataBaseHandler == null){ myDataBaseHandler = new DBDataHandler(context); }
        return myDataBaseHandler;
    }

    private DBDataHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myDataBase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do-nothing
    }

    // Add a stock to the database
    public static void addStock(Stock stock) {
        Log.d(TAG,"Adding stock: " + stock.getStockSymbol());
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getStockSymbol());
        values.put(COMPANY, stock.getCompanyName());

        deleteStock(stock.getStockSymbol());
        myDataBase.insert(TABLE_NAME, null, values);
        Log.d(TAG,"Adding stock complete ");
    }

    // Delete a stock from the database
    public static void deleteStock(String symbol) {
        Log.d(TAG,"Deleting stock: " + symbol);
        myDataBase.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{ symbol });
        Log.d(TAG,"Deleting stock complete ");
    }

    public static List<Stock> loadStocks() {
        List<Stock> stocks = new ArrayList<>();

        Cursor cursor = myDataBase.query(
                TABLE_NAME, // The table to query
                new String[]{ SYMBOL, COMPANY }, // The columns to return
                null, // The columns for the WHERE clause, null means “*”
                null, // The values for the WHERE clause, null means “*”
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order

        if(cursor != null) {
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);

                Stock stock = new Stock();
                stock.setCompanyName(company);
                stock.setStockSymbol(symbol);
                stocks.add(stock);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }
}
