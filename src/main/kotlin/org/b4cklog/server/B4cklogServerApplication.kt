package org.b4cklog.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class B4cklogServerApplication

fun main(args: Array<String>) {
	runApplication<B4cklogServerApplication>(*args)
}
