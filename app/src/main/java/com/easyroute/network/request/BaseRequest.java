package com.easyroute.network.request;

import com.easyroute.network.ServiceMethod;
import com.google.gson.Gson;

public class BaseRequest {

    private static Gson sGson = new Gson();

    public ServiceMethod getServiceMethod() {
        for (ServiceMethod method : ServiceMethod.values()) {
            if (this.getClass() == method.getRequestClass()) {
                return method;
            }
        }
        return null;
    }

    public boolean isLocker() {
        return getServiceMethod().isLocker();
    }

    @Override
    public String toString() {
        return sGson.toJson(this);
    }
}
