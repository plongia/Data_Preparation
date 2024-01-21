package com.wiredbraincoffee;

public class SaleTransaction {

    private String uuid;
    private String timestamp;
    private String type;
    private String size;
    private String price;
    private String offer;
    private String discount;
    private long userId;
    private Country country;
    private String city;

    public String getUuid(){
        return uuid;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getSize(){
        return size;
    }

    public void setSize(String size){
        this.size = size;
    }

    public String getPrice(){
        return price;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public String getOffer(){
        return offer;
    }

    public void setOffer(String offer){
        this.offer = offer;
    }

    public String getDiscount(){
        return discount;
    }

    public void setDiscount(String discount){
        this.discount = discount;
    }

    public long getUserId(){
        return userId;
    }

    public void setUserId(long userId){
        this.userId = userId;
    }

    public Country getCountry(){
        return country;
    }

    public void setCountry(Country country){
        this.country = country;
    }

    public String getCity(){
        return city;
    }

    public void setCity(String city){
        this.city = city;
    }

    public enum Country{
        UK, JAPAN, ITALY, CANADA;
    }

    @Override
    public String toString(){
        return "SaleTransaction {" + "uuid = '" + uuid + '\'' +
                ", type = '" + type + '\'' + ", size = '" + size + '\'' +
                ", price = '" + price + '\'' + ", offer = '" + offer + '\'' +
                ", discount = '" + discount + '\'' + ", userId = '" + userId +
                ", country = '" + country.name() + '\'' + ", city = " + city +
                '}';
    }

}
