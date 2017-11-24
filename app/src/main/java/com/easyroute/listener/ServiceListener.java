package com.easyroute.listener;

import com.easyroute.network.ErrorResponse;
import com.easyroute.network.ServiceMethod;
import com.easyroute.network.response.BaseResponse;
public interface ServiceListener {

    void onSuccessResponse(ServiceMethod method, BaseResponse data);

    void onErrorResponse(ServiceMethod method, ErrorResponse errorResponse);

    void onRequestFinish(ServiceMethod method);
}
