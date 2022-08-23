package com.slupicki.spring

import com.slupicki.spring.model.Onboarding
import com.slupicki.spring.model.OnboardingEvent
import com.slupicki.spring.model.OnboardingState
import com.slupicki.spring.repository.OnboardingRepository
import com.slupicki.spring.service.FactoryPersistStateMachineHandler
import mu.KotlinLogging
import org.apache.commons.lang3.math.NumberUtils
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.jms.core.JmsTemplate
import org.springframework.messaging.Message
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.state.State
import org.springframework.statemachine.transition.Transition
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@SpringBootApplication
class SpringPocApplication(
//    @Autowired private val graphService: SpringGraphService,
//    @Autowired private val flyway: Flyway,
    @Autowired private val environment: Environment,
//    @Autowired private val stateMachineEngine: StateMachineEngine,
//    @Autowired private val stateMachineTypeRepository: StateMachineTypeRepository,
//    @Autowired private val stateMachineRepository: StateMachineRepository,
    @Autowired private val onboardingRepository: OnboardingRepository,
    @Autowired private val jmsTemplate: JmsTemplate,
    @Autowired private val stateMachineFactory: StateMachineFactory<OnboardingState, OnboardingEvent>
): CommandLineRunner {
    companion object {
        val log = KotlinLogging.logger {}
        val CLIENT_ID_HEADER = "onboardingClientId"
    }

    override fun run(vararg args: String?) {
        //flyway.clean()
        //flyway.migrate()

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

    @Bean
    fun persistListener(): FactoryPersistStateMachineHandler.PersistListener<OnboardingState, OnboardingEvent> {
        return object : FactoryPersistStateMachineHandler.PersistListener<OnboardingState, OnboardingEvent> {
            override fun onPersist(
                state: State<OnboardingState, OnboardingEvent>,
                message: Message<OnboardingEvent>,
                transition: Transition<OnboardingState, OnboardingEvent>,
                stateMachine: StateMachine<OnboardingState, OnboardingEvent>
            ) {
                Mono.justOrEmpty(message.headers[CLIENT_ID_HEADER])
                    .map { it.toString() }
                    .filter { NumberUtils.isCreatable(it) }
                    .map { NumberUtils.createLong(it) }
                    .flatMap { onboardingRepository.findById(it) }
                    .switchIfEmpty { Onboarding(state = OnboardingState.ONBOARDING_INIT.name).toMono() }
                    .flatMap { onboardingRepository.save(it) }
                    .subscribe()
            }

        }
    }


}

fun main(args: Array<String>) {
    runApplication<SpringPocApplication>(*args)
}
