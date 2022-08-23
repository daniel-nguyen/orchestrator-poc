package com.slupicki.spring.config

import com.slupicki.spring.model.OnboardingEvent
import com.slupicki.spring.model.OnboardingEvent.FAILURE
import com.slupicki.spring.model.OnboardingEvent.RETRY
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
import org.springframework.statemachine.action.Action
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.StateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer

@Configuration
@EnableStateMachineFactory
class SpringOnboardingStateMachineConfiguration : StateMachineConfigurerAdapter<OnboardingState, OnboardingEvent>() {

    override fun configure(states: StateMachineStateConfigurer<OnboardingState, OnboardingEvent>?) {
        val entryAction = Action<OnboardingState, OnboardingEvent> {}
        val exitAction = Action<OnboardingState, OnboardingEvent> {}

        states!!
            .withStates()
            .initial(ONBOARDING_INIT)
            .end(ONBOARDING_ENDS)
            .state(ONBOARDING_INIT, entryAction, exitAction)
            .state(FINSTAR, entryAction, exitAction)
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
            transitions.withExternal().source(it.sourceState).target(it.targetState).event(it.event).and()
        }
//
//
//        transitions
//            .withExternal().source(ONBOARDING_INIT).target(FINSTAR).event(SUCCESS).and()
//            .withExternal().source(FINSTAR).target(SMARTTRADE).event(SUCCESS).action(enterFinstarAction).and()
//            .withExternal().source(SMARTTRADE).target(ATLAS).event(SUCCESS).and()
//            .withExternal().source(ATLAS).target(ONBOARDING_ENDS).event(SUCCESS).and()
//
//            .withExternal().source(FINSTAR).target(FINSTAR_MANUAL).event(FAILURE).and()
//            .withExternal().source(SMARTTRADE).target(SMARTTRADE_MANUAL).event(FAILURE).and()
//            .withExternal().source(ATLAS).target(ATLAS_MANUAL).event(FAILURE).and()
//
//            .withExternal().source(FINSTAR_MANUAL).target(FINSTAR).event(RETRY).and()
//            .withExternal().source(SMARTTRADE_MANUAL).target(SMARTTRADE).event(RETRY).and()
//            .withExternal().source(ATLAS_MANUAL).target(ATLAS).event(RETRY).and()
//
//            .withExternal().source(FINSTAR_MANUAL).target(SMARTTRADE).event(SUCCESS).and()
//            .withExternal().source(SMARTTRADE_MANUAL).target(ATLAS).event(SUCCESS).and()
//            .withExternal().source(ATLAS_MANUAL).target(ONBOARDING_ENDS).event(SUCCESS)
    }
}