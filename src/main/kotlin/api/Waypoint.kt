package org.rikardv.api

class Waypoint {
    val id: Int
    val routeId: Int
    val lat: Double
    val lon: Double

    constructor(id: Int, routeId: Int, lat: Double, lon: Double) {
        this.id = id
        this.routeId = routeId
        this.lat = lat
        this.lon = lon
    }

}
