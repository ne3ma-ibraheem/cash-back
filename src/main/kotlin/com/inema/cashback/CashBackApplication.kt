package com.inema.cashback


import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
@Configuration
class CashBackApplication {
    @Bean
    fun bootstrap() = CommandLineRunner {

    }
}

fun main(args: Array<String>) {
    runApplication<CashBackApplication>(*args)
}
