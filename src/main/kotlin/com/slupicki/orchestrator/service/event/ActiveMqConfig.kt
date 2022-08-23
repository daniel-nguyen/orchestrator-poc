package com.slupicki.orchestrator.service.event

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.broker.BrokerService
import org.apache.activemq.broker.TransportConnector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.core.JmsTemplate
import java.net.URI


@Configuration
@EnableJms
class ActiveMqConfig {

    @Bean
    fun createBrokerService(): BrokerService {
        val broker = BrokerService()
        val connector = TransportConnector()
        connector.uri = URI("tcp://localhost:61616")
        broker.addConnector(connector)
        return broker
    }

    @Bean
    fun mqConnectionFactory(broker: BrokerService): ActiveMQConnectionFactory {
        val connectionFactory = ActiveMQConnectionFactory()
        connectionFactory.brokerURL = broker.defaultSocketURIString
        //connectionFactory.password = BROKER_USERNAME
        //connectionFactory.userName = BROKER_PASSWORD
        return connectionFactory
    }

    @Bean
    fun jmsTemplate(connectionFactory: ActiveMQConnectionFactory): JmsTemplate {
        val template = JmsTemplate()
        template.connectionFactory = connectionFactory
        return template
    }

    @Bean
    fun jmsListenerContainerFactory(connectionFactory: ActiveMQConnectionFactory): DefaultJmsListenerContainerFactory? {
        val factory = DefaultJmsListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setConcurrency("1-1")
        return factory
    }
}