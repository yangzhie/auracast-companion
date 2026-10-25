package com.innovatex.auracast.data

// Demo routes for the proof of concept.
object SampleData {

    // Route 1
    private val route1 = listOf(
        Stop("r1s1", "Stop 1 (R1)", "Stop 1 (R1)", BroadcastIdentity(1, 1)),
        Stop("r1s2", "Stop 2 (R1)", "Stop 2 (R1)", BroadcastIdentity(1, 2)),
        Stop("r1s3", "Stop 3 (R1)", "Stop 3 (R1)", BroadcastIdentity(1, 3)),
        Stop("r1s4", "Stop 4 (R1)", "Stop 4 (R1)", BroadcastIdentity(1, 4))
    )

    // Route 2 (for route filtering)
    private val route2 = listOf(
        Stop("r2s1", "Stop 1 (R2)", "Stop 1 (R2)", BroadcastIdentity(2, 1)),
        Stop("r2s2", "Stop 2 (R2)", "Stop 2 (R2)", BroadcastIdentity(2, 2))
    )

    val routes = listOf(
        TransitRoute("1", "1", "via Route 1", route1),
        TransitRoute("2", "2", "via Route 2", route2),
        TransitRoute("3", "3", "To Unfitted", emptyList())
    )

    fun routeById(id: String): TransitRoute =
        routes.firstOrNull { it.id == id }
            ?: error("No route with id '$id'. Known ids: ${routes.joinToString { it.id }}")
}