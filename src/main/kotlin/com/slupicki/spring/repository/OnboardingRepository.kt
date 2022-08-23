package com.slupicki.spring.repository

import com.slupicki.spring.model.Onboarding
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface OnboardingRepository : ReactiveCrudRepository<Onboarding, Long> {
}