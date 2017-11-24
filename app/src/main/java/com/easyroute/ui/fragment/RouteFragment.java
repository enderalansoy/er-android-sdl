package com.easyroute.ui.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyroute.R;
import com.easyroute.constant.Event;
import com.easyroute.content.Preference;
import com.easyroute.content.model.CalculatedRoute;
import com.easyroute.content.model.VehicleInfo;
import com.easyroute.ui.activity.MainActivity;
import com.easyroute.ui.activity.OptionsActivity;
import com.easyroute.ui.adapter.TimeWheelAdapter;
import com.easyroute.ui.dialog.VehicleInfoDialog;
import com.easyroute.ui.view.RouteOptionTabLayout;
import com.easyroute.ui.view.RouteOptionTabLayout.OnRouteOptionTabItemSelectListener;
import com.easyroute.ui.view.StickyTab;
import com.easyroute.ui.view.StickyTab.OnStickyTabItemSelectListener;
import com.easyroute.utility.Analytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.WheelHorizontalView;

public class RouteFragment extends BaseFragment implements OnClickListener, OnStickyTabItemSelectListener, OnRouteOptionTabItemSelectListener {

    private RouteOptionTabLayout routeOptionTabLayout;
    private LinearLayout llFooter;
    private LinearLayout llRouteCost;
    private LinearLayout llRoadFares;
    private FrameLayout flTimeOptionContainer;
    private FloatingActionButton fabMyLocation;
    private FloatingActionButton fabNavigate;
    private FloatingActionButton fabOptions;
    private WheelHorizontalView wvTime;
    private StickyTab stTimeOption;
    private TextView tvEstimatedLabel;
    private TextView tvEnterVehicleInfo;
    private TextView tvFuelCost;
    private TextView tvEstimatedTime;
    private TextView tvAverageSpeed;
    private TextView tvRoadFares;
    private TextView tvStartNavigation;

    private TimeWheelAdapter mWheelAdapter;
    public static VehicleInfo mVehicleInfo;
    private CalculatedRoute mSelectedRoute;

    private boolean mIsActive;
    private boolean mIsCurrentLocationSelectedForSource;
    private int mTimeWheelLastSelectedIndex;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_route;
    }

    @Override
    public void initViews() {
        routeOptionTabLayout = (RouteOptionTabLayout) findViewById(R.id.routeOptionTabLayout);
        llFooter = (LinearLayout) findViewById(R.id.llFooter);
        llRouteCost = (LinearLayout) findViewById(R.id.llRouteCost);
        llRoadFares = (LinearLayout) findViewById(R.id.llRoadFares);
        flTimeOptionContainer = (FrameLayout) findViewById(R.id.flTimeOptionContainer);
        fabMyLocation = (FloatingActionButton) findViewById(R.id.fabMyLocation);
        fabNavigate = (FloatingActionButton) findViewById(R.id.fabNavigate);
        fabOptions = (FloatingActionButton) findViewById(R.id.fabOptions);
        wvTime = (WheelHorizontalView) findViewById(R.id.wvTime);
        stTimeOption = (StickyTab) findViewById(R.id.stTimeOption);
        tvEstimatedLabel = (TextView) findViewById(R.id.tvEstimatedLabel);
        tvEnterVehicleInfo = (TextView) findViewById(R.id.tvEnterVehicleInfo);
        tvFuelCost = (TextView) findViewById(R.id.tvFuelCost);
        tvEstimatedTime = (TextView) findViewById(R.id.tvEstimatedTime);
        tvAverageSpeed = (TextView) findViewById(R.id.tvAverageSpeed);
        tvRoadFares = (TextView) findViewById(R.id.tvRoadFares);
        tvStartNavigation = (TextView) findViewById(R.id.tvStartNavigation);
    }

    @Override
    public void defineObjects() {
        mWheelAdapter = new TimeWheelAdapter(context);
        mVehicleInfo = Preference.getInstance(context).getVehicleInfo();
    }

    @Override
    public void bindEvents() {
        fabMyLocation.setOnClickListener(this);
        fabNavigate.setOnClickListener(this);
        tvStartNavigation.setOnClickListener(this);
        fabOptions.setOnClickListener(this);
        llRouteCost.setOnClickListener(this);
        tvEnterVehicleInfo.setOnClickListener(this);
        wvTime.addScrollingListener(mTimeWheelListener);
        stTimeOption.setOnItemSelectListener(this);
        routeOptionTabLayout.setListener(this);
    }

    @Override
    public void setProperties() {
        wvTime.setViewAdapter(mWheelAdapter);
        if (mVehicleInfo != null) {
            showVehicleInfoLabels();
        } else {
            hideVehicleInfoLabels();
        }
        stTimeOption.setTexts(R.string.route_fragment_departure_time_label, R.string.route_fragment_arrival_time_label);
        Spannable spannable = new SpannableString(getString(R.string.route_fragment_enter_vehicle_info_click_label));
        spannable.setSpan(new RelativeSizeSpan(1.2f), 0, spannable.length(), 0);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvEnterVehicleInfo.append(" ");
        tvEnterVehicleInfo.append(spannable);
    }

    @Override
    public void onLayoutCreate() {
        deactivate(false);
    }

    @Override
    public void onClick(View v) {
        if (fabMyLocation == v) {
            sendEvent(Event.MAIN_ACTIVITY_MY_LOCATION_BUTTON_CLICKED);
        } else if (fabNavigate == v || tvStartNavigation == v) {
            sendEvent(Event.MAIN_ACTIVITY_NAVIGATE_BUTTON_CLICKED, mSelectedRoute);
        } else if (fabOptions == v) {
            onOptionsFabClick();
        } else if (llRouteCost == v || tvEnterVehicleInfo == v) {
            VehicleInfoDialog.newInstance(false).show(getFragmentManager(), null);
        }
    }

    @Override
    public void onStickyTabItemSelect(int pos) {
        if (pos == 0) {
            tvEstimatedLabel.setText(R.string.route_fragment_estimated_arrival_label);
        } else {
            tvEstimatedLabel.setText(R.string.route_fragment_estimated_departure_label);
        }
        sendEvent(Event.MAIN_ACTIVITY_ROUTE_PARAMETERS_CHANGED);
    }

    @Override
    public void onEventReceive(Event event, Object data) {
        if (event == Event.VEHICLE_INFO_DIALOG_SAVE) {
            mVehicleInfo = Preference.getInstance(context).getVehicleInfo();
            showVehicleInfoLabels();
        }
    }

    @Override
    public void onRouteOptionTabItemSelectListener(CalculatedRoute selectedRoute) {
        mSelectedRoute = selectedRoute;
        Analytics.routeType(context, selectedRoute.getRouteType());
        getPreference().setDefaultRouteType(selectedRoute.getRouteType());
        setSelectedRouteInformations();
        sendEvent(Event.ROUTE_FRAGMENT_ROUTE_SELECTED);
    }

    public void setSelectedRoute(CalculatedRoute selectedRoute) {
        mSelectedRoute = selectedRoute;
        getPreference().setDefaultRouteType(selectedRoute.getRouteType());
        setSelectedRouteInformations();
        routeOptionTabLayout.setSelectedRoute(selectedRoute);
    }

    public void reset() {
        wvTime.setCurrentItem(0);
        stTimeOption.setSelectedIndex(0);
    }

    public CalculatedRoute getSelectedRoute() {
        return mSelectedRoute;
    }

    public void setRoutes(boolean isCurrentLocationSelectedForSource, CalculatedRoute fastRoute, CalculatedRoute economicRoute, CalculatedRoute relaxRoute) {
        mIsCurrentLocationSelectedForSource = isCurrentLocationSelectedForSource;
        if (!mIsCurrentLocationSelectedForSource) {
            hideNavigateButton(true);
        }
        routeOptionTabLayout.setDefaultRouteType(getPreference().getDefaultRouteType());
        routeOptionTabLayout.setVehicleInfo(mVehicleInfo);
        routeOptionTabLayout.createRouteViews(fastRoute, economicRoute, relaxRoute);
        mSelectedRoute = routeOptionTabLayout.getSelectedRoute();
        setSelectedRouteInformations();
    }

    private void onOptionsFabClick() {
        Intent intent = new Intent(context, OptionsActivity.class);
        int x = (int) fabOptions.getX() + fabOptions.getWidth() / 2;
        int y = (int) fabOptions.getY() + fabOptions.getHeight() / 2;
        intent.putExtra(OptionsActivity.EXTRA_REVEAL_ANIM_HORIZONTAL_POSITION, x);
        intent.putExtra(OptionsActivity.EXTRA_REVEAL_ANIM_VERTICAL_POSITION, y);
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_SELECT_OPTIONS);
    }

    private void setSelectedRouteInformations() {
        if (mSelectedRoute.getRoadFares() == 0) {
            llRoadFares.setVisibility(View.INVISIBLE);
        } else {
            llRoadFares.setVisibility(View.VISIBLE);
            tvRoadFares.setText(mSelectedRoute.getRoadFaresString() + " " + getString(R.string.route_fragment_cost_unit));
        }
        tvAverageSpeed.setText(mSelectedRoute.getRoute().getAverageSpeed() + "");
        int travelTimeMinutes = mSelectedRoute.getRoute().getTravelTimeMinutes();
        if (stTimeOption.getSelectedIndex() == 0) {
            Calendar calendar = mWheelAdapter.getItemAsCalendar(wvTime.getCurrentItem());
            calendar.add(Calendar.MINUTE, travelTimeMinutes);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String estimatedTime = format.format(calendar.getTime());
            tvEstimatedTime.setText(estimatedTime);
        } else {
            Calendar calendar = mWheelAdapter.getItemAsCalendar(wvTime.getCurrentItem());
            calendar.add(Calendar.MINUTE, -travelTimeMinutes);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String estimatedTime = format.format(calendar.getTime());
            tvEstimatedTime.setText(estimatedTime);
        }
        if (mVehicleInfo != null) {
            tvFuelCost.setText(mSelectedRoute.getFuelCostString() + " " + getString(R.string.route_fragment_cost_unit));
        }
    }

    public void activate() {
        mIsActive = true;
        routeOptionTabLayout.animate().setDuration(500).translationY(0);
        llFooter.animate().setDuration(500).translationY(0);
        flTimeOptionContainer.animate().setDuration(500).translationY(0);
        if (mIsCurrentLocationSelectedForSource) {
            showNavigateButton();
        }
        hideOptionsButton();
    }

    public void deactivate(boolean animate) {
        mIsActive = false;
        int duration = animate ? 500 : 0;
        routeOptionTabLayout.animate().setDuration(duration).translationY(-routeOptionTabLayout.getHeight());
        llFooter.animate().setDuration(duration).translationY(llFooter.getHeight());
        flTimeOptionContainer.animate().setDuration(duration).translationY(llFooter.getHeight() + flTimeOptionContainer.getHeight() / 2);
        showOptionsButton();
        hideNavigateButton(animate);
    }

    public boolean isActive() {
        return mIsActive;
    }

    public boolean isDepartureTimeOptionSelected() {
        return stTimeOption.getSelectedIndex() == 0;
    }

    public Date getSelectedDateTime() {
        return mWheelAdapter.getItemAsDate(wvTime.getCurrentItem());
    }

    private void showVehicleInfoLabels() {
        tvEnterVehicleInfo.setVisibility(View.GONE);
        llRouteCost.setVisibility(View.VISIBLE);
    }

    private void hideVehicleInfoLabels() {
        tvEnterVehicleInfo.setVisibility(View.VISIBLE);
        llRouteCost.setVisibility(View.GONE);
    }

    private void showNavigateButton() {
        fabNavigate.setVisibility(View.VISIBLE);
        fabNavigate.animate().setDuration(500).scaleX(1).scaleY(1).setListener(null);
        tvStartNavigation.setVisibility(View.VISIBLE);
        tvStartNavigation.animate().setDuration(500).scaleX(1).scaleY(1).setListener(null);
    }

    private void hideNavigateButton(boolean animate) {
        int duration = animate ? 500 : 0;
        tvStartNavigation.animate().setDuration(duration).scaleX(0).scaleY(0);
        fabNavigate.animate().setDuration(duration).scaleX(0).scaleY(0).setListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabNavigate.setVisibility(View.INVISIBLE);
                tvStartNavigation.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void showOptionsButton() {
        fabOptions.setVisibility(View.VISIBLE);
        fabOptions.animate().setDuration(500).scaleX(1).scaleY(1).setListener(null);
    }

    private void hideOptionsButton() {
        fabOptions.animate().setDuration(500).scaleX(0).scaleY(0).setListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabOptions.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private Handler mTimeWheelScrollHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isActive()) {
                int index = wvTime.getCurrentItem();
                if (index != mTimeWheelLastSelectedIndex) {
                    mTimeWheelLastSelectedIndex = index;
                    sendEvent(Event.MAIN_ACTIVITY_ROUTE_PARAMETERS_CHANGED);
                }
            }
        }
    };

    private OnWheelScrollListener mTimeWheelListener = new OnWheelScrollListener() {

        @Override
        public void onScrollingFinished(AbstractWheel wheel) {
            if (isActive()) {
                mTimeWheelScrollHandler.removeMessages(0);
                mTimeWheelScrollHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }

        @Override
        public void onScrollingStarted(AbstractWheel wheel) {
            mTimeWheelScrollHandler.removeMessages(0);
        }
    };
}

