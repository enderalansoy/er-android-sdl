package com.easyroute.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by imenekse on 28/02/17.
 */

public class GetRouteSegmentsResponse extends BaseResponse {

    private SegmentResultSet SegmentResultSet;

    public List<Integer> getSegments() {
        List<Integer> list = new ArrayList<>();
        if (SegmentResultSet != null && SegmentResultSet.Segment != null) {
            for (Segment segment : SegmentResultSet.Segment) {
                list.add(segment.code);
            }
        }
        return list;
    }

    private class SegmentResultSet {

        private List<Segment> Segment;
    }

    private class Segment {

        @SerializedName("@code")
        private int code;
    }
}
