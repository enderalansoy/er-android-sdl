package com.easyroute.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.easyroute.App;
import com.easyroute.R;
import com.easyroute.utility.L;
import com.easyroute.utility.Xson;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class BodyRequest<T> extends Request<T> {

    private static final String CHARSET = "UTF-8";
    private final String TAG = App.getInstance().getApplicationContext().getResources().getString(R.string.app_name);

    private Gson mGson;
    private Xson mXson;
    private Class<T> mResponseClass;
    private String mBody;
    private Map<String, String> mHeaders;
    private Response.Listener<T> mListener;
    private ResponseFormat mResponseFormat;

    public BodyRequest(ServiceMethod serviceMethod, Object body, Map<String, String> headers, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(serviceMethod.getMethodType(), serviceMethod.getUrl(), errorListener);
        mResponseClass = (Class<T>) serviceMethod.getResponseClass();
        mResponseFormat = serviceMethod.getResponseFormat();
        mHeaders = headers;
        mListener = listener;
        if (mResponseFormat == ResponseFormat.JSON) {
            mGson = new Gson();
            mBody = mGson.toJson(body);
        } else {
            // Burasi henuz yapilmadi
            // mXson = new Xson();
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    public byte[] getBody() {
        try {
            return mBody == null ? null : mBody.getBytes(CHARSET);
        } catch (UnsupportedEncodingException uee) {
            return null;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = new String(response.data, CHARSET);
            L.e(TAG, "Message received: " + responseString);
            T result;
            if (mResponseFormat == ResponseFormat.JSON) {
                result = mGson.fromJson(responseString, mResponseClass);
            } else {
                result = mXson.fromXml(responseString, mResponseClass);
            }
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}