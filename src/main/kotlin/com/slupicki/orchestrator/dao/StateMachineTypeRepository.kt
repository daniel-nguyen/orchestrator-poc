package com.slupicki.orchestrator.dao;

import com.slupicki.orchestrator.model.StateMachineType
import com.slupicki.orchestrator.model.Transition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

interface StateMachineTypeRepository : JpaRepository<StateMachineType, Long> {


    @Transactional()
    fun findByName(name: String): StateMachineType

}