package com.easyroute;

import android.app.Application;

import com.easyroute.Sdl.LockScreenActivity;
import com.easyroute.constant.Event;
import com.easyroute.listener.EventListener;
import com.easyroute.listener.ServiceListener;
import com.easyroute.network.ErrorResponse;
import com.easyroute.network.ServiceMethod;
import com.easyroute.network.response.BaseResponse;
import com.easyroute.network.response.InitializeResponse;
import com.easyroute.utility.PermissionHelper;
import com.inrix.sdk.InrixCore;
/**
 * Created by imenekse on 30/01/17.
 */

public class App extends Application implements ServiceListener, EventListener {

    private static App sApp;

    private InitializeResponse mInitializeResponse;

    public static App getInstance() {
        return sApp;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        LockScreenActivity.registerActivityLifecycle(this);
        if (PermissionHelper.hasAllPermissions(this)) {
            InrixCore.initialize(this);
        }
    }

    @Override
    public void onSuccessResponse(ServiceMethod method, BaseResponse data) {
        if (method == ServiceMethod.INITIALIZE) {
            mInitializeResponse = (InitializeResponse) data;
        }
    }

    @Override
    public void onErrorResponse(ServiceMethod method, ErrorResponse errorResponse) {}

    @Override
    public void onRequestFinish(ServiceMethod method) {}

    @Override
    public void onEventReceive(Event event, Object data) {}

    public InitializeResponse getInitializeResponse() {
        return mInitializeResponse;
    }
}
