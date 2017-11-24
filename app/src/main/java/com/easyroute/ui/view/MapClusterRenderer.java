package com.easyroute.ui.view;

import android.content.Context;

import com.easyroute.R;
import com.easyroute.utility.UI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.inrix.sdk.model.Incident.IncidentType;
/**
 * Created by imenekse on 24/02/17.
 */

public class MapClusterRenderer extends DefaultClusterRenderer<MapClusterItem> {

    private Context mContext;
    private int mMarkerSize;

    public MapClusterRenderer(Context context, GoogleMap map, ClusterManager<MapClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mMarkerSize = UI.dpToPx(25, context);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapClusterItem item, MarkerOptions markerOptions) {
        IncidentType incidentType = item.getIncidentType();
        BitmapDescriptor bmp = getMarkerBitmap(incidentType);
        markerOptions.icon(bmp);
    }

    private BitmapDescriptor getMarkerBitmap(IncidentType incidentType) {
        int resId = R.drawable.ic_marker_camera;
        if (incidentType == IncidentType.ACCIDENT) {
            resId = R.drawable.ic_marker_accident;
        } else if (incidentType == IncidentType.HAZARD) {
            resId = R.drawable.ic_marker_hazard;
        } else if (incidentType == IncidentType.CONSTRUCTION) {
            resId = R.drawable.ic_marker_construction;
        } else if (incidentType == IncidentType.POLICE) {
            resId = R.drawable.ic_marker_police;
        } else if (incidentType == IncidentType.CONGESTION) {
            resId = R.drawable.ic_marker_congestion;
        } else if (incidentType == IncidentType.EVENT) {
            resId = R.drawable.ic_marker_event;
        } else if (incidentType == IncidentType.ROAD_CLOSURE) {
            resId = R.drawable.ic_marker_road_closure;
        }
        return UI.getBitmapDescriptorFromResource(mContext, resId, mMarkerSize);
    }
}
