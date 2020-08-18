package com.doconline.doconline.ehr.model;

import java.io.Serializable;
import java.util.List;

public class VitalTemplateResponse implements Serializable
{
    private String profile_type;
    private List<VitalTemplate> root_fields;
    private List<VitalTemplate> record_fields;


    public String getProfileType()
    {
        return this.profile_type;
    }

    public void setProfileType(String profile_type)
    {
        this.profile_type = profile_type;
    }

    public List<VitalTemplate> getRootFields()
    {
        return this.root_fields;
    }

    public void setRootFields(List<VitalTemplate> root_fields)
    {
        this.root_fields = root_fields;
    }

    public List<VitalTemplate> getRecordFields()
    {
        return this.record_fields;
    }

    public void setRecordFields(List<VitalTemplate> record_fields)
    {
        this.record_fields = record_fields;
    }
}