package com.slupicki.orchestrator

import com.slupicki.orchestrator.dao.StateMachineRepository
import com.slupicki.orchestrator.dao.StateMachineTypeRepository
import com.slupicki.orchestrator.model.Event
import com.slupicki.orchestrator.service.GraphService
import com.slupicki.orchestrator.service.StateMachineEngine
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OrchestratorPocApplication: CommandLineRunner {
    val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var graphService: GraphService

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var stateMachineEngine: StateMachineEngine

    @Autowired
    private lateinit var stateMachineTypeRepository: StateMachineTypeRepository

    @Autowired
    private lateinit var stateMachineRepository: StateMachineRepository


    override fun run(vararg args: String?) {
        //flyway.clean()
        //flyway.migrate()

        //stateMachineEngine.createMachine("ONBOARDING", "client1", mapOf("a" to "value_a"))
    }

    private fun logOnboardingDot() {
        val s = graphService.createDot("ONBOARDING", "Atlas")
        log.info { s }
    }

    private fun testOnboarding() {
        stateMachineEngine.showTypes()
        val stateMachine = stateMachineEngine.createMachine("ONBOARDING", "client1", mapOf("a" to "value_a"))
        log.info { "Created machine: ${stateMachine.id}" }
        stateMachineEngine.receiveEvent(
            Event.SUCCESS,
            "client1",
            mapOf("initialize" to "Hello world"),
        )
        stateMachineEngine.receiveEvent(
            Event.FAILURE,
            "client1",
            mapOf("step2" to "got failure"),
        )
        stateMachineEngine.receiveEvent(
            Event.SUCCESS,
            "client1",
            mapOf("step3" to "got success"),
        )
        stateMachineEngine.receiveEvent(
            Event.SUCCESS,
            "client1",
            mapOf("step4" to "got success"),
        )
        stateMachineEngine.receiveEvent(
            Event.SUCCESS,
            "client1",
            mapOf("step5" to "got success"),
        )
        stateMachineEngine.showMachines()
    }

}

fun main(args: Array<String>) {
    runApplication<OrchestratorPocApplication>(*args)
}
