package com.slupicki.spring

import com.slupicki.spring.SpringPocApplication.Companion.CLIENT_ID_HEADER
import com.slupicki.spring.model.OnboardingEvent
import com.slupicki.spring.model.OnboardingState
import com.slupicki.spring.service.FactoryPersistStateMachineHandler
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder

@SpringBootTest
class SpringStateMachinePocApplicationIT(
    @Autowired private val stateMachineHandler: FactoryPersistStateMachineHandler<OnboardingState, OnboardingEvent>

) {

    @Test
    fun doSomeAction() {

        stateMachineHandler.handleEventWithState(
            MessageBuilder.createMessage(OnboardingEvent.SUCCESS, MessageHeaders(mapOf(Pair(CLIENT_ID_HEADER, 1L)))),
            OnboardingState.ONBOARDING_INIT)
            .collectList()
            .subscribe()

    }
}
