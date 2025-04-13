package org.rikardv.resources

import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jobrunr.scheduling.BackgroundJob
import org.rikardv.client.OpenStreetServiceClient
import org.rikardv.service.JobExecutionService


@Path("/trip")
class TripResource(private val openStreetServiceClient: OpenStreetServiceClient, private val jobExecutionService: JobExecutionService) {


    @POST
    @Path("/route")
    fun createRoute(@FormParam("start") start: String, @FormParam("end") end: String): Response {
        val routeResponse = openStreetServiceClient.createRoute(start, end)
        return Response.status(Response.Status.OK).entity(routeResponse).type(MediaType.APPLICATION_JSON).build()
    }

    @POST
    @Path("/hyperlapse")
    fun createHyperlapse(@FormParam("routeId") routeId: Int): Response {
        // val hyperLapse = mapillaryClient.createHyperlapse(routeId)
        val jobId = jobExecutionService.createHyperlapse(routeId)
        return Response.status(Response.Status.OK).entity(jobId).type(MediaType.APPLICATION_JSON).build()

    }

    @GET
    @Path("/hyperlapse/progress")
    fun getHyperlapseProgress(): Response {
        // val progress = mapillaryClient.getHyperlapseProgress()
        // return Response.status(Response.Status.OK).entity(progress).type(MediaType.APPLICATION_JSON).build()
        return Response.status(Response.Status.OK).entity("Progress").type(MediaType.APPLICATION_JSON).build()
    }
}