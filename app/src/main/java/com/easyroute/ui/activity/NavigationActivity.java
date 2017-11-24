package com.easyroute.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.easyroute.R;
import com.easyroute.content.model.CalculatedRoute;
import com.easyroute.helper.SKToolsAdvicePlayer;
import com.easyroute.utility.Analytics;
import com.easyroute.utility.SkobblerListeners.MapSurfaceListener;
import com.easyroute.utility.SkobblerListeners.NavigationListener;
import com.easyroute.utility.SkobblerListeners.RouteListener;
import com.inrix.sdk.model.GeoPoint;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitializationListener;
import com.skobbler.ngx.map.SKMapFragment;
import com.skobbler.ngx.map.SKMapSettings;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.navigation.SKAdvisorSettings.SKAdvisorLanguage;
import com.skobbler.ngx.navigation.SKAdvisorSettings.SKAdvisorType;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationSettings.SKNavigationType;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.routing.SKRouteSettings.SKRouteMode;
import com.skobbler.ngx.util.SKLogging;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("MissingPermission")
public class NavigationActivity extends BaseActivity {

    public static CalculatedRoute sRoute;

    private FrameLayout flRoot;
    private SKMapSurfaceView mapView;

    private List<SKPosition> mPoints;
    private SKCurrentPositionProvider mCurrentPositionProvider;
    private String mMapCreatorFilePath;
    private String mMapResourcesPath;
    private boolean mIsSkMapInitialized;

    @Override
    public int getLayoutId() {
        return R.layout.activity_navigation;
    }

    @Override
    public void initViews() {
        flRoot = (FrameLayout) findViewById(R.id.flRoot);
    }

    @Override
    public void defineObjects(Bundle state) {
        mPoints = new ArrayList<>();
        for (GeoPoint position : sRoute.getRoute().getPoints()) {
            mPoints.add(new SKPosition(position.getLatitude(), position.getLongitude()));
        }
    }

    @Override
    public void onLayoutCreate() {
        Analytics.navigationStart(context);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (SKMaps.getInstance().isSKMapsInitialized()) {
            onSKMapInitilized();
        } else {
            SKMaps.getInstance().initializeSKMaps(getApplication(), mSKMapsInitializationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsSkMapInitialized) {
            mCurrentPositionProvider.stopLocationUpdates();
            SKRouteManager.getInstance().clearCurrentRoute();
            SKNavigationManager.getInstance().setNavigationListener(null);
            SKNavigationManager.getInstance().stopNavigation();
        }
    }

    private void onSKMapInitilized() {
        mIsSkMapInitialized = true;
        SKLogging.enableLogs(true);
        mCurrentPositionProvider = new SKCurrentPositionProvider(this);
        mCurrentPositionProvider.setCurrentPositionListener(mCurrentPositionListener);
        mCurrentPositionProvider.requestLocationUpdates(true, true, false);
        mMapResourcesPath = SKMaps.getInstance().getMapInitSettings().getMapResourcesPath();
        mMapCreatorFilePath = mMapResourcesPath + "MapCreator/mapcreatorFile.json";
        SKMapFragment skMapFragment = new SKMapFragment();
        skMapFragment.setMapSurfaceListener(mMapSurfaceListener);
        replaceFragment(flRoot, skMapFragment, false);
    }

    private void onMapReady(SKMapViewHolder mapHolder) {
        mapView = mapHolder.getMapSurfaceView();
        mapView.getMapSettings().setMapDisplayMode(SKMapSettings.SKMapDisplayMode.MODE_3D);
        mapView.getMapSettings().setCompassShown(true);
        mapView.animateToLocation(mPoints.get(0).getCoordinate(), 0);
        prepareAudio();
        createRoute();
    }

    private void prepareAudio() {
        SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
        advisorSettings.setLanguage(SKAdvisorLanguage.LANGUAGE_TR);
        advisorSettings.setAdvisorConfigPath(mMapResourcesPath + "/Advisor");
        advisorSettings.setResourcePath(mMapResourcesPath + "/Advisor/Languages");
        advisorSettings.setAdvisorVoice("tr");
        advisorSettings.setAdvisorType(SKAdvisorType.AUDIO_FILES);
        SKMaps.getInstance().getMapInitSettings().setAdvisorSettings(advisorSettings);
        SKRouteManager.getInstance().setAdvisorSettings(advisorSettings);
    }

    private void launchNavigation() {
        SKNavigationSettings navigationSettings = new SKNavigationSettings();
        navigationSettings.setNavigationType(SKNavigationType.REAL);
        SKNavigationManager navigationManager = SKNavigationManager.getInstance();
        navigationManager.setMapView(mapView);
        navigationManager.setNavigationListener(mNavigationListener);
        navigationManager.startNavigation(navigationSettings);
    }

    private void createRoute() {
        SKRouteManager.getInstance().clearCurrentRoute();
        SKRouteSettings route = new SKRouteSettings();
        route.setMaximumReturnedRoutes(1);
        route.setRouteMode(SKRouteMode.CAR_FASTEST);
        route.setStartCoordinate(mPoints.get(0).getCoordinate());
        route.setDestinationCoordinate(mPoints.get(mPoints.size() - 1).getCoordinate());
        route.setRouteExposed(false);
        SKRouteManager.getInstance().setRouteListener(mRouteListener);
        SKRouteManager.getInstance().calculateRouteWithPoints(mPoints, route);
    }

    private SKMapsInitializationListener mSKMapsInitializationListener = new SKMapsInitializationListener() {
        @Override
        public void onLibraryInitialized(boolean isSuccessful) {
            if (isSuccessful) {
                onSKMapInitilized();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage(getString(R.string.splash_activity_map_not_initialized_alert_message));
                alert.setPositiveButton(getString(R.string.splash_activity_exit_app_alert_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                showDialog(alert.create());
            }
        }
    };

    private MapSurfaceListener mMapSurfaceListener = new MapSurfaceListener() {
        @Override
        public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {
            onMapReady(skMapViewHolder);
        }
    };

    private RouteListener mRouteListener = new RouteListener() {

        @Override
        public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo) {
            launchNavigation();
        }

        @Override
        public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {}
    };

    private NavigationListener mNavigationListener = new NavigationListener() {

        @Override
        public void onSignalNewAdviceWithAudioFiles(String[] audioFiles, boolean b) {
            SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);
        }

        @Override
        public void onDestinationReached() {
            Analytics.navigationEnd(context);
            snackbar(R.string.navigation_activity_destination_reached_snackbar_message, R.string.navigation_activity_destination_reached_snackbar_action, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    };

    private SKCurrentPositionListener mCurrentPositionListener = new SKCurrentPositionListener() {
        private boolean mIsFirstPositionUpdate = true;

        @Override
        public void onCurrentPositionUpdate(SKPosition currentPosition) {
            SKPositionerManager.getInstance().reportNewGPSPosition(currentPosition);
            if (mapView != null && mIsFirstPositionUpdate) {
                mapView.animateToLocation(currentPosition.getCoordinate(), 0);
                mIsFirstPositionUpdate = false;
            }
        }
    };
}
