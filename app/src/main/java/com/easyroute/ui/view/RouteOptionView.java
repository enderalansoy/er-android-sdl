package com.easyroute.ui.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyroute.R;
import com.easyroute.content.constant.RouteType;
import com.easyroute.content.model.CalculatedRoute;
import com.easyroute.content.model.VehicleInfo;
/**
 * Created by imenekse on 02/03/17.
 */

public class RouteOptionView extends CardView {

    private final float UNSELECTED_SCALE = 0.9f;
    private final float UNSELECTED_ALPHA = 0.75f;

    private LinearLayout llBackground;
    private LinearLayout llCostRow;
    private TextView tvTitle;
    private TextView tvTravelTime;
    private TextView tvDistance;
    private TextView tvCost;

    private Context mContext;
    private CalculatedRoute mRoute;



    public RouteType mRouteType;
    private VehicleInfo mVehicleInfo;

    public RouteOptionView(Context context, RouteType routeType, CalculatedRoute route, VehicleInfo vehicleInfo) {
        super(context);
        mContext = context;
        mRoute = route;
        mRouteType = routeType;
        mVehicleInfo = vehicleInfo;
        LayoutInflater.from(context).inflate(R.layout.layout_route_option_view, this);
        llBackground = (LinearLayout) findViewById(R.id.llBackground);
        llCostRow = (LinearLayout) findViewById(R.id.llCostRow);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTravelTime = (TextView) findViewById(R.id.tvTravelTime);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvTravelTime.setText(route.getRoute().getTravelTimeMinutes() + " " + context.getString(R.string.route_fragment_time_unit));
        tvDistance.setText(route.getRoute().getTotalDistance() + " " + context.getString(R.string.route_fragment_distance_unit));
        setVehicleInfo();
        setRouteType();
        setSelected(false, false);
    }

    public CalculatedRoute getRoute() {
        return mRoute;
    }

    public void setSelected(boolean isSelected) {
        setSelected(isSelected, true);
    }

    public void setSelected(boolean isSelected, boolean animate) {
        int duration = animate ? 200 : 0;
        if (isSelected) {
            animate().setDuration(duration).alpha(1f);
            animate().setDuration(duration).scaleX(1f).scaleY(1f);
        } else {
            animate().setDuration(duration).alpha(UNSELECTED_ALPHA);
            animate().setDuration(duration).scaleX(UNSELECTED_SCALE).scaleY(UNSELECTED_SCALE);
        }
    }

    private void setVehicleInfo() {
        if (mVehicleInfo == null) {
            llCostRow.setVisibility(View.GONE);
        } else {
            llCostRow.setVisibility(View.VISIBLE);
            tvCost.setText(mRoute.getTotalCostString() + " " + mContext.getString(R.string.route_fragment_cost_unit));
        }
    }
    public RouteType getmRouteType() {
        return mRouteType;
    }
    private void setRouteType() {
        llBackground.setBackgroundColor(mRouteType.getColor());
        if (mRouteType == RouteType.FAST) {
            tvTitle.setText(R.string.route_fragment_fast_route_label);
        } else if (mRouteType == RouteType.ECONOMIC) {
            tvTitle.setText(R.string.route_fragment_economic_route_label);
        } else if (mRouteType == RouteType.RELAX) {
            tvTitle.setText(R.string.route_fragment_relax_route_label);
        } else if (mRouteType == RouteType.ALL) {
            tvTitle.setText(R.string.route_fragment_single_route_label);
        } else if (mRouteType == RouteType.FAST_ECONOMIC) {
            tvTitle.setText(R.string.route_fragment_fast_economic_route_label);
        } else if (mRouteType == RouteType.FAST_RELAX) {
            tvTitle.setText(R.string.route_fragment_fast_relax_route_label);
        } else if (mRouteType == RouteType.ECONOMIC_RELAX) {
            tvTitle.setText(R.string.route_fragment_economic_relax_route_label);
        }
    }
}
