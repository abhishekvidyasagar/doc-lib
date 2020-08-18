package com.doconline.doconline.ehr.model;

import com.doconline.doconline.utils.FileUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EHR implements Serializable
{
    @SerializedName("_id")
    private String id;
    private String created_at;
    private String recorded_at;
    private int category_id;
    private int user_id;

    private List<FileUtils> files;


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

    public String getRecordedAt()
    {
        return this.recorded_at;
    }

    public void setRecordedAt(String recorded_at)
    {
        this.recorded_at = recorded_at;
    }

    public int getCategoryId()
    {
        return this.category_id;
    }

    public void setCategoryId(int category_id)
    {
        this.category_id = category_id;
    }

    public int getUser_id()
    {
        return this.user_id;
    }

    public void setUserId(int user_id)
    {
        this.user_id = user_id;
    }

    public List<FileUtils> getFiles()
    {
        return this.files;
    }

    public void setFiles(List<FileUtils> files)
    {
        this.files = files;
    }
}