package ru.rudedude.sandbox.model.stackexchange;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.rudedude.sandbox.model.ModelEntry;

public class User implements ModelEntry {

    @SerializedName("badge_counts")
    @Expose
    private BadgeCounts badgeCounts;

    @SerializedName("account_id")
    @Expose
    private Integer accountId;

    @SerializedName("is_employee")
    @Expose
    private Boolean isEmployee;

    @SerializedName("last_modified_date")
    @Expose
    private Integer lastModifiedDate;

    @SerializedName("last_access_date")
    @Expose
    private Integer lastAccessDate;

    @SerializedName("reputation_change_year")
    @Expose
    private Integer reputationChangeYear;

    @SerializedName("reputation_change_quarter")
    @Expose
    private Integer reputationChangeQuarter;

    @SerializedName("reputation_change_month")
    @Expose
    private Integer reputationChangeMonth;

    @SerializedName("reputation_change_week")
    @Expose
    private Integer reputationChangeWeek;

    @SerializedName("reputation_change_day")
    @Expose
    private Integer reputationChangeDay;

    @Expose
    private Integer reputation;

    @SerializedName("creation_date")
    @Expose
    private Integer creationDate;

    @SerializedName("user_type")
    @Expose
    private String userType;

    @SerializedName("user_id")
    @Expose
    private Integer userId;

    @Expose
    private Integer age;

    @Expose
    private String location;

    @SerializedName("website_url")
    @Expose
    private String websiteUrl;

    @Expose
    private String link;

    @SerializedName("display_name")
    @Expose
    private String displayName;

    @SerializedName("profile_image")
    @Expose
    private String profileImage;


    public void setWebsiteUrl(String s) {
        websiteUrl = s;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setProfileImage(String s) {
        profileImage = s;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setReputation(Integer s) {
        reputation = s;
    }

    public Integer getReputation() {
        return reputation;
    }

    public void setLocation(String s) {
        location = s;
    }

    public String getLocation() {
        return location;
    }

    public void setLink(String s) {
        link = s;
    }

    public String getLink() {
        return link;
    }

    public void setDisplayName(String s) {
        displayName = s;
    }

    public String getDisplayName() {
        return displayName;
    }
}
