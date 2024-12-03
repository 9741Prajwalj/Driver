package com.mlt.driver.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;


import java.util.List;

public class RouteResponse {

    private List<LatLng> routePoints;
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public static class Route {
        @SerializedName("overview_polyline")
        private OverviewPolyline overviewPolyline;

        public OverviewPolyline getOverviewPolyline() {
            return overviewPolyline;
        }
    }

    public static class OverviewPolyline {
        @SerializedName("points")
        private String points;

        public String getPoints() {
            return points;
        }
    }

    public List<LatLng> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<LatLng> routePoints) {
        this.routePoints = routePoints;
    }
}

