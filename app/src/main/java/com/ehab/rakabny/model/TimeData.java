package com.ehab.rakabny.model;

import java.util.List;


/**
 * Created by ehabhamdy on 1/10/18.
 */

public class TimeData {

    private List<Route> routes = null;
    private String status;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
