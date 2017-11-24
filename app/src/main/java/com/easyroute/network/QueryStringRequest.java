package com.easyroute.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.easyroute.App;
import com.easyroute.R;
import com.easyroute.network.response.BaseResponse;
import com.easyroute.utility.L;
import com.easyroute.utility.Xson;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Created by imenekse on 23/01/17.
 */

public class QueryStringRequest<T> extends Request<T> {

    private static final String CHARSET = "UTF-8";
    private final String TAG = App.getInstance().getApplicationContext().getResources().getString(R.string.app_name);

    private Gson mGson;
    private Xson mXson;
    private String mUrl;
    private Class<T> mResponseClass;
    private Map<String, String> mHeaders;
    private Map<String, String> mParams;
    private Response.Listener mListener;
    private ResponseFormat mResponseFormat;

    public QueryStringRequest(ServiceMethod serviceMethod, Object body, Map<String, String> headers, Response.Listener listener, Response.ErrorListener errorListener) {
        super(serviceMethod.getMethodType(), serviceMethod.getUrl(), errorListener);
        mResponseClass = (Class<T>) serviceMethod.getResponseClass();
        mResponseFormat = serviceMethod.getResponseFormat();
        mHeaders = headers;
        mListener = listener;
        mGson = new Gson();
        String parametersInJson = mGson.toJson(body);
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        mParams = mGson.fromJson(parametersInJson, type);
        if (mResponseFormat == ResponseFormat.XML) {
            mXson = new Xson();
        }
        mUrl = prepareUrl(serviceMethod.getUrl());
        L.e("QueryString: " + mUrl);
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=UTF-8";
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            T result;
            if (mResponseFormat == ResponseFormat.JSON) {
                String responseString = new String(response.data, CHARSET);
                result = mGson.fromJson(responseString, mResponseClass);
                L.e(TAG, "Message received: " + responseString);
            } else if (mResponseFormat == ResponseFormat.XML) {
                String responseString = new String(response.data, CHARSET);
                result = mXson.fromXml(responseString, mResponseClass);
                L.e(TAG, "Message received: " + responseString);
            } else {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setByteData(response.data);
                result = (T) baseResponse;
                L.e(TAG, "Message received: byte data");
            }
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    private String prepareUrl(String url) {
        String preparedUrl = url;
        Entry<String, String>[] entrySet = (Entry<String, String>[]) mParams.entrySet().toArray(new Entry[mParams.size()]);
        for (int i = 0; i < entrySet.length; i++) {
            Entry<String, String> param = entrySet[i];
            try {
                String key = URLEncoder.encode(param.getKey(), "utf-8");
                String value = URLEncoder.encode(param.getValue(), "utf-8");
                if (i == 0) {
                    preparedUrl += "?";
                } else {
                    preparedUrl += "&";
                }
                preparedUrl += key + "=" + value;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return preparedUrl;
    }
}
