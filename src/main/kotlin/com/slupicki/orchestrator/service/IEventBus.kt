package com.slupicki.orchestrator.service

import com.slupicki.orchestrator.service.event.Bus
import com.slupicki.orchestrator.service.event.EventInContext
import java.util.function.Consumer

interface IEventBus {
    fun send(destination: Bus, event: EventInContext)

    fun subscribe(destination: Bus, function: Consumer<EventInContext>)
}