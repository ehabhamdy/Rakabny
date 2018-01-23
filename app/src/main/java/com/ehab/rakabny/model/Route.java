package com.ehab.rakabny.model;

import java.util.List;

/**
 * Created by ehabhamdy on 1/10/18.
 */

public class Route {

    private List<Legs> legs = null;
    private OverviewPolyline overview_polyline;

    public List<Legs> getLegs() {
        return legs;
    }

    public void setLegs(List<Legs> legs) {
        this.legs = legs;
    }

    public OverviewPolyline getOverviewPolyline() {
        return overview_polyline;
    }

    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overview_polyline = overviewPolyline;
    }
}
