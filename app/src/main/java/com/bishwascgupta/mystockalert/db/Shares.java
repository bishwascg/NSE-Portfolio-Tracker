package com.bishwascgupta.mystockalert.db;



public class Shares {

    private String stockName;
    private int noOfShares;
    private float cmp, change;
    private float lower, upper;

    public Shares(String stockName, float cmp, float change, int noOfShares, float lower, float upper){
        this.stockName = stockName;
        this.noOfShares = noOfShares;
        this.cmp = cmp;
        this.change = change;
        this.lower = lower;
        this.upper = upper;
    }

    public Shares(String stockName, float cmp, float change, int noOfShares){
        this.stockName = stockName;
        this.noOfShares = noOfShares;
        this.cmp = cmp;
        this.change = change;
        this.lower = -9.9f;
        this.upper = -9.9f;
    }

    public Shares(String stockName, float lower, float upper){
        this.stockName = stockName;
        this.lower = lower;
        this.upper = upper;
    }

    public Shares(String stockName, float cmp, float lower, float upper){
        this.cmp = cmp;
        this.stockName = stockName;
        this.lower = lower;
        this.upper = upper;
    }


    public Shares() {

    }

    public float getLower()
    {
        return this.lower;
    }

    public void setLower(float lower)
    {
        this.lower = lower;
    }

    public float getUpper(){
        return this.upper;
    }

    public void setUpper(float upper){
        this.upper = upper;
    }

    public String getStockName() {
        return this.stockName;
    }

    public void setStockName(String stockName){
        this.stockName = stockName;
    }

    public int getNoOfShares() {
        return this.noOfShares;
    }

    public void setNoOfShares(int noOfShares){
        this.noOfShares = noOfShares;
    }

    public float getCmp(){
        return this.cmp;
    }

    public void setCmp(float cmp){
        this.cmp = cmp;
    }

    public  float getChange(){
        return  Float.parseFloat(String.format("%.2f", this.change));
    }

    public void setChange(float change){
        this.change = change;
    }

    public int getValue() {
        return (int) (this.cmp*this.noOfShares);
    }
}
