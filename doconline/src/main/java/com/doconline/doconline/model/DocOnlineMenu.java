package com.doconline.doconline.model;

import com.doconline.doconline.app.MyApplication;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.TYPE_B2B;

/**
 * Created by chiranjitbardhan on 08/01/18.
 */

public class DocOnlineMenu
{
    private int id;
    private boolean isEnable;
    private String title, description, icon;
    private Appointment mAppointment = new Appointment();


    DocOnlineMenu(int id, String title, String description, String icon)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
    }


    DocOnlineMenu(int id, String title, String description, String icon, boolean isEnable)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.isEnable = isEnable;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public static List<DocOnlineMenu> getHomeMenuList()
    {
        List<DocOnlineMenu> mMenu = new ArrayList<>();

        mMenu.add(new DocOnlineMenu(1, "Book a Consultation", "Book a Consultation as per your convenience!", "ic_book_consultation"));
        mMenu.add(new DocOnlineMenu(2, "My Appointments", "View all your appointment details in one place!", "ic_my_appointments"));
        mMenu.add(new DocOnlineMenu(3, "Order Medicines", "How about the pharmacy coming to your doorstep?\nMin Order Rs. 200/-", "ic_order_medicines"));
        mMenu.add(new DocOnlineMenu(4, "Chat with Doctor", "Put your questions to our panel of experts!", "ic_chat"));
        mMenu.add(new DocOnlineMenu(5, "Book Diagnostics", "Book Diagnostic  tests/packages at your doorstep." /*"Need diagnosis at your convenience?"*/, "ic_book_diagnostics"));
        mMenu.add(new DocOnlineMenu(6, "Medical Records(EHR)", "You can save all your Medical Records here.", "ic_medical_records_ehr"));
        mMenu.add(new DocOnlineMenu(7, "Medicine Reminders", "You can set reminders for your medicines here.", "ic_medicine_reminders"));
        mMenu.add(new DocOnlineMenu(8, "Add Ons", "Are you monitoring your health regularly?", "ic_add_ons"));

        if (MyApplication.getInstance().getSession().getUserType().equalsIgnoreCase(TYPE_B2B)) {
            mMenu.add(new DocOnlineMenu(9, "Wellness", "Book activities from the top fitness studios around you", "ic_wellness"));
            mMenu.add(new DocOnlineMenu(10, "HRA", "You can calculate your health risk assessment here", "ic_hra"));
        }

        return mMenu;
    }


    public static List<DocOnlineMenu> getDashboardMenuList()
    {
        List<DocOnlineMenu> mMenu = new ArrayList<>();

        mMenu.add(new DocOnlineMenu(1, "Book a Consultation", "Book a Consultation as per your convenience!", "ic_book_consultation", true));
        mMenu.add(new DocOnlineMenu(2, "My Appointments(EHR)", "View all your appointment details in one place!", "ic_my_appointments    ", false));
        mMenu.add(new DocOnlineMenu(3, "Order Medicines", "How about the pharmacy coming to your doorstep?\nMin Order Rs. 200/-", "ic_order_medicines", false));
        mMenu.add(new DocOnlineMenu(4, "Chat with Doctor", "Put your questions to our panel of experts!", "ic_chat", false));
        mMenu.add(new DocOnlineMenu(3, "Book Diagnostics", "Book Diagnostic  tests/packages at your doorstep.", "ic_book_diagnostics", false));
        mMenu.add(new DocOnlineMenu(4, "Add Ons", "Are you monitoring your health regularly?", "ic_add_ons", false));

        return mMenu;
    }

    public static List<DocOnlineMenu> getAddOnsMenuList()
    {
        List<DocOnlineMenu> mMenu = new ArrayList<>();

        mMenu.add(new DocOnlineMenu(1, "BMI (Body Mass Index)", "Know your BMI ", "ic_icon_bmi"));
        mMenu.add(new DocOnlineMenu(2, "Speed Test", "Know your bandwidth speed ", "ic_icon_bmi"));
        //mMenu.add(new DocOnlineMenu(3, "HRA", "Know your health risk assessment", "ic_icon_add_on_bmi"));

        return mMenu;
    }

    public Appointment getAppointment()
    {
        return this.mAppointment;
    }

    public void setAppointment(Appointment mAppointment)
    {
        this.mAppointment = mAppointment;
    }

    public boolean isEnable()
    {
        return this.isEnable;
    }

    public void setEnable(boolean enable)
    {
        this.isEnable = enable;
    }
}