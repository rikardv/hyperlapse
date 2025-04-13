package org.rikardv.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.rikardv.api.Route

object Routes : IntIdTable() {
    val start = varchar("start", 255)
    val end = varchar("end", 255)
}


class RouteRepository {
    fun initDatabase() {
        transaction {
            SchemaUtils.create(Routes)
        }
    }

    fun saveRoute(start: String, end: String): Int {
        return transaction {
            Routes.insertAndGetId {
                it[Routes.start] = start
                it[Routes.end] = end
            }.value
        }
    }

    fun getRouteById(routeId: Int): Route? {
        return transaction {
            Routes.select { Routes.id eq routeId }.map {
                Route(
                    it[Routes.id].value,
                    it[Routes.start],
                    it[Routes.end]
                )
            }.firstOrNull()
        }
    }
}
