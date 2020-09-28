package com.example.stockwatch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StocksViewHolder> {

    private List<Stock> StockList;
    private MainActivity myMainActivity;

    public StocksAdapter(List<Stock> stockList, MainActivity mainActivity) {
        StockList = stockList;
        myMainActivity = mainActivity;
    }

    // create new views invoked by layout manager
    @Override
    public StocksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_record_row, parent, false);
        itemView.setOnClickListener(myMainActivity);
        itemView.setOnLongClickListener(myMainActivity);
        return new StocksViewHolder(itemView);
    }

    // replace the contents of the view invoked by the layout manager
    @Override
    public void onBindViewHolder(StocksViewHolder holder, int position) {
        Stock newStock = StockList.get(position);
        holder.getStockSymbolView().setText(newStock.getStockSymbol());
        holder.getCompanyNameView().setText(newStock.getCompanyName());
        holder.getPriceView().setText(newStock.getPrice());

        if(Double.parseDouble(newStock.getPriceChange()) >= 0.0) {
            holder.getChangeAmountView().setText("\u25B2 " + newStock.getPriceChange()+ " " + newStock.getChangePercentage());
            SetGreenColor(holder);
        }
        else {
            holder.getChangeAmountView().setText("\u25BC " + newStock.getPriceChange()+ " " + newStock.getChangePercentage());
            SetRedColor(holder);
        }
    }

    private void SetRedColor(StocksViewHolder holder) {
        holder.getStockSymbolView().setTextColor(Color.RED);
        holder.getCompanyNameView().setTextColor(Color.RED);
        holder.getPriceView().setTextColor(Color.RED);
        holder.getChangeAmountView().setTextColor(Color.RED);
    }
    private void SetGreenColor(StocksViewHolder holder) {
        holder.getStockSymbolView().setTextColor(Color.GREEN);
        holder.getCompanyNameView().setTextColor(Color.GREEN);
        holder.getPriceView().setTextColor(Color.GREEN);
        holder.getChangeAmountView().setTextColor(Color.GREEN);
    }

    @Override
    public int getItemCount() {
        return StockList.size();
    }
}