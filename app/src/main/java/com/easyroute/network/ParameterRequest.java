package com.easyroute.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.easyroute.App;
import com.easyroute.R;
import com.easyroute.utility.L;
import com.easyroute.utility.Xson;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;
/**
 * Created by imenekse on 23/01/17.
 */

public class ParameterRequest<T> extends com.android.volley.toolbox.StringRequest {

    private static final String CHARSET = "UTF-8";
    private final String TAG = App.getInstance().getApplicationContext().getResources().getString(R.string.app_name);

    private Gson mGson;
    private Xson mXson;
    private Class<T> mResponseClass;
    private Map<String, String> mHeaders;
    private Map<String, String> mParams;
    private Response.Listener mListener;
    private Response.ErrorListener mErrorListener;
    private ResponseFormat mResponseFormat;

    public ParameterRequest(ServiceMethod serviceMethod, Object body, Map<String, String> headers, Response.Listener listener, Response.ErrorListener errorListener) {
        super(serviceMethod.getMethodType(), serviceMethod.getUrl(), listener, errorListener);
        mGson = new Gson();
        mResponseClass = (Class<T>) serviceMethod.getResponseClass();
        mHeaders = headers;
        mListener = listener;
        mErrorListener = errorListener;
        mResponseFormat = serviceMethod.getResponseFormat();
        String parametersInJson = mGson.toJson(body);
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        mParams = mGson.fromJson(parametersInJson, type);
        if (mResponseFormat == ResponseFormat.XML) {
            mXson = new Xson();
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    protected void deliverResponse(String response) {
        T result;
        if (mResponseFormat == ResponseFormat.JSON) {
            result = mGson.fromJson(response, mResponseClass);
        } else {
            result = mXson.fromXml(response, mResponseClass);
        }
        mListener.onResponse(result);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = new String(response.data, CHARSET);
            L.e(TAG, "Message received: " + responseString);
            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
