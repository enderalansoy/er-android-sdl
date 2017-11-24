package com.easyroute.content.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
/**
 * Created by imenekse on 20/01/17.
 */

public class Place implements Serializable {

    private String Id;
    private String name;
    private String address;
    private String fullText;
    private String primaryText;
    private String secondaryText;
    private boolean isCurrentLocation;
    private double latitude;
    private double longitude;

    public void setCurrentLocation(boolean currentLocation) {
        isCurrentLocation = currentLocation;
    }

    public boolean isCurrentLocation() {
        return isCurrentLocation;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public void setLatLng(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public boolean isAirport() {
        if (fullText != null) {
            return fullText.contains("havalimanı") || fullText.contains("airport") || fullText.contains("havaalanı");
        }
        return false;
    }
}
