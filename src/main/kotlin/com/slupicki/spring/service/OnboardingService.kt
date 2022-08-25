package com.slupicki.spring.service

import com.slupicki.spring.model.Onboarding
import com.slupicki.spring.model.OnboardingEvent
import com.slupicki.spring.model.OnboardingState
import com.slupicki.spring.repository.OnboardingCrudRepository
import com.slupicki.spring.repository.OnboardingReactiveRepository
import mu.KotlinLogging
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.StateMachineEventResult
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.state.State
import org.springframework.statemachine.transition.Transition
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.Optional.ofNullable

@Service
class OnboardingService(
    @Autowired private val onboardingCrudRepository: OnboardingCrudRepository,
    @Autowired private val onboardingReactiveRepository: OnboardingReactiveRepository,
    @Autowired private val stateMachineFactory: StateMachineFactory<OnboardingState, OnboardingEvent>
) {
    private val stateMachineHandler: FactoryPersistStateMachineHandler<OnboardingState, OnboardingEvent> = FactoryPersistStateMachineHandler(
        stateMachineFactory, createPersistListener(onboardingReactiveRepository)
    )

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val ONBOARDING_ID_HEADER = "onboardingId"

        fun createMessage(event: OnboardingEvent, clientId: Long): Message<OnboardingEvent> =
            MessageBuilder.createMessage(event, MessageHeaders(mapOf(Pair(ONBOARDING_ID_HEADER, clientId))))

        private fun retrieveOnboardingId(message: Message<OnboardingEvent>): Long? =
            ofNullable(message.headers[ONBOARDING_ID_HEADER])
                .map { it.toString() }
                .filter { NumberUtils.isCreatable(it) }
                .map { NumberUtils.createLong(it) }
                .orElse(null)

        private fun createPersistListener(onboardingReactiveRepository: OnboardingReactiveRepository): FactoryPersistStateMachineHandler.PersistListener<OnboardingState, OnboardingEvent> =
            object : FactoryPersistStateMachineHandler.PersistListener<OnboardingState, OnboardingEvent> {
                override fun onPersist(state: State<OnboardingState, OnboardingEvent>, message: Message<OnboardingEvent>, transition: Transition<OnboardingState, OnboardingEvent>, stateMachine: StateMachine<OnboardingState, OnboardingEvent>) {
                    retrieveOnboardingId(message)
                        .toMono()
                        .flatMap { onboardingReactiveRepository.findById(it) }
                        .map { it.copy(state = state.id.name) }
                        .flatMap { onboardingReactiveRepository.save(it) }
                        .doOnNext { System.err.println("Saved $it") }
                        .subscribe { System.err.println("subscribed: $it ") }
                }

            }
        private fun createPersistListener(onboardingCrudRepository: OnboardingCrudRepository): FactoryPersistStateMachineHandler.PersistListener<OnboardingState, OnboardingEvent> =
            object : FactoryPersistStateMachineHandler.PersistListener<OnboardingState, OnboardingEvent> {
                override fun onPersist(state: State<OnboardingState, OnboardingEvent>, message: Message<OnboardingEvent>, transition: Transition<OnboardingState, OnboardingEvent>, stateMachine: StateMachine<OnboardingState, OnboardingEvent>) {
                    ofNullable(retrieveOnboardingId(message))
                        .flatMap { onboardingCrudRepository.findById(it) }
                        .map { it.copy(state = state.id.name) }
                        .map { onboardingCrudRepository.save(it) }
                        .ifPresentOrElse({ System.err.println("Saved $it") }) { System.err.println("Not saved") }
                }

            }

    }

    fun createOnboardingMono(): Mono<Onboarding> =
        onboardingReactiveRepository.save(Onboarding(0L, OnboardingState.ONBOARDING_INIT.name))

    fun createOnboarding(): Onboarding =
        onboardingCrudRepository.save(Onboarding(0L, OnboardingState.ONBOARDING_INIT.name))

    fun handleEvent(event: OnboardingEvent, clientId: Long): Flux<StateMachineEventResult<OnboardingState, OnboardingEvent>> =
        stateMachineHandler.handleEvent(createMessage(event, clientId)) { msg ->
            retrieveOnboardingId(msg)
                .toMono()
                .flatMap { onboardingReactiveRepository.findById(it) }
                .map { OnboardingState.valueOf(it.state) }
        }
}