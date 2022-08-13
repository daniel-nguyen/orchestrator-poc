package com.slupicki.orchestrator.service.event

import com.slupicki.orchestrator.model.Action
import com.slupicki.orchestrator.model.Event

data class EventInContext(
    val stateMachineId: String,
    val event: Event = Event.UNKNOWN,
    val context: Map<String, String> = emptyMap(),
    val action: Action = Action.NO_ACTION,
)