package com.easyroute.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.easyroute.R;
import com.easyroute.helper.SKToolsAdvicePlayer;
import com.easyroute.utility.L;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSettings;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.navigation.SKAdvisorSettings.SKAdvisorLanguage;
import com.skobbler.ngx.navigation.SKAdvisorSettings.SKAdvisorType;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationSettings.SKNavigationType;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.navigation.SKNavigationState.SKStreetType;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.routing.SKRouteSettings.SKRouteMode;
import com.skobbler.ngx.util.SKLogging;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestActivity extends BaseActivity {

    private SKMapViewHolder mapHolder;
    private SKMapSurfaceView mapView;

    private TextToSpeech textToSpeechEngine;

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void initViews() {
        mapHolder = (SKMapViewHolder) findViewById(R.id.mapHolder);
    }

    @Override
    public void defineObjects(Bundle state) {
        textToSpeechEngine = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeechEngine.setLanguage(new Locale("tr_TR"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "This Language is not supported", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Text to speech calismiyor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void bindEvents() {
        mapHolder.setMapSurfaceListener(mSKMapSurfaceListener);
    }

    @Override
    public void setProperties() {
        SKLogging.enableLogs(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapHolder.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapHolder.onResume();
    }

    private void onMapReady() {
        mapView = mapHolder.getMapSurfaceView();
        mapView.getMapSettings().setMapDisplayMode(SKMapSettings.SKMapDisplayMode.MODE_3D);
        prepareAudio();
        //        launchRouteCalculation();
        //        routeWithPoints();
    }

    private void launchRouteCalculation() {
        SKRouteSettings route = new SKRouteSettings();
        route.setStartCoordinate(new SKCoordinate(41.071262, 28.995367));
        route.setDestinationCoordinate(new SKCoordinate(41.022725, 29.045416));
        route.setMaximumReturnedRoutes(1);
        route.setRouteMode(SKRouteMode.CAR_FASTEST);
        route.setRouteExposed(true);
        SKRouteManager.getInstance().setRouteListener(mSKRouteListener);
        SKRouteManager.getInstance().calculateRoute(route);
    }

    private void launchNavigation() {
        SKNavigationSettings navigationSettings = new SKNavigationSettings();
        navigationSettings.setNavigationType(SKNavigationType.SIMULATION);
        SKNavigationManager navigationManager = SKNavigationManager.getInstance();
        navigationManager.setMapView(mapView);
        navigationManager.setNavigationListener(mSKNavigationListener);
        navigationManager.startNavigation(navigationSettings);
    }

    private void prepareAudio() {
        SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
        advisorSettings.setLanguage(SKAdvisorLanguage.LANGUAGE_TR);
        //        advisorSettings.setAdvisorConfigPath(App.getInstance().getMapResourcesDirPath() + "/Advisor");
        //        advisorSettings.setResourcePath(App.getInstance().getMapResourcesDirPath() + "/Advisor/Languages");
        advisorSettings.setAdvisorVoice("tr");
        advisorSettings.setAdvisorType(SKAdvisorType.AUDIO_FILES);
        SKMaps.getInstance().getMapInitSettings().setAdvisorSettings(advisorSettings);
        SKRouteManager.getInstance().setAdvisorSettings(advisorSettings);
    }

    private void createRouteFromGPSPoints(List<SKPosition> points) {
        SKRouteManager.getInstance().clearCurrentRoute();
        SKRouteSettings route = new SKRouteSettings();
        route.setMaximumReturnedRoutes(1);
        route.setRouteMode(SKRouteSettings.SKRouteMode.CAR_FASTEST);
        route.setRouteExposed(true);
        SKRouteManager.getInstance().setRouteListener(mSKRouteListener);
        SKRouteManager.getInstance().calculateRouteWithPoints(points, route);
    }

    private void routeWithPoints() {
        List<SKPosition> points = new ArrayList<SKPosition>();
        double latitude, longitude;
        latitude = 51.798445;
        longitude = 5.247005;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.798297;
        longitude = 5.246562;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79811;
        longitude = 5.24618;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.797867;
        longitude = 5.245662;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.798393;
        longitude = 5.245139;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.797867;
        longitude = 5.245662;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79741;
        longitude = 5.246288;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.797004;
        longitude = 5.246844;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79692;
        longitude = 5.246959;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796598;
        longitude = 5.247536;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796352;
        longitude = 5.247978;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796257;
        longitude = 5.248148;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796211;
        longitude = 5.248279;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796204;
        longitude = 5.248375;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796336;
        longitude = 5.248744;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796392;
        longitude = 5.248975;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79643;
        longitude = 5.249127;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796437;
        longitude = 5.249313;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796412;
        longitude = 5.249472;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796393;
        longitude = 5.249606;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796301;
        longitude = 5.249583;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.796205;
        longitude = 5.249695;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79533;
        longitude = 5.24918;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79463;
        longitude = 5.25177;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79431;
        longitude = 5.25328;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79422;
        longitude = 5.25387;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79414;
        longitude = 5.25454;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79407;
        longitude = 5.25538;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79405;
        longitude = 5.25561;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79481;
        longitude = 5.25576;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79504;
        longitude = 5.25583;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79526;
        longitude = 5.25598;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79543;
        longitude = 5.25621;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79555;
        longitude = 5.256517;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.795576;
        longitude = 5.256741;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79558;
        longitude = 5.256968;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79552;
        longitude = 5.25733;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79541;
        longitude = 5.25755;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.7952;
        longitude = 5.2578;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79495;
        longitude = 5.25792;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.79461;
        longitude = 5.25796;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.793554;
        longitude = 5.258019;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.793095;
        longitude = 5.258014;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.792199;
        longitude = 5.257949;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.791324;
        longitude = 5.257889;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.789594;
        longitude = 5.257782;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.788528;
        longitude = 5.257702;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.786997;
        longitude = 5.257625;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.786273;
        longitude = 5.257626;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.785559;
        longitude = 5.257653;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.785111;
        longitude = 5.257677;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.78318;
        longitude = 5.257894;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.782143;
        longitude = 5.257971;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.781948;
        longitude = 5.257991;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.781009;
        longitude = 5.258104;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.777785;
        longitude = 5.258481;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.777265;
        longitude = 5.258551;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.776744;
        longitude = 5.258658;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.775773;
        longitude = 5.258943;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.775059;
        longitude = 5.259238;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.774435;
        longitude = 5.25954;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.77392;
        longitude = 5.259843;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.773681;
        longitude = 5.259991;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.773292;
        longitude = 5.260248;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.772458;
        longitude = 5.260889;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.771593;
        longitude = 5.261693;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.770589;
        longitude = 5.262779;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.769989;
        longitude = 5.263557;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.769469;
        longitude = 5.264308;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.768876;
        longitude = 5.265254;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.768603;
        longitude = 5.265736;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.768027;
        longitude = 5.266848;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.767232;
        longitude = 5.268653;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.766449;
        longitude = 5.270536;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.766148;
        longitude = 5.271252;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.765747;
        longitude = 5.272163;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.765341;
        longitude = 5.273005;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.765052;
        longitude = 5.273548;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.764708;
        longitude = 5.274162;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.764274;
        longitude = 5.274872;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.763869;
        longitude = 5.275491;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.76317;
        longitude = 5.276467;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.762229;
        longitude = 5.277724;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.761582;
        longitude = 5.278569;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.759824;
        longitude = 5.280758;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.757165;
        longitude = 5.283882;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.756797;
        longitude = 5.28427;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.753489;
        longitude = 5.288133;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.752455;
        longitude = 5.289247;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.748609;
        longitude = 5.293489;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.74606;
        longitude = 5.296271;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.744188;
        longitude = 5.298307;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.742397;
        longitude = 5.300324;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.739547;
        longitude = 5.303427;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.738774;
        longitude = 5.30427;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.737659;
        longitude = 5.305484;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.736011;
        longitude = 5.307288;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.735718;
        longitude = 5.307615;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.73445;
        longitude = 5.309035;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.733147;
        longitude = 5.310602;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.732386;
        longitude = 5.311576;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.731252;
        longitude = 5.313053;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.730183;
        longitude = 5.31451;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.729403;
        longitude = 5.315458;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.726267;
        longitude = 5.319991;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.726051;
        longitude = 5.320235;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.725818;
        longitude = 5.320549;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.725644;
        longitude = 5.320761;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.725422;
        longitude = 5.32097;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.725254;
        longitude = 5.321085;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.725057;
        longitude = 5.32117;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.724901;
        longitude = 5.321213;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.724729;
        longitude = 5.321231;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.724585;
        longitude = 5.321227;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.724413;
        longitude = 5.321198;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.724241;
        longitude = 5.321158;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.724054;
        longitude = 5.321092;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.723843;
        longitude = 5.320998;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.723279;
        longitude = 5.320708;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.722915;
        longitude = 5.320496;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.722681;
        longitude = 5.320351;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.722469;
        longitude = 5.320177;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.72232;
        longitude = 5.32003;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.722164;
        longitude = 5.319845;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.722019;
        longitude = 5.319624;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.721895;
        longitude = 5.319417;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.721759;
        longitude = 5.319137;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.721636;
        longitude = 5.318827;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.72134;
        longitude = 5.317891;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.721138;
        longitude = 5.316979;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.720948;
        longitude = 5.316108;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.720756;
        longitude = 5.315131;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.720676;
        longitude = 5.3147;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.720315;
        longitude = 5.312686;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.719976;
        longitude = 5.310693;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.719412;
        longitude = 5.30688;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.718862;
        longitude = 5.302984;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.71799;
        longitude = 5.296756;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.717074;
        longitude = 5.290078;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.716606;
        longitude = 5.286635;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.715941;
        longitude = 5.281859;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.715749;
        longitude = 5.280614;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.715518;
        longitude = 5.279383;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.715245;
        longitude = 5.278161;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.714947;
        longitude = 5.276924;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.714575;
        longitude = 5.275551;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.714269;
        longitude = 5.274403;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.713867;
        longitude = 5.272992;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.713378;
        longitude = 5.271527;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.713119;
        longitude = 5.270825;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.71282;
        longitude = 5.270077;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.71248;
        longitude = 5.26927;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.711995;
        longitude = 5.268186;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.711279;
        longitude = 5.266761;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.71059;
        longitude = 5.265551;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.70997;
        longitude = 5.26453;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.708612;
        longitude = 5.262308;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.707882;
        longitude = 5.2612;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.707281;
        longitude = 5.26035;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.706659;
        longitude = 5.259509;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.705955;
        longitude = 5.258604;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.705554;
        longitude = 5.258126;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.704844;
        longitude = 5.257324;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.704267;
        longitude = 5.256711;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.70298;
        longitude = 5.2554;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.7021;
        longitude = 5.25442;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.701457;
        longitude = 5.253616;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.701077;
        longitude = 5.253132;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.700745;
        longitude = 5.252705;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.699797;
        longitude = 5.251242;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69915;
        longitude = 5.2501;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69867;
        longitude = 5.24918;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698033;
        longitude = 5.247813;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.697624;
        longitude = 5.246827;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.697238;
        longitude = 5.245825;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69698;
        longitude = 5.24506;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696728;
        longitude = 5.244243;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696373;
        longitude = 5.243183;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696015;
        longitude = 5.241848;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695737;
        longitude = 5.240654;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695512;
        longitude = 5.239457;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695356;
        longitude = 5.238596;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69521;
        longitude = 5.237614;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694704;
        longitude = 5.233957;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69461;
        longitude = 5.233298;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694443;
        longitude = 5.232157;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69429;
        longitude = 5.23137;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69415;
        longitude = 5.230779;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693967;
        longitude = 5.230099;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693648;
        longitude = 5.229035;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692694;
        longitude = 5.226234;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692506;
        longitude = 5.225599;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692327;
        longitude = 5.224945;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692138;
        longitude = 5.22415;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69198;
        longitude = 5.22339;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69185;
        longitude = 5.22256;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691766;
        longitude = 5.221762;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691699;
        longitude = 5.221029;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69166;
        longitude = 5.22041;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691652;
        longitude = 5.219833;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69166;
        longitude = 5.21927;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69169;
        longitude = 5.21821;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691785;
        longitude = 5.217089;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69336;
        longitude = 5.20514;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69342;
        longitude = 5.20472;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693518;
        longitude = 5.204038;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693797;
        longitude = 5.201964;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694087;
        longitude = 5.200161;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69439;
        longitude = 5.198521;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694659;
        longitude = 5.196887;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694826;
        longitude = 5.195919;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694975;
        longitude = 5.194886;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695021;
        longitude = 5.194536;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69513;
        longitude = 5.193633;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695229;
        longitude = 5.192618;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695437;
        longitude = 5.19032;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695558;
        longitude = 5.188986;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695783;
        longitude = 5.186813;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695996;
        longitude = 5.185204;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.697428;
        longitude = 5.174662;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698293;
        longitude = 5.16805;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698475;
        longitude = 5.166235;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69854;
        longitude = 5.165161;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698594;
        longitude = 5.164084;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698608;
        longitude = 5.162759;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698595;
        longitude = 5.161203;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698503;
        longitude = 5.15955;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698409;
        longitude = 5.158386;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69831;
        longitude = 5.157438;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698156;
        longitude = 5.156237;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.698059;
        longitude = 5.155584;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.697889;
        longitude = 5.154641;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.697597;
        longitude = 5.153094;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696538;
        longitude = 5.147937;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696442;
        longitude = 5.147456;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696029;
        longitude = 5.145475;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695644;
        longitude = 5.143444;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695467;
        longitude = 5.142249;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695303;
        longitude = 5.141007;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694639;
        longitude = 5.135322;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69436;
        longitude = 5.133377;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.6925;
        longitude = 5.120932;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692325;
        longitude = 5.119579;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692252;
        longitude = 5.118728;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69219;
        longitude = 5.117904;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692166;
        longitude = 5.117287;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692163;
        longitude = 5.116596;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692186;
        longitude = 5.116012;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692228;
        longitude = 5.11532;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692316;
        longitude = 5.114522;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692376;
        longitude = 5.114013;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692439;
        longitude = 5.113584;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692538;
        longitude = 5.113023;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69264;
        longitude = 5.11247;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692749;
        longitude = 5.112021;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692865;
        longitude = 5.111546;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693036;
        longitude = 5.110935;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693242;
        longitude = 5.11022;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694432;
        longitude = 5.106244;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694829;
        longitude = 5.104847;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695237;
        longitude = 5.103439;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69542;
        longitude = 5.1028;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69557;
        longitude = 5.102188;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695665;
        longitude = 5.101752;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695732;
        longitude = 5.101356;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695847;
        longitude = 5.100686;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695898;
        longitude = 5.100329;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695958;
        longitude = 5.099843;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696061;
        longitude = 5.098875;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696114;
        longitude = 5.098179;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696151;
        longitude = 5.097652;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696171;
        longitude = 5.097262;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696167;
        longitude = 5.096609;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696156;
        longitude = 5.096316;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.696072;
        longitude = 5.094927;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695886;
        longitude = 5.093037;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695147;
        longitude = 5.086661;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693497;
        longitude = 5.07278;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693119;
        longitude = 5.069686;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693045;
        longitude = 5.06908;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691167;
        longitude = 5.054252;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691093;
        longitude = 5.052391;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691034;
        longitude = 5.05052;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691037;
        longitude = 5.049612;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691037;
        longitude = 5.048206;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691048;
        longitude = 5.04695;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691061;
        longitude = 5.046222;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691072;
        longitude = 5.045589;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69111;
        longitude = 5.044524;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691152;
        longitude = 5.043723;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691207;
        longitude = 5.042916;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691277;
        longitude = 5.04198;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691385;
        longitude = 5.040882;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691542;
        longitude = 5.039416;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.691615;
        longitude = 5.038909;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692112;
        longitude = 5.034536;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692633;
        longitude = 5.030154;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692653;
        longitude = 5.029915;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692708;
        longitude = 5.029353;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69308;
        longitude = 5.025558;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693652;
        longitude = 5.020815;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694078;
        longitude = 5.016779;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695523;
        longitude = 4.999739;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695659;
        longitude = 4.997733;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695721;
        longitude = 4.996007;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695751;
        longitude = 4.994044;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69568;
        longitude = 4.991017;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69556;
        longitude = 4.987161;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695474;
        longitude = 4.984436;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69544;
        longitude = 4.983288;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695344;
        longitude = 4.980407;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695274;
        longitude = 4.978787;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.695104;
        longitude = 4.97618;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694549;
        longitude = 4.969571;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69403;
        longitude = 4.963284;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69339;
        longitude = 4.955858;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693183;
        longitude = 4.953354;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693031;
        longitude = 4.95115;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692974;
        longitude = 4.949797;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692956;
        longitude = 4.948657;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692969;
        longitude = 4.947398;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693001;
        longitude = 4.946132;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693155;
        longitude = 4.943375;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693179;
        longitude = 4.942866;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693264;
        longitude = 4.941418;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693838;
        longitude = 4.933055;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694356;
        longitude = 4.924837;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694394;
        longitude = 4.923271;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694402;
        longitude = 4.921909;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694366;
        longitude = 4.92061;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694326;
        longitude = 4.919644;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.694149;
        longitude = 4.91675;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693763;
        longitude = 4.911283;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693699;
        longitude = 4.91058;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693689;
        longitude = 4.910468;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.69347;
        longitude = 4.908361;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693249;
        longitude = 4.906821;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.693021;
        longitude = 4.905528;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692722;
        longitude = 4.904051;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.692368;
        longitude = 4.902626;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.689197;
        longitude = 4.891645;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.689045;
        longitude = 4.891119;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.688678;
        longitude = 4.889858;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.688363;
        longitude = 4.888551;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.687685;
        longitude = 4.885404;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.687586;
        longitude = 4.884945;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.6875;
        longitude = 4.884687;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.6874;
        longitude = 4.884286;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.687207;
        longitude = 4.883197;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.68713;
        longitude = 4.882811;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.687097;
        longitude = 4.882741;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.686993;
        longitude = 4.88261;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.686865;
        longitude = 4.88269;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.686715;
        longitude = 4.882767;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.686487;
        longitude = 4.88283;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.686304;
        longitude = 4.882849;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.686126;
        longitude = 4.88287;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.685816;
        longitude = 4.882837;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.685428;
        longitude = 4.882805;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.684972;
        longitude = 4.88276;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.684526;
        longitude = 4.882741;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.683917;
        longitude = 4.882706;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.683366;
        longitude = 4.882529;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.682814;
        longitude = 4.882361;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.682071;
        longitude = 4.882131;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.679357;
        longitude = 4.881291;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.675886;
        longitude = 4.880239;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.674955;
        longitude = 4.880007;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.673868;
        longitude = 4.879792;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.67298;
        longitude = 4.879673;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.671592;
        longitude = 4.879615;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.670877;
        longitude = 4.879615;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.669511;
        longitude = 4.879659;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.668141;
        longitude = 4.879833;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.66582;
        longitude = 4.88022;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.659032;
        longitude = 4.881214;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.655437;
        longitude = 4.881747;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.654155;
        longitude = 4.881925;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.652366;
        longitude = 4.882138;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.649744;
        longitude = 4.88233;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.648603;
        longitude = 4.882419;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.6469;
        longitude = 4.882523;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.642813;
        longitude = 4.882834;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.641235;
        longitude = 4.882933;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.640596;
        longitude = 4.88298;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.63988;
        longitude = 4.882987;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.63933;
        longitude = 4.88291;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.638924;
        longitude = 4.882827;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.638535;
        longitude = 4.882716;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.637859;
        longitude = 4.882453;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.637417;
        longitude = 4.882231;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.636665;
        longitude = 4.88178;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.636131;
        longitude = 4.881378;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.635512;
        longitude = 4.880785;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.633274;
        longitude = 4.878277;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.629961;
        longitude = 4.874489;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.629428;
        longitude = 4.873884;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.629066;
        longitude = 4.873478;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.627089;
        longitude = 4.871206;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.62673;
        longitude = 4.870791;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.62513;
        longitude = 4.869003;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.623804;
        longitude = 4.867556;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.623093;
        longitude = 4.866819;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.612817;
        longitude = 4.856776;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.61139;
        longitude = 4.85532;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.610434;
        longitude = 4.854226;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.60963;
        longitude = 4.85326;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.609029;
        longitude = 4.852478;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.608536;
        longitude = 4.851788;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.607789;
        longitude = 4.850691;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.607349;
        longitude = 4.85002;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.60517;
        longitude = 4.84622;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.604107;
        longitude = 4.844334;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.60313;
        longitude = 4.842578;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.602187;
        longitude = 4.840996;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.601669;
        longitude = 4.840197;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.600938;
        longitude = 4.839168;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.60062;
        longitude = 4.83873;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.59995;
        longitude = 4.837828;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.597443;
        longitude = 4.834789;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.596954;
        longitude = 4.834203;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.596154;
        longitude = 4.833226;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.593937;
        longitude = 4.83057;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.593071;
        longitude = 4.829604;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.592658;
        longitude = 4.829174;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.59177;
        longitude = 4.828324;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.591236;
        longitude = 4.827844;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.590689;
        longitude = 4.82739;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.589931;
        longitude = 4.826826;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58918;
        longitude = 4.826306;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.588418;
        longitude = 4.825826;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58757;
        longitude = 4.82536;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.586528;
        longitude = 4.824882;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58593;
        longitude = 4.82463;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.5853;
        longitude = 4.82443;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58465;
        longitude = 4.82406;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58408;
        longitude = 4.82381;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58348;
        longitude = 4.82361;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58309;
        longitude = 4.82351;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58243;
        longitude = 4.82339;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58189;
        longitude = 4.82334;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58154;
        longitude = 4.82327;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581427;
        longitude = 4.823168;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581279;
        longitude = 4.822986;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58118;
        longitude = 4.822773;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58112;
        longitude = 4.8225;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58112;
        longitude = 4.82219;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58114;
        longitude = 4.82204;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.58122;
        longitude = 4.82175;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581345;
        longitude = 4.821488;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581506;
        longitude = 4.821326;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581661;
        longitude = 4.821252;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581846;
        longitude = 4.821223;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.582204;
        longitude = 4.821225;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.582311;
        longitude = 4.821223;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.582313;
        longitude = 4.821022;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.582307;
        longitude = 4.820262;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.582289;
        longitude = 4.819512;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.582248;
        longitude = 4.818463;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.582206;
        longitude = 4.817738;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.582141;
        longitude = 4.816766;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.582057;
        longitude = 4.815893;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581954;
        longitude = 4.81495;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581899;
        longitude = 4.814503;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581772;
        longitude = 4.813666;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581555;
        longitude = 4.812359;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581373;
        longitude = 4.811445;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581216;
        longitude = 4.810707;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.581018;
        longitude = 4.80991;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.580877;
        longitude = 4.80932;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.579945;
        longitude = 4.806014;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.579844;
        longitude = 4.805679;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.579669;
        longitude = 4.805043;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.578557;
        longitude = 4.801125;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.578323;
        longitude = 4.800285;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.578134;
        longitude = 4.799611;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.577918;
        longitude = 4.79883;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.577744;
        longitude = 4.79813;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.577666;
        longitude = 4.79775;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.577558;
        longitude = 4.797283;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.577345;
        longitude = 4.796272;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.577183;
        longitude = 4.795357;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576917;
        longitude = 4.793602;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576807;
        longitude = 4.793502;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576571;
        longitude = 4.793583;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576481;
        longitude = 4.793609;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576414;
        longitude = 4.793627;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576353;
        longitude = 4.793626;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57628;
        longitude = 4.793561;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576216;
        longitude = 4.793407;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57604;
        longitude = 4.792243;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576015;
        longitude = 4.792094;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.575856;
        longitude = 4.792002;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.575459;
        longitude = 4.792048;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.575023;
        longitude = 4.792035;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.575004;
        longitude = 4.792177;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57491;
        longitude = 4.79314;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57475;
        longitude = 4.79457;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57471;
        longitude = 4.79474;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57486;
        longitude = 4.794838;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57523;
        longitude = 4.79508;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.575492;
        longitude = 4.794829;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.575984;
        longitude = 4.794363;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576067;
        longitude = 4.794294;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57629;
        longitude = 4.79411;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576053;
        longitude = 4.793867;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57595;
        longitude = 4.79393;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57594;
        longitude = 4.793038;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.575937;
        longitude = 4.792868;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57594;
        longitude = 4.793038;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57595;
        longitude = 4.79393;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.575937;
        longitude = 4.792868;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.57594;
        longitude = 4.793038;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.575966;
        longitude = 4.792262;
        points.add(new SKPosition(latitude, longitude));
        latitude = 51.576058;
        longitude = 4.79295;
        points.add(new SKPosition(latitude, longitude));
        createRouteFromGPSPoints(points);
    }

    private SKNavigationListener mSKNavigationListener = new SKNavigationListener() {
        @Override
        public void onDestinationReached() {
        }

        @Override
        public void onSignalNewAdviceWithInstruction(String instruction) {}

        @Override
        public void onSignalNewAdviceWithAudioFiles(String[] audioFiles, boolean b) {
            SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);
        }

        @Override
        public void onSpeedExceededWithAudioFiles(String[] strings, boolean b) {
        }

        @Override
        public void onSpeedExceededWithInstruction(String s, boolean b) {
        }

        @Override
        public void onUpdateNavigationState(SKNavigationState skNavigationState) {
        }

        @Override
        public void onReRoutingStarted() {
        }

        @Override
        public void onFreeDriveUpdated(String s, String s1, String s2, SKStreetType skStreetType, double v, double v1) {
        }

        @Override
        public void onViaPointReached(int i) {
        }

        @Override
        public void onVisualAdviceChanged(final boolean firstVisualAdviceChanged, final boolean secondVisualAdviceChanged, final SKNavigationState skNavigationState) {
        }

        @Override
        public void onTunnelEvent(boolean b) {
        }
    };

    private SKRouteListener mSKRouteListener = new SKRouteListener() {
        @Override
        public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo) {
            L.e("onRouteCalculationCompleted");
        }

        @Override
        public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {
            L.e("onRouteCalculationFailed: " + skRoutingErrorCode);
        }

        @Override
        public void onAllRoutesCompleted() {
            launchNavigation();
        }

        @Override
        public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {
        }

        @Override
        public void onOnlineRouteComputationHanging(int i) {
        }
    };

    private SKMapSurfaceListener mSKMapSurfaceListener = new SKMapSurfaceListener() {
        @Override
        public void onActionPan() {
        }

        @Override
        public void onActionZoom() {
        }

        @Override
        public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {
            onMapReady();
        }

        @Override
        public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion) {
        }

        @Override
        public void onMapRegionChangeStarted(SKCoordinateRegion skCoordinateRegion) {
        }

        @Override
        public void onMapRegionChangeEnded(SKCoordinateRegion skCoordinateRegion) {
        }

        @Override
        public void onDoubleTap(SKScreenPoint skScreenPoint) {
        }

        @Override
        public void onSingleTap(SKScreenPoint skScreenPoint) {
        }

        @Override
        public void onRotateMap() {
        }

        @Override
        public void onLongPress(SKScreenPoint skScreenPoint) {
        }

        @Override
        public void onInternetConnectionNeeded() {
        }

        @Override
        public void onMapActionDown(SKScreenPoint skScreenPoint) {
        }

        @Override
        public void onMapActionUp(SKScreenPoint skScreenPoint) {
        }

        @Override
        public void onPOIClusterSelected(SKPOICluster skpoiCluster) {
        }

        @Override
        public void onMapPOISelected(SKMapPOI skMapPOI) {
        }

        @Override
        public void onAnnotationSelected(SKAnnotation skAnnotation) {
        }

        @Override
        public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI) {
        }

        @Override
        public void onCompassSelected() {
        }

        @Override
        public void onCurrentPositionSelected() {
        }

        @Override
        public void onObjectSelected(int i) {
        }

        @Override
        public void onInternationalisationCalled(int i) {
        }

        @Override
        public void onBoundingBoxImageRendered(int i) {
        }

        @Override
        public void onGLInitializationError(String s) {
        }

        @Override
        public void onScreenshotReady(Bitmap bitmap) {
        }
    };
}
