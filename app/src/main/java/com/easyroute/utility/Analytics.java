package com.easyroute.utility;

import android.content.Context;
import android.os.Bundle;

import com.easyroute.content.constant.RouteType;
import com.easyroute.content.model.CalculatedRoute;
import com.easyroute.content.model.VehicleInfo;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
/**
 * Created by macbookpro on 11/12/15.
 */
public class Analytics {

    public static FirebaseAnalytics sFirebaseAnalytics;

    private static FirebaseAnalytics getFirebaseAnalytics(Context context) {
        if (sFirebaseAnalytics == null) {
            sFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        return sFirebaseAnalytics;
    }

    public static void logEvent(Context context, String key, Bundle params) {
        getFirebaseAnalytics(context).logEvent(key, params);
    }

    public static void trafficForecast(Context context, int minutes) {
        Bundle params = new Bundle();
        params.putInt(Param.VALUE, minutes);
        logEvent(context, "traffic_forecast", params);
    }

    public static void routeType(Context context, RouteType routeType) {
        Bundle params = new Bundle();
        params.putString(Param.ITEM_CATEGORY, routeType.toString());
        logEvent(context, "route_option", params);
    }

    public static void route(Context context, boolean isDepartureTimeOptionSelected, CalculatedRoute fastRoute, CalculatedRoute economicRoute, CalculatedRoute relaxRoute) {
        int routeOptionCount = 0;
        if (fastRoute != null) {
            routeOptionCount++;
        }
        if (economicRoute != null) {
            routeOptionCount++;
        }
        if (relaxRoute != null) {
            routeOptionCount++;
        }
        Bundle params = new Bundle();
        params.putInt(Param.VALUE, routeOptionCount);
        if (isDepartureTimeOptionSelected) {
            params.putString(Param.ITEM_CATEGORY, "departure");
        } else {
            params.putString(Param.ITEM_CATEGORY, "arrival");
        }
        logEvent(context, "request_route", params);
    }

    public static void vehicleInfo(Context context, VehicleInfo vehicleInfo) {
        Bundle params = new Bundle();
        params.putString("fuel_type", vehicleInfo.getFuelType().toString());
        params.putString("vehicle_type", vehicleInfo.getVehicleType().toString());
        params.putDouble("in_city_fuel_consumption", vehicleInfo.getInCityFuelConsumption());
        params.putDouble("highway_fuel_consumption", vehicleInfo.getHighwayFuelConsumption());
        logEvent(context, "vehicle_info", params);
    }

    public static void navigationStart(Context context) {
        logEvent(context, "start_navigation", null);
    }

    public static void navigationEnd(Context context) {
        logEvent(context, "end_navigation", null);
    }

    public static void restoreInstanceState(Context context) {
        logEvent(context, "restore_instance_state", null);
    }
}