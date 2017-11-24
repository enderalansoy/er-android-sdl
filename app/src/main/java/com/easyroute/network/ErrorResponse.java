package com.easyroute.network;

import com.android.volley.VolleyError;
import com.easyroute.network.model.BaseModel;
import com.easyroute.network.request.BaseRequest;
/**
 * Created by imenekse on 22/02/17.
 */

public class ErrorResponse extends BaseModel {

    private String message;
    private VolleyError error;
    private BaseRequest request;
    private ServiceMethod serviceMethod;

    public ServiceMethod getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(ServiceMethod serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VolleyError getError() {
        return error;
    }

    public void setError(VolleyError error) {
        this.error = error;
    }

    public BaseRequest getRequest() {
        return request;
    }

    public void setRequest(BaseRequest request) {
        this.request = request;
    }
}
