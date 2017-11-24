package com.easyroute.content.model;

import com.easyroute.content.constant.RouteType;
import com.easyroute.network.model.BaseModel;
import com.easyroute.utility.DoubleUtils;
import com.inrix.sdk.model.Route;
/**
 * Created by imenekse on 26/02/17.
 */

public class CalculatedRoute extends BaseModel {

    private Route route;
    private RouteType mRouteType;
    private double roadFares;
    private double fuelCost;

    public CalculatedRoute(Route route) {
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }

    public void setRouteType(RouteType routeType) {
        mRouteType = routeType;
    }

    public RouteType getRouteType() {
        return mRouteType;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public double getRoadFares() {
        return roadFares;
    }

    public String getRoadFaresString() {
        return DoubleUtils.roundToString(roadFares);
    }

    public void setRoadFares(double roadFares) {
        this.roadFares = roadFares;
    }

    public double getFuelCost() {
        return fuelCost;
    }

    public String getFuelCostString() {
        return DoubleUtils.roundToString(fuelCost);
    }

    public void setFuelCost(double fuelCost) {
        this.fuelCost = fuelCost;
    }

    public double getTotalCost() {
        return fuelCost + roadFares;
    }

    public String getTotalCostString() {
        return DoubleUtils.roundToString(getTotalCost());
    }
}
