package com.slupicki.spring.service

import org.springframework.messaging.Message
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.StateMachineEventResult
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.state.State
import org.springframework.statemachine.support.DefaultStateMachineContext
import org.springframework.statemachine.support.LifecycleObjectSupport
import org.springframework.statemachine.support.StateMachineInterceptorAdapter
import org.springframework.statemachine.transition.Transition
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

class FactoryPersistStateMachineHandler<S, E>(
    private val factory: StateMachineFactory<S, E>,
    private val persistListener: PersistListener<S, E>) : LifecycleObjectSupport() {

    interface PersistListener<S, E> {
        fun onPersist(state: State<S, E>, message: Message<E>, transition: Transition<S, E>, stateMachine: StateMachine<S, E>)
    }

    private val interceptor = object : StateMachineInterceptorAdapter<S, E>() {
//        override fun preStateChange(state: State<S, E>, message: Message<E>, transition: Transition<S, E>, stateMachine: StateMachine<S, E>, rootStateMachine: StateMachine<S, E>?) {
//            persistListener.onPersist(state, message, transition, stateMachine)
//        }
//
        override fun postStateChange(state: State<S, E>, message: Message<E>, transition: Transition<S, E>, stateMachine: StateMachine<S, E>, rootStateMachine: StateMachine<S, E>?) {
            persistListener.onPersist(state, message, transition, stateMachine)
        }
    }

    fun handleEvent(message: Message<E>, stateRetriever: (Message<E>)->Mono<S>): Flux<StateMachineEventResult<S, E>> =
        stateRetriever(message)
            .switchIfEmpty { Mono.error { IllegalArgumentException("Unable to find state") } }
            .flatMapMany { state -> handleEventWithState(message, state) }

    private fun handleEventWithState(event: Message<E>, state: S): Flux<StateMachineEventResult<S, E>> =
        Mono.just(getInitStateMachine())
            .zipWhen { it.stopReactively().thenReturn(Unit) }
            .map { it.t1 }
            .zipWhen { stateMachine ->
                Flux.fromIterable(stateMachine.stateMachineAccessor.withAllRegions())
                    .flatMap { region -> region.resetStateMachineReactively(DefaultStateMachineContext(state, null, null, null)).thenReturn(Unit)}
                    .collectList()
            }
            .map { it.t1 }
            .zipWhen { it.startReactively().thenReturn(Unit) }
            .map { it.t1 }
            .flatMapMany { it.sendEvent(Mono.just(event)) }

    private fun getInitStateMachine(): StateMachine<S, E> {
        val stateMachine: StateMachine<S, E> = factory.stateMachine
        val withAllRegions = stateMachine.stateMachineAccessor.withAllRegions()
        for (accessor in withAllRegions) {
            accessor.addStateMachineInterceptor(interceptor)
        }
        return stateMachine
    }
}