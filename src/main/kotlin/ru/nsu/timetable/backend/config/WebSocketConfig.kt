package ru.nsu.timetable.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import ru.nsu.timetable.backend.websocket.SocketHandler


@Configuration
@EnableWebSocket
class WebSocketConfig(val handler: SocketHandler) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(handler, "/api/v1/socket").setAllowedOriginPatterns("*")
    }
}