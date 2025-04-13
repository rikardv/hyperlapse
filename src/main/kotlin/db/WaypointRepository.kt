package org.rikardv.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.rikardv.api.Waypoint

object Waypoints : Table() {
    val id = integer("id").autoIncrement()
    val routeId = integer("route_id").references(Routes.id)
    val lat = double("lat")
    val lon = double("lon")
    override val primaryKey = PrimaryKey(id)
}

class WaypointRepository {
    fun initDatabase() {
        transaction {
            SchemaUtils.create(Waypoints)
        }
    }

    fun saveWaypoint(routeId: Int, lat: Double, lon: Double) {
        transaction {
            Waypoints.insert {
                it[Waypoints.routeId] = routeId
                it[Waypoints.lat] = lat
                it[Waypoints.lon] = lon
            }
        }
    }

    fun getWaypointsByRouteId(routeId: Int): List<Waypoint> {
        return transaction {
            Waypoints.select { Waypoints.routeId eq routeId }.map {
                Waypoint(
                    it[Waypoints.id],
                    it[Waypoints.routeId],
                    it[Waypoints.lat],
                    it[Waypoints.lon]
                )
            }
        }
    }
}