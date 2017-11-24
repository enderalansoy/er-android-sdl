package com.easyroute.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.easyroute.content.constant.RouteType;
import com.easyroute.content.model.Place;
import com.easyroute.content.model.VehicleInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inrix.sdk.model.Incident.IncidentType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Preference {

    private static Preference sInstance;

    private SharedPreferences mPreferences;
    private Gson mGson;

    public static Preference getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Preference(context);
        }
        return sInstance;
    }

    public Preference(Context context) {
        mGson = new Gson();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mPreferences.edit().commit();
    }

    public void remove(String key) {
        mPreferences.edit().remove(key).commit();
    }

    public void setString(String key, String value) {
        Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setBoolean(String key, Boolean value) {
        Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public void setInt(String key, int value) {
        Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public float getFloat(String key, float defValue) {
        return mPreferences.getFloat(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public void setHomePlace(Place place) {
        String json = mGson.toJson(place);
        setString(Keys.HOME_PLACE, json);
    }

    public Place getHomePlace() {
        String json = getString(Keys.HOME_PLACE, "");
        return mGson.fromJson(json, Place.class);
    }

    public void setWorkPlace(Place place) {
        String json = mGson.toJson(place);
        setString(Keys.WORK_PLACE, json);
    }

    public Place getWorkPlace() {
        String json = getString(Keys.WORK_PLACE, "");
        return mGson.fromJson(json, Place.class);
    }

    public List<Place> getRecentPlaces() {
        String json = getString(Keys.RECENT_PLACES, "[]");
        Type type = new TypeToken<ArrayList<Place>>() {}.getType();
        List<Place> places = mGson.fromJson(json, type);
        return places;
    }

    public void addRecentPlace(Place place) {
        String json = getString(Keys.RECENT_PLACES, "[]");
        Type type = new TypeToken<ArrayList<Place>>() {}.getType();
        List<Place> places = mGson.fromJson(json, type);
        for (Place p : places) {
            if (p.getId().equals(place.getId())) {
                places.remove(p);
                break;
            }
        }
        places.add(0, place);
        if (places.size() > 10) {
            places.remove(10);
        }
        String newJson = mGson.toJson(places);
        setString(Keys.RECENT_PLACES, newJson);
    }

    public VehicleInfo getVehicleInfo() {
        String json = getString(Keys.VEHICLE_INFO, "");
        return mGson.fromJson(json, VehicleInfo.class);
    }

    public void setVehicleInfo(VehicleInfo vehicleInfo) {
        String json = mGson.toJson(vehicleInfo);
        setString(Keys.VEHICLE_INFO, json);
    }

    public boolean isVehicleInfoDialogShownForFirstTime() {
        return getBoolean(Keys.IS_VEHICLE_INFO_DIALOG_SHOWN_FOR_FIRST_TIME, false);
    }

    public void setIsVehicleInfoDialogShownForFirstTime(boolean value) {
        setBoolean(Keys.IS_VEHICLE_INFO_DIALOG_SHOWN_FOR_FIRST_TIME, value);
    }

    public String getDeviceId() {
        return getString(Keys.DEVICE_ID, null);
    }

    public void setDeviceId(String deviceId) {
        setString(Keys.DEVICE_ID, deviceId);
    }

    public void setActivatedIncidentTypes(List<IncidentType> incidentTypes) {
        String json = mGson.toJson(incidentTypes);
        setString(Keys.ACTIVATED_INCIDENT_TYPES, json);
    }

    public List<IncidentType> getActivatedIncidentTypes() {
        String json = getString(Keys.ACTIVATED_INCIDENT_TYPES, null);
        if (json == null) {
            // Uygulamaya ilk kez giriliyor, tum incidentType'lar ekleniyor
            List<IncidentType> all = Arrays.asList(IncidentType.values());
            setActivatedIncidentTypes(all);
            return getActivatedIncidentTypes();
        }
        Type type = new TypeToken<ArrayList<IncidentType>>() {}.getType();
        return mGson.fromJson(json, type);
    }

    public RouteType getDefaultRouteType() {
        String routeType = getString(Keys.DEFAULT_ROUTE_TYPE, RouteType.FAST.toString());
        return RouteType.valueOf(routeType);
    }

    public void setDefaultRouteType(RouteType routeType) {
        setString(Keys.DEFAULT_ROUTE_TYPE, routeType.toString());
    }

    public boolean isPrivacyPolicyAccepted() {
        return getBoolean(Keys.IS_PRIVACY_POLICY_ACCEPTED, false);
    }

    public void setPrivacyPolicyAccepted(boolean value) {
        setBoolean(Keys.IS_PRIVACY_POLICY_ACCEPTED, value);
    }

    public class Keys {

        public static final String DEVICE_ID = "DEVICE_ID";
        public static final String RECENT_PLACES = "RECENT_PLACES";
        public static final String HOME_PLACE = "HOME_PLACE";
        public static final String WORK_PLACE = "WORK_PLACE";
        public static final String VEHICLE_INFO = "VEHICLE_INFO";
        public static final String IS_VEHICLE_INFO_DIALOG_SHOWN_FOR_FIRST_TIME = "IS_VEHICLE_INFO_DIALOG_SHOWN_FOR_FIRST_TIME";
        public static final String ACTIVATED_INCIDENT_TYPES = "ACTIVATED_INCIDENT_TYPES";
        public static final String DEFAULT_ROUTE_TYPE = "DEFAULT_ROUTE_TYPE";
        public static final String IS_PRIVACY_POLICY_ACCEPTED = "IS_PRIVACY_POLICY_ACCEPTED";
    }
}
