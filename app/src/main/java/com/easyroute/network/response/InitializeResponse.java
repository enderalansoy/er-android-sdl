package com.easyroute.network.response;

import com.easyroute.network.model.BridgePrice;

import java.util.List;
/**
 * Created by imenekse on 09/03/17.
 */

public class InitializeResponse extends BaseResponse {

    private int androidVersion;
    private int isAppActive;
    private List<BridgePrice> bridgePrices;
    private double gasolinePrice;
    private double dieselPrice;

    public double getGasolinePrice() {
        return gasolinePrice;
    }

    public double getDieselPrice() {
        return dieselPrice;
    }

    public double getBridgePrice(int classId, String bridgeName) {
        for (BridgePrice bridgePrice : bridgePrices) {
            if (bridgePrice.getName().equals(bridgeName)) {
                return bridgePrice.getPrice(classId);
            }
        }
        return 0;
    }

    public int getAndroidVersion() {
        return androidVersion;
    }

    public boolean isAppActive() {
        return isAppActive == 1;
    }
}
