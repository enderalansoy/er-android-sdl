package com.easyroute.network.model;

import java.util.Map;
import java.util.Map.Entry;
/**
 * Created by imenekse on 09/03/17.
 */

public class BridgePrice extends BaseModel {

    private String name;
    private Map<Integer, Double> prices;

    public String getName() {
        return name;
    }

    public Map<Integer, Double> getPrices() {
        return prices;
    }

    public double getPrice(int classId) {
        for (Entry<Integer, Double> price : prices.entrySet()) {
            if (price.getKey() == classId) {
                return price.getValue();
            }
        }
        return 0;
    }
}
