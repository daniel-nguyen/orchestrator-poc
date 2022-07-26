package com.slupicki.orchestrator.service

import com.slupicki.orchestrator.model.Event
import mu.KotlinLogging
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ExecutorEngine(
    val eventBus: EventBus,
) {
    val log = KotlinLogging.logger {}

    @PostConstruct
    fun postConstruct() {
        eventBus.subscribe(Bus.TO_EXECUTOR) {
            execute(it)
        }
    }

    private fun execute(event: EventInContext) {
        log.info { "Receive event: $event" }
        when (event.action) {
            else -> noActionExecutor(event)
        }
    }

    private fun noActionExecutor(event: EventInContext) {
        val logMsg = "For machine ${event.stateMachineId} executed ${event.action} - he,he just kidding. This is no action executor :-)"
        log.info { "$logMsg in context $event" }
        val attributes = event.context.toMutableMap()
        addLogToContext("ExecutorEngine: $logMsg", attributes)
        val response = event.copy(
            context = attributes,
            event = Event.UNKNOWN,
        )
        log.info { "Send to Bus.TO_STATE_MACHINE: $response" }
        eventBus.send(Bus.TO_STATE_MACHINE, response)
    }
}

fun addLogToContext(logMsg: String, context: MutableMap<String, String>) {
    if (!context.containsKey("log_counter")) {
        context["log_counter"] = "0"
    }
    val logCounter = context["log_counter"]!!.toInt() + 1
    context["log$logCounter"] = logMsg
    context["log_counter"] = logCounter.toString()

}