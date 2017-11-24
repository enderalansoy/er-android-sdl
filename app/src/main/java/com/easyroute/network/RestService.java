package com.easyroute.network;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.easyroute.App;
import com.easyroute.R;
import com.easyroute.listener.ServiceListener;
import com.easyroute.network.request.BaseRequest;
import com.easyroute.network.response.BaseResponse;
import com.easyroute.utility.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RestService {

    private static RestService instance;

    private final String TAG = App.getInstance().getApplicationContext().getResources().getString(R.string.app_name);
    private final int RETRY_COUNT = 0;

    private List<ServiceListener> mOtherListeners;
    private RequestQueue mRequestQueue;
    private Map<String, String> mHeaders;
    private List<Object> mErrorCounter;

    public static RestService getInstance(Context context) {
        if (instance == null) {
            instance = new RestService(context);
        }
        return instance;
    }

    private RestService(Context context) {
        mRequestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        mRequestQueue.start();
        mErrorCounter = new ArrayList<>();
        mOtherListeners = new ArrayList<>();
        mHeaders = new HashMap<>();
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Content-Type", "application/json");
    }

    public static RestService newInstance(Context context) {
        return new RestService(context);
    }

    public void addListener(ServiceListener listener) {
        mOtherListeners.add(listener);
    }

    public void removeListener(ServiceListener listener) {
        mOtherListeners.remove(listener);
    }

    public void cancelRequest(ServiceMethod method) {
        mRequestQueue.cancelAll(method);
    }

    public void setAuthorizationToken(String token) {
        addHeader("Authorization", token);
    }

    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    public void send(BaseRequest request) {
        L.e(TAG, "Message sending: " + request.getServiceMethod() + " : " + request);
        clearErrorCounter(request);
        RequestType requestType = request.getServiceMethod().getRequestType();
        if (requestType == RequestType.PARAMETER) {
            mRequestQueue.add(generateStringRequest(request));
        } else if (requestType == RequestType.BODY) {
            mRequestQueue.add(generateGsonRequest(request));
        } else {
            mRequestQueue.add(generateQueryStringRequest(request));
        }
    }

    public void send(ServiceMethod method) {
        L.e(TAG, "Message sending: " + method);
        clearErrorCounter(method);
        mRequestQueue.add(generateGsonRequest(method));
    }

    public void retry(BaseRequest request) {
        L.e(TAG, "Retrying: " + request.getServiceMethod() + " : " + request);
        RequestType requestType = request.getServiceMethod().getRequestType();
        if (requestType == RequestType.PARAMETER) {
            mRequestQueue.add(generateStringRequest(request));
        } else if (requestType == RequestType.BODY) {
            mRequestQueue.add(generateGsonRequest(request));
        } else {
            mRequestQueue.add(generateQueryStringRequest(request));
        }
    }

    public void retry(ServiceMethod method) {
        L.e(TAG, "Retrying: " + method);
        mRequestQueue.add(generateGsonRequest(method));
    }

    public void clearErrorCounter(Object object) {
        Iterator<Object> iterator = mErrorCounter.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (object instanceof BaseRequest) {
                if (obj.getClass() == object.getClass()) {
                    iterator.remove();
                }
            } else {
                if (obj == object) {
                    iterator.remove();
                }
            }
        }
    }

    public boolean canRetry(Object object) {
        int i = 0;
        for (Object obj : mErrorCounter) {
            if (object instanceof BaseRequest) {
                if (object.getClass() == obj.getClass()) {
                    i++;
                }
            } else {
                if (object == obj) {
                    i++;
                }
            }
        }
        return i < RETRY_COUNT;
    }

    private <T> void handleSuccessResponse(ServiceMethod method, BaseRequest request, T response) {
        ((BaseResponse) response).setRequest(request);
        ((BaseResponse) response).setServiceMethod(method);
        for (ServiceListener listener : mOtherListeners) {
            listener.onSuccessResponse(method, (BaseResponse) response);
            listener.onRequestFinish(method);
        }
    }

    private void handleErrorResponse(ServiceMethod method, BaseRequest request, VolleyError error) {
        L.e(error.toString() + "\n" + error.getMessage());
        if ((error instanceof NoConnectionError || error instanceof TimeoutError) && canRetry(request)) {
            if (request != null) {
                mErrorCounter.add(request);
            } else {
                mErrorCounter.add(method);
            }
            retry(request);
        } else {
            for (ServiceListener listener : mOtherListeners) {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setError(error);
                errorResponse.setMessage(error.getMessage());
                errorResponse.setRequest(request);
                errorResponse.setServiceMethod(method);
                listener.onErrorResponse(method, errorResponse);
                listener.onRequestFinish(method);
            }
        }
    }

    private <T> Response.Listener<T> newResponseListener(final ServiceMethod method, final BaseRequest request) {
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                handleSuccessResponse(method, request, response);
            }
        };
    }

    private Response.ErrorListener newErrorListener(final ServiceMethod method, final BaseRequest request) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorResponse(method, request, error);
            }
        };
    }

    private <T> QueryStringRequest<T> generateQueryStringRequest(final BaseRequest request) {
        Response.Listener<T> responseListener = newResponseListener(request.getServiceMethod(), request);
        Response.ErrorListener errorListener = newErrorListener(request.getServiceMethod(), request);
        ServiceMethod method = request.getServiceMethod();
        QueryStringRequest queryStringRequest = new QueryStringRequest(method, request, mHeaders, responseListener, errorListener);
        queryStringRequest.setTag(request.getServiceMethod());
        return queryStringRequest;
    }

    private <T> ParameterRequest<T> generateStringRequest(final BaseRequest request) {
        ServiceMethod method = request.getServiceMethod();
        Response.Listener<T> responseListener = newResponseListener(request.getServiceMethod(), request);
        Response.ErrorListener errorListener = newErrorListener(request.getServiceMethod(), request);
        ParameterRequest parameterRequest = new ParameterRequest(method, request, mHeaders, responseListener, errorListener);
        parameterRequest.setTag(request.getServiceMethod());
        return parameterRequest;
    }

    private <T> BodyRequest<T> generateGsonRequest(final BaseRequest request) {
        ServiceMethod method = request.getServiceMethod();
        Response.Listener<T> responseListener = newResponseListener(request.getServiceMethod(), request);
        Response.ErrorListener errorListener = newErrorListener(request.getServiceMethod(), request);
        BodyRequest bodyRequest = new BodyRequest<T>(method, request, mHeaders, responseListener, errorListener);
        bodyRequest.setTag(request.getServiceMethod());
        return bodyRequest;
    }

    private <T> BodyRequest<T> generateGsonRequest(final ServiceMethod method) {
        Response.Listener<T> responseListener = newResponseListener(method, null);
        Response.ErrorListener errorListener = newErrorListener(method, null);
        BodyRequest request = new BodyRequest(method, null, mHeaders, responseListener, errorListener);
        request.setTag(method);
        return request;
    }
}
