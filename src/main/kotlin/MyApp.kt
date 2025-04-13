package org.rikardv

import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.core.Application
import io.dropwizard.core.setup.Environment
import org.jetbrains.exposed.sql.Database
import org.jobrunr.configuration.JobRunr
import org.jobrunr.configuration.JobRunrConfiguration
import org.jobrunr.storage.sql.postgres.PostgresStorageProvider
import org.rikardv.client.MapillaryClient
import org.rikardv.client.MapillaryClientFactory
import org.rikardv.client.OpenStreetServiceClient
import org.rikardv.config.CustomJobActivator
import org.rikardv.configuration.ApplicationConfiguration
import org.rikardv.db.RouteRepository
import org.rikardv.db.StreetSnapshotRepository
import org.rikardv.db.WaypointRepository
import org.rikardv.filters.CorsFilter
import org.rikardv.resources.TripResource
import org.rikardv.service.JobExecutionService

class MyApp: Application<ApplicationConfiguration>() {
    companion object {
        @JvmStatic fun main(args: Array<String>) = MyApp().run(*args)
    }
    override fun run(appConfig: ApplicationConfiguration, environment: Environment) {

        val dataSourceFactory = appConfig.getDataSourceFactory()
        val dataSource = dataSourceFactory.build(environment.metrics(), "postgresql")



        Database.connect(dataSource)
        // we are going to implement this function soon
        val httpClient = HttpClientBuilder(environment).using(appConfig.getHttpClientConfiguration())
        .build(name);


        val routeRepository = RouteRepository()
        val waypointRepository = WaypointRepository()
        val streetSnapshotRepository = StreetSnapshotRepository()



        routeRepository.initDatabase() // Initialize the database
        waypointRepository.initDatabase() // Initialize the database
        streetSnapshotRepository.initDatabase() // Initialize the database




        val mapillaryClient = MapillaryClient(httpClient, appConfig.mapillaryBaseUrl, appConfig.mapillaryApiKey, routeRepository, waypointRepository, streetSnapshotRepository)
        val jobExecutionService = JobExecutionService(mapillaryClient)
        val openStreetServiceClient = OpenStreetServiceClient(httpClient, appConfig.openStreetServiceBaseUrl, appConfig.openStreetServiceApiKey, routeRepository, waypointRepository)

        // Register the service with the environment
        environment.jersey().register(TripResource(openStreetServiceClient, jobExecutionService))
        environment.jersey().register(CorsFilter()) // Register the CORS filter

        val storageProvider = PostgresStorageProvider(dataSource)

        val jobActivator = CustomJobActivator(mapillaryClient)
        JobRunr.configure().useStorageProvider(storageProvider).useJobActivator(jobActivator).useBackgroundJobServer().useDashboard().initialize()
    }
}