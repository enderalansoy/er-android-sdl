package com.easyroute.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.easyroute.constant.Event;
import com.easyroute.listener.EventListener;
import com.easyroute.listener.ServiceListener;
import com.easyroute.network.ErrorResponse;
import com.easyroute.network.ServiceMethod;
import com.easyroute.network.request.BaseRequest;
import com.easyroute.network.response.BaseResponse;
import com.easyroute.ui.activity.BaseActivity;
import com.easyroute.utility.UI;

public abstract class BaseDialog extends DialogFragment implements ServiceListener, EventListener {

    private final int DEFAULT_WIDTH = 300;

    public int getLayoutId() {
        return -1;
    }

    public void initViews() {}

    public void bindEvents() {}

    public void defineObjects() {}

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

    public Context context;
    public BaseActivity activity;

    private ViewGroup vgContainer;
    private boolean mIsCanceledOnTouchOutside = false;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(UI.dpToPx(DEFAULT_WIDTH, getActivity()), LayoutParams.WRAP_CONTENT);
        }
    }

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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(mIsCanceledOnTouchOutside);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    public void setCanceledOnTouchOutside(boolean val) {
        mIsCanceledOnTouchOutside = val;
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
}
