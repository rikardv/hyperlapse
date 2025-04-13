package org.rikardv.client

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.rikardv.db.RouteRepository
import org.rikardv.db.StreetSnapshotRepository
import org.rikardv.db.WaypointRepository

class MapillaryClientFactory(
    private val httpClient: CloseableHttpClient,
    private val baseUrl: String,
    private val apiKey: String,
    private val routeRepository: RouteRepository,
    private val waypointRepository: WaypointRepository,
    private val streetSnapshotRepository: StreetSnapshotRepository
) {
    fun create(): MapillaryClient {
        return MapillaryClient(httpClient, baseUrl, apiKey, routeRepository, waypointRepository, streetSnapshotRepository)
    }
}