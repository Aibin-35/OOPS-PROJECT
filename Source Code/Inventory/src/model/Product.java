// src/model/Product.java
package model;

import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    private int categoryId;
    private String categoryName; 
    private int quantity;
    private double unitCost;
    private LocalDate expiryDate;
    private int addedByUserId;

    public Product() {}
    public Product(int id, String name, int categoryId, String categoryName,
                   int quantity, double unitCost, LocalDate expiryDate, int addedByUserId) {
    	
        this.id=id; 
        this.name=name;
        this.categoryId=categoryId; 
        this.categoryName=categoryName;
        this.quantity=quantity;
        this.unitCost=unitCost; 
        this.expiryDate=expiryDate; 
        this.addedByUserId=addedByUserId;
    }
    // getters/setters...
    public int getId(){
    	return id;
    	} 
    public String getName(){
    	return name;}
    public int getCategoryId(){
    	return categoryId;} 
    public String getCategoryName()
    {return categoryName;}
    public int getQuantity(){
    	return quantity;} 
    public double getUnitCost(){
    	return unitCost;}
    public LocalDate getExpiryDate(){
    	return expiryDate;} public int getAddedByUserId(){
    		return addedByUserId;}
    public void setId(int id){
    	this.id=id;} 
    public void setName(String name){
    	this.name=name;}
    public void setCategoryId(int categoryId)
    {this.categoryId=categoryId;}
    public void setCategoryName(String cn){
    	this.categoryName=cn;}
    public void setQuantity(int q){
    	this.quantity=q;} 
    public void setUnitCost(double c){
    	this.unitCost=c;}
    public void setExpiryDate(LocalDate d){
    	this.expiryDate=d;} 
    public void setAddedByUserId(int x){
    	this.addedByUserId=x;}
 
    public void setStockStatus(String s) {  }
    public void setExpiryStatus(String s) {}
 // --- Stock status
    public String getStockStatus() {
        if (quantity <= 0) return "OUT";
        if (quantity < 5)  return "LOW";
        if (quantity <= 20) return "MEDIUM";
        return "HIGH";
    }
    //Expiry Status
    public String getExpiryStatus() {
        if (expiryDate == null) return "N/A";
        java.time.LocalDate today = java.time.LocalDate.now();
        if (expiryDate.isBefore(today)) return "EXPIRED";
        long days = java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
        if (days <= 7) return "SOON";
        return "OK";
    }

    
}

