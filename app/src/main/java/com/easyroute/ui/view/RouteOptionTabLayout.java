package com.easyroute.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.easyroute.content.constant.RouteType;
import com.easyroute.content.model.CalculatedRoute;
import com.easyroute.content.model.VehicleInfo;
/**
 * Created by imenekse on 02/03/17.
 */

public class RouteOptionTabLayout extends LinearLayout implements OnClickListener {

    public interface OnRouteOptionTabItemSelectListener {

        void onRouteOptionTabItemSelectListener(CalculatedRoute selectedRoute);
    }

    private Context mContext;
    public static RouteOptionView rovFast;
    public static RouteOptionView rovEconomic;
    public static RouteOptionView rovRelax;

    private VehicleInfo mVehicleInfo;
    private RouteType mDefaultRouteType;
    public static CalculatedRoute mFastRoute;
    public static CalculatedRoute mEconomicRoute;
    public static CalculatedRoute mRelaxRoute;
    public static CalculatedRoute mSelectedRoute;
    public static OnRouteOptionTabItemSelectListener mListener;

    public RouteOptionTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mContext = context;
            setOrientation(LinearLayout.HORIZONTAL);
            setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    public void setListener(OnRouteOptionTabItemSelectListener listener) {
        mListener = listener;
    }

    public void setDefaultRouteType(RouteType defaultRouteType) {
        mDefaultRouteType = defaultRouteType;
    }

    public void setVehicleInfo(VehicleInfo vehicleInfo) {
        mVehicleInfo = vehicleInfo;
    }

    public static void setSelectedRoute(CalculatedRoute selectedRoute) {
        mSelectedRoute = selectedRoute;
        if (rovFast != null) {
            rovFast.setSelected(selectedRoute == mFastRoute);
        }
        if (rovEconomic != null) {
            rovEconomic.setSelected(selectedRoute == mEconomicRoute);
        }
        if (rovRelax != null) {
            rovRelax.setSelected(selectedRoute == mRelaxRoute);
        }
    }

    public void createRouteViews(CalculatedRoute fastRoute, CalculatedRoute economicRoute, CalculatedRoute relaxRoute) {
        removeAllViews();
        mFastRoute = fastRoute;
        mEconomicRoute = economicRoute;
        mRelaxRoute = relaxRoute;
        rovFast = null;
        rovEconomic = null;
        rovRelax = null;
        if (mVehicleInfo != null) {
            if (fastRoute != economicRoute && fastRoute != relaxRoute && economicRoute != relaxRoute) {
                rovFast = new RouteOptionView(mContext, RouteType.FAST, fastRoute, mVehicleInfo);
                rovEconomic = new RouteOptionView(mContext, RouteType.ECONOMIC, economicRoute, mVehicleInfo);
                rovRelax = new RouteOptionView(mContext, RouteType.RELAX, relaxRoute, mVehicleInfo);
                rovFast.setOnClickListener(this);
                rovEconomic.setOnClickListener(this);
                rovRelax.setOnClickListener(this);
                if (mDefaultRouteType == RouteType.FAST) {
                    rovFast.setSelected(true);
                    mSelectedRoute = fastRoute;
                } else if (mDefaultRouteType == RouteType.ECONOMIC) {
                    rovEconomic.setSelected(true);
                    mSelectedRoute = economicRoute;
                } else {
                    rovRelax.setSelected(true);
                    mSelectedRoute = relaxRoute;
                }
                LinearLayout.LayoutParams layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
                addView(rovFast, layoutParams);
                addView(rovEconomic, layoutParams);
                addView(rovRelax, layoutParams);
            } else if (fastRoute == economicRoute && fastRoute == relaxRoute) {
                rovFast = new RouteOptionView(mContext, RouteType.ALL, fastRoute, mVehicleInfo);
                rovFast.setOnClickListener(this);
                LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                addView(rovFast, layoutParams);
                rovFast.setSelected(true);
                mSelectedRoute = fastRoute;
            } else if (fastRoute == economicRoute) {
                rovFast = new RouteOptionView(mContext, RouteType.FAST_ECONOMIC, fastRoute, mVehicleInfo);
                rovRelax = new RouteOptionView(mContext, RouteType.RELAX, relaxRoute, mVehicleInfo);
                rovFast.setOnClickListener(this);
                rovRelax.setOnClickListener(this);
                if (mDefaultRouteType == RouteType.FAST || mDefaultRouteType == RouteType.ECONOMIC) {
                    rovFast.setSelected(true);
                    mSelectedRoute = fastRoute;
                } else {
                    rovRelax.setSelected(true);
                    mSelectedRoute = relaxRoute;
                }
                LinearLayout.LayoutParams layoutParams1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                addView(rovFast, layoutParams1);
                addView(rovRelax, layoutParams2);
            } else if (fastRoute == relaxRoute) {
                rovFast = new RouteOptionView(mContext, RouteType.FAST_RELAX, fastRoute, mVehicleInfo);
                rovEconomic = new RouteOptionView(mContext, RouteType.ECONOMIC, economicRoute, mVehicleInfo);
                rovFast.setOnClickListener(this);
                rovEconomic.setOnClickListener(this);
                if (mDefaultRouteType == RouteType.FAST || mDefaultRouteType == RouteType.RELAX) {
                    rovFast.setSelected(true);
                    mSelectedRoute = fastRoute;
                } else {
                    rovEconomic.setSelected(true);
                    mSelectedRoute = economicRoute;
                }
                LinearLayout.LayoutParams layoutParams1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                addView(rovFast, layoutParams1);
                addView(rovEconomic, layoutParams2);
            } else if (economicRoute == relaxRoute) {
                rovFast = new RouteOptionView(mContext, RouteType.FAST, fastRoute, mVehicleInfo);
                rovEconomic = new RouteOptionView(mContext, RouteType.ECONOMIC_RELAX, economicRoute, mVehicleInfo);
                rovFast.setOnClickListener(this);
                rovEconomic.setOnClickListener(this);
                if (mDefaultRouteType == RouteType.FAST) {
                    rovFast.setSelected(true);
                    mSelectedRoute = fastRoute;
                } else {
                    rovEconomic.setSelected(true);
                    mSelectedRoute = economicRoute;
                }
                LinearLayout.LayoutParams layoutParams1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                addView(rovFast, layoutParams1);
                addView(rovEconomic, layoutParams2);
            }
        } else {
            if (fastRoute != relaxRoute) {
                rovFast = new RouteOptionView(mContext, RouteType.FAST, fastRoute, mVehicleInfo);
                rovRelax = new RouteOptionView(mContext, RouteType.RELAX, relaxRoute, mVehicleInfo);
                rovFast.setOnClickListener(this);
                rovRelax.setOnClickListener(this);
                if (mDefaultRouteType == RouteType.FAST || mDefaultRouteType == RouteType.ECONOMIC) {
                    rovFast.setSelected(true);
                    mSelectedRoute = fastRoute;
                } else {
                    rovRelax.setSelected(true);
                    mSelectedRoute = relaxRoute;
                }
                LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addView(rovFast, layoutParams);
                addView(rovRelax, layoutParams);
            } else {
                rovFast = new RouteOptionView(mContext, RouteType.ALL, fastRoute, mVehicleInfo);
                rovFast.setOnClickListener(this);
                LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                addView(rovFast, layoutParams);
                rovFast.setSelected(true);
                mSelectedRoute = fastRoute;
            }
        }
    }

    @Override
    public void onClick(View v) {
        RouteOptionView selectedRov = (RouteOptionView) v;
        if (mSelectedRoute != selectedRov.getRoute()) {
            rovFast.setSelected(false);
            if (rovEconomic != null) {
                rovEconomic.setSelected(false);
            }
            if (rovRelax != null) {
                rovRelax.setSelected(false);
            }
            selectedRov.setSelected(true);
            mSelectedRoute = selectedRov.getRoute();
            mListener.onRouteOptionTabItemSelectListener(mSelectedRoute);
        }
    }

    public CalculatedRoute getSelectedRoute() {
        return mSelectedRoute;
    }
}
