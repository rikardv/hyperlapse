package org.rikardv.service

import org.jobrunr.jobs.JobId
import org.jobrunr.jobs.context.JobContext
import org.jobrunr.scheduling.BackgroundJob
import org.rikardv.client.MapillaryClient
import org.rikardv.client.MapillaryClientFactory


class JobExecutionService(
    private val mapillaryClient: MapillaryClient
) {

    fun createHyperlapse(routeId: Int): JobId {
        println("Starting jobs")
        return BackgroundJob.enqueue { mapillaryClient.createHyperlapse(routeId, JobContext.Null) }
    }


}
