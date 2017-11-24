package com.easyroute.helper;

import android.app.Activity;

import com.easyroute.App;
import com.easyroute.constant.Constant;
import com.easyroute.content.Preference;
import com.easyroute.content.constant.FuelType;
import com.easyroute.content.constant.RouteType;
import com.easyroute.content.constant.TollCollection;
import com.easyroute.content.constant.TollCollectionType;
import com.easyroute.content.constant.VehicleType;
import com.easyroute.content.model.CalculatedRoute;
import com.easyroute.content.model.VehicleInfo;
import com.easyroute.network.response.InitializeResponse;
import com.easyroute.network.response.GetRouteSegmentsResponse;
import com.easyroute.utility.L;
import com.easyroute.utility.Xson;
import com.inrix.sdk.Error;
import com.inrix.sdk.IDataResponseListener;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.Route;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by imenekse on 25/02/17.
 */

public class RouteHelper {

    public interface OnRoutesCalculateFinishListener {

        void onRoutesCalculateFinishListener(CalculatedRoute fast, CalculatedRoute economic, CalculatedRoute relax);
    }

    private final double MIN_SPEED_FOR_OUT_CITY = 50;

    private Activity mActivity;
    private Preference mPreference;
    private Xson mXson;
    private List<CalculatedRoute> mCalculatedRoutes;
    private InitializeResponse mInitializeResponse;

    public RouteHelper(Activity activity) {
        mActivity = activity;
        mPreference = Preference.getInstance(activity);
        mCalculatedRoutes = new ArrayList<>();
        mInitializeResponse = App.getInstance().getInitializeResponse();
        mXson = new Xson();
    }

    public void calculateRoutes(final List<Route> routes, final OnRoutesCalculateFinishListener listener) {
        mCalculatedRoutes.clear();
        for (final Route route : routes) {
            String routeId = route.getId();
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("RouteID", routeId);
            InrixCore.invokeService("Mobile.Segment.Route", arguments, null, new IDataResponseListener<String>() {
                @Override
                public void onResult(final String xmlResponse) {
                    startRouteCalculatorThread(xmlResponse, route, routes, listener);
                }

                @Override
                public void onError(Error error) {}
            });
        }
    }

    private void startRouteCalculatorThread(final String xmlResponse, final Route route, final List<Route> routes, final OnRoutesCalculateFinishListener listener) {
        new Thread() {
            @Override
            public void run() {
                synchronized (mCalculatedRoutes) {
                    GetRouteSegmentsResponse response = mXson.fromXml(xmlResponse, GetRouteSegmentsResponse.class);
                    List<Integer> segments = response == null ? null : response.getSegments();
                    CalculatedRoute calculatedRoute = new CalculatedRoute(route);
                    double roadFares = 0;
                    if (segments != null && !segments.isEmpty()) {
                        roadFares = calculateRoadFares(segments);
                    }
                    double fuelCost = calculateFuelCost(route);
                    calculatedRoute.setRoute(route);
                    calculatedRoute.setRoadFares(roadFares);
                    calculatedRoute.setFuelCost(fuelCost);
                    mCalculatedRoutes.add(calculatedRoute);
                    if (mCalculatedRoutes.size() == routes.size()) {
                        CalculatedRoute fast = mCalculatedRoutes.get(0);
                        CalculatedRoute economic = mCalculatedRoutes.get(0);
                        CalculatedRoute relax = mCalculatedRoutes.get(0);
                        for (CalculatedRoute cr : mCalculatedRoutes) {
                            if (cr.getRoute().getTravelTimeMinutes() < fast.getRoute().getTravelTimeMinutes()) {
                                fast = cr;
                            }
                            if (cr.getTotalCost() < economic.getTotalCost()) {
                                economic = cr;
                            }
                            if (cr.getRoute().getAverageSpeed() > relax.getRoute().getAverageSpeed()) {
                                relax = cr;
                            }
                        }
                        fast.setRouteType(RouteType.FAST);
                        economic.setRouteType(RouteType.ECONOMIC);
                        relax.setRouteType(RouteType.RELAX);
                        final CalculatedRoute fastFinal = fast;
                        final CalculatedRoute economicFinal = economic;
                        final CalculatedRoute relaxFinal = relax;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onRoutesCalculateFinishListener(fastFinal, economicFinal, relaxFinal);
                            }
                        });
                    }
                }
            }
        }.start();
    }

    private double calculateRoadFares(List<Integer> segments) {
        VehicleInfo vehicleInfo = mPreference.getVehicleInfo();
        if (vehicleInfo != null) {
            VehicleType vehicleType = vehicleInfo.getVehicleType();
            List<TollCollection> tollCollections = getEntryAndExitTollCollections(segments);
            double totalRoadFare = 0;
            for (int i = 0; i < tollCollections.size(); i += 2) {
                TollCollection entryTollCollection = tollCollections.get(i);
                TollCollection exitTollCollection = tollCollections.get(i + 1);
                if (entryTollCollection.getType() == TollCollectionType.BRIDGE) {
                    totalRoadFare += mInitializeResponse.getBridgePrice(vehicleType.getId(), entryTollCollection.getName());
                } else {
                    totalRoadFare += getTollCollectionPrice(vehicleType, entryTollCollection, exitTollCollection);
                }
            }
            return totalRoadFare;
        }
        return 0;
    }

    private double calculateFuelCost(Route route) {
        double price = 0;
        VehicleInfo vehicleInfo = mPreference.getVehicleInfo();
        if (vehicleInfo != null) {
            FuelType fuelType = vehicleInfo.getFuelType();
            double averageSpeed = route.getAverageSpeed();
            double distance = route.getTotalDistance();
            double fuelConsumption;
            if (averageSpeed > MIN_SPEED_FOR_OUT_CITY) {
                fuelConsumption = vehicleInfo.getHighwayFuelConsumption();
            } else {
                fuelConsumption = vehicleInfo.getInCityFuelConsumption();
            }
            if (fuelType == FuelType.GASOLIN) {
                price = distance * fuelConsumption / 100 * mInitializeResponse.getGasolinePrice();
            } else {
                price = distance * fuelConsumption / 100 * mInitializeResponse.getDieselPrice();
            }
        }
        return price;
    }

    private List<TollCollection> getEntryAndExitTollCollections(List<Integer> segments) {
        List<TollCollection> tollCollections = new ArrayList<>();
        for (TollCollection tollCollection : TollCollection.values()) {
            for (Integer segment : segments) {
                boolean isSegmentExists = tollCollection.checkSegment(segment);
                if (isSegmentExists && !tollCollections.contains(tollCollection)) {
                    tollCollections.add(tollCollection);
                    if (tollCollection.getType() == TollCollectionType.BRIDGE) {
                        tollCollections.add(null);
                    }
                }
            }
        }
        if (tollCollections.size() % 2 != 0) {
            TollCollection lastTollCollection = tollCollections.get(tollCollections.size() - 1);
            if (lastTollCollection.getType() == TollCollectionType.ANATOLIA) {
                tollCollections.add(TollCollection.AKINCI_GISESI);
            } else if (lastTollCollection.getType() == TollCollectionType.EUROPE) {
                if (segments.get(segments.size() - 1) == TollCollection.ISPARTAKULE_LAST_SEGMENT) {
                    tollCollections.add(TollCollection.ISPARTAKULE_GISESI);
                } else {
                    tollCollections.add(TollCollection.EDIRNE_GISESI);
                }
            }
        }
        L.e(tollCollections);
        return tollCollections;
    }

    private double getTollCollectionPrice(VehicleType vehicleType, TollCollection entryTollCollection, TollCollection exitTollCollection) {
        String method = Constant.GET_TOLL_COLLECTION_PRICE_SERVICE_METHOD;
        String nameSpace = Constant.GET_TOLL_COLLECTION_PRICE_SERVICE_NAMESPACE;
        String serviceUrl = Constant.GET_TOLL_COLLECTION_PRICE_SERVICE_URL;
        SoapObject soRequest = new SoapObject(nameSpace, method);
        soRequest.addProperty("firstPosition", entryTollCollection.getName());
        soRequest.addProperty("lastPosition", exitTollCollection.getName());
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(110);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soRequest);
        HttpTransportSE httpTransport = new HttpTransportSE(serviceUrl);
        httpTransport.debug = true;
        try {
            httpTransport.call(nameSpace + method, envelope);
            SoapObject soResponse = (SoapObject) ((SoapObject) envelope.getResponse()).getProperty(0);
            for (int i = 0; i < soResponse.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) soResponse.getProperty(i);
                int classId = Integer.parseInt(obj.getProperty(3).toString());
                if (VehicleType.valueof(classId) == vehicleType) {
                    return Double.parseDouble(obj.getProperty(2).toString().replace(",", "."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
