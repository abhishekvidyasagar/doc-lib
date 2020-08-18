package com.doconline.doconline.api.response.settings;

public class FamilyConfig
{
    private int family_members_count;
    private int family_members_allowed;
    private boolean family_members_config;
    private String message;


    public int getFamilyMembersCount()
    {
        return this.family_members_count;
    }

    public void setFamilyMembersCount(int family_members_count)
    {
        this.family_members_count = family_members_count;
    }

    public int getFamilyMembersAllowed()
    {
        return this.family_members_allowed;
    }

    public void setFamilyMembersAllowed(int family_members_allowed)
    {
        this.family_members_allowed = family_members_allowed;
    }

    public boolean getFamilyMembersConfig()
    {
        return this.family_members_config;
    }

    public void setFamilyMembersConfig(boolean family_members_config)
    {
        this.family_members_config = family_members_config;
    }

    public String getMessage()
    {
        return this.message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}