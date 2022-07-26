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

    private fun execute(context: EventInContext) {
        when (context.action) {
            else -> successfulResponse(context)
        }
    }

    private fun successfulResponse(context: EventInContext) {
        TODO("Not yet implemented")
    }

    fun execute(action: Action, stateMachineId: String, context: MutableMap<String, String>): ExecutorResponse = when (action) {
        Action.ATLAS -> successfulResponse(action, stateMachineId, context)
        Action.FINSTAR -> successfulResponse(action, stateMachineId, context)
        Action.FINSTAR_MANUAL -> successfulResponse(action, stateMachineId, context)
        Action.SMART_TRADE -> successfulResponse(action, stateMachineId, context)
        Action.SMART_TRADE_MANUAL -> successfulResponse(action, stateMachineId, context)
        Action.ATLAS_MANUAL -> successfulResponse(action, stateMachineId, context)
        Action.SUCCESFUL_ONBOARDING -> successfulResponse(action, stateMachineId, context)
        else -> successfulResponse(action, stateMachineId, context)
    }

    private fun successfulResponse(action: Action, stateMachineId: String, context: MutableMap<String, String>): ExecutorResponse {
        val logMsg = "For machine $stateMachineId executed $action"
        log.info { "$logMsg in context $context" }
        addLogToContext("ExecutorEngine: $logMsg", context)
        return ExecutorResponse(Event.SUCCESS, stateMachineId, context)
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