package com.slupicki.spring

import com.slupicki.spring.service.OnboardingService
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

@SpringBootApplication
class SpringPocApplication(
    @Autowired private val environment: Environment,
    @Autowired private val onboardingService: OnboardingService,
    @Autowired private val jmsTemplate: JmsTemplate,
//    @Autowired private val flyway: Flyway,
): CommandLineRunner {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    override fun run(vararg args: String?) {
//        flyway.clean()
//        flyway.migrate()

        //stateMachineEngine.createMachine("ONBOARDING", "client1", mapOf("a" to "value_a"))

//        jmsTemplate.send("test") { it.createTextMessage("Hello world")}
//        val stateMachine = stateMachineFactory.getStateMachine("1234")
//        stateMachine.sendEventCollect(Mono.just(MessageBuilder.createMessage()))


    }
    private fun logOnboardingDot() {
//        val s = graphService.createDot("ONBOARDING", "Atlas")
//        log.info { s }
    }

    @Bean(initMethod = "migrate")
    fun flyway(): Flyway = Flyway(
        Flyway.configure()
            .baselineOnMigrate(true)
//                .locations("classpath:db")
            .dataSource(
                environment["spring.datasource.url"],
                environment["spring.datasource.username"],
                environment["spring.datasource.password"]
            )
    )

}

fun main(args: Array<String>) {
    runApplication<SpringPocApplication>(*args)
}
