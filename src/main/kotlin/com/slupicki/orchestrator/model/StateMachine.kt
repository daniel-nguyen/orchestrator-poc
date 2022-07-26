package com.slupicki.orchestrator.model

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "state_machine")
data class StateMachine(
    @Id
    @Column(name = "id", nullable = false)
    val id: String? = null,

    @ManyToOne(cascade = [CascadeType.DETACH])
    @JoinColumn(name = "current_state_id")
    var currentState: State,

    @ManyToOne(cascade = [CascadeType.DETACH])
    @JoinColumn(name = "state_machine_type_id")
    val stateMachineType: StateMachineType,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id")
    val context: Context,
) {
    fun transitionsFromCurrentState(): Map<Event, Transition> = stateMachineType.transitions
            .filter { it.fromState.id == currentState.id }
            .associateBy { it.event }
}
