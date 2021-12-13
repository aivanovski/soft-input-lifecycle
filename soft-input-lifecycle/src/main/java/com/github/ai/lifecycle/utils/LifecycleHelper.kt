package com.github.ai.lifecycle.utils

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State

private val LIFECYCLE_NODES = listOf(
    LifecycleNode(
        State.INITIALIZED,
        onNext = Event.ON_CREATE
    ),
    LifecycleNode(
        State.CREATED,
        onNext = Event.ON_START
    ),
    LifecycleNode(
        State.STARTED,
        onNext = Event.ON_RESUME,
        onPrev = Event.ON_STOP
    ),
    LifecycleNode(
        State.RESUMED,
        onNext = Event.ON_PAUSE,
        onPrev = Event.ON_PAUSE
    ),
    LifecycleNode(
        State.STARTED,
        onNext = Event.ON_STOP,
        onPrev = Event.ON_RESUME
    ),
    LifecycleNode(
        State.CREATED,
        onNext = Event.ON_DESTROY,
        onPrev = Event.ON_START
    ),
    LifecycleNode(
        State.DESTROYED
    )
)

internal fun getEventsBetween(source: State, destination: State): List<Event> {
    if (source == destination) {
        return emptyList()
    }

    val srcPositions = getLifecyclePosition(source)
    val dstPositions = getLifecyclePosition(destination)

    val routes = mutableListOf<List<Event>>()
    for (srcPosition in srcPositions) {
        for (dstPosition in dstPositions) {
            routes.add(getRouteBetween(srcPosition, dstPosition))
        }
    }

    return routes.minByOrNull { it.size } ?: emptyList()
}

private fun getRouteBetween(fromIdx: Int, toIdx: Int): List<Event> {
    val path = mutableListOf<Event>()
    var idx = fromIdx
    while (idx != toIdx) {
        val node = LIFECYCLE_NODES[idx]

        if (toIdx > fromIdx) {
            node.onNext?.let {
                path.add(it)
            }
            idx++
        } else {
            node.onPrev?.let {
                path.add(it)
            }
            idx--
        }
    }

    return path
}

private fun getLifecyclePosition(state: State): List<Int> {
    return LIFECYCLE_NODES
        .withIndex()
        .filter { (_, node) -> node.state == state }
        .map { (position, _) -> position }
}

private data class LifecycleNode(
    val state: State,
    val onNext: Event? = null,
    val onPrev: Event? = null
)