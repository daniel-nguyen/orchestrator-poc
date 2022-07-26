package com.slupicki.orchestrator.dao;

import com.slupicki.orchestrator.model.StateMachine
import org.springframework.data.jpa.repository.JpaRepository

interface StateMachineRepository : JpaRepository<StateMachine, String> {
}