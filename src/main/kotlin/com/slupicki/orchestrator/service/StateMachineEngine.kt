package com.slupicki.orchestrator.service

import com.slupicki.orchestrator.dao.StateMachineRepository
import com.slupicki.orchestrator.dao.StateMachineTypeRepository
import com.slupicki.orchestrator.model.Context
import com.slupicki.orchestrator.model.Event
import com.slupicki.orchestrator.model.StateMachine
import com.slupicki.orchestrator.model.StateMachineType
import mu.KotlinLogging
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.annotation.PostConstruct

@Service
class StateMachineEngine(
    val stateMachineTypeRepository: StateMachineTypeRepository,
    val stateMachineRepository: StateMachineRepository,
    val eventBus: EventBus,
) {
    val log = KotlinLogging.logger {}

    @Transactional
    fun getMachines(): List<StateMachine> = stateMachineRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))

    @Transactional
    fun getMachineTypes(): List<StateMachineType> = stateMachineTypeRepository.findAll()

    @Transactional
    fun showTypes() = getMachineTypes().forEach {
            log.info { "Found type of state machine '${it.name}'" }
            log.info { "Transitions:" }
            it.transitions.forEach {
                log.info { "Transition from ${it.fromState} to ${it.toState} when event ${it.event}" }
            }
            log.info { "----" }
        }

    @Transactional
    fun showMachines()  = getMachines().forEach {
            log.info { "Machine ${it.id} of type ${it.stateMachineType.name} is in state ${it.currentState} and have context ${it.context.attributes}" }
        }

    @Transactional
    fun createMachine(type: String, id: String = UUID.randomUUID().toString(), initialContext: Map<String, String> = emptyMap()): StateMachine {
        val stateMachineType = if (type.toLongOrNull() != null) {
            stateMachineTypeRepository.findById(type.toLong()).get()
        } else {
            stateMachineTypeRepository.findByName(type)
        }

        val newStateMachine = StateMachine(
            id = id,
            currentState = stateMachineType.initialState(),
            context = Context(
                attributes = initialContext.toMutableMap(),
            ),
            stateMachineType = stateMachineType,
        )

        stateMachineRepository.save(newStateMachine)
        return newStateMachine
    }

    @Transactional
    fun receiveEvent(event: EventInContext) {
        log.info { "Receive event: $event" }
        val stateMachine = stateMachineRepository.findById(event.stateMachineId).get()
        val attributes = stateMachine.context.attributes
        attributes.putAll(event.context)
        if (stateMachine.transitionsFromCurrentState().isEmpty()) {
            log.warn { "There is no transitions from ${stateMachine.currentState.name} for machine ${stateMachine.id} - do nothing" }
            return
        }
        if (event.event == Event.UNKNOWN) {
            log.info { "Got event ${event.event} - do nothing" }
            return
        }
        val transition = stateMachine.transitionsFromCurrentState()[event.event]!!
        val logInfo = "Perform transition from ${transition.fromState} to ${transition.toState} on machine ${event.stateMachineId} -> action ${transition.action}"
        log.info { "$logInfo, context $attributes" }
        stateMachine.currentState = transition.toState
        addLogToContext(logInfo, attributes)
        val eventForExecutor = event.copy(
            context = attributes,
            action = transition.action,
        )
        eventBus.send(Bus.TO_EXECUTOR, eventForExecutor)
    }
}

// This is to make sure that call to stateMachineEngine.receiveEvent(it) is in transaction
@Component
class StateMachineEngineRegistrator(
    val stateMachineEngine: StateMachineEngine,
    val eventBus: EventBus,
) {
    val log = KotlinLogging.logger {}

    @PostConstruct
    fun postConstruct() {
        eventBus.subscribe(Bus.TO_STATE_MACHINE) {
            stateMachineEngine.receiveEvent(it)
        }
        log.info { "StateMachineEngine now subscribe channel ${Bus.TO_STATE_MACHINE}" }
    }
}