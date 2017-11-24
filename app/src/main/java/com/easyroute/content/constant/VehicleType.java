package com.easyroute.content.constant;

import android.content.Context;

import com.easyroute.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by imenekse on 16/02/17.
 */

public enum VehicleType {
    CLASS_1(1, R.string.vehicle_type_class_1),
    CLASS_2(2, R.string.vehicle_type_class_2),
    CLASS_3(3, R.string.vehicle_type_class_3),
    CLASS_4(4, R.string.vehicle_type_class_4),
    CLASS_5(5, R.string.vehicle_type_class_5),
    CLASS_6(6, R.string.vehicle_type_class_6);

    private int mId;
    private int mNameResId;

    VehicleType(int id, int nameResId) {
        mId = id;
        mNameResId = nameResId;
    }

    public int getId() {
        return mId;
    }

    public int getNameResId() {
        return mNameResId;
    }

    public String getName(Context context) {
        return context.getString(mNameResId);
    }

    public static VehicleType valueof(int id) {
        for (VehicleType vehicleType : values()) {
            if (id == vehicleType.getId()) {
                return vehicleType;
            }
        }
        return CLASS_1;
    }

    public static List<String> getAllNames(Context context) {
        java.util.List<java.lang.String> list = new ArrayList<>();
        for (VehicleType vehicleType : values()) {
            list.add(context.getString(vehicleType.getNameResId()));
        }
        return list;
    }
}
