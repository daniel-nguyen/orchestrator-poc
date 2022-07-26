package com.slupicki.orchestrator.model

import javax.persistence.CascadeType
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapKeyColumn
import javax.persistence.OneToOne
import javax.persistence.Table


@Entity
@Table(name = "context")
data class Context(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @ElementCollection
    @CollectionTable(
        name = "context_attributes",
        joinColumns = [JoinColumn(name = "context_id", referencedColumnName = "id")]
    )
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    val attributes: MutableMap<String, String> = mutableMapOf(),

    @OneToOne(mappedBy = "context", cascade = [CascadeType.DETACH], fetch = FetchType.LAZY)
    val stateMachine: StateMachine? = null,

)