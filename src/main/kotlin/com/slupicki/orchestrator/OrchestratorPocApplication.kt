package com.slupicki.orchestrator

import com.slupicki.orchestrator.dao.StateMachineRepository
import com.slupicki.orchestrator.dao.StateMachineTypeRepository
import com.slupicki.orchestrator.service.GraphService
import com.slupicki.orchestrator.service.StateMachineEngine
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jms.core.JmsTemplate

//@SpringBootApplication
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

    @Autowired
    lateinit var jmsTemplate: JmsTemplate


    override fun run(vararg args: String?) {
        //flyway.clean()
        //flyway.migrate()

        //stateMachineEngine.createMachine("ONBOARDING", "client1", mapOf("a" to "value_a"))

        jmsTemplate.send("test") { it.createTextMessage("Hello world")}
    }

    private fun logOnboardingDot() {
        val s = graphService.createDot("ONBOARDING", "Atlas")
        log.info { s }
    }

}

//fun main(args: Array<String>) {
//    runApplication<OrchestratorPocApplication>(*args)
//}
