package com.doconline.doconline.ehr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Vital implements Serializable
{
    @SerializedName("_id")
    private String id;
    private String created_at;
    private long recorded_at;

    @SerializedName(value = "records", alternate = "vitals")
    private List<VitalTemplate> vitals;

    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getCreatedAt()
    {
        return this.created_at;
    }

    public void setCreatedAt(String created_at)
    {
        this.created_at = created_at;
    }

    public long getRecordedAt()
    {
        return this.recorded_at;
    }

    public void setRecordedAt(long recorded_at)
    {
        this.recorded_at = recorded_at;
    }

    public List<VitalTemplate> getVitals()
    {
        return this.vitals;
    }

    public void setVitals(List<VitalTemplate> vitals)
    {
        this.vitals = vitals;
    }
}