package com.easyroute.utility;

import android.graphics.Bitmap;

import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.navigation.SKNavigationState.SKStreetType;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
/**
 * Created by imenekse on 17/02/17.
 */

public abstract class SkobblerListeners {

    public static class NavigationListener implements SKNavigationListener {

        @Override
        public void onDestinationReached() {
        }

        @Override
        public void onSignalNewAdviceWithInstruction(String s) {
        }

        @Override
        public void onSignalNewAdviceWithAudioFiles(String[] strings, boolean b) {
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
        public void onVisualAdviceChanged(boolean b, boolean b1, SKNavigationState skNavigationState) {
        }

        @Override
        public void onTunnelEvent(boolean b) {
        }
    }

    public static class RouteListener implements SKRouteListener {

        @Override
        public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo) {
        }

        @Override
        public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {
        }

        @Override
        public void onAllRoutesCompleted() {
        }

        @Override
        public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {
        }

        @Override
        public void onOnlineRouteComputationHanging(int i) {
        }
    }

    public static class MapSurfaceListener implements SKMapSurfaceListener {

        @Override
        public void onActionPan() {
        }

        @Override
        public void onActionZoom() {
        }

        @Override
        public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {
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
    }
}
