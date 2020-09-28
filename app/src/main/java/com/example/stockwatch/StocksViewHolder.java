package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class StocksViewHolder extends RecyclerView.ViewHolder {

    private TextView StockSymbol;
    private TextView CompanyName;
    private TextView Price;
    private TextView ChangeAmount;

    public StocksViewHolder(View itemView) {
        super(itemView);

        StockSymbol = itemView.findViewById(R.id.stockSymbol);
        setStockSymbolView((TextView) StockSymbol);

        CompanyName = itemView.findViewById(R.id.companyName);
        setCompanyNameView((TextView) CompanyName);

        Price = itemView.findViewById(R.id.Price);
        setPriceView((TextView) Price);

        ChangeAmount = itemView.findViewById(R.id.priceChange);
        setChangeAmountView((TextView) ChangeAmount);
    }

    public TextView getStockSymbolView() {
        return StockSymbol;
    }

    public void setStockSymbolView(TextView StockSymbol) {
        this.StockSymbol = StockSymbol;
    }

    public TextView getCompanyNameView() {
        return CompanyName;
    }

    public void setCompanyNameView(TextView CompanyName) {
        this.CompanyName = CompanyName;
    }

    public TextView getPriceView() {
        return Price;
    }

    public void setPriceView(TextView Price) { this.Price = Price; }

    public TextView getChangeAmountView() {
        return ChangeAmount;
    }

    public void setChangeAmountView(TextView ChangeAmount) { this.ChangeAmount = ChangeAmount; }
}
