package com.slupicki.spring

import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.get

@SpringBootApplication
class SpringPocApplication(
    @Autowired private val environment: Environment,
): CommandLineRunner {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    override fun run(vararg args: String?) {
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
