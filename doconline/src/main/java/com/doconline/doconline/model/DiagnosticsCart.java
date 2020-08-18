package com.doconline.doconline.model;

import com.doconline.doconline.app.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2018-03-16.
 */

public class DiagnosticsCart implements Serializable {

    float totalCartPrice;
    int iCartItemQty;

    ArrayList<DiagnosticsCartItem> cartItemsList;

    public DiagnosticsCart() {
        totalCartPrice = 0.f;
        iCartItemQty = 0;
        this.cartItemsList = new ArrayList<>();
    }

    public DiagnosticsCart(float totalCartPrice, int iCartItemQty) {
        this.totalCartPrice = totalCartPrice;
        this.iCartItemQty = iCartItemQty;
        cartItemsList = new ArrayList<>();
    }

    public float getTotalCartPrice() {
        return totalCartPrice;
    }

    public void setTotalCartPrice(float totalCartPrice) {
        this.totalCartPrice = totalCartPrice;
    }

    public int getiCartItemQty() {
        return iCartItemQty;
    }

    public void setiCartItemQty(int iCartItemQty) {
        this.iCartItemQty = iCartItemQty;
    }

    public ArrayList<DiagnosticsCartItem> getCartItemsList() {
        return cartItemsList;
    }

    public void setCartItemsList(ArrayList<DiagnosticsCartItem> cartItemsList) {
        this.cartItemsList = cartItemsList;
    }

    public int getCartItemsCount() {
        return this.cartItemsList.size();
    }

    public void addNewCartItem(DiagnosticsCartItem _item) {

        if(null != _item) {
            this.cartItemsList.add(_item);

            if (this.cartItemsList.size() > 0)
                this.CalculateTotalPriceOfItems();
        }
    }

    public void deleteCartItem(int packageID) {

        int found = 0;

        for (DiagnosticsCartItem cartItem : cartItemsList) {

            if (cartItem.productID == packageID) {
                break;
            } else
                found++;
        }

        this.cartItemsList.remove(found);

        if(this.cartItemsList.size() > 0)
            this.CalculateTotalPriceOfItems();
    }

    private void CalculateTotalPriceOfItems() {

        totalCartPrice = 0.f;
        for(int i=0; i < cartItemsList.size(); i++)
            totalCartPrice+= cartItemsList.get(i).productPrice;
    }

    public void clearCartItems() {

        this.cartItemsList.clear();
        this.totalCartPrice = 0.f;
    }

    public void UpdateCartInformation(int cartCount, float price){
        this.setTotalCartPrice(price);
        this.setiCartItemQty(cartCount);
    }

    public static DiagnosticsCart getDiagnosticsCartDetailsFromJSON(JSONObject cartInfo) {

        DiagnosticsCart cart = new DiagnosticsCart();
        cart.cartItemsList = new ArrayList<>();

        try {

            int cartCount = cartInfo.getInt(Constants.KEY_DIAGNOSTICS_CART_COUNT);
            float cartTotalAmt = (float)cartInfo.getInt(Constants.KEY_DIAGNOSTICS_CART_AMOUNT);

            if (cartCount > 0) {

                cart.setiCartItemQty(cartCount);
                cart.setTotalCartPrice(cartTotalAmt);

                JSONArray jsonArrayCartItems = cartInfo.getJSONArray(Constants.KEY_DIAGNOSTICS_CART_DATA);
                for (int i = 0; i < jsonArrayCartItems.length(); i++) {
                    JSONObject json = jsonArrayCartItems.getJSONObject(i);

                    cart.cartItemsList.add(new DiagnosticsCartItem(
                            json.getInt(Constants.KEY_DIAGNOSTICS_CART_PACKAGEID),
                            json.getInt(Constants.KEY_DIAGNOSTICS_CART_PACKAGE_QUANTITY),
                            json.getString(Constants.KEY_DIAGNOSTICS_CART_PACKAGE_NAME),
                            (float) json.getDouble(Constants.KEY_DIAGNOSTICS_CART_PACKAGE_PRICE),
                                    json.getInt(Constants.KEY_DIAGNOSTICS_PARTNER_ID))
                    );
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cart;
    }
}
