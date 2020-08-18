package com.doconline.doconline.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_ADDRESS_1;
import static com.doconline.doconline.app.Constants.KEY_ADDRESS_2;
import static com.doconline.doconline.app.Constants.KEY_CODE;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_ITEMS;
import static com.doconline.doconline.app.Constants.KEY_PIN_CODE;
import static com.doconline.doconline.app.Constants.KEY_STATUS;

/**
 * Created by cbug on 25/9/17.
 */

public class Medicine
{
    private String medicine_name, medicine_id, medicine_type, manufacturer, pack_size, mrp, price;
    private int quantity, required_quantity, available_quantity, required_pack_size, default_required_pack_size;
    private double discount;

    public Medicine(String medicine_name, String medicine_id, String manufacturer, int available_quantity, int required_quantity, String pack_size, String mrp, double discount, String price, int required_pack_size, int default_required_pack_size) {
        this.setMedicineName(medicine_name);
        this.setMedicineId(medicine_id);
        this.setManufacturer(manufacturer);
        this.setAvailableQuantity(available_quantity);
        this.setRequiredQuantity(required_quantity);
        this.setPackSize(pack_size);
        this.setMrp(mrp);
        this.setDiscount(discount);
        this.setPrice(price);
        this.setRequired_pack_size(required_pack_size);
        this.setDefault_required_pack_size(default_required_pack_size);
    }


    public Medicine(String medicine_id, String medicine_name, String medicine_type, String manufacturer, String pack_size, int quantity, String mrp, double discount, String price)
    {
        this.setMedicineId(medicine_id);
        this.setMedicineName(medicine_name);
        this.setMedicineType(medicine_type);
        this.setManufacturer(manufacturer);
        this.setPackSize(pack_size);
        this.setQuantity(quantity);
        this.setMrp(mrp);
        this.setDiscount(discount);
        this.setPrice(price);
    }

    public void setMedicineName(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getMedicineName() {
        return this.medicine_name;
    }

    public void setMedicineId(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getMedicineId() {
        return this.medicine_id;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setAvailableQuantity(int available_quantity) {
        this.available_quantity = available_quantity;
    }

    public int getAvailableQuantity() {
        return this.available_quantity;
    }

    public void setRequiredQuantity(int required_quantity) {
        this.required_quantity = required_quantity;
    }

    public int getRquiredQuantity() {
        return this.required_quantity;
    }

    public void setPackSize(String pack_size) {
        this.pack_size = pack_size;
    }

    public String getPackSize() {
        return this.pack_size;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getMrp() {
        return this.mrp;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return this.discount;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return this.price;
    }

    public void setRequired_pack_size(int required_pack_size) {
        this.required_pack_size = required_pack_size;
    }

    public int getRequired_pack_size() {
        return this.required_pack_size;
    }

    public void setDefault_required_pack_size(int default_required_pack_size) {
        this.default_required_pack_size = default_required_pack_size;
    }

    public int getDefault_required_pack_size() {
        return this.default_required_pack_size;
    }


    public static HashMap<String, Integer> isAllMedicineAvailable(List<Medicine> medicineList)
    {
        HashMap<String, Integer> hm = new HashMap<>();

        for(Medicine em: medicineList)
        {
            if(em.getAvailableQuantity() == 0)
            {
                hm.put(em.getMedicineName(), em.getAvailableQuantity());
            }
        }

        return hm;
    }


    public static ArrayList<Medicine> getMedicineFromJSON(String json_data)
    {
        ArrayList<Medicine> mList = new ArrayList<>();

        try
        {
            JSONObject json = new JSONObject(json_data);

            int code = json.getInt(KEY_CODE);
            String status = json.getString(KEY_STATUS);
            json = json.getJSONObject(KEY_DATA);

            Iterator keys = json.keys();
            JSONArray jsonArray = new JSONArray();

            while (keys.hasNext())
            {
                String key = (String) keys.next();
                jsonArray.put(json.get(key));
            }

            for (int i = 0; i < jsonArray.length(); i++)
            {
                double discount = 0;
                int req_packs, available_qty = 0;

                String pro_id = jsonArray.getJSONObject(i).getString("product_id");
                int required_quantity = jsonArray.getJSONObject(i).getInt("qty");
                String product_name = jsonArray.getJSONObject(i).getString("name");
                String manufacturer = jsonArray.getJSONObject(i).getString("manf");
                String price = jsonArray.getJSONObject(i).getString("price");
                String packSize = jsonArray.getJSONObject(i).getString("packsize");

                if(jsonArray.getJSONObject(i).has("discountPercentage"))
                {
                    try
                    {
                        discount = jsonArray.getJSONObject(i).getDouble("discountPercentage");
                    }

                    catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                    }
                }

                String mrp = jsonArray.getJSONObject(i).getString("mrp");

                if(jsonArray.getJSONObject(i).has("availableQty"))
                {
                    String availableQuantity = jsonArray.getJSONObject(i).getString("availableQty");

                    try
                    {
                        available_qty = (int) Double.parseDouble(availableQuantity);
                    }

                    catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                    }
                }

                int packsize = Integer.parseInt(packSize);

                if(packsize > 0)
                {
                    double req_packs_double = Math.ceil((double) required_quantity / packsize);
                    req_packs = (int) req_packs_double;
                }

                else
                {
                    req_packs = required_quantity;
                }

                Medicine editMedicine = new Medicine(product_name, pro_id, manufacturer, available_qty, required_quantity, packSize, mrp, discount, price, req_packs, req_packs);
                mList.add(editMedicine);
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return mList;
    }


    public static HashMap<String, Object> composePincodeMap(String pincode)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_PIN_CODE, pincode);

        return hashMap;
    }


    public static String getMedicineJSON(List<Medicine> mList, Address mAddress)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put(KEY_ADDRESS_1, mAddress.getAddress_1());
            json.put(KEY_ADDRESS_2, "" + mAddress.getAddress_2());
            json.put(KEY_PIN_CODE, "" + mAddress.getPincode());

            JSONObject json_item = new JSONObject();

            for (Medicine m : mList)
            {
                json_item.put("" + m.getMedicineId(), "" + m.getRequired_pack_size());
            }

            json.put(KEY_ITEMS, json_item);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json.toString();
    }

    public int getQuantity()
    {
        return this.quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public String getMedicineType()
    {
        return this.medicine_type;
    }

    public void setMedicineType(String medicine_type) {
        this.medicine_type = medicine_type;
    }
}