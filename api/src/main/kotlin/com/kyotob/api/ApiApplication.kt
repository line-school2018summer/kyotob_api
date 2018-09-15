package com.kyotob.api
import org.glassfish.tyrus.server.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.boot.runApplication

@SpringBootApplication
@Configuration
class ApiApplication

fun main(args: Array<String>) {
    // BootStrap apiの起動
    runApplication<ApiApplication>(*args)

    // Tyrus Websocket Serverの起動
    val server = Server("localhost", 8181,
            "" ,mapOf(), WebSocketServer::class.java)
    try {
        server.start()
        System.`in`.read()
    } finally {
//        server.stop() // サーバーは立ち上げっぱなし
    }
}
