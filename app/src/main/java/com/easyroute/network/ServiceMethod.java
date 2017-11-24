package com.easyroute.network;

import com.android.volley.Request.Method;
import com.easyroute.constant.Constant;
import com.easyroute.network.response.InitializeResponse;

public enum ServiceMethod {
    INITIALIZE("http://www.alberun.com/easyroute/api/initialize.php", null, InitializeResponse.class, RequestType.BODY, ResponseFormat.JSON, Method.GET, false, false);

    private static final ResponseFormat DEFAULT_RESPONSE_FORMAT = ResponseFormat.XML;
    private static final RequestType DEFAULT_REQUEST_TYPE = RequestType.QUERY;
    private static final int DEFAULT_METHOD_TYPE = Method.GET;
    private static final boolean DEFAULT_IS_LOCKER = true;
    private static final boolean DEFAULT_IS_SPECIFIC_ERROR = false;

    private final String BASE_URL = Constant.getServiceBaseUrl();

    private String mMethodName;
    private Class<?> mRequestClass;
    private Class<?> mResponseClass;
    private int mMethodType = -1;
    private RequestType mRequestType;
    private ResponseFormat mResponseFormat;
    private boolean mIsLocker = DEFAULT_IS_LOCKER;
    private boolean mIsSpesificError = DEFAULT_IS_SPECIFIC_ERROR;

    ServiceMethod(String methodName, Class<?> requestClass, Class<?> responseClass) {
        mMethodName = methodName;
        mRequestClass = requestClass;
        mResponseClass = responseClass;
    }

    ServiceMethod(String methodName, Class<?> requestClass, Class<?> responseClass, RequestType requestType, ResponseFormat responseFormat, int methodType, boolean isLocker, boolean isSpesificError) {
        mMethodName = methodName;
        mRequestClass = requestClass;
        mResponseClass = responseClass;
        mMethodType = methodType;
        mResponseFormat = responseFormat;
        mIsLocker = isLocker;
        mIsSpesificError = isSpesificError;
        mRequestType = requestType;
    }

    public String getUrl() {
        if (mMethodName.startsWith("http")) {
            return mMethodName;
        } else {
            return BASE_URL + "/" + mMethodName;
        }
    }

    public int getMethodType() {
        if (mMethodType == -1) {
            return DEFAULT_METHOD_TYPE;
        }
        return mMethodType;
    }

    public RequestType getRequestType() {
        if (mRequestType == null) {
            return DEFAULT_REQUEST_TYPE;
        }
        return mRequestType;
    }

    public ResponseFormat getResponseFormat() {
        if (mResponseFormat == null) {
            return DEFAULT_RESPONSE_FORMAT;
        }
        return mResponseFormat;
    }

    public boolean isLocker() {
        return mIsLocker;
    }

    public boolean isSpesificError() {
        return mIsSpesificError;
    }

    public Class<?> getRequestClass() {
        return mRequestClass;
    }

    public Class<?> getResponseClass() {
        return mResponseClass;
    }
}