package com.slupicki.orchestrator.dao;

import com.slupicki.orchestrator.model.State
import org.springframework.data.jpa.repository.JpaRepository

interface StateRepository : JpaRepository<State, Long> {
}