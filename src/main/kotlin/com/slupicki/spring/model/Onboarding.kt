package com.slupicki.spring.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("onboarding")
data class Onboarding(
    @Id @Column("id") val id: Long = 0L,
    @Column("state") val state: String
)
