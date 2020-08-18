package com.doconline.doconline.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_APPOINTMENT_DATE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_APPOINTMENT_ID;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PACKAGENAME;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PARTNERIMAGE;

/**
 * Created by Developer on 3/28/2018.
 */

public class DiagnosticsHistoryItem implements Serializable {
    String partnerImgURL;
    String appointmentID;
    String productName;
    String appointmentDate;

    public DiagnosticsHistoryItem(String appointmentID, String appointmentDate, String productName, String partnerImgURL) {
        this.appointmentID = appointmentID;
        this.appointmentDate = appointmentDate;
        this.productName = productName;
        this.partnerImgURL = partnerImgURL;
    }

    public String getPartnerImgURL() {
        return partnerImgURL;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getProductName() {
        return productName;
    }

    public static ArrayList<DiagnosticsHistoryItem> getDiagnosticsHistoryItemListFromJSON(JSONArray jsonObject) {

        ArrayList<DiagnosticsHistoryItem> mDiagnosticsHistoryItemList = new ArrayList<>();

        JSONArray diagnosticsHistory = null;
        try {
            diagnosticsHistory = jsonObject;

            DiagnosticsHistoryItem item;
            for (int i = 0; i < diagnosticsHistory.length(); i++) {

                JSONObject json = diagnosticsHistory.getJSONObject(i);

                item = new DiagnosticsHistoryItem(
                        json.getString(KEY_DIAGNOSTICS_APPOINTMENT_ID),
                        json.getString(KEY_DIAGNOSTICS_APPOINTMENT_DATE),
                        json.getString(KEY_DIAGNOSTICS_PACKAGENAME),
                        json.getString(KEY_DIAGNOSTICS_PARTNERIMAGE)
                );
                mDiagnosticsHistoryItemList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mDiagnosticsHistoryItemList;
    }

    public static Boolean compareGreaterThanEqualDates(String d1, String d2) {

        ArrayList<DiagnosticsHistoryItem> mDiagnosticsHistoryItemList = new ArrayList<>();
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            System.out.println("Date1" + sdf.format(date1));
            System.out.println("Date2" + sdf.format(date2));
            System.out.println();

            if (date1.after(date2)) {
                System.out.println("Date1 is after Date2");
                return true;
            }

            if (date1.equals(date2)) {
                System.out.println("Date1 is equal Date2");
                return true;
            }

            System.out.println();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return false;

    }

    public static Boolean compareLessThanDates(String d1, String d2) {

        ArrayList<DiagnosticsHistoryItem> mDiagnosticsHistoryItemList = new ArrayList<>();
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            System.out.println("Date1" + sdf.format(date1));
            System.out.println("Date2" + sdf.format(date2));
            System.out.println();

            if (date1.before(date2)) {
                System.out.println("Date1 is before Date2");
                return true;
            }

            System.out.println();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return false;

    }
}
