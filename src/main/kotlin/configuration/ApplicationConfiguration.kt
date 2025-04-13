package org.rikardv.configuration

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.core.Configuration
import io.dropwizard.db.DataSourceFactory
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

class ApplicationConfiguration: Configuration() {

    @Valid
    @NotNull
    private var httpClient: HttpClientConfiguration = HttpClientConfiguration()
    private var database: DataSourceFactory = DataSourceFactory()


    @JsonProperty("httpClient")
    fun getHttpClientConfiguration(): HttpClientConfiguration {
        return httpClient
    }

    @JsonProperty("httpClient")
    fun setHttpClientConfiguration(httpClient: HttpClientConfiguration) {
        this.httpClient = httpClient
    }

    @JsonProperty("database")
    fun getDataSourceFactory(): DataSourceFactory {
        return database
    }

    @JsonProperty("database")
    fun setDataSourceFactory(database: DataSourceFactory) {
        this.database = database
    }

    @NotNull
    @JsonProperty("openStreetServiceBaseUrl")
    lateinit var openStreetServiceBaseUrl: String

    @NotNull
    @JsonProperty("openStreetServiceApiKey")
    lateinit var openStreetServiceApiKey: String

    @NotNull
    @JsonProperty("mapillaryBaseUrl")
    lateinit var mapillaryBaseUrl: String

    @NotNull
    @JsonProperty("mapillaryApiKey")
    lateinit var mapillaryApiKey: String


}