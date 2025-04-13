package org.rikardv.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object StreetSnapshots : IntIdTable() {
    val routeId = integer("route_id").references(Routes.id)
    val waypointId = integer("waypoint_id").references(Waypoints.id)
    val imageId = varchar("image_id", 255)
    val thumbUrl = varchar("thumb_url", 1024)
}

class StreetSnapshotRepository {
    fun initDatabase() {
        transaction {
            SchemaUtils.create(StreetSnapshots)
        }
    }

    fun saveSnapshot(routeId: Int, waypointId: Int, imageId: String, thumbUrl: String) {
        transaction {
            StreetSnapshots.insert {
                it[StreetSnapshots.routeId] = routeId
                it[StreetSnapshots.waypointId] = waypointId
                it[StreetSnapshots.imageId] = imageId
                it[StreetSnapshots.thumbUrl] = thumbUrl
            }
        }
    }
}