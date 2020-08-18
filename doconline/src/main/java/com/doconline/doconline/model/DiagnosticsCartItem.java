package com.doconline.doconline.model;


/**
 * Created by admin on 2018-03-16.
 */

public class DiagnosticsCartItem {

    int productID;
    int productQuantity;
    String productName;
    float productPrice;
    int partnerId;


    public DiagnosticsCartItem( int productID, int productQuantity, String productName, float productPrice, int partnerId) {
        this.productID = productID;
        this.productQuantity = productQuantity;
        this.productName = productName;
        this.productPrice = productPrice;
        this.partnerId = partnerId;
    }

    public int getProductID() {
        return productID;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public String getProductName() {
        return productName;
    }
    public float getProductPrice() {
        return productPrice;
    }

    public int getPartnerId() {
        return partnerId;
    }
}
