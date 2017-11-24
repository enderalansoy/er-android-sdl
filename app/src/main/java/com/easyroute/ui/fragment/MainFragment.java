package com.easyroute.ui.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyroute.R;
import com.easyroute.constant.Event;
import com.easyroute.content.model.Place;
import com.easyroute.ui.activity.PlaceAutoCompleteActivity;
import com.easyroute.ui.adapter.TimeWheelAdapter;
import com.easyroute.ui.view.GoogleMapView;
import com.easyroute.utility.Analytics;
import com.easyroute.utility.MapUtils;
import com.inrix.sdk.model.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.WheelHorizontalView;

import static com.easyroute.constant.Event.MAIN_FRAGMENT_TIME_CHANGED;
/**
 * Created by imenekse on 07/02/17.
 */

public class MainFragment extends BaseFragment implements OnClickListener {

    private final int REQUEST_SOURCE_PLACE = 0;
    private final int REQUEST_DESTINATION_PLACE = 1;

    private RelativeLayout rlHeader;
    private LinearLayout llFooter;
    private LinearLayout llSourceButton;
    private LinearLayout llDestinationButton;
    private TextView tvSource;
    private TextView tvDestination;
    private TextView tvDateTime;
    private FloatingActionButton fabSwitch;
    private FloatingActionButton fabRoute;
    private WheelHorizontalView wvTime;

    private TimeWheelAdapter mWheelAdapter;
    private Place mSourcePlace;
    private Place mDestinationPlace;
    private boolean mIsCurrentLocationSelectedForSource;
    private boolean mIsCurrentLocationSelectedForDestination;
    private boolean mIsLocationServicesEnabledOnInitialize;
    private boolean mIsActive = true;
    private boolean mIsReset = true;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void initViews() {
        rlHeader = (RelativeLayout) findViewById(R.id.rlHeader);
        llFooter = (LinearLayout) findViewById(R.id.llFooter);
        llSourceButton = (LinearLayout) findViewById(R.id.llSourceButton);
        llDestinationButton = (LinearLayout) findViewById(R.id.llDestinationButton);
        tvSource = (TextView) findViewById(R.id.tvSource);
        tvDestination = (TextView) findViewById(R.id.tvDestination);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        fabSwitch = (FloatingActionButton) findViewById(R.id.fabSwitch);
        fabRoute = (FloatingActionButton) findViewById(R.id.fabRoute);
        wvTime = (WheelHorizontalView) findViewById(R.id.wvTime);
    }

    @Override
    public void defineObjects() {
        mIsLocationServicesEnabledOnInitialize = MapUtils.isLocationServicesEnabled(context);
        mIsCurrentLocationSelectedForSource = mIsLocationServicesEnabledOnInitialize;
        mWheelAdapter = new TimeWheelAdapter(context);
    }

    @Override
    public void bindEvents() {
        fabSwitch.setOnClickListener(this);
        fabRoute.setOnClickListener(this);
        llSourceButton.setOnClickListener(this);
        llDestinationButton.setOnClickListener(this);
        wvTime.addScrollingListener(mTimeWheelListener);
    }

    @Override
    public void setProperties() {
        if (mIsLocationServicesEnabledOnInitialize) {
            mSourcePlace = new Place();
            mSourcePlace.setCurrentLocation(true);
            tvSource.setText(R.string.your_location);
        }
        fabSwitch.setEnabled(false);
        fabSwitch.animate().setDuration(0).scaleX(0).scaleY(0);
        fabRoute.setEnabled(false);
        fabRoute.animate().setDuration(0).scaleX(0).scaleY(0);
        wvTime.setViewAdapter(mWheelAdapter);
        setDateTimeLabel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SOURCE_PLACE) {
            if (resultCode == Activity.RESULT_OK) {
                mSourcePlace = (Place) data.getSerializableExtra(PlaceAutoCompleteActivity.EXTRA_PLACE_DATA);
                onSourceSelect();
            }
        } else if (requestCode == REQUEST_DESTINATION_PLACE) {
            if (resultCode == Activity.RESULT_OK) {
                mDestinationPlace = (Place) data.getSerializableExtra(PlaceAutoCompleteActivity.EXTRA_PLACE_DATA);
                onDestinationSelect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (llSourceButton == v) {
            startPlacesActivityForSource();
        } else if (llDestinationButton == v) {
            startPlacesActivityForDestination();
        } else if (fabSwitch == v) {
            Place tempPlace = mSourcePlace;
            mSourcePlace = mDestinationPlace;
            mDestinationPlace = null;
            onSourceSelect();
            mDestinationPlace = tempPlace;
            onDestinationSelect();
        } else if (fabRoute == v) {
            sendEvent(Event.MAIN_ACTIVITY_SECOND_PLACE_SELECTED);
        }
    }

    @Override
    public void onEventReceive(Event event, Object data) {
        if (event == Event.MAIN_ACTIVITY_SOURCE_MARKER_CLICKED) {
            startPlacesActivityForSource();
        } else if (event == Event.MAIN_ACTIVITY_DESTINATION_MARKER_CLICKED) {
            startPlacesActivityForDestination();
        }
    }

    public int getFooterHeight() {
        return llFooter.getHeight();
    }

    public boolean isCurrentLocationSelectedForSource() {
        return mIsCurrentLocationSelectedForSource;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public boolean isReset() {
        return mIsReset;
    }

    public void reset() {
        mIsReset = true;
        mIsLocationServicesEnabledOnInitialize = MapUtils.isLocationServicesEnabled(context);
        mIsCurrentLocationSelectedForSource = mIsLocationServicesEnabledOnInitialize;
        fabSwitch.setEnabled(false);
        fabSwitch.animate().setDuration(0).scaleX(0).scaleY(0);
        fabRoute.setEnabled(false);
        fabRoute.animate().setDuration(0).scaleX(0).scaleY(0);
        wvTime.setCurrentItem(0);
        if (mIsLocationServicesEnabledOnInitialize) {
            mSourcePlace = new Place();
            mSourcePlace.setCurrentLocation(true);
            tvSource.setText(R.string.your_location);
        } else {
            mSourcePlace = null;
            tvSource.setText(R.string.main_fragment_source_location_default_label);
        }
        tvDestination.setText(R.string.main_fragment_destination_location_default_label);
        mDestinationPlace = null;
    }

    public void activate() {
        mIsActive = true;
        rlHeader.animate().setDuration(500).translationY(0);
        llFooter.animate().setDuration(500).translationY(0);
    }

    public void deactivate() {
        mIsActive = false;
        mIsReset = false;
        rlHeader.animate().setDuration(500).translationY(-rlHeader.getHeight());
        llFooter.animate().setDuration(500).translationY(llFooter.getHeight());
    }

    public GeoPoint getStartCoordinate() {
        if (mIsCurrentLocationSelectedForSource) {
            return GoogleMapView.getCurrentGeoPoint();
        } else {
            return new GeoPoint(mSourcePlace.getLatLng().latitude, mSourcePlace.getLatLng().longitude);
        }
    }

    public GeoPoint getDestinationCoordinate() {
        if (mIsCurrentLocationSelectedForDestination) {
            return GoogleMapView.getCurrentGeoPoint();
        } else {
            return new GeoPoint(mDestinationPlace.getLatLng().latitude, mDestinationPlace.getLatLng().longitude);
        }
    }

    public Date getSelectedDateTime() {
        return mWheelAdapter.getItemAsDate(wvTime.getCurrentItem());
    }

    private void startPlacesActivityForSource() {
        Intent intent = new Intent(context, PlaceAutoCompleteActivity.class);
        intent.putExtra(PlaceAutoCompleteActivity.EXTRA_IS_CURRENT_LOCATION_SELECTABLE, !mIsCurrentLocationSelectedForDestination);
        startActivityForResult(intent, REQUEST_SOURCE_PLACE);
    }

    private void startPlacesActivityForDestination() {
        Intent intent = new Intent(context, PlaceAutoCompleteActivity.class);
        intent.putExtra(PlaceAutoCompleteActivity.EXTRA_IS_CURRENT_LOCATION_SELECTABLE, !mIsCurrentLocationSelectedForSource);
        startActivityForResult(intent, REQUEST_DESTINATION_PLACE);
    }

    private void onSourceSelect() {
        sendEvent(Event.MAIN_ACTIVITY_SOURCE_PLACE_SELECTED, mSourcePlace);
        mIsCurrentLocationSelectedForSource = mSourcePlace.isCurrentLocation();
        if (mIsCurrentLocationSelectedForSource) {
            tvSource.setText(R.string.your_location);
        } else {
            tvSource.setText(mSourcePlace.getPrimaryText());
        }
        if (mDestinationPlace != null || mIsCurrentLocationSelectedForDestination) {
            fabSwitch.setEnabled(true);
            fabSwitch.animate().setDuration(500).scaleX(1).scaleY(1);
            fabRoute.setEnabled(true);
            fabRoute.animate().setDuration(500).scaleX(1).scaleY(1);
            sendEvent(Event.MAIN_ACTIVITY_SECOND_PLACE_SELECTED);
        }
    }

    private void onDestinationSelect() {
        sendEvent(Event.MAIN_ACTIVITY_DESTINATION_PLACE_SELECTED, mDestinationPlace);
        mIsCurrentLocationSelectedForDestination = mDestinationPlace.isCurrentLocation();
        if (mIsCurrentLocationSelectedForDestination) {
            tvDestination.setText(R.string.your_location);
        } else {
            tvDestination.setText(mDestinationPlace.getPrimaryText());
        }
        if (mSourcePlace != null || mIsCurrentLocationSelectedForSource) {
            fabSwitch.setEnabled(true);
            fabSwitch.animate().setDuration(500).scaleX(1).scaleY(1);
            fabRoute.setEnabled(true);
            fabRoute.animate().setDuration(500).scaleX(1).scaleY(1);
            sendEvent(Event.MAIN_ACTIVITY_SECOND_PLACE_SELECTED);
        }
    }

    private void setDateTimeLabel() {
        Date date = mWheelAdapter.getItemAsDate(wvTime.getCurrentItem());
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        String dateTimeStr = format.format(date);
        tvDateTime.setText(dateTimeStr);
    }

    private OnWheelScrollListener mTimeWheelListener = new OnWheelScrollListener() {
        private int mLastSelectedIndex;

        @Override
        public void onScrollingFinished(AbstractWheel wheel) {
            int index = wvTime.getCurrentItem();
            if (index != mLastSelectedIndex) {
                mLastSelectedIndex = index;
                setDateTimeLabel();
                sendEvent(MAIN_FRAGMENT_TIME_CHANGED);
                Analytics.trafficForecast(context, index * 15);
            }
        }

        @Override
        public void onScrollingStarted(AbstractWheel wheel) {}
    };
}
