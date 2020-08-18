package com.doconline.doconline.ehr.model;

import com.google.gson.annotations.SerializedName;

public class VitalRanges
{
    private float min;
    private float max;

    @SerializedName("neonate") private VitalRanges neonate;
    @SerializedName("adult") private VitalRanges adult;
    @SerializedName("infant") private VitalRanges infant;
    @SerializedName("toddler") private VitalRanges toddler;
    @SerializedName("school_age") private VitalRanges school_age;
    @SerializedName("children") private VitalRanges children;
    @SerializedName("children_13") private VitalRanges children_13;


    public float getMinRange()
    {
        return this.min;
    }

    public void setMinRange(float min)
    {
        this.min = min;
    }

    public float getMaxRange()
    {
        return this.max;
    }

    public void setMaxRange(float max)
    {
        this.max = max;
    }


    public VitalRanges getNeonate()
    {
        return this.neonate;
    }

    public void setNeonate(VitalRanges neonate)
    {
        this.neonate = neonate;
    }


    public VitalRanges getAdult()
    {
        return this.adult;
    }

    public void setAdult(VitalRanges adult)
    {
        this.adult = adult;
    }


    public VitalRanges getInfant()
    {
        return this.infant;
    }

    public void setInfant(VitalRanges infant)
    {
        this.infant = infant;
    }


    public VitalRanges getToddler()
    {
        return this.toddler;
    }

    public void setToddler(VitalRanges toddler)
    {
        this.toddler = toddler;
    }


    public VitalRanges getSchoolAge()
    {
        return this.school_age;
    }

    public void setSchoolAge(VitalRanges school_age)
    {
        this.school_age = school_age;
    }


    public VitalRanges getChildren()
    {
        return this.children;
    }

    public void setChildren(VitalRanges children)
    {
        this.children = children;
    }


    public VitalRanges getChildren13()
    {
        return this.children_13;
    }

    public void setChildren13(VitalRanges children_13)
    {
        this.children_13 = children_13;
    }
}