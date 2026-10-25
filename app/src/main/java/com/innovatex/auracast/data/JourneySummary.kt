package com.innovatex.auracast.data

data class JourneySummary(
    val destinationName: String,
    val coveredStopsConnected: Int,
    val coveredStopsTotal: Int,
    val uncoveredStopsPassed: Int,
    val journeyMinutes: Int,
    val completed: Boolean
) {
    val allCoveredStopsConnected: Boolean
        get() = coveredStopsTotal > 0 && coveredStopsConnected == coveredStopsTotal
}