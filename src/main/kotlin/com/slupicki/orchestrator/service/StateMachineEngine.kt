package com.slupicki.orchestrator.service

import com.slupicki.orchestrator.dao.StateMachineRepository
import com.slupicki.orchestrator.dao.StateMachineTypeRepository
import com.slupicki.orchestrator.model.Context
import com.slupicki.orchestrator.model.Event
import com.slupicki.orchestrator.model.StateMachine
import com.slupicki.orchestrator.model.StateMachineType
import mu.KotlinLogging
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class StateMachineEngine(
    val stateMachineTypeRepository: StateMachineTypeRepository,
    val stateMachineRepository: StateMachineRepository,
    val executorEngine: ExecutorEngine,
) {
    val log = KotlinLogging.logger {}

    @Transactional
    fun showTypes(): List<StateMachineType> {
        val stateMachineTypes = stateMachineTypeRepository.findAll()

        stateMachineTypes.forEach {
            log.info { "Found type of state machine '${it.name}'" }
            log.info { "Transitions:" }
            it.transitions.forEach {
                log.info { "Transition from ${it.fromState} to ${it.toState} when event ${it.event}" }
            }
            log.info { "----" }
        }
        return stateMachineTypes
    }

    @Transactional
    fun showMachines(): List<StateMachine> {
        val machines = stateMachineRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
        machines.forEach {
            log.info { "Machine ${it.id} of type ${it.stateMachineType.name} is in state ${it.currentState} and have context ${it.context.attributes}" }
        }
        return machines
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
    fun receiveEvent(event: Event, stateMachineId: String, context: Map<String, String> = emptyMap()) {
        val stateMachine = stateMachineRepository.findById(stateMachineId).get()
        val transition = stateMachine.transitionsFromCurrentState()[event]!!
        val logInfo = "Perform transition from ${transition.fromState} to ${transition.toState} on machine $stateMachineId -> action ${transition.action}"
        val attributes = stateMachine.context.attributes
        log.info { "Perform transition from ${transition.fromState} to ${transition.toState} on machine $stateMachineId -> action ${transition.action}, context $attributes" }
        stateMachine.currentState = transition.toState
        attributes.putAll(context)
        addLogToContext(logInfo, attributes)
        // This should be done asynchronous. Just send event to executorEngine and response will be handle by this function (receiveEvent())
        val executorResponse = executorEngine.execute(transition.action, stateMachineId, attributes)
        attributes.putAll(executorResponse.context)
        // -----------------------------------
    }
}