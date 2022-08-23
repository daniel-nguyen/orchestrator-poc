package com.slupicki.spring.config

import com.slupicki.spring.model.OnboardingEvent
import com.slupicki.spring.model.OnboardingEvent.SUCCESS
import com.slupicki.spring.model.OnboardingState
import com.slupicki.spring.model.OnboardingState.ATLAS
import com.slupicki.spring.model.OnboardingState.ATLAS_MANUAL
import com.slupicki.spring.model.OnboardingState.FINSTAR
import com.slupicki.spring.model.OnboardingState.FINSTAR_MANUAL
import com.slupicki.spring.model.OnboardingState.ONBOARDING_ENDS
import com.slupicki.spring.model.OnboardingState.ONBOARDING_INIT
import com.slupicki.spring.model.OnboardingState.SMARTTRADE
import com.slupicki.spring.model.OnboardingState.SMARTTRADE_MANUAL
import com.slupicki.spring.model.OnboardingTransition
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.action.Action
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.StateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import reactor.core.publisher.Mono

@Configuration
@EnableStateMachineFactory
class SpringOnboardingStateMachineConfiguration : StateMachineConfigurerAdapter<OnboardingState, OnboardingEvent>() {

    override fun configure(states: StateMachineStateConfigurer<OnboardingState, OnboardingEvent>?) {
        val entryAction = Action<OnboardingState, OnboardingEvent> { stateContext ->
            System.err.println("entryAction - source: ${stateContext?.source?.id}, target: ${stateContext.target.id}, stage: ${stateContext.stage}, event: ${stateContext.event}, ${stateContext.transition.name} ${stateContext.messageHeaders}")
        }
        val exitAction = Action<OnboardingState, OnboardingEvent> { stateContext ->
            System.err.println("exitAction - source: ${stateContext.source.id}, target: ${stateContext?.target?.id}, stage: ${stateContext.stage}, event: ${stateContext.event}, ${stateContext.transition.name} ${stateContext.messageHeaders}")
        }

        val finstarEntryAction = Action<OnboardingState, OnboardingEvent> { stateContext ->
            System.err.println("entryAction - source: ${stateContext.source.id}, target: ${stateContext.target.id}, stage: ${stateContext.stage}, event: ${stateContext.event}, ${stateContext.transition.name} ${stateContext.messageHeaders} ")
            stateContext.stateMachine.sendEvent(Mono.just(MessageBuilder.createMessage(SUCCESS, stateContext.messageHeaders))).collectList().subscribe()
        }

        states!!
            .withStates()
            .initial(ONBOARDING_INIT)
            .end(ONBOARDING_ENDS)
            .state(ONBOARDING_INIT, entryAction, exitAction)
            .state(FINSTAR, finstarEntryAction, exitAction)
            .state(FINSTAR_MANUAL, entryAction, exitAction)
            .state(SMARTTRADE, entryAction, exitAction)
            .state(SMARTTRADE_MANUAL, entryAction, exitAction)
            .state(ATLAS, entryAction, exitAction)
            .state(ATLAS_MANUAL, entryAction, exitAction)
            .state(ONBOARDING_ENDS, entryAction, exitAction)
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<OnboardingState, OnboardingEvent>) {
        val enterFinstarAction = Action<OnboardingState, OnboardingEvent> {
            context -> context
            TODO("Not yet implemented")
        }

        //TODO use some guard() to entry or exit states or action()

        OnboardingTransition.values().forEach {
            transitions.withExternal()
                .source(it.sourceState)
                .target(it.targetState)
                .event(it.event)
                .actionFunction { stateContext -> Mono
                    .just(Unit)
                    .doOnNext { System.err.println("action - stage: ${stateContext.stage}, event: ${stateContext.event}, ${stateContext.transition.name} ") }
                    .flatMap { Mono.empty() }
                }
                .and()
        }
    }
}