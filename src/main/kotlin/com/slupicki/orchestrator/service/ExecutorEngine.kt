package com.slupicki.orchestrator.service

import com.slupicki.orchestrator.model.Action
import com.slupicki.orchestrator.model.Event
import mu.KotlinLogging
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

data class ExecutorResponse(
    val event: Event,
    val stateMachineId: String,
    val context: Map<String, String>,
)

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
        val logMsg = "For machine ${event.stateMachineId} executed ${event.action}"
        log.info { "$logMsg in context $event" }
        val attributes = event.context.toMutableMap()
        addLogToContext("ExecutorEngine: $logMsg", attributes)
        val response = event.copy(
            context = attributes
        )
        //eventBus.send(Bus.TO_STATE_MACHINE, response)
        log.info { "Normally this will be send to Bus.TO_STATE_MACHINE but this is ${Action.NO_ACTION} executor: $response" }
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