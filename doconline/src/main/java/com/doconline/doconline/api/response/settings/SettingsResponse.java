package com.doconline.doconline.api.response.settings;

import com.doconline.doconline.utils.DocumentUtils;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SettingsResponse
{
    @SerializedName("document_categories")
    private List<DocumentUtils> docCategories;

    @SerializedName("family_member")
    private FamilyConfig familyConfig;

    @SerializedName("hotline")
    private HotlineConfig hotlineConfig;

    private int ehr_document_consent;
    private int max_file_size;
    private String vital_info_url;


    public List<DocumentUtils> getDocCategories()
    {
        return this.docCategories;
    }

    public void setDocCategories(List<DocumentUtils> docCategories)
    {
        this.docCategories = docCategories;
    }


    public FamilyConfig getFamilyConfig() {
        return this.familyConfig;
    }

    public void setFamilyConfig(FamilyConfig familyConfig) {
        this.familyConfig = familyConfig;
    }

    public int getDocumentConsent()
    {
        return this.ehr_document_consent;
    }

    public void setDocumentConsent(int ehr_document_consent)
    {
        this.ehr_document_consent = ehr_document_consent;
    }

    public HotlineConfig getHotlineConfig()
    {
        return this.hotlineConfig;
    }

    public void setHotlineConfig(HotlineConfig hotlineConfig)
    {
        this.hotlineConfig = hotlineConfig;
    }

    public int getMaxFileSize()
    {
        return this.max_file_size;
    }

    public void setMaxFileSize(int max_file_size)
    {
        this.max_file_size = max_file_size;
    }

    public String getVitalInfoUrl() {
        return this.vital_info_url;
    }

    public void setVitalInfoUrl(String vital_info_url) {
        this.vital_info_url = vital_info_url;
    }
}