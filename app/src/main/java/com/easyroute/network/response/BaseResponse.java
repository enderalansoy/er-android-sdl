package com.easyroute.network.response;

import com.easyroute.network.ServiceMethod;
import com.easyroute.network.request.BaseRequest;
import com.google.gson.Gson;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaseResponse implements Serializable {

    private static Gson sGson = new Gson();

    private int statusId;
    private String message;
    private byte[] byteData;
    private ServiceMethod serviceMethod;
    private BaseRequest request;

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return statusId != 0;
    }

    public byte[] getByteData() {
        return byteData;
    }

    public void setByteData(byte[] byteData) {
        this.byteData = byteData;
    }

    public void setRequest(BaseRequest request) {
        this.request = request;
    }

    public BaseRequest getRequest() {
        return request;
    }

    public void setServiceMethod(ServiceMethod serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public ServiceMethod getServiceMethod() {
        return serviceMethod;
    }

    @Override
    public String toString() {
        return sGson.toJson(this);
    }
}
