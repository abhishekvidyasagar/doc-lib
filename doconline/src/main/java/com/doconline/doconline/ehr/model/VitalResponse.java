package com.doconline.doconline.ehr.model;

import com.doconline.doconline.helper.PaginationModel;

import java.util.List;

public class VitalResponse extends PaginationModel
{
    private List<Vital> data;

    public List<Vital> getData()
    {
        return this.data;
    }

    public void setData(List<Vital> data)
    {
        this.data = data;
    }
}