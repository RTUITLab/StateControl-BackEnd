package ru.realityfamily.controllback.Controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.realityfamily.controllback.GameMessageHandler;
import ru.realityfamily.controllback.Repository.DevicesRepository;
import ru.realityfamily.controllback.Repository.StateRepository;

import java.util.*;

@RestController
@RequestMapping("api/control")
public class ControlController {
    @Autowired
    private DevicesRepository devicesRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private GameMessageHandler gameMessageHandler;

    @GetMapping(value = "/games", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllGames() {
        gameMessageHandler.check();

        System.out.println("request for games");

        Set<String> games = new HashSet<>();
        devicesRepository.findAll().forEach(device -> {
            games.add(device.getGame());
        });

        Map<String, Object> tempSequence = new HashMap<String, Object>();
        tempSequence.put("Data", games);
        tempSequence.put("Status", "Games");
        JSONObject out_json = new JSONObject(tempSequence);
        return out_json.toString();
    }

    @GetMapping(value = "/{game}/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDevicesByGame (@PathVariable(value = "game") String Game) {
        gameMessageHandler.check();

        System.out.println("request for devices");

        List<String> devices = new ArrayList<>();
        devicesRepository.findAll().forEach(device -> {
            if (Game.equals(device.getGame())) {
                devices.add(device.getSessionId());
            }
        });

        Map<String, Object> tempSequence = new HashMap<String, Object>();
        tempSequence.put("Data", devices);
        tempSequence.put("Status", "Devices");
        JSONObject out_json = new JSONObject(tempSequence);
        return out_json.toString();
    }

    @GetMapping(value = "/{game}/states", produces = MediaType.APPLICATION_JSON_VALUE)
    public String GetGameStates(@PathVariable(value = "game") String Game) {
        System.out.println("request for states");

        List<String> States = new ArrayList<>();
        stateRepository.findAll().forEach(state -> {
            if (Game.equals(state.getGameName())) {
                States.addAll(state.getStatesList());
            }
        });

        Map<String, Object> tempSequence = new HashMap<String, Object>();
        tempSequence.put("Data", States);
        tempSequence.put("Status", "States");
        JSONObject out_json = new JSONObject(tempSequence);
        return out_json.toString();
    }

    @PostMapping(value = "/state", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity SendState(@RequestBody String json) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (json.equals("") || json.equals("{}")) {
            return new ResponseEntity<>("No content in request.", HttpStatus.NO_CONTENT);
        }else if (!(obj.has("DeviceId") && obj.has("GameName") && obj.has("State"))) {
            return new ResponseEntity<String>("Incomplete content in the request.", HttpStatus.NO_CONTENT);
        } else {
            try {
                gameMessageHandler.SendStateToDevice(obj.getString("DeviceId"), obj.getString("GameName"), obj.getString("State"));
                return ResponseEntity.ok(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new ResponseEntity("Error in getting info from Json", HttpStatus.BAD_REQUEST);
        }
    }
}
