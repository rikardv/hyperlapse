package org.rikardv.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.postgis.LineString
import org.rikardv.api.RouteResponse
import org.rikardv.db.RouteRepository
import org.rikardv.db.WaypointRepository
import org.slf4j.LoggerFactory

class OpenStreetServiceClient(
    private val httpClient: CloseableHttpClient,
    private val baseUrl: String,
    private val apiKey: String,
    private val routeRepository: RouteRepository,
    private val waypointRepository: WaypointRepository
) {
    private val logger = LoggerFactory.getLogger(OpenStreetServiceClient::class.java)
    private val objectMapper = jacksonObjectMapper()

    fun createRoute(start: String, end: String): RouteResponse? {
        val requestUri = "$baseUrl/v2/directions/driving-car?api_key=$apiKey&start=$start&end=$end"
        val request = HttpGet(requestUri)

        logger.info("Invoking OpenStreetServiceClient with URI: $requestUri")

        return try {
            val response = httpClient.execute(request)
            response.use { it ->
                if (it.code == 200) {
                    val responseBody = EntityUtils.toString(it.entity)
                    val routeResponse = objectMapper.readValue<RouteResponse>(responseBody)
                    val routeId = routeRepository.saveRoute(start, end)

                    // loop through the waypoints and save them to the database
                    routeResponse.features.flatMap { it.geometry.coordinates }.forEach {
                        waypointRepository.saveWaypoint(routeId, it[1], it[0])
                    }

                    routeResponse
                } else {
                    logger.error("Failed to fetch route, status code: ${it.code}")
                    null
                }
            }
        } catch (e: Exception) {
            logger.error("Exception occurred while fetching route: ${e.message}", e)
            null
        }
    }


}