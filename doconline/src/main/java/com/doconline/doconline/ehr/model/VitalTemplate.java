package com.doconline.doconline.ehr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VitalTemplate implements Serializable
{
    private String type = "";
    private Object value;

    private String key = "";
    private String label = "";
    private String unit = "";

    private VitalRanges ranges;

    @SerializedName("fields")
    private List<VitalTemplate> child_field;


    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Object getValue()
    {
        return this.value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String getKey()
    {
        return this.key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getLabel()
    {
        return this.label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getUnit()
    {
        return this.unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public VitalRanges getRanges()
    {
        return this.ranges;
    }

    public void setRanges(VitalRanges ranges)
    {
        this.ranges = ranges;
    }

    public List<VitalTemplate> getChildField()
    {
        return this.child_field;
    }

    public void setChildField(List<VitalTemplate> child_field)
    {
        this.child_field = child_field;
    }


    public boolean hasChild()
    {
        return child_field != null && child_field.size() != 0;
    }
}