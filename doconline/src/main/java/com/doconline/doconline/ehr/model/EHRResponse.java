package com.doconline.doconline.ehr.model;

import com.doconline.doconline.helper.PaginationModel;

import java.util.List;

public class EHRResponse extends PaginationModel
{
    private List<EHR> data;

    public List<EHR> getEHRList()
    {
        return this.data;
    }

    public void setEHRList(List<EHR> data)
    {
        this.data = data;
    }
}