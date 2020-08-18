package com.doconline.doconline.api.response.settings;

public class HotlineConfig
{
    private int is_hotline;
    private String hotline_number;
    private String number_type;


    public int getIsHotline()
    {
        return this.is_hotline;
    }

    public void setIsHotline(int is_hotline)
    {
        this.is_hotline = is_hotline;
    }

    public String getHotlineNumber()
    {
        return this.hotline_number;
    }

    public void setHotlineNumber(String hotline_number)
    {
        this.hotline_number = hotline_number;
    }

    public String getNumberType()
    {
        return this.number_type;
    }

    public void setNumberType(String number_type)
    {
        this.number_type = number_type;
    }
}