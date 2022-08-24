package com.slupicki.spring

import com.slupicki.spring.model.OnboardingEvent
import com.slupicki.spring.service.OnboardingService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringPocApplicationIT(
    @Autowired private val onboardingService: OnboardingService
) {

    @Test
    fun doSomeAction() {
//        val onboarding = onboardingService.createOnboarding().block()!!
        onboardingService.handleEvent(OnboardingEvent.SUCCESS, 1L).collectList().block()

        Thread.sleep(2000L)
    }
}
