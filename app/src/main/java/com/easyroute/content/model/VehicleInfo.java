package com.easyroute.content.model;

import com.easyroute.content.constant.FuelType;
import com.easyroute.content.constant.VehicleType;
/**
 * Created by imenekse on 16/02/17.
 */

public class VehicleInfo {

    private FuelType fuelType;
    private VehicleType vehicleType;
    private float inCityFuelConsumption;
    private float highwayFuelConsumption;

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public float getInCityFuelConsumption() {
        return inCityFuelConsumption;
    }

    public void setInCityFuelConsumption(float inCityFuelConsumption) {
        this.inCityFuelConsumption = inCityFuelConsumption;
    }

    public float getHighwayFuelConsumption() {
        return highwayFuelConsumption;
    }

    public void setHighwayFuelConsumption(float highwayFuelConsumption) {
        this.highwayFuelConsumption = highwayFuelConsumption;
    }
}
