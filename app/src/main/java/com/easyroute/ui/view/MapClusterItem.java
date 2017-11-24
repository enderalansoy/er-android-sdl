package com.easyroute.ui.view;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.model.Incident.IncidentType;
/**
 * Created by imenekse on 24/02/17.
 */

public class MapClusterItem implements ClusterItem {

    private IncidentType mIncidentType;
    private LatLng mPosition;

    public MapClusterItem(Incident incident) {
        mIncidentType = incident.getType();
        mPosition = new LatLng(incident.getLatitude(), incident.getLongitude());
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public IncidentType getIncidentType() {
        return mIncidentType;
    }
}
