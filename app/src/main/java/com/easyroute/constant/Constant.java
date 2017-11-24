package com.easyroute.constant;

import com.easyroute.BuildConfig;
import com.google.android.gms.maps.model.LatLng;
public class Constant {

    public static final boolean DEBUG = true;
    public static final String SERVICE_BASE_URL_TEST = "";
    public static final String SERVICE_BASE_URL_PROD = "";
    public static final LatLng ISTANBUL_COORDINATE = new LatLng(41.011237, 28.982008);
    public static final String GET_TOLL_COLLECTION_PRICE_SERVICE_URL;
    public static final String GET_TOLL_COLLECTION_PRICE_SERVICE_NAMESPACE;
    public static final String GET_TOLL_COLLECTION_PRICE_SERVICE_METHOD;

    static {
        System.loadLibrary("app");
        String[] constants = getConstants();
        GET_TOLL_COLLECTION_PRICE_SERVICE_URL = constants[0];
        GET_TOLL_COLLECTION_PRICE_SERVICE_NAMESPACE = constants[1];
        GET_TOLL_COLLECTION_PRICE_SERVICE_METHOD = constants[2];
    }

    public static native String[] getConstants();

    public static String getServiceBaseUrl() {
        if (BuildConfig.DEBUG) {
            if (DEBUG) {
                return SERVICE_BASE_URL_TEST;
            } else {
                return SERVICE_BASE_URL_PROD;
            }
        } else {
            return SERVICE_BASE_URL_PROD;
        }
    }


}

