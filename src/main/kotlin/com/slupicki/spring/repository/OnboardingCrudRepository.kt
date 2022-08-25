package com.slupicki.spring.repository

import com.slupicki.spring.model.Onboarding
import org.springframework.data.repository.CrudRepository

interface OnboardingCrudRepository : CrudRepository<Onboarding, Long> {
}