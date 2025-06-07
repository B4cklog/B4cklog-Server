package com.rarmash.b4cklog_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.github.cdimascio.dotenv.dotenv

@SpringBootApplication
class B4cklogServerApplication

fun main(args: Array<String>) {
	val dotenv = dotenv()

	System.setProperty("DB_URL", dotenv["DB_URL"])
	System.setProperty("DB_USERNAME", dotenv["DB_USERNAME"])
	System.setProperty("DB_PASSWORD", dotenv["DB_PASSWORD"])
	System.setProperty("FRONTEND_URL", dotenv["FRONTEND_URL"])

	runApplication<B4cklogServerApplication>(*args)
}
