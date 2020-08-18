package com.doconline.doconline.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_CREATED_AT;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_ORDER_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_ORDER_ID;

/**
 * Created by cbug on 29/9/17.
 */

public class Orders
{
    private String id, order_id, date, amount, status;
    private List<Medicine> medicineList = new ArrayList<>();
    private Address mAddress = new Address();


    public Orders()
    {

    }


    public Orders(String id, String order_id, String date, String amount)
    {
        this.id = id;
        this.order_id = order_id;
        this.date = date;
        this.amount = amount;
    }


    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }


    public void setOrderId(String order_id)
    {
        this.order_id = order_id;
    }

    public String getOrderId()
    {
        return this.order_id;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getDate()
    {
        return this.date;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getAmount()
    {
        return this.amount;
    }


    public List<Medicine> getMedicineList()
    {
        return this.medicineList;
    }

    public void setMedicineList(List<Medicine> medicineList)
    {
        this.medicineList = medicineList;
    }

    public Address getAddress()
    {
        return this.mAddress;
    }

    public void setAddress(Address mAddress)
    {
        this.mAddress = mAddress;
    }


    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public static double calculateGrandTotal(List<Medicine> medicineList)
    {
        double grand_total = 0;

        for(Medicine m: medicineList)
        {
            grand_total += Double.parseDouble(m.getPrice()) * m.getQuantity();
        }

        return grand_total;
    }


    public static List<Orders> getOrderListFromJSON(String json_data)
    {
        List<Orders> ordersList = new ArrayList<>();

        try
        {
            JSONArray data_array = new JSONArray(json_data);

            for (int i = 0; i < data_array.length(); i++)
            {
                JSONObject json = data_array.getJSONObject(i);

                String id = json.getString(KEY_ID);
                String orderId = json.getString(KEY_ORDER_ID);
                String orderAmount = json.getString(KEY_ORDER_AMOUNT);
                String createdAt = json.getString(KEY_CREATED_AT);

                Orders orderList = new Orders(id, orderId, createdAt, orderAmount);
                ordersList.add(orderList);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return ordersList;
    }
}