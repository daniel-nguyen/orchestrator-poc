package com.slupicki.orchestrator.service

import com.slupicki.orchestrator.model.Event
import com.slupicki.orchestrator.service.event.ActiveMqConfig
import com.slupicki.orchestrator.service.event.ActiveMqEventBus
import com.slupicki.orchestrator.service.event.Bus
import com.slupicki.orchestrator.service.event.EventBus
import com.slupicki.orchestrator.service.event.EventInContext
import mu.KotlinLogging
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalTime


@RunWith(SpringRunner::class)
@SpringBootTest(
    classes = [
        ActiveMqConfig::class,
        ActiveMqEventBus::class,
        EventBus::class,
    ],
)
@EnableAutoConfiguration
class EventBusTest {
    val log = KotlinLogging.logger {}

    @Configuration
    @EnableJms
    class TestConfiguration {

    }

    @Qualifier("InMemoryEventBus")
    @Autowired
    lateinit var inMemoryEventBus: IEventBus

    @Autowired
    lateinit var jmsTemplate: JmsTemplate

    @Test
    fun `basic test`() {
        inMemoryEventBus.subscribe(Bus.TO_EXECUTOR) {
            log.info { "Executor got event: $it" }
            Thread.sleep(100)
            val executorCtx = it.context.toMutableMap()
            executorCtx["Response"] = "Hello from executor"
            val executorEvent = it.copy(context = executorCtx)
            inMemoryEventBus.send(Bus.TO_STATE_MACHINE, executorEvent)
        }
        inMemoryEventBus.subscribe(Bus.TO_STATE_MACHINE) {
            log.info { "State machine got event: $it" }
        }

        (1..10).forEach {
            val event = EventInContext(
                "machine1",
                Event.SUCCESS,
                mapOf("event" to it.toString()),
            )
            inMemoryEventBus.send(Bus.TO_EXECUTOR, event)
            log.info { "To ${Bus.TO_EXECUTOR} sent $event" }
        }

        Thread.sleep(2 * 1000)
        log.info { "The end" }
    }

    @Test
    fun `jms test`() {
        (1..1000).forEach {i ->
            jmsTemplate.send("test") { it.createTextMessage("Hello world: $i sent ${LocalTime.now()}") }
        }
    }

    @Test
    fun `jms test2`() {
        Thread.sleep(10 * 1000)
    }
}