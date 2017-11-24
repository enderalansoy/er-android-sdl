package com.easyroute.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.easyroute.App;
import com.easyroute.R;
import com.easyroute.constant.Event;
import com.easyroute.content.Preference;
import com.easyroute.listener.EventListener;
import com.easyroute.listener.ServiceListener;
import com.easyroute.network.ErrorResponse;
import com.easyroute.network.RestService;
import com.easyroute.network.ServiceMethod;
import com.easyroute.network.request.BaseRequest;
import com.easyroute.network.response.BaseResponse;
import com.easyroute.ui.dialog.ProgressDialog;
import com.easyroute.utility.Analytics;
import com.easyroute.utility.L;
import com.easyroute.utility.UI;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements ServiceListener, EventListener {

    private ViewGroup mContentView;
    private ViewGroup mSnackbarAnchor;

    public Context context;
    public Activity activity;

    private RestService mService;
    private ProgressDialog mProgressDialog;
    private List<ServiceMethod> mAllActiveServiceMethods;
    private List<ServiceMethod> mActiveLockerServiceMethods;
    private List<ServiceListener> mServiceListeners;
    private List<EventListener> mEventListeners;
    private List<Dialog> mActiveDialogs;
    private boolean isRunning;
    private boolean isRecreated;

    @Override
    public void onErrorResponse(ServiceMethod method, ErrorResponse errorResponse) {}

    @Override
    public void onSuccessResponse(ServiceMethod method, BaseResponse data) {}

    @Override
    public void onRequestFinish(ServiceMethod method) {}

    @Override
    public void onEventReceive(Event event, Object data) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLayoutId() != -1) {
            setContentView(getLayoutId());
        }

        context = this;
        activity = this;
        mService = RestService.getInstance(this);
        mService.addListener(mServiceListener);
        mAllActiveServiceMethods = new ArrayList<>();
        mActiveLockerServiceMethods = new ArrayList<>();
        mServiceListeners = new ArrayList<>();
        mEventListeners = new ArrayList<>();
        mActiveDialogs = new ArrayList<>();
        mProgressDialog = new ProgressDialog(this);
        mContentView = (ViewGroup) findViewById(android.R.id.content);
        isRecreated = savedInstanceState != null;
        findSnackbarAnchor();
        initViews();
        defineObjects(savedInstanceState);
        bindEvents();
        setProperties();
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mContentView.post(new Runnable() {
                    @Override
                    public void run() {
                        onLayoutCreate();
                    }
                });
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Analytics.restoreInstanceState(context);
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public int getLayoutId() {return -1;}

    public void initViews() {}

    public void defineObjects(Bundle state) {}

    public void bindEvents() {}

    public void setProperties() {}

    public void onLayoutCreate() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
        mService.removeListener(mServiceListener);
        dismissActiveDialogs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
        showActiveDialogs();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
        hideActiveDialogs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isRunning = true;
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setRunning(boolean val) {
        isRunning = val;
    }

    public View getSnackbarAnchor() {
        return mSnackbarAnchor;
    }

    public boolean isRecreated() {
        return isRecreated;
    }

    public void cancelRequest(ServiceMethod method) {
        mService.cancelRequest(method);
    }

    public void sendRequest(BaseRequest request) {
        mAllActiveServiceMethods.add(request.getServiceMethod());
        if (request.isLocker()) {
            if (mActiveLockerServiceMethods.size() == 0) {
                mProgressDialog.show();
            }
            mActiveLockerServiceMethods.add(request.getServiceMethod());
        }
        mService.send(request);
    }

    public void sendRequest(ServiceMethod method) {
        mAllActiveServiceMethods.add(method);
        if (method.isLocker()) {
            if (mActiveLockerServiceMethods.size() == 0) {
                mProgressDialog.show();
            }
            mActiveLockerServiceMethods.add(method);
        }
        mService.send(method);
    }

    public void sendEvent(Event event) {
        sendEvent(event, null);
    }

    public void sendEvent(Event event, Object data) {
        if (!isRunning) {
            return;
        }
        onEventReceive(event, data);
        for (EventListener l : mEventListeners) {
            l.onEventReceive(event, data);
        }
    }

    public void addServiceListener(ServiceListener listener) {
        mServiceListeners.add(listener);
    }

    public void removeServiceListener(ServiceListener listener) {
        mServiceListeners.remove(listener);
    }

    public void addEventListener(EventListener listener) {
        mEventListeners.add(listener);
    }

    public void removeEventListener(EventListener listener) {
        mEventListeners.remove(listener);
    }

    public boolean isRequestsFinished() {
        return mAllActiveServiceMethods.isEmpty();
    }

    public void showDialog(final Dialog dialog) {
        mActiveDialogs.add(dialog);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    dialog.show();
                }
            }
        });
    }

    public void dismissDialog(final Dialog dialog) {
        mActiveDialogs.remove(dialog);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRunning && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    public void showActiveDialogs() {
        for (Dialog dialog : mActiveDialogs) {
            dialog.show();
        }
    }

    public void hideActiveDialogs() {
        for (Dialog dialog : mActiveDialogs) {
            dialog.hide();
        }
    }

    public void dismissActiveDialogs() {
        for (Dialog dialog : mActiveDialogs) {
            dialog.dismiss();
        }
        mActiveDialogs.clear();
    }

    public void replaceFragment(View view, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(view.getId(), fragment);
        if (addToBackStack) {
            ft.addToBackStack(fragment.getClass().getSimpleName());
        }
        ft.commit();
    }

    public void snackbar(String text) {
        UI.snackbar(mSnackbarAnchor, text, Snackbar.LENGTH_LONG).show();
    }

    public void snackbar(int textResId) {
        UI.snackbar(mSnackbarAnchor, getString(textResId), Snackbar.LENGTH_LONG).show();
    }

    public void snackbar(int textResId, int duration) {
        UI.snackbar(mSnackbarAnchor, getString(textResId), duration).show();
    }

    public void snackbar(int textResId, int actionResId, View.OnClickListener actionClickListener) {
        UI.snackbar(mSnackbarAnchor, getString(textResId), getString(actionResId), actionClickListener, Snackbar.LENGTH_INDEFINITE).show();
    }

    public void snackbar(String text, String action, View.OnClickListener actionClickListener) {
        UI.snackbar(mSnackbarAnchor, text, action, actionClickListener, Snackbar.LENGTH_INDEFINITE).show();
    }

    public void toast(int textResId) {
        UI.toast(context, textResId, Toast.LENGTH_LONG);
    }

    public void showProgress() {
        mProgressDialog.show();
    }

    public void hideProgress() {
        mProgressDialog.hide();
    }

    public Preference getPreference() {
        return Preference.getInstance(context);
    }

    private void findSnackbarAnchor() {
        mSnackbarAnchor = (ViewGroup) findViewById(R.id.root);
        if (mSnackbarAnchor == null) {
            mSnackbarAnchor = mContentView;
        }
    }

    private ServiceListener mServiceListener = new ServiceListener() {

        @Override
        public void onSuccessResponse(ServiceMethod method, BaseResponse data) {
            if (data == null) {
                snackbar(R.string.service_unexpected_error);
            } else if (data.isError()) {
                if (!method.isSpesificError() && data.getMessage() != null && !data.getMessage().isEmpty()) {
                    snackbar(data.getMessage());
                } else if (data.getMessage() == null || data.getMessage().isEmpty()) {
                    snackbar(R.string.service_unexpected_error);
                }
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setMessage(data.getMessage());
                App.getInstance().onErrorResponse(method, errorResponse);
                BaseActivity.this.onErrorResponse(method, errorResponse);
                for (ServiceListener l : mServiceListeners) {
                    l.onErrorResponse(method, errorResponse);
                }
            } else {
                App.getInstance().onSuccessResponse(method, data);
                BaseActivity.this.onSuccessResponse(method, data);
                for (ServiceListener l : mServiceListeners) {
                    l.onSuccessResponse(method, data);
                }
            }
        }

        @Override
        public void onErrorResponse(ServiceMethod method, ErrorResponse errorResponse) {
            String message = errorResponse.getMessage();
            if (message != null) {
                L.e("Service exception: " + message);
            }
            VolleyError volleyError = errorResponse.getError();
            if (!(volleyError instanceof AuthFailureError)) {
                if (message != null && message.startsWith("java.net.ConnectException")) {
                    message = getString(R.string.network_connection_error);
                } else {
                    message = getString(R.string.service_unexpected_error);
                }
                if (!method.isSpesificError()) {
                    snackbar(message);
                }
            }
            errorResponse.setMessage(message);
            App.getInstance().onErrorResponse(method, errorResponse);
            BaseActivity.this.onErrorResponse(method, errorResponse);
            for (ServiceListener l : mServiceListeners) {
                l.onErrorResponse(method, errorResponse);
            }
        }

        @Override
        public void onRequestFinish(ServiceMethod method) {
            mAllActiveServiceMethods.remove(method);
            if (method.isLocker()) {
                mActiveLockerServiceMethods.remove(method);
                if (mActiveLockerServiceMethods.size() == 0) {
                    mProgressDialog.hide();
                }
            }
            App.getInstance().onRequestFinish(method);
            BaseActivity.this.onRequestFinish(method);
            for (ServiceListener l : mServiceListeners) {
                l.onRequestFinish(method);
            }
        }
    };
}
