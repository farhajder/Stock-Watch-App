package com.example.stockwatch;

import androidx.annotation.NonNull;

public class Stock implements Comparable {

    private String StockSymbol;
    private String CompanyName;
    private String Price;
    private String PriceChange;
    private String ChangePercentage;

    public Stock(){
        StockSymbol = null;
        CompanyName = null;
        Price = null;
        PriceChange = null;
        ChangePercentage = null;
    }

    public String getStockSymbol() {
        return StockSymbol;
    }

    public void setStockSymbol(String StockSymbol) {
        this.StockSymbol = StockSymbol;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String CompanyName) {
        this.CompanyName = CompanyName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) { this.Price = Price; }

    public String getPriceChange() {
        return PriceChange;
    }

    public void setPriceChange(String PriceChange) { this.PriceChange = PriceChange; }

    public String getChangePercentage() {
        return ChangePercentage;
    }

    public void setChangePercentage(String ChangePercentage) { this.ChangePercentage = ChangePercentage; }

    @Override
    public int compareTo(@NonNull Object obj) { return this.getStockSymbol().compareTo(((Stock) obj).getStockSymbol()); }
}
