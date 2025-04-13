package org.rikardv.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.jobrunr.jobs.context.JobContext
import org.jobrunr.scheduling.BackgroundJob
import org.rikardv.api.ImageData
import org.rikardv.db.RouteRepository
import org.rikardv.db.StreetSnapshotRepository
import org.rikardv.db.WaypointRepository
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class MapillaryClient(
    private val httpClient: CloseableHttpClient,
    private val baseUrl: String,
    private val apiKey: String,
    private val routeRepository: RouteRepository,
    private val waypointRepository: WaypointRepository,
    private val streetSnapshotRepository: StreetSnapshotRepository,
) {
    private val objectMapper = jacksonObjectMapper()


    fun createHyperlapse(routeId: Int, jobContext: JobContext) {
        println("Creating hyperlapse for routeId: $routeId")
        val waypoints = waypointRepository.getWaypointsByRouteId(routeId) ?: return

        if (waypoints.size < 2) return // Need at least two points for direction

        val progressBar = jobContext.progressBar(waypoints.size)

        val images = mutableListOf<ImageData>()

        for (i in 0 until waypoints.size - 1) {



            val waypoint1 = waypoints[i]
            val waypoint2 = waypoints[i + 1]

            val bearing = calculateBearing(waypoint1.lat, waypoint1.lon, waypoint2.lat, waypoint2.lon)
            val fetchedImages = fetchImagesV4(waypoint1.lat, waypoint1.lon)

            if (fetchedImages.isNullOrEmpty()) {
                progressBar.increaseByOne()
                continue
            }

            val bestImage = fetchedImages.minByOrNull { abs(it.compass_angle - bearing) }

            if (bestImage != null) {
                println("Saving snapshot for routeId: $routeId, waypointId: ${waypoint1.id}, imageId: ${bestImage.id}")
                streetSnapshotRepository.saveSnapshot(routeId, waypoint1.id, bestImage.id, bestImage.thumb_1024_url)
                images.add(bestImage)
            }
            else {
                println("No image found for routeId: $routeId, waypointId: ${waypoint1.id}")
            }

            progressBar.increaseByOne()
        }

    }

    private fun fetchImagesV4(lat: Double, lon: Double): List<ImageData>? {
        val delta = 0.00005 // ~50m buffer
        val bbox = "${lon - delta},${lat - delta},${lon + delta},${lat + delta}"

        val encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8.toString())
        val encodedBbox = URLEncoder.encode(bbox, StandardCharsets.UTF_8.toString())

        val requestUri = "$baseUrl/images/?access_token=$encodedApiKey&fields=id,thumb_1024_url,compass_angle&bbox=$encodedBbox"

        val request = HttpGet(requestUri)
        val response = httpClient.execute(request)

        response.use {
            return if (it.code == 200) {
                val responseBody = EntityUtils.toString(it.entity)
                val jsonResponse = objectMapper.readTree(responseBody)
                jsonResponse["data"]?.map {
                    ImageData(
                        it["id"].asText(),
                        it["thumb_1024_url"].asText(),
                        it["compass_angle"].asDouble()
                    )
                }
            } else {
                null
            }
        }
    }

    private fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val theta = atan2(y, x)

        return (Math.toDegrees(theta) + 360) % 360 // Normalize to 0-360 degrees
    }

}
