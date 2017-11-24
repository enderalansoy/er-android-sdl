package com.easyroute.ui.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easyroute.R;
import com.easyroute.Sdl.SdlActivity;
import com.easyroute.network.ServiceMethod;
import com.easyroute.network.response.BaseResponse;
import com.easyroute.network.response.InitializeResponse;
import com.easyroute.utility.UI;
import com.easyroute.utility.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class SplashActivity extends BaseActivity {

    private final int REQUEST_UPDATE_GOOGLE_PLAY_SERVICES = 7;

    private ImageView ivLogo;
    private ProgressBar progressBar;
    private TextView tvVersion;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initViews() {
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
    }

    @Override
    public void setProperties() {
        ivLogo.animate().setDuration(0).translationY(-UI.getScreenHeight(context));
        progressBar.animate().setDuration(0).scaleX(0).scaleY(0);
        tvVersion.setText("v" + Utils.getAppVersionName(context));
    }

    @Override
    public void onLayoutCreate() {
        ivLogo.animate().setDuration(250).translationY(0).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.animate().setDuration(500).scaleX(1).scaleY(1).setListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        checkPlayServices();
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
            }

            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_UPDATE_GOOGLE_PLAY_SERVICES) {
            checkPlayServices();
        }
    }

    @Override
    public void onSuccessResponse(ServiceMethod method, BaseResponse data) {
        if (method == ServiceMethod.INITIALIZE) {
            InitializeResponse response = (InitializeResponse) data;

                startActivity(new Intent(context, MainActivity.class));
                finish();

        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                Dialog dialog = googleAPI.getErrorDialog(this, result, REQUEST_UPDATE_GOOGLE_PLAY_SERVICES);
                dialog.show();
                dialog.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
            }
            return false;
        } else {
            sendRequest(ServiceMethod.INITIALIZE);
        }
        return true;
    }
}
