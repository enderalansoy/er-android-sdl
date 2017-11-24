package com.easyroute.ui.view;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easyroute.R;
import com.easyroute.constant.Constant;
import com.easyroute.content.model.CalculatedRoute;
import com.easyroute.content.model.Place;
import com.easyroute.ui.activity.BaseActivity;
import com.easyroute.utility.GeoPointHelper;
import com.easyroute.utility.MapUtils;
import com.easyroute.utility.UI;
import com.easyroute.utility.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"MissingPermission"})
public class GoogleMapView extends FrameLayout implements OnMapReadyCallback, OnMarkerClickListener, OnCameraMoveListener, OnMapClickListener {

    public interface MapViewCallback {

        void onSourceMarkerClick();

        void onDestinationMarkerClick();

        void onGoogleApiClientConnectionFailure();

        void onMapReady();

        void onCameraMove();

        void onRouteSelect(CalculatedRoute selectedRoute);
    }

    private final int MAP_ZOOM_DEFAULT = 10;
    private final int MAP_ZOOM_CURRENT_LOCATION = 13;
    private final int MAP_ZOOM_SINGLE_LOCATION_SELECTED = 11;

    public static Location sCurrentLocation;

    private SupportMapFragment frMap;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Marker mSourceMarker;
    private Marker mDestinationMarker;
    private Marker mSourceInfoMarker;
    private Marker mDestinationInfoMarker;
    private Place mSourcePlace;
    private Place mDestinationPlace;
    private MapViewCallback mMapViewCallback;
    private boolean mIsLocationServicesEnabledOnInitilize;
    private boolean mIsMoveToCurrentLocationOnFirstLocationChange;
    private ClusterManager<MapClusterItem> mClusterManager;
    private CalculatedRoute mFastRoute;
    private CalculatedRoute mEconomicRoute;
    private CalculatedRoute mRelaxRoute;
    private CalculatedRoute mSelectedRoute;
    private TileOverlay mTrafficTileOverlay;
    private InrixTileRouteOverlay mRouteTileOverlay;
    private List<MapClusterItem> mMapClusterItems;

    public GoogleMapView(Context context) {
        super(context);
        setId(Utils.generateViewId());
        mContext = context;
        frMap = new SupportMapFragment();
        ((BaseActivity) context).getSupportFragmentManager().beginTransaction().add(getId(), frMap).commit();
        mMapClusterItems = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Places.GEO_DATA_API).addApi(LocationServices.API).addConnectionCallbacks(mGoogleConnectionCallbacks).addOnConnectionFailedListener(mGoogleConnectionFailedListener).build();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        sCurrentLocation = MapUtils.getLastKnownLocation(context);
        mIsLocationServicesEnabledOnInitilize = MapUtils.isLocationServicesEnabled(context);
        if (mIsLocationServicesEnabledOnInitilize) {
            mSourcePlace = new Place();
            mSourcePlace.setCurrentLocation(true);
        }
        frMap.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constant.ISTANBUL_COORDINATE, MAP_ZOOM_DEFAULT));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMapClickListener(this);
        if (sCurrentLocation != null && !mIsLocationServicesEnabledOnInitilize) {
            moveCameraToCurrentLocation();
        }
        mClusterManager = new ClusterManager<>(mContext, mMap);
        mClusterManager.setRenderer(new MapClusterRenderer(mContext, mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);
        mMapViewCallback.onMapReady();
    }

    @Override
    public void onCameraMove() {
        mCameraMoveCallbackHandler.removeMessages(0);
        mCameraMoveCallbackHandler.sendEmptyMessageDelayed(0, 100);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        checkRouteClick(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(mSourceMarker) || marker.equals(mSourceInfoMarker)) {
            mMapViewCallback.onSourceMarkerClick();
            return true;
        } else if (marker.equals(mDestinationMarker) || marker.equals(mDestinationInfoMarker)) {
            mMapViewCallback.onDestinationMarkerClick();
            return true;
        }
        return false;
    }

    public static GeoPoint getCurrentGeoPoint() {
        if (sCurrentLocation == null) {
            return new GeoPoint(Constant.ISTANBUL_COORDINATE.latitude, Constant.ISTANBUL_COORDINATE.longitude);
        } else {
            return new GeoPoint(sCurrentLocation.getLatitude(), sCurrentLocation.getLongitude());
        }
    }

    public static LatLng getCurrentLatLng() {
        if (sCurrentLocation == null) {
            return Constant.ISTANBUL_COORDINATE;
        } else {
            return new LatLng(sCurrentLocation.getLatitude(), sCurrentLocation.getLongitude());
        }
    }

    public void reset() {
        boolean isLocationServicesEnabled = MapUtils.isLocationServicesEnabled(mContext);
        if (isLocationServicesEnabled) {
            mSourcePlace = new Place();
            mSourcePlace.setCurrentLocation(true);
            if (sCurrentLocation != null) {
                moveCameraToCurrentLocation();
            }
        }
        clearMarkers();
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void setIncidents(List<Incident> incidents) {
        mMapClusterItems.clear();
        for (Incident incident : incidents) {
            MapClusterItem item = new MapClusterItem(incident);
            mMapClusterItems.add(item);
        }
        showIncidents();
    }

    public void showTraffic(Date dateTime) {
        clearRoute();
        showIncidents();
        TileOverlayOptions options = new TileOverlayOptions();
        options.fadeIn(true);
        options.tileProvider(new InrixTrafficTileProvider(InrixCore.getTileManager(), dateTime.getTime()));
        if (mTrafficTileOverlay != null) {
            mTrafficTileOverlay.remove();
        }
        mTrafficTileOverlay = mMap.addTileOverlay(options);
    }

    public void hideTraffic() {
        hideIncidents();
        mTrafficTileOverlay.remove();
    }

    public void setRoutes(CalculatedRoute fast, CalculatedRoute economic, CalculatedRoute relax, CalculatedRoute selectedRoute) {
        setSourcePlace(mSourcePlace);
        setDestinationPlace(mDestinationPlace);
        hideTraffic();
        mFastRoute = fast;
        mEconomicRoute = null;
        mRelaxRoute = null;
        mSelectedRoute = selectedRoute;
        if (economic != null) {
            if (fast != economic && fast != relax && economic != relax) {
                mEconomicRoute = economic;
                mRelaxRoute = relax;
            } else if (fast == economic && fast == relax) {
                // bos
            } else if (fast == economic) {
                mRelaxRoute = relax;
            } else if (fast == relax) {
                mEconomicRoute = economic;
            } else if (economic == relax) {
                mEconomicRoute = economic;
            }
        } else {
            if (fast != relax) {
                mRelaxRoute = relax;
            } else {
                // bos
            }
        }
        mRouteTileOverlay.displayRoute(mFastRoute, mEconomicRoute, mRelaxRoute, selectedRoute);
    }

    public void clearMarkers() {
        if (mSourceMarker != null) {
            mSourceMarker.remove();
            mSourceMarker = null;
        }
        if (mSourceInfoMarker != null) {
            mSourceInfoMarker.remove();
            mSourceInfoMarker = null;
        }
        if (mDestinationMarker != null) {
            mDestinationMarker.remove();
            mDestinationMarker = null;
        }
        if (mDestinationInfoMarker != null) {
            mDestinationInfoMarker.remove();
            mDestinationInfoMarker = null;
        }
    }

    public void clearRoute() {
        mRouteTileOverlay.clear();
    }

    public void setSelectedRoute(CalculatedRoute route) {
        mSelectedRoute = route;
        mRouteTileOverlay.displayRoute(mFastRoute, mEconomicRoute, mRelaxRoute, route);
    }

    public boolean isCurrentLocationReceived() {
        return sCurrentLocation != null;
    }

    public boolean isMapReady() {
        return mMap != null;
    }

    public void setMoveToCurrentLocationOnFirstLocationChange(boolean val) {
        mIsMoveToCurrentLocationOnFirstLocationChange = val;
    }

    public void setMapViewCallback(MapViewCallback mapViewCallback) {
        mMapViewCallback = mapViewCallback;
    }

    public void setSourcePlace(Place place) {
        mSourcePlace = place;
        mRouteTileOverlay.clear();
        if (mSourceMarker != null) {
            mSourceMarker.remove();
            mSourceMarker = null;
        }
        if (mSourceInfoMarker != null) {
            mSourceInfoMarker.remove();
            mSourceInfoMarker = null;
        }
        BitmapDescriptor bmpDesc = UI.getBitmapDescriptorFromResource(mContext, R.drawable.ic_marker_source, UI.dpToPx(25, mContext));
        MarkerOptions mo;
        if (mSourcePlace.isCurrentLocation()) {
            mo = new MarkerOptions().icon(bmpDesc).position(getCurrentLatLng());
        } else {
            mo = new MarkerOptions().icon(bmpDesc).position(mSourcePlace.getLatLng());
        }
        mSourceMarker = mMap.addMarker(mo);
        mSourceInfoMarker = addInfoMarker(mSourcePlace);
        if (mSourcePlace.isCurrentLocation()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng(), MAP_ZOOM_SINGLE_LOCATION_SELECTED));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mSourcePlace.getLatLng(), MAP_ZOOM_SINGLE_LOCATION_SELECTED));
        }
    }

    public void setDestinationPlace(Place place) {
        mDestinationPlace = place;
        mRouteTileOverlay.clear();
        if (mDestinationMarker != null) {
            mDestinationMarker.remove();
            mDestinationMarker = null;
        }
        if (mDestinationInfoMarker != null) {
            mDestinationInfoMarker.remove();
            mDestinationInfoMarker = null;
        }
        BitmapDescriptor bmpDesc = UI.getBitmapDescriptorFromResource(mContext, R.drawable.ic_marker_destination, UI.dpToPx(35, mContext));
        MarkerOptions mo;
        if (mDestinationPlace.isCurrentLocation()) {
            mo = new MarkerOptions().icon(bmpDesc).anchor(0, 1).position(getCurrentLatLng());
        } else {
            mo = new MarkerOptions().icon(bmpDesc).anchor(0, 1).position(mDestinationPlace.getLatLng());
        }
        mDestinationMarker = mMap.addMarker(mo);
        mDestinationInfoMarker = addInfoMarker(mDestinationPlace);
        if (mDestinationPlace.isCurrentLocation()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng(), MAP_ZOOM_SINGLE_LOCATION_SELECTED));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestinationPlace.getLatLng(), MAP_ZOOM_SINGLE_LOCATION_SELECTED));
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }

    public void scrollBy(int height) {
        mMap.animateCamera(CameraUpdateFactory.scrollBy(0, height));
    }

    public void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
        }
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
        }
    }

    public void onLocationPermissionGranted() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleApiClient.connect();
        mRouteTileOverlay = new InrixTileRouteOverlay(mContext, mMap);
    }

    public void moveCameraToCurrentLocation() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng(), MAP_ZOOM_CURRENT_LOCATION));
    }

    public Marker addInfoMarker(Place place) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_info_window, null);
        TextView tvPlace = (TextView) v.findViewById(R.id.tvPlace);
        MarkerOptions markerOpt;
        if (place.isCurrentLocation()) {
            tvPlace.setText(R.string.your_location);
            markerOpt = MapUtils.generateInfoMarkerOptions(mContext, v, getCurrentLatLng(), 200);
        } else {
            tvPlace.setText(place.getPrimaryText());
            markerOpt = MapUtils.generateInfoMarkerOptions(mContext, v, place.getLatLng(), 200);
        }
        return mMap.addMarker(markerOpt);
    }

    public void connectToGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private void showIncidents() {
        hideIncidents();
        mClusterManager.addItems(mMapClusterItems);
        mClusterManager.cluster();
    }

    private void hideIncidents() {
        mClusterManager.clearItems();
    }

    private boolean isClickedOnRoute(CalculatedRoute route, LatLng clickedCoordinate) {
        Projection projection = mMap.getProjection();
        Point point1 = projection.toScreenLocation(clickedCoordinate);
        for (LatLng position : GeoPointHelper.toLatLngList(route.getRoute().getPoints())) {
            Point point2 = projection.toScreenLocation(position);
            double distance = Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
            if (distance < 100) {
                return true;
            }
        }
        return false;
    }

    private void checkRouteClick(LatLng latLng) {
        if (mFastRoute != null && mSelectedRoute != mFastRoute) {
            if (isClickedOnRoute(mFastRoute, latLng)) {
                setSelectedRoute(mFastRoute);
                mMapViewCallback.onRouteSelect(mFastRoute);
                return;
            }
        }
        if (mEconomicRoute != null && mSelectedRoute != mEconomicRoute) {
            if (isClickedOnRoute(mEconomicRoute, latLng)) {
                setSelectedRoute(mEconomicRoute);
                mMapViewCallback.onRouteSelect(mEconomicRoute);
                return;
            }
        }
        if (mRelaxRoute != null && mSelectedRoute != mRelaxRoute) {
            if (isClickedOnRoute(mRelaxRoute, latLng)) {
                setSelectedRoute(mRelaxRoute);
                mMapViewCallback.onRouteSelect(mRelaxRoute);
                return;
            }
        }
    }

    private Handler mCameraMoveCallbackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mMapViewCallback.onCameraMove();
        }
    };

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            sCurrentLocation = location;
            if (mIsLocationServicesEnabledOnInitilize) {
                mIsLocationServicesEnabledOnInitilize = false;
                moveCameraToCurrentLocation();
            } else if (mIsMoveToCurrentLocationOnFirstLocationChange) {
                mIsMoveToCurrentLocationOnFirstLocationChange = false;
                moveCameraToCurrentLocation();
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks mGoogleConnectionCallbacks = new ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {}
    };

    private GoogleApiClient.OnConnectionFailedListener mGoogleConnectionFailedListener = new OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            mMapViewCallback.onGoogleApiClientConnectionFailure();
        }
    };
}
