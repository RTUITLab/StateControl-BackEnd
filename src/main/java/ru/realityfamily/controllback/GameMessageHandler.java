package ru.realityfamily.controllback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.realityfamily.controllback.Models.Devices;
import ru.realityfamily.controllback.Repository.DevicesRepository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameMessageHandler extends TextWebSocketHandler {

    private List<WebSocketSession> establishedSessions = new CopyOnWriteArrayList<>();
    @Autowired
    private DevicesRepository devicesRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        establishedSessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        devicesRepository.findAll().forEach(device -> {
            if (device.getSessionId() == establishedSessions.indexOf(session)) {
                devicesRepository.delete(device);
            }
        });
        establishedSessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        establishedSessions.forEach(establishedSessions -> sendMessageToClient(message, establishedSessions));
    }

    private void sendMessageToClient(TextMessage message, WebSocketSession establishedSessions) {
        try{
            establishedSessions.sendMessage(new TextMessage(message.getPayload()));
        } catch (IOException e) {
            System.out.println("Failed to send message. " + e);
        }
    }
}
