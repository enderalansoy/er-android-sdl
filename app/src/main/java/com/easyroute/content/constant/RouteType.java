package com.easyroute.content.constant;

/**
 * Created by imenekse on 18/02/17.
 */

public enum RouteType {
    FAST(0xffFF5722, 0x55FF5722),
    ECONOMIC(0xff9C27B0, 0x559C27B0),
    RELAX(0xff2196F3, 0x552196F3),
    ALL(FAST.getColor(), FAST.getTransculentColor()),
    FAST_ECONOMIC(FAST.getColor(), FAST.getTransculentColor()),
    FAST_RELAX(FAST.getColor(), FAST.getTransculentColor()),
    ECONOMIC_RELAX(ECONOMIC.getColor(), ECONOMIC.getTransculentColor());

    private int mColor;
    private int mTransculentColor;

    RouteType(int color, int transculentColor) {
        mColor = color;
        mTransculentColor = transculentColor;
    }

    public int getColor() {
        return mColor;
    }

    public int getTransculentColor() {
        return mTransculentColor;
    }
}
