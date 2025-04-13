package org.rikardv.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RouteResponse(
    val type: String,
    val bbox: List<Double>,
    val features: List<Feature>,
    val metadata: Metadata
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Feature(
    val bbox: List<Double>,
    val type: String,
    val properties: Properties,
    val geometry: Geometry
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Properties(
    val segments: List<Segment>,
    val way_points: List<Int>,
    val summary: Summary
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Segment(
    val distance: Double,
    val duration: Double,
    val steps: List<Step>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Step(
    val distance: Double,
    val duration: Double,
    val type: Int,
    val instruction: String,
    val name: String,
    val way_points: List<Int>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Summary(
    val distance: Double,
    val duration: Double
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Geometry(
    val coordinates: List<List<Double>>,
    val type: String
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Metadata(
    val attribution: String,
    val service: String,
    val timestamp: Long,
    val query: Query,
    val engine: Engine
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Query(
    val coordinates: List<List<Double>>,
    val profile: String,
    val profileName: String,
    val format: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Engine(
    val version: String,
    val build_date: String,
    val graph_date: String
)