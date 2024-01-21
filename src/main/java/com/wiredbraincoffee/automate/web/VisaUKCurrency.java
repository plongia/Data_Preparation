package com.wiredbraincoffee.automate.web;

public enum VisaUKCurrency {
    GBP("British Pound", "GBP"),
    EUR("Euro", "EUR"),
    JPY("Japanese Yen", "JPY"),
    CAD("Canadian Dollar", "CAD");

    private final String currencyName;
    private final String currencySymbol;

    VisaUKCurrency(String name, String symbol){
        this.currencyName = name;
        this.currencySymbol = symbol;

    }

    public String getLabel() {
        return String.format("%s (%s)", currencyName, currencySymbol);
    }

    public String getCurrencyName(){
        return currencyName;
    }

    public String getCurrencySymbol(){
        return currencySymbol;
    }

}
