package com.easyroute.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.easyroute.constant.Event;
import com.easyroute.content.Preference;
import com.easyroute.listener.EventListener;
import com.easyroute.listener.ServiceListener;
import com.easyroute.network.ErrorResponse;
import com.easyroute.network.ServiceMethod;
import com.easyroute.network.request.BaseRequest;
import com.easyroute.network.response.BaseResponse;
import com.easyroute.ui.activity.BaseActivity;

public abstract class BaseFragment extends Fragment implements ServiceListener, EventListener {

    public Context context;
    public BaseActivity activity;

    private ViewGroup vgContainer;

    public int getLayoutId() { return -1; }

    public void initViews() {}

    public void defineObjects() {}

    public void bindEvents() {}

    public void setProperties() {}

    public void onLayoutCreate() {}

    @Override
    public void onSuccessResponse(ServiceMethod method, BaseResponse data) {}

    @Override
    public void onErrorResponse(ServiceMethod method, ErrorResponse errorResponse) {}

    @Override
    public void onRequestFinish(ServiceMethod method) {}

    @Override
    public void onEventReceive(Event event, Object data) {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        context = activity;
        activity.addServiceListener(this);
        activity.addEventListener(this);
        if (getLayoutId() != -1) {
            vgContainer = (ViewGroup) inflater.inflate(getLayoutId(), null);
        }
        initViews();
        defineObjects();
        bindEvents();
        setProperties();
        if (vgContainer != null) {
            ViewTreeObserver observer = vgContainer.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        vgContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        onLayoutCreate();
                    }
                });
            }
        }
        return vgContainer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.removeServiceListener(this);
        activity.removeEventListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ((BaseActivity) getActivity()).setRunning(true);
    }

    public View findViewById(int id) {
        return vgContainer.findViewById(id);
    }

    public void sendRequest(BaseRequest request) {
        activity.sendRequest(request);
    }

    public void sendRequest(ServiceMethod method) {
        activity.sendRequest(method);
    }

    public void sendEvent(Event event) {
        sendEvent(event, null);
    }

    public void sendEvent(Event event, Object data) {
        activity.sendEvent(event, data);
    }

    public void snackbar(int textResId) {
        activity.snackbar(textResId);
    }

    public Preference getPreference() {
        return Preference.getInstance(context);
    }

    public int getColor(int id) {
        return ContextCompat.getColor(context, id);
    }
}
