package com.slupicki.orchestrator.service

import com.slupicki.orchestrator.model.Action
import com.slupicki.orchestrator.model.Event
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.util.function.Consumer

enum class Bus {
    TO_EXECUTOR,
    TO_STATE_MACHINE,
}

data class EventInContext(
    val stateMachineId: String,
    val event: Event = Event.UNKNOWN,
    val context: Map<String, String> = emptyMap(),
    val action: Action = Action.NO_ACTION,
)

@Service
class EventBus {
    private val buses = mutableMapOf<Bus, Channel<EventInContext>>()

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun send(destination: Bus, event: EventInContext) {
        setUpBusIfNotExist(destination)
        GlobalScope.launch {
            val channel = buses[destination]!!
            if (channel.isClosedForSend)
                throw Exception("Channel to $destination closed!")
            channel.send(event)
        }
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun subscribe(destination: Bus, function: Consumer<EventInContext>) {
        setUpBusIfNotExist(destination)
        GlobalScope.launch {
            val channel = buses[destination]!!
            while (!channel.isClosedForReceive) {
                function.accept(channel.receive())
            }
        }
    }

    private fun setUpBusIfNotExist(destination: Bus) {
        if (!buses.containsKey(destination)) {
            buses[destination] = Channel()
        }
    }
}