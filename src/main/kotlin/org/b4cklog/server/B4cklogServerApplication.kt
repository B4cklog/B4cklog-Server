package org.b4cklog.server

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
	System.setProperty("IGDB_CLIENT_ID", dotenv["IGDB_CLIENT_ID"])
	System.setProperty("IGDB_CLIENT_SECRET", dotenv["IGDB_CLIENT_SECRET"])

	runApplication<B4cklogServerApplication>(*args)
}
