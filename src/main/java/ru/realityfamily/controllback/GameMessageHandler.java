package ru.realityfamily.controllback;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.realityfamily.controllback.Models.Devices;
import ru.realityfamily.controllback.Repository.DevicesRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GameMessageHandler extends TextWebSocketHandler {
    @Autowired
    private DevicesRepository devicesRepository;
    private List<WebSocketSession> establishedSessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        establishedSessions.add(session);
        System.out.println("Connected new client: " + session.getRemoteAddress());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        /*devicesRepository.findAll().forEach(device -> {
            if (device.getSessionId() == establishedSessions.indexOf(session)) {
                devicesRepository.delete(device);
            }
        });*/
        establishedSessions.remove(session);
        System.out.println("Disconnected client: " + session.getRemoteAddress());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //establishedSessions.forEach(establishedSessions -> sendMessageToClient(message, establishedSessions));

        System.out.println("Message from client: " + session.getRemoteAddress() + " \n Message: " + message.getPayload());

        JSONObject in_json = new JSONObject(message.getPayload());
        if (in_json.has("Game")) {
            Devices newDevice = new Devices(in_json.getString("Game"), establishedSessions.indexOf(session));
            //devicesRepository.save(newDevice);

            Map<String, Long> tempSequence = new HashMap<String, Long>();
            tempSequence.put("DeviceName", newDevice.getId());
            JSONObject out_json = new JSONObject(tempSequence);

            sendMessageToClient(new TextMessage(out_json.toString()), session);
        }
    }

    private void sendMessageToClient(TextMessage message, WebSocketSession establishedSessions) {
        System.out.println("Message to client: " + establishedSessions.getRemoteAddress() + " \n Message: " + message.getPayload());

        try{
            establishedSessions.sendMessage(new TextMessage(message.getPayload()));
        } catch (IOException e) {
            System.out.println("Failed to send message. " + e);
        }
    }
}
