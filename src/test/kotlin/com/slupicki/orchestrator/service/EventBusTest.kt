package com.slupicki.orchestrator.service

import com.slupicki.orchestrator.model.Event
import mu.KotlinLogging
import org.junit.jupiter.api.Test


internal class EventBusTest {
    val log = KotlinLogging.logger {}

    @Test
    internal fun `basic test`() {
        val eventBus = EventBus()

        eventBus.subscribe(Bus.TO_EXECUTOR) {
            log.info { "Executor got event: $it" }
            Thread.sleep(100)
            val executorCtx = it.context.toMutableMap()
            executorCtx["Response"] = "Hello from executor"
            val executorEvent = it.copy(context = executorCtx)
            eventBus.send(Bus.TO_STATE_MACHINE, executorEvent)
        }
        eventBus.subscribe(Bus.TO_STATE_MACHINE) {
            log.info { "State machine got event: $it" }
        }

        (1..10).forEach {
            val event = EventInContext(
                Event.SUCCESS,
                "machine1",
                mapOf("event" to it.toString()),
            )
            eventBus.send(Bus.TO_EXECUTOR, event)
            log.info { "To ${Bus.TO_EXECUTOR} sent $event" }
        }

        Thread.sleep(2 * 1000)
        log.info { "The end" }
    }
}