package com.example.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private List<Stock> StocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StocksAdapter stockAdapter;
    private DBDataHandler DBDataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        stockAdapter = new StocksAdapter(StocksList, this);

        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DBDataHandler = DBDataHandler.getDatabaseHandler(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        if(!isConnectionAvailable()) {
            ShowNoNetworkDialog();
            return;
        }

        List<Stock> stockList = DBDataHandler.loadStocks();
        if(!stockList.isEmpty()) {
            for(int index = 0 ; index < stockList.size(); index++) {
                AsyncStockFinancialData task = new AsyncStockFinancialData(MainActivity.this);
                task.execute(stockList.get(index).getStockSymbol(), stockList.get(index).getCompanyName());
            }
        }
        recyclerView.computeVerticalScrollOffset();
    }
    
    private boolean isConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void doRefresh() {
        if(!isConnectionAvailable()) {
            swipeRefreshLayout.setRefreshing(false);
            ShowNoNetworkDialog();
            return;
        }
        StocksList.clear();
        List<Stock> stockList = DBDataHandler.loadStocks();
        if(!stockList.isEmpty()) {
            for(int index = 0 ; index < stockList.size(); index++) {
                AsyncStockFinancialData task = new AsyncStockFinancialData(MainActivity.this);
                task.execute(stockList.get(index).getStockSymbol(), stockList.get(index).getCompanyName());
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addStockMenu :

                if(!isConnectionAvailable()) {
                    ShowNoNetworkDialog();
                    break;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.stockSelection));
                builder.setMessage(getString(R.string.stockSelectionData));

                final EditText stockSymbol = new EditText(this);
                stockSymbol.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                stockSymbol.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                stockSymbol.setGravity(Gravity.CENTER_HORIZONTAL);

                builder.setView(stockSymbol);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String stockSymbolText = stockSymbol.getText().toString();

                        AsyncStockSymbolNameData task = new AsyncStockSymbolNameData(MainActivity.this);
                        task.execute(stockSymbolText);
                    }
                });

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowNoNetworkDialog() {
        AlertDialog.Builder noConnectivityDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        noConnectivityDialogBuilder.setTitle(getString(R.string.noNetworkConnection));
        noConnectivityDialogBuilder.setMessage(getString(R.string.noNetworkConnectionData));
        AlertDialog noConnectivityStockDialog = noConnectivityDialogBuilder.create();
        noConnectivityStockDialog.show();
    }

    @Override
    public void onClick(View v) {
        final int position = recyclerView.getChildLayoutPosition(v);
        String url = getString(R.string.marketWatch);
        url += StocksList.get(position).getStockSymbol();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        final int position = recyclerView.getChildLayoutPosition(v);
        Stock stock = StocksList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete);
        builder.setTitle(getString(R.string.deleteStock));
        builder.setMessage(getString(R.string.deleteStockSymbol) + " " + stock.getStockSymbol()+" ?");

        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the stock details
                Stock stock = StocksList.get(position);

                // Delete the stock from the database
                DBDataHandler.deleteStock(stock.getStockSymbol());

                // Delete the stock from the stock list
                StocksList.remove(position);

                // Notify adapter about the change in stock list
                stockAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    public void UpdateFromAsyncStockSymbolLoaderTask(final List<HashMap<String, String>> stockSymbolDataList, String stockSymbolText) {
        final String StockSymbol = getString(R.string.companySymbol);
        final String CompanyName = getString(R.string.companyName);

        Log.d(TAG, "UpdateFromAsyncStockSymbolLoaderTask: " + stockSymbolText + " " + stockSymbolDataList);

        if(stockSymbolDataList.size() == 0) {
            AlertDialog.Builder noStockDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            noStockDialogBuilder.setTitle("Symbol Not Found: " + stockSymbolText);
            noStockDialogBuilder.setMessage("Data: 404 - Not Found");
            AlertDialog noStockDialog = noStockDialogBuilder.create();
            noStockDialog.show();
            return;
        }

        if(stockSymbolDataList.size() == 1) {
            HashMap<String, String> selectedItem = stockSymbolDataList.get(0);
            AsyncStockFinancialData task = new AsyncStockFinancialData(MainActivity.this);
            task.execute(selectedItem.get(StockSymbol), selectedItem.get(CompanyName));
        }

        else if(stockSymbolDataList.size() > 1) {
            final CharSequence[] stockSymbolList = new CharSequence[stockSymbolDataList.size()];
            for(int i = 0; i < stockSymbolDataList.size(); i++) {
                HashMap<String, String> item = stockSymbolDataList.get(i);
                stockSymbolList[i] = item.get(StockSymbol) + " - " + item.get(CompanyName);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.makeSelection));
            builder.setItems(stockSymbolList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HashMap<String, String> selectedItem = stockSymbolDataList.get(which);
                    AsyncStockFinancialData task = new AsyncStockFinancialData(MainActivity.this);
                    task.execute(selectedItem.get(StockSymbol), selectedItem.get(CompanyName));
                }
            });

            builder.setNegativeButton(getString(R.string.neverMind), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User cancelled the dialog
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void UpdateFromAsyncStockFinancialLoaderTask(Stock stock, String stockSymbolText ) {
        Log.d(TAG, "UpdateFromAsyncStockFinancialLoaderTask: " + stock + " " + stockSymbolText);
        if(stock == null) {
            AlertDialog.Builder noStockDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            noStockDialogBuilder.setTitle("Symbol Not Found: " + stockSymbolText);
            noStockDialogBuilder.setMessage("Data: 404 - Not Found");
            AlertDialog noStockDialog = noStockDialogBuilder.create();
            noStockDialog.show();
            return;
        }

        boolean duplicateStock = false;
        for(int i = 0; i < StocksList.size(); i++) {
            if(stock.getStockSymbol().compareTo(StocksList.get(i).getStockSymbol()) == 0 &&
                    stock.getCompanyName().compareTo(StocksList.get(i).getCompanyName()) == 0) {
                duplicateStock = true;
            }
        }
        if(duplicateStock) {
            AlertDialog.Builder duplicateStockDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            duplicateStockDialogBuilder.setIcon(R.drawable.ic_duplicate);
            duplicateStockDialogBuilder.setTitle(getString(R.string.duplicateStock));
            duplicateStockDialogBuilder.setMessage(getString(R.string.stockSymbol) + " " + stockSymbolText + " " + getString(R.string.alreadyDisplayed));
            AlertDialog duplicateStockDialog = duplicateStockDialogBuilder.create();
            duplicateStockDialog.show();
            return;
        }

        StocksList.add(stock);
        Collections.sort(StocksList);
        DBDataHandler.addStock(stock);
        stockAdapter.notifyDataSetChanged();
    }
}
