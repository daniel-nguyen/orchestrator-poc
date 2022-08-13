package com.slupicki.orchestrator.service.event

import com.slupicki.orchestrator.service.IEventBus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("InMemoryEventBus")
class EventBus : IEventBus {
    private val buses = mutableMapOf<Bus, Channel<EventInContext>>()

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun send(destination: Bus, event: EventInContext) {
        setUpBusIfNotExist(destination)
        GlobalScope.launch {
            val channel = buses[destination]!!
            if (channel.isClosedForSend)
                throw Exception("Channel to $destination closed!")
            channel.send(event)
        }
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun subscribe(destination: Bus, function: Consumer<EventInContext>) {
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