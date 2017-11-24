package com.easyroute.content.constant;

import android.content.Context;

import com.easyroute.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by imenekse on 16/02/17.
 */

public enum FuelType {
    GASOLIN(R.string.fuel_type_gasoline),
    DIESEL(R.string.fuel_type_diesel);

    private int mNameResId;

    FuelType(int nameResId) {
        mNameResId = nameResId;
    }

    public int getNameResId() {
        return mNameResId;
    }

    public String getName(Context context) {
        return context.getString(mNameResId);
    }

    public static List<String> getAllNames(Context context) {
        List<String> list = new ArrayList<>();
        for (FuelType fuelType : values()) {
            list.add(context.getString(fuelType.getNameResId()));
        }
        return list;
    }
}
