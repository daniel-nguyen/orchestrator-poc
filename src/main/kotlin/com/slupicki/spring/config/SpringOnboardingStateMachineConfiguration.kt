package com.slupicki.spring.config

import com.slupicki.spring.model.SpringOnboardingEvent
import com.slupicki.spring.model.SpringOnboardingEvent.FAILURE
import com.slupicki.spring.model.SpringOnboardingEvent.SUCCESS
import com.slupicki.spring.model.SpringOnboardingState
import com.slupicki.spring.model.SpringOnboardingState.ATLAS
import com.slupicki.spring.model.SpringOnboardingState.ATLAS_MANUAL
import com.slupicki.spring.model.SpringOnboardingState.FINSTAR
import com.slupicki.spring.model.SpringOnboardingState.FINSTAR_MANUAL
import com.slupicki.spring.model.SpringOnboardingState.ONBOARDING_ENDS
import com.slupicki.spring.model.SpringOnboardingState.ONBOARDING_INIT
import com.slupicki.spring.model.SpringOnboardingState.SMARTTRADE
import com.slupicki.spring.model.SpringOnboardingState.SMARTTRADE_MANUAL
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.action.Action
import org.springframework.statemachine.config.EnableStateMachine
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.StateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer

@Configuration
@EnableStateMachineFactory
class SpringOnboardingStateMachineConfiguration : StateMachineConfigurerAdapter<SpringOnboardingState, SpringOnboardingEvent>() {

    override fun configure(states: StateMachineStateConfigurer<SpringOnboardingState, SpringOnboardingEvent>?) {
        val entryAction = Action<SpringOnboardingState, SpringOnboardingEvent> {}
        val exitAction = Action<SpringOnboardingState, SpringOnboardingEvent> {}

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

    override fun configure(transitions: StateMachineTransitionConfigurer<SpringOnboardingState, SpringOnboardingEvent>?) {
        val enterFinstarAction = Action<SpringOnboardingState, SpringOnboardingEvent> {
            context -> context
            TODO("Not yet implemented")
        }

        transitions!!
            .withExternal().source(ONBOARDING_INIT).target(FINSTAR).event(SUCCESS).and()
            .withExternal().source(FINSTAR).target(SMARTTRADE).event(SUCCESS).action(enterFinstarAction).and() //TODO use some guard() to entry or exit states or action()
            .withExternal().source(SMARTTRADE).target(ATLAS).event(SUCCESS).and()
            .withExternal().source(ATLAS).target(ONBOARDING_ENDS).event(SUCCESS).and()

            .withExternal().source(FINSTAR).target(FINSTAR_MANUAL).event(FAILURE).and()
            .withExternal().source(SMARTTRADE).target(SMARTTRADE_MANUAL).event(FAILURE).and()
            .withExternal().source(ATLAS).target(ATLAS_MANUAL).event(FAILURE).and()

            .withExternal().source(FINSTAR_MANUAL).target(SMARTTRADE).event(SUCCESS).and()
            .withExternal().source(SMARTTRADE_MANUAL).target(ATLAS).event(SUCCESS).and()
            .withExternal().source(ATLAS_MANUAL).target(ONBOARDING_ENDS).event(SUCCESS)
    }
}