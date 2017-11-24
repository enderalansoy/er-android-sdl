package com.easyroute.ui.activity;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyroute.R;
import com.easyroute.Sdl.AppLinkService;
import com.easyroute.Sdl.MyPresentation;
import com.easyroute.constant.Event;
import com.easyroute.content.Preference;
import com.easyroute.content.model.CalculatedRoute;
import com.easyroute.content.model.Place;
import com.easyroute.helper.RouteHelper;
import com.easyroute.helper.RouteHelper.OnRoutesCalculateFinishListener;
import com.easyroute.ui.dialog.PrivacyPolicyDialog;
import com.easyroute.ui.dialog.PrivacyPolicyDialog.OnPrivacyPolicyAcceptListener;
import com.easyroute.ui.dialog.VehicleInfoDialog;
import com.easyroute.ui.fragment.MainFragment;
import com.easyroute.ui.fragment.RouteFragment;
import com.easyroute.ui.view.GoogleMapView;
import com.easyroute.ui.view.GoogleMapView.MapViewCallback;
import com.easyroute.utility.Analytics;
import com.easyroute.utility.L;
import com.easyroute.utility.MapUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.IncidentsManager.IIncidentsResponseListener;
import com.inrix.sdk.IncidentsManager.IncidentRadiusOptions;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.RequestRouteOptions;
import com.inrix.sdk.RouteManager.RequestTime;
import com.inrix.sdk.RouteManager.RouteType;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.model.Incident.IncidentType;
import com.inrix.sdk.model.RequestRouteResults;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.utils.UserPreferences;
import com.inrix.sdk.utils.UserPreferences.Unit;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.enums.TouchType;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hotchemi.android.rate.AppRate;
/**
 * Created by imenekse on 01/02/17.
 */

public class MainActivity extends BaseActivity {

    public static final int REQUEST_SELECT_OPTIONS = 88;

    private final int REQUEST_ENABLE_LOCATION_SERVICES = 2;
    private final int REQUEST_LOCATION_PERMISSION = 6;

    public static GoogleMapView mapView;
    private FrameLayout flMapViewContainer;
    private FrameLayout flMainFragmentContainer;
    private FrameLayout flRouteFragmentContainer;
    public static Drawable drawable;


    public static MainFragment frMain;
    private RouteFragment frRoute;

    private IncidentsManager mIncidentsManager;
    private RouteHelper mRouteHelper;
    private RouteManager mRouteManager;
    private ICancellable mRouteRequest;
    private ICancellable mIncidentsRequest;
    private List<Incident> mIncidents;
    private List<Route> mRoutes;
    GoogleMap.SnapshotReadyCallback callback;


    private boolean mIsLocationPermissionGranted;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    public static void onTouchEvent (OnTouchEvent notification) {

    }


    @Override
    public void initViews() {
        flMapViewContainer = (FrameLayout) findViewById(R.id.flMapViewContainer);
        flMainFragmentContainer = (FrameLayout) findViewById(R.id.flMainFragmentContainer);
        flRouteFragmentContainer = (FrameLayout) findViewById(R.id.flRouteFragmentContainer);
    }

    @Override
    public void defineObjects(Bundle state) {
        frMain = new MainFragment();
        frRoute = new RouteFragment();
        mRouteHelper = new RouteHelper(activity);
    }

    @Override
    public void setProperties() {
        replaceFragment(flMainFragmentContainer, frMain, false);
        replaceFragment(flRouteFragmentContainer, frRoute, false);
    }

    @Override
    public void onLayoutCreate() {
        AppRate.with(this).setInstallDays(4).setLaunchTimes(8).monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
        ((MarginLayoutParams) flMapViewContainer.getLayoutParams()).bottomMargin = frMain.getFooterHeight();
        checkPrivacyPolicy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap bitmap) {
               // bitmap.getScaledHeight(2);
             //   bitmap2 = getResizedBitmap(bitmap,1000,720);
                Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 300, bitmap.getWidth(), bitmap.getHeight() - 300);
                drawable = new BitmapDrawable(getResources(), croppedBitmap);
            }
        };

    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }



    @Override
    protected void onStart() {
        super.onStart();

        if (mapView != null) {
            mapView.startLocationUpdates();
        }
        if (AppLinkService.getProxyInstance() == null) {
            Intent startIntent = new Intent(this, AppLinkService.class);
            startService(startIntent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelRouteRequest();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_OPTIONS && resultCode == RESULT_OK) {
            setIncidentsToMapView();
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            checkLocationPermission(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            checkLocationPermission(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (frRoute.isActive()) {
            showMainFragment();
            mapView.showTraffic(frMain.getSelectedDateTime());
        } else if (!frMain.isReset()) {
            resetMain();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onEventReceive(Event event, Object data) {
        if (event == Event.MAIN_ACTIVITY_MY_LOCATION_BUTTON_CLICKED) {
            if (mapView.isCurrentLocationReceived()) {
                mapView.moveCameraToCurrentLocation();
            } else {
                checkLocationSettings();
            }
        } else if (event == Event.MAIN_ACTIVITY_NAVIGATE_BUTTON_CLICKED) {
            NavigationActivity.sRoute = (CalculatedRoute) data;
            startActivity(new Intent(context, NavigationActivity.class));
        } else if (event == Event.MAIN_ACTIVITY_SOURCE_PLACE_SELECTED) {
            Place place = (Place) data;
            mapView.setSourcePlace(place);
        } else if (event == Event.MAIN_ACTIVITY_DESTINATION_PLACE_SELECTED) {
            Place place = (Place) data;
            mapView.setDestinationPlace(place);
        } else if (event == Event.MAIN_ACTIVITY_SECOND_PLACE_SELECTED) {
            sendRouteRequest();
        } else if (event == Event.MAIN_ACTIVITY_ROUTE_PARAMETERS_CHANGED) {
            sendRouteRequest();
        } else if (event == Event.MAIN_FRAGMENT_TIME_CHANGED) {
            mapView.showTraffic(frMain.getSelectedDateTime());
        } else if (event == Event.ROUTE_FRAGMENT_ROUTE_SELECTED) {
            mapView.setSelectedRoute(frRoute.getSelectedRoute());
        } else if (event == Event.VEHICLE_INFO_DIALOG_SAVE) {
            if (frRoute.isActive()) {
                calculateRoutes();
            }
        }
    }

    public void sendIncidentsRequest() {
        cancelIncidentsRequest();
        IncidentRadiusOptions options = new IncidentRadiusOptions(mapView.getCurrentGeoPoint(), 100000);
        mIncidentsRequest = mIncidentsManager.getIncidentsInRadius(options, new IIncidentsResponseListener() {
            @Override
            public void onResult(List<Incident> data) {
                mIncidentsRequest = null;
                mIncidents = data;
                setIncidentsToMapView();
            }

            @Override
            public void onError(Error error) {
                mIncidentsRequest = null;
            }
        });
    }

    private void calculateRoutes() {
        showProgress();
        mRouteHelper.calculateRoutes(mRoutes, new OnRoutesCalculateFinishListener() {
            @Override
            public void onRoutesCalculateFinishListener(CalculatedRoute fast, CalculatedRoute economic, CalculatedRoute relax) {
                L.e(fast);
                L.e(economic);
                L.e(relax);
                if (getPreference().getVehicleInfo() == null) {
                    Analytics.route(context, frRoute.isDepartureTimeOptionSelected(), fast, null, relax);
                    frRoute.setRoutes(frMain.isCurrentLocationSelectedForSource(), fast, null, relax);
                    mapView.setRoutes(fast, null, relax, frRoute.getSelectedRoute());
                } else {
                    Analytics.route(context, frRoute.isDepartureTimeOptionSelected(), fast, economic, relax);
                    frRoute.setRoutes(frMain.isCurrentLocationSelectedForSource(), fast, economic, relax);
                    mapView.setRoutes(fast, economic, relax, frRoute.getSelectedRoute());
                }
                hideProgress();
                showRouteFragment();
            }
        });
    }

    private void setIncidentsToMapView() {
        List<Incident> filteredIncidents = new ArrayList<>();
        if (mIncidents != null) {
            List<IncidentType> activatedIncidentTypes = Preference.getInstance(context).getActivatedIncidentTypes();
            for (Incident incident : mIncidents) {
                if (activatedIncidentTypes.contains(incident.getType())) {
                    filteredIncidents.add(incident);
                }
            }
        }
        mapView.setIncidents(filteredIncidents);
    }

    private void sendRouteRequest() {
        cancelRouteRequest();
        showProgress();
        GeoPoint startCoordinate = frMain.getStartCoordinate();
        GeoPoint destinationCoordinate = frMain.getDestinationCoordinate();
        Date selectedDateTime = frRoute.getSelectedDateTime();
        boolean isDepartureTimeOptionSelected = frRoute.isDepartureTimeOptionSelected();
        RequestRouteOptions options = new RequestRouteOptions(startCoordinate, destinationCoordinate);
        options.setNumAlternates(2);
        options.setUnits(Unit.METERS);
        options.setRouteType(RouteType.FASTEST);
        options.setSpeedBucketsEnabled(true);
        options.setOutputFields(RouteManager.ROUTE_OUTPUT_FIELD_BOUNDING_BOX | RouteManager.ROUTE_OUTPUT_FIELD_POINTS | RouteManager.ROUTE_OUTPUT_FIELD_SUMMARY);
        if (isDepartureTimeOptionSelected) {
            options.setRouteRequestTime(RequestTime.DEPARTURE_TIME, selectedDateTime);
        } else {
            options.setRouteRequestTime(RequestTime.ARRIVAL_TIME, selectedDateTime);
        }
        mRouteRequest = mRouteManager.requestRoutes(options, new IRouteResponseListener() {
            @Override
            public void onResult(RequestRouteResults requestRouteResults) {
                mRouteRequest = null;
                mRoutes = new ArrayList<>(requestRouteResults.getRoutes());
                calculateRoutes();
                if (mRoutes == null || mRoutes.isEmpty()) {
                    snackbar(R.string.main_activity_find_route_method_error_snackbar_message);
                    hideProgress();
                }
            }

            @Override
            public void onError(Error error) {
                mRouteRequest = null;
                hideProgress();
                snackbar(R.string.network_connection_error, R.string.snackbar_retry_button, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendRouteRequest();
                    }
                });
            }
        });
    }

    private void cancelRouteRequest() {
        if (mRouteRequest != null) {
            mRouteRequest.cancel();
            mRouteRequest = null;
        }
    }

    private void cancelIncidentsRequest() {
        if (mIncidentsRequest != null) {
            mIncidentsRequest.cancel();
            mIncidentsRequest = null;
        }
    }

    private void showRouteFragment() {
        frMain.deactivate();
        frRoute.activate();
    }

    private void showMainFragment() {
        frMain.activate();
        frRoute.deactivate(true);
        frRoute.reset();
    }

    private void resetMain() {
        frMain.reset();
        mapView.reset();
    }

    @SuppressWarnings("MissingPermission")
    private void onLocationPermissionGranted() {
        if (mIsLocationPermissionGranted) {
            return;
        }
        mIsLocationPermissionGranted = true;
        if (!InrixCore.isInitialized()) {
            InrixCore.initialize(getApplicationContext());
        }
        UserPreferences.setSettingUnits(Unit.METERS);
        mIncidentsManager = InrixCore.getIncidentsManager();
        mRouteManager = InrixCore.getRouteManager();
        mapView.onLocationPermissionGranted();
        mapView.showTraffic(new Date());
        Analytics.trafficForecast(context, 0);
        sendIncidentsRequest();
        checkVehicleInfoDialogShown();
    }

    private void initializeMapView() {
        showProgress();
        mapView = new GoogleMapView(context);
        mapView.setMapViewCallback(mMapViewCallback);
        flMapViewContainer.addView(mapView);

    }

    private void checkPrivacyPolicy() {
        if (getPreference().isPrivacyPolicyAccepted()) {
            initializeMapView();
        } else {
            PrivacyPolicyDialog privacyPolicyDialog = new PrivacyPolicyDialog();
            privacyPolicyDialog.setOnPrivacyPolicyAcceptListener(new OnPrivacyPolicyAcceptListener() {
                @Override
                public void onPrivacyPolicyAccept() {
                    initializeMapView();
                }
            });
            privacyPolicyDialog.show(getFragmentManager(), null);
        }
    }

    private void checkVehicleInfoDialogShown() {
        if (!getPreference().isVehicleInfoDialogShownForFirstTime()) {
            VehicleInfoDialog.newInstance(true).show(getFragmentManager(), null);
        }
    }

    private void checkLocationPermission(boolean callRequestPermission) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onLocationPermissionGranted();
        } else {
            if (callRequestPermission) {
                ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showLocationPermissionAlert(false);
                } else {
                    showLocationPermissionAlert(true);
                }
            }
        }
    }
    public static Drawable getDrawable() {

        return drawable;
    }



    private void checkLocationSettings() {
        mapView.setMoveToCurrentLocationOnFirstLocationChange(true);
        MapUtils.checkLocationSettings(mapView.getGoogleApiClient(), mapView.getLocationRequest(), new MapUtils.LocationSettingsResultCallback() {
            @Override
            public void onLocationSettingsResultReceive(Status status) {
                if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        status.startResolutionForResult(activity, REQUEST_ENABLE_LOCATION_SERVICES);
                    } catch (IntentSender.SendIntentException e) {}
                } else if (status.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                    snackbar(R.string.enable_location_services_alert_message);
                }
            }
        });
    }

    private void showLocationPermissionAlert(final boolean isNeverAskAgainChecked) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setMessage(R.string.main_activity_my_location_permission_alert_message);
        alert.setPositiveButton(R.string.main_activity_my_location_permission_alert_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNeverAskAgainChecked) {
                    MapUtils.startAppDetailsSettings(activity, REQUEST_LOCATION_PERMISSION);
                } else {
                    checkLocationPermission(true);
                }
            }
        });
        alert.setNegativeButton(R.string.main_activity_my_location_permission_alert_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.create().show();
    }

    public MapViewCallback mMapViewCallback = new MapViewCallback() {
        @Override
        public void onSourceMarkerClick() {
            sendEvent(Event.MAIN_ACTIVITY_SOURCE_MARKER_CLICKED);
        }

        @Override
        public void onDestinationMarkerClick() {
            sendEvent(Event.MAIN_ACTIVITY_DESTINATION_MARKER_CLICKED);
        }

        @Override
        public void onGoogleApiClientConnectionFailure() {
            snackbar(R.string.main_activity_google_api_client_connection_failed_alert_message, R.string.main_activity_google_api_client_connection_failed_alert_retry_button, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapView.connectToGoogleApiClient();
                }
            });
        }

        @Override
        public void onMapReady() {
            hideProgress();
            checkLocationPermission(true);
            mapView.getMap().snapshot(callback);

        }

        @Override
        public void onCameraMove() {
           /* screenView = getWindow().getDecorView();
            screenView.buildDrawingCache();
            drawable = new BitmapDrawable(screenView.getDrawingCache());*/
       /*     Bitmap bitmap = Bitmap.createBitmap(mapView.getWidth(),
                    mapView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            mapView.draw(canvas);
            drawable = new BitmapDrawable(getResources(), bitmap);
            System.out.println("asd");*/



            mapView.getMap().snapshot(callback);

        }

        @Override
        public void onRouteSelect(CalculatedRoute selectedRoute) {
            frRoute.setSelectedRoute(selectedRoute);

        }
    };


}
