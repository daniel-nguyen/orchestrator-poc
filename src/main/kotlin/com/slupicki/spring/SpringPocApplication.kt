package com.slupicki.spring

import com.slupicki.spring.model.OnboardingEvent
import com.slupicki.spring.model.OnboardingState
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.jms.core.JmsTemplate
import org.springframework.statemachine.config.StateMachineFactory

@SpringBootApplication
class SpringPocApplication(
//    @Autowired private val graphService: SpringGraphService,
//    @Autowired private val flyway: Flyway,
    @Autowired private val environment: Environment,
//    @Autowired private val stateMachineEngine: StateMachineEngine,
//    @Autowired private val stateMachineTypeRepository: StateMachineTypeRepository,
//    @Autowired private val stateMachineRepository: StateMachineRepository,
    @Autowired private val jmsTemplate: JmsTemplate,
    @Autowired private val stateMachineFactory: StateMachineFactory<OnboardingState, OnboardingEvent>
): CommandLineRunner {
    companion object {
        val log = KotlinLogging.logger {}
    }

    override fun run(vararg args: String?) {
        //flyway.clean()
        //flyway.migrate()

        //stateMachineEngine.createMachine("ONBOARDING", "client1", mapOf("a" to "value_a"))

//        jmsTemplate.send("test") { it.createTextMessage("Hello world")}
        val stateMachine = stateMachineFactory.getStateMachine("1234")
//        stateMachine.sendEventCollect(Mono.just(MessageBuilder.createMessage()))
    }
    private fun logOnboardingDot() {
//        val s = graphService.createDot("ONBOARDING", "Atlas")
//        log.info { s }
    }

    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        return Flyway(
            Flyway.configure()
                .baselineOnMigrate(true)
//                .locations(environment["spring.flyway.locations"])
                .dataSource(
                    environment["spring.datasource.url"],
                    environment["spring.datasource.username"],
                    environment["spring.datasource.password"]
                )
        )
    }

}

fun main(args: Array<String>) {
    runApplication<SpringPocApplication>(*args)
}
