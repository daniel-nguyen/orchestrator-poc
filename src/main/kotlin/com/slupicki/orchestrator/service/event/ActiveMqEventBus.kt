package com.slupicki.orchestrator.service.event

import com.slupicki.orchestrator.service.IEventBus
import mu.KotlinLogging
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("ActiveMqEventBus")
class ActiveMqEventBus: IEventBus {
    val log = KotlinLogging.logger {}

    @JmsListener(
        destination = "test",
    )
    fun testReceiver(msg: String) {
        log.info { "Received: $msg"}
        Thread.sleep(200)
        log.info { "End of processing: $msg" }
    }

    override fun send(destination: Bus, event: EventInContext) {
        TODO("Not yet implemented")
    }

    override fun subscribe(destination: Bus, function: Consumer<EventInContext>) {
        TODO("Not yet implemented")
    }
}