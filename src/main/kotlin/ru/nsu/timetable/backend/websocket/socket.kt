package ru.nsu.timetable.backend.websocket

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList


@Component
class SocketHandler : TextWebSocketHandler() {
    var sessions: MutableSet<WebSocketSession> = mutableSetOf()
    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {

    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
    }

    fun notifyAllClients(){
        sessions.forEach {
            try{
                it.sendMessage(TextMessage("reload"))
            }catch (_: Exception){}
        }
    }
}