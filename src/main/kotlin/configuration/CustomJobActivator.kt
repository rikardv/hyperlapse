package org.rikardv.config

import org.jobrunr.server.JobActivator
import org.rikardv.client.MapillaryClient

class CustomJobActivator(
    private val mapillaryClient: MapillaryClient
) : JobActivator {
    override fun <T : Any> activateJob(type: Class<T>): T {
        return when (type) {
            MapillaryClient::class.java -> mapillaryClient as T
            else -> throw IllegalArgumentException("Unknown job type: $type")
        }
    }
}
