package com.slupicki.spring

import com.slupicki.spring.model.OnboardingEvent
import com.slupicki.spring.model.OnboardingState
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.config.StateMachineFactory
import reactor.core.publisher.Mono

@SpringBootTest
class SpringStateMachinePocApplicationIT(
    @Autowired private val stateMachineFactory: StateMachineFactory<OnboardingState, OnboardingEvent>
) {

    @Test
    fun doSomeAction() {

        val stateMachine = stateMachineFactory.stateMachine

        stateMachine.startReactively().block()
        System.err.println("current state: ${stateMachine.state.id}")
        stateMachine.sendEvent(Mono.just(MessageBuilder.createMessage(OnboardingEvent.SUCCESS, MessageHeaders(mapOf(Pair("onboardingClientId", "1")))))).collectList().block()
        System.err.println("current state: ${stateMachine.state.id}")

    }
}
