package ru.realityfamily.controllback;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.realityfamily.controllback.Models.Devices;
import ru.realityfamily.controllback.Repository.DevicesRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GameMessageHandler extends TextWebSocketHandler {
    @Autowired
    private DevicesRepository devicesRepository;
    private static Map<UUID, WebSocketSession> establishedSessions = new HashMap<>();

    @Override
    public synchronized void afterConnectionEstablished(WebSocketSession session) throws Exception {
        establishedSessions.put(UUID.randomUUID(), session);
        System.out.println("Connected new client: " + session.getRemoteAddress());
    }

    @Override
    public synchronized void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        devicesRepository.findAll().forEach(device -> {
            if (device.getSessionId().equals(getKeyByValue(establishedSessions, session).toString())) {
                devicesRepository.delete(device);
            }
        });
        establishedSessions.remove(session);
        System.out.println("Disconnected client: " + session.getRemoteAddress());
    }

    @Override
    protected synchronized void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //establishedSessions.forEach(establishedSessions -> sendMessageToClient(message, establishedSessions));

        System.out.println("Message from client: " + session.getRemoteAddress() + " \nMessage: " + message.getPayload());

        JSONObject in_json = new JSONObject(message.getPayload());
        if (in_json.has("Game")) {
            Devices newDevice = new Devices(in_json.getString("Game"), getKeyByValue(establishedSessions, session).toString());
            devicesRepository.save(newDevice);

            Map<String, String> tempSequence = new HashMap<String, String>();
            tempSequence.put("DeviceSession", newDevice.getSessionId());
            JSONObject out_json = new JSONObject(tempSequence);

            sendMessageToClient(new TextMessage(out_json.toString()), session);
        }
    }

    private synchronized void sendMessageToClient(TextMessage message, WebSocketSession establishedSessions) {
        System.out.println("Message to client: " + establishedSessions.getRemoteAddress() + " \nMessage: " + message.getPayload());

        try{
            establishedSessions.sendMessage(new TextMessage(message.getPayload()));
        } catch (IOException e) {
            System.out.println("Failed to send message. " + e);
        }
    }

    public synchronized void SendStateToDevice(String deviceSession, String gameName, String state){
        System.out.println("Message State to client: " + deviceSession + " \nState: " + state);

        devicesRepository.findAll().forEach(device -> {
            if (deviceSession.equals(device.getSessionId()) && gameName.equals(device.getGame())) {
                Map<String, String> tempSequence = new HashMap<String, String>();
                tempSequence.put("DeviceSession", deviceSession);
                tempSequence.put("GameName", gameName);
                tempSequence.put("State", state);
                JSONObject out_json = new JSONObject(tempSequence);

                sendMessageToClient(new TextMessage(out_json.toString()), establishedSessions.get(UUID.fromString(deviceSession)));
            }
        });
    }

    private <T, K> T getKeyByValue(Map<T, K> map, K value) {
        for (Map.Entry<T, K> entity: map.entrySet()){
            if (Objects.equals(entity.getValue(), value)){
                return entity.getKey();
            }
        }
        return null;
    }

    public synchronized void check() {
        devicesRepository.findAll().forEach(device -> {
            if (!establishedSessions.containsKey(UUID.fromString(device.getSessionId()))) {
                System.out.println("Deleting device: " + device.getId());
                devicesRepository.delete(device);
            }
        });
    }
}
