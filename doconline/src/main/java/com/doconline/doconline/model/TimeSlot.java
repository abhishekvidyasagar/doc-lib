package com.doconline.doconline.model;

import java.io.Serializable;

/**
 * Created by chiranjit on 14/12/16.
 */
public class TimeSlot implements Serializable
{
    public String start_time, slot_date;

    public TimeSlot(String slot_date)
    {
        this.slot_date = slot_date;
    }

    public TimeSlot(String slot_date, String start_time)
    {
        this.slot_date = slot_date;
        this.start_time = start_time;
    }
}