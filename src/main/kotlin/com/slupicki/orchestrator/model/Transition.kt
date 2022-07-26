package com.slupicki.orchestrator.model

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "transition")
data class Transition(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @OneToOne(cascade = [CascadeType.DETACH])
    @JoinColumn(name = "from_state_id")
    val fromState: State,

    @OneToOne(cascade = [CascadeType.DETACH])
    @JoinColumn(name = "to_state_id")
    val toState: State,

    @Enumerated(EnumType.STRING)
    @Column(name = "event", nullable = false)
    val event: Event,

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    val action: Action,

    @ManyToOne(fetch = FetchType.LAZY)
    val stateMachineType: StateMachineType? = null
)