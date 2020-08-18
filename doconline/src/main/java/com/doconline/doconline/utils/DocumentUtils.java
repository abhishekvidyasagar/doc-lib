package com.doconline.doconline.utils;

import java.util.ArrayList;
import java.util.List;

public class DocumentUtils
{
    private int id;
    private String title;
    private String created_at;
    private String updated_at;
    private String deleted_at;

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getCreatedAt()
    {
        return this.created_at;
    }

    public void setCreatedAt(String created_at)
    {
        this.created_at = created_at;
    }

    public String getUpdatedAt()
    {
        return this.updated_at;
    }

    public void setUpdatedAt(String updated_at)
    {
        this.updated_at = updated_at;
    }

    public String getDeletedAt()
    {
        return deleted_at;
    }

    public void setDeletedAt(String deleted_at)
    {
        this.deleted_at = deleted_at;
    }


    public static String getCategoryName(List<DocumentUtils> docList, int category_id)
    {
        for(DocumentUtils doc: docList)
        {
            if(doc.getId() == category_id)
            {
                return doc.getTitle();
            }
        }

        return "Unknown";
    }


    public static List<DocumentUtils> getActiveCategory(List<DocumentUtils> docList)
    {
        List<DocumentUtils> documentUtils = new ArrayList<>();

        for(DocumentUtils doc: docList)
        {
            if(doc.getDeletedAt() == null)
            {
                documentUtils.add(doc);
            }
        }

        return documentUtils;
    }
}