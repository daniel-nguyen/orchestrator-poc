package com.slupicki.orchestrator.model

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "state_machine_type")
data class StateMachineType(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "name", unique = true)
    val name: String,

    @OneToMany(mappedBy = "stateMachineType", fetch = FetchType.LAZY)
    val transitions: List<Transition> = mutableListOf()
) {
    fun initialState(): State = transitions.first { it.fromState.initial }.fromState
}